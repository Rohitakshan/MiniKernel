package com.minikernel.app.ui.syscall;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.syscall.SystemCall;
import com.minikernel.app.simulation.syscall.SystemCallSimulator;
import com.minikernel.app.simulation.syscall.SyscallStep;

import java.util.ArrayList;
import java.util.List;

public class SystemCallFragment extends Fragment {
    private RadioGroup callGroup;
    private TextView processText, stepText, stageText, actionText, detailText, progressText, statusText;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private List<SimulatedProcess> processes = new ArrayList<>();
    private List<SyscallStep> trace = new ArrayList<>();
    private int currentStep = 0;
    private boolean autoPlaying = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_syscall, container, false);
        callGroup = view.findViewById(R.id.syscall_group);
        processText = view.findViewById(R.id.text_syscall_process);
        stepText = view.findViewById(R.id.text_syscall_step);
        stageText = view.findViewById(R.id.text_syscall_stage);
        actionText = view.findViewById(R.id.text_syscall_action);
        detailText = view.findViewById(R.id.text_syscall_detail_live);
        progressText = view.findViewById(R.id.text_syscall_progress);
        statusText = view.findViewById(R.id.text_syscall_status);

        view.findViewById(R.id.button_refresh_syscall_processes).setOnClickListener(v -> loadProcesses());
        view.findViewById(R.id.button_run_syscall).setOnClickListener(v -> startTrace());
        view.findViewById(R.id.button_next_syscall).setOnClickListener(v -> nextStep());
        view.findViewById(R.id.button_auto_syscall).setOnClickListener(v -> toggleAutoPlay());
        view.findViewById(R.id.button_reset_syscall).setOnClickListener(v -> reset());

        loadProcesses();
        return view;
    }

    private void loadProcesses() {
        processes = new ArrayList<>(ProcessManager.getInstance().getProcesses());
        if (processes.isEmpty()) {
            processText.setText("Process: none — create a process in Process Manager first.");
        } else {
            processText.setText("Process: PID " + processes.get(0).getPid() + " — " + processes.get(0).getName());
        }
        reset();
    }

    private int selectedPid() {
        return processes.isEmpty() ? -1 : processes.get(0).getPid();
    }

    private SystemCall selectedCall() {
        int id = callGroup.getCheckedRadioButtonId();
        if (id == R.id.radio_read) return SystemCall.READ;
        if (id == R.id.radio_write) return SystemCall.WRITE;
        if (id == R.id.radio_open) return SystemCall.OPEN;
        if (id == R.id.radio_close) return SystemCall.CLOSE;
        if (id == R.id.radio_fork) return SystemCall.FORK;
        if (id == R.id.radio_exec) return SystemCall.EXEC;
        return SystemCall.WAIT;
    }

    private void startTrace() {
        if (selectedPid() < 0) {
            Toast.makeText(requireContext(), "Create a process in Process Manager first.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            trace = new SystemCallSimulator().createTrace(selectedCall(), selectedPid());
            currentStep = 0;
            autoPlaying = false;
            renderCurrent();
            statusText.setText("Trace ready. Press Next Step or Auto Play.");
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void nextStep() {
        if (trace.isEmpty()) {
            startTrace();
            return;
        }
        if (currentStep >= trace.size()) {
            statusText.setText("System call complete.");
            return;
        }
        currentStep++;
        renderCurrent();
        if (currentStep >= trace.size()) statusText.setText("System call complete — control returned to user mode.");
    }

    private void toggleAutoPlay() {
        if (trace.isEmpty()) startTrace();
        if (trace.isEmpty()) return;
        autoPlaying = !autoPlaying;
        if (autoPlaying) {
            statusText.setText("Auto Play running...");
            autoStep();
        } else {
            statusText.setText("Auto Play paused.");
        }
    }

    private void autoStep() {
        if (!autoPlaying || currentStep >= trace.size()) {
            autoPlaying = false;
            return;
        }
        nextStep();
        if (autoPlaying) handler.postDelayed(this::autoStep, 750);
    }

    private void renderCurrent() {
        if (trace.isEmpty()) return;
        int index = Math.min(currentStep, trace.size()) - 1;
        if (index < 0) {
            stepText.setText("Step 0");
            stageText.setText("READY");
            actionText.setText("Press Next Step to begin.");
            detailText.setText("The selected system call will be traced through user mode, trap, kernel handling, and return.");
            progressText.setText("0 / " + trace.size());
            return;
        }
        SyscallStep step = trace.get(index);
        stepText.setText("Step " + (index + 1));
        stageText.setText(step.getStage());
        actionText.setText(step.getAction());
        detailText.setText(step.getDetail());
        progressText.setText((index + 1) + " / " + trace.size());
    }

    private void reset() {
        autoPlaying = false;
        handler.removeCallbacksAndMessages(null);
        trace = new ArrayList<>();
        currentStep = 0;
        stepText.setText("Step 0");
        stageText.setText("READY");
        actionText.setText("Choose a system call and press Run Trace.");
        detailText.setText("The trace will simulate the transition from user mode to kernel mode and back.");
        progressText.setText("0 / 0");
        statusText.setText("Ready.");
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
