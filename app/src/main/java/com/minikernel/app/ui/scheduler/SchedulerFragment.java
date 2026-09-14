package com.minikernel.app.ui.scheduler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.scheduling.FCFSScheduler;
import com.minikernel.app.simulation.scheduling.RoundRobinScheduler;
import com.minikernel.app.simulation.scheduling.SJFScheduler;
import com.minikernel.app.simulation.scheduling.ScheduleSegment;
import com.minikernel.app.simulation.scheduling.Scheduler;
import com.minikernel.app.simulation.scheduling.SchedulingProcess;
import com.minikernel.app.simulation.scheduling.SchedulingResult;
import com.minikernel.app.simulation.scheduling.SchedulingSimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SchedulerFragment extends Fragment {

    private LinearLayout processContainer;
    private RadioGroup algorithmGroup;
    private LinearLayout quantumRow;
    private EditText quantumInput;
    private TextView timeText, cpuText, queueText, ganttText, metricsText, averagesText, statusText;
    private final List<ProcessRow> rows = new ArrayList<>();
    private SchedulingSimulation simulation;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean autoPlaying;

    private static class ProcessRow {
        SimulatedProcess process;
        CheckBox selected;
        EditText arrival;
        EditText burst;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduler, container, false);

        processContainer = view.findViewById(R.id.scheduler_process_container);
        algorithmGroup = view.findViewById(R.id.algorithm_group);
        quantumRow = view.findViewById(R.id.quantum_row);
        quantumInput = view.findViewById(R.id.edit_quantum);
        timeText = view.findViewById(R.id.text_sim_time);
        cpuText = view.findViewById(R.id.text_cpu);
        queueText = view.findViewById(R.id.text_ready_queue);
        ganttText = view.findViewById(R.id.text_gantt);
        metricsText = view.findViewById(R.id.text_metrics);
        averagesText = view.findViewById(R.id.text_averages);
        statusText = view.findViewById(R.id.text_scheduler_status);

        algorithmGroup.setOnCheckedChangeListener((group, checkedId) ->
                quantumRow.setVisibility(checkedId == R.id.radio_rr ? View.VISIBLE : View.GONE));

        view.findViewById(R.id.button_refresh_processes).setOnClickListener(v -> loadProcesses());
        view.findViewById(R.id.button_run_scheduler).setOnClickListener(v -> startSimulation());
        view.findViewById(R.id.button_next_step).setOnClickListener(v -> nextStep());
        view.findViewById(R.id.button_auto_play).setOnClickListener(v -> toggleAutoPlay());
        view.findViewById(R.id.button_reset_scheduler).setOnClickListener(v -> resetSimulation());

        loadProcesses();
        return view;
    }

    private void loadProcesses() {
        processContainer.removeAllViews();
        rows.clear();

        List<SimulatedProcess> processes = ProcessManager.getInstance().getProcesses();
        for (int i = 0; i < processes.size(); i++) {
            SimulatedProcess process = processes.get(i);
            ProcessRow row = new ProcessRow();
            row.process = process;

            LinearLayout line = new LinearLayout(requireContext());
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setGravity(Gravity.CENTER_VERTICAL);
            line.setPadding(0, 8, 0, 8);

            row.selected = new CheckBox(requireContext());
            row.selected.setChecked(true);
            row.selected.setText("PID " + process.getPid() + "  " + process.getName());
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2.4f);
            line.addView(row.selected, cp);

            row.arrival = smallNumberInput(i == 0 ? 0 : i);
            row.burst = smallNumberInput(5 - Math.min(i, 3));
            line.addView(row.arrival, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));
            line.addView(row.burst, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));

            processContainer.addView(line);
            rows.add(row);
        }

        statusText.setText(processes.isEmpty()
                ? "No processes available. Create processes in Process Manager first."
                : processes.size() + " process(es) loaded from Process Manager.");
        resetSimulation();
    }

    private EditText smallNumberInput(int value) {
        EditText e = new EditText(requireContext());
        e.setText(String.valueOf(value));
        e.setInputType(InputType.TYPE_CLASS_NUMBER);
        e.setSingleLine(true);
        e.setGravity(Gravity.CENTER);
        e.setHint("0");
        e.setPadding(4, 0, 4, 0);
        return e;
    }

    private void startSimulation() {
        List<SchedulingProcess> inputs = new ArrayList<>();
        for (ProcessRow row : rows) {
            if (!row.selected.isChecked()) continue;
            try {
                int arrival = Integer.parseInt(row.arrival.getText().toString().trim());
                int burst = Integer.parseInt(row.burst.getText().toString().trim());
                inputs.add(new SchedulingProcess(row.process.getPid(), row.process.getName(), arrival, burst));
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Enter valid arrival and burst times.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (inputs.size() < 2) {
            Toast.makeText(requireContext(), "Select at least 2 processes.", Toast.LENGTH_SHORT).show();
            return;
        }

        Scheduler scheduler;
        int checked = algorithmGroup.getCheckedRadioButtonId();
        int quantum = 2;
        if (checked == R.id.radio_sjf) scheduler = new SJFScheduler();
        else if (checked == R.id.radio_rr) {
            try { quantum = Integer.parseInt(quantumInput.getText().toString().trim()); }
            catch (Exception e) { quantum = 2; }
            if (quantum <= 0) {
                Toast.makeText(requireContext(), "Quantum must be greater than 0.", Toast.LENGTH_SHORT).show();
                return;
            }
            scheduler = new RoundRobinScheduler();
        } else scheduler = new FCFSScheduler();

        try {
            simulation = new SchedulingSimulation(scheduler.schedule(inputs, quantum));
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        autoPlaying = false;
        renderSimulation();
        statusText.setText("Simulation ready. Press Next Step or Auto Play.");
    }

    private void nextStep() {
        if (simulation == null) {
            startSimulation();
            return;
        }
        if (!simulation.hasNext()) {
            statusText.setText("Simulation complete.");
            return;
        }
        ScheduleSegment segment = simulation.nextStep();
        renderSimulation();
        cpuText.setText("CPU: PID " + segment.getPid() + " — " + segment.getName()
                + "  [" + segment.getStartTime() + " → " + segment.getEndTime() + "]");
        if (!simulation.hasNext()) statusText.setText("Simulation complete.");
    }

    private void toggleAutoPlay() {
        if (simulation == null) {
            startSimulation();
            if (simulation == null) return;
        }
        autoPlaying = !autoPlaying;
        if (autoPlaying) {
            statusText.setText("Auto Play running...");
            autoPlayStep();
        } else {
            statusText.setText("Auto Play paused.");
        }
    }

    private void autoPlayStep() {
        if (!autoPlaying || simulation == null || !simulation.hasNext()) {
            autoPlaying = false;
            if (simulation != null && simulation.isComplete()) statusText.setText("Simulation complete.");
            return;
        }
        nextStep();
        handler.postDelayed(this::autoPlayStep, 850);
    }

    private void resetSimulation() {
        autoPlaying = false;
        handler.removeCallbacksAndMessages(null);
        simulation = null;
        timeText.setText("Time: 0");
        cpuText.setText("CPU: —");
        queueText.setText("Ready Queue: —");
        ganttText.setText("No execution segments yet.");
        metricsText.setText("Run a simulation to calculate metrics.");
        averagesText.setText("Average Waiting: —    Average Turnaround: —");
    }

    private void renderSimulation() {
        if (simulation == null) return;
        timeText.setText("Time: " + simulation.getCurrentTime());

        List<ScheduleSegment> visible = simulation.getVisibleSegments();
        StringBuilder gantt = new StringBuilder();
        for (ScheduleSegment s : visible) {
            if (gantt.length() > 0) gantt.append("  ");
            gantt.append("| PID ").append(s.getPid()).append(" ").append(s.getName())
                    .append(" [").append(s.getStartTime()).append("-").append(s.getEndTime()).append("] |");
        }
        ganttText.setText(gantt.length() == 0 ? "No execution segments yet." : gantt.toString());

        StringBuilder queue = new StringBuilder();
        if (simulation.hasNext()) {
            for (int i = simulation.getCurrentIndex(); i < simulation.getResult().getSegments().size(); i++) {
                ScheduleSegment s = simulation.getResult().getSegments().get(i);
                if (queue.length() > 0) queue.append(" → ");
                queue.append("PID ").append(s.getPid());
            }
        }
        queueText.setText("Upcoming CPU segments: " + (queue.length() == 0 ? "none" : queue));

        SchedulingResult result = simulation.getResult();
        StringBuilder metrics = new StringBuilder("PID / Name              CT    TAT    WT\n");
        for (SchedulingResult.Metric m : result.getMetrics()) {
            metrics.append(String.format(Locale.US, "%d / %s                 %d      %d      %d\n",
                    m.getProcess().getPid(), m.getProcess().getName(),
                    m.getCompletionTime(), m.getTurnaroundTime(), m.getWaitingTime()));
        }
        metricsText.setText(metrics.toString());
        averagesText.setText(String.format(Locale.US, "Average Waiting: %.2f    Average Turnaround: %.2f",
                result.getAverageWaitingTime(), result.getAverageTurnaroundTime()));
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
