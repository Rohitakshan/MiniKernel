package com.minikernel.app.ui.deadlock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.minikernel.app.R;
import com.minikernel.app.simulation.deadlock.BankersAlgorithm;
import com.minikernel.app.simulation.deadlock.BankersStep;
import com.minikernel.app.simulation.deadlock.DeadlockScenario;

import java.util.ArrayList;
import java.util.List;

public class DeadlockFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<BankersStep> steps = new ArrayList<>();
    private int stepIndex = 0;
    private BankersAlgorithm algorithm;

    private TextView stepText, actionText, availableText, finishText, resultText, sequenceText;
    private TextView[] processStates;
    private EditText[][] needInputs;
    private EditText[] availableInputs;
    private Button nextButton, autoButton;
    private boolean autoPlaying;

    private static final int[][] SAFE_ALLOCATION = {
            {0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}
    };
    private static final int[][] UNSAFE_ALLOCATION = {
            {1, 0, 1}, {0, 1, 0}, {1, 1, 0}, {0, 0, 0}, {0, 0, 0}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deadlock, container, false);
        stepText = root.findViewById(R.id.deadlock_step);
        actionText = root.findViewById(R.id.deadlock_action);
        availableText = root.findViewById(R.id.deadlock_available);
        finishText = root.findViewById(R.id.deadlock_finish);
        resultText = root.findViewById(R.id.deadlock_result);
        sequenceText = root.findViewById(R.id.deadlock_sequence);
        processStates = new TextView[] {
                root.findViewById(R.id.deadlock_p1), root.findViewById(R.id.deadlock_p2),
                root.findViewById(R.id.deadlock_p3), root.findViewById(R.id.deadlock_p4),
                root.findViewById(R.id.deadlock_p5)
        };
        nextButton = root.findViewById(R.id.deadlock_next);
        autoButton = root.findViewById(R.id.deadlock_auto);

        needInputs = new EditText[5][3];
        int[][] ids = {
                {R.id.need_p1_r1, R.id.need_p1_r2, R.id.need_p1_r3},
                {R.id.need_p2_r1, R.id.need_p2_r2, R.id.need_p2_r3},
                {R.id.need_p3_r1, R.id.need_p3_r2, R.id.need_p3_r3},
                {R.id.need_p4_r1, R.id.need_p4_r2, R.id.need_p4_r3},
                {R.id.need_p5_r1, R.id.need_p5_r2, R.id.need_p5_r3}
        };
        for (int i = 0; i < 5; i++) for (int r = 0; r < 3; r++) {
            needInputs[i][r] = root.findViewById(ids[i][r]);
            needInputs[i][r].setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        availableInputs = new EditText[] {
                root.findViewById(R.id.available_r1), root.findViewById(R.id.available_r2), root.findViewById(R.id.available_r3)
        };
        for (EditText input : availableInputs) input.setInputType(InputType.TYPE_CLASS_NUMBER);

        RadioGroup scenarios = root.findViewById(R.id.deadlock_scenarios);
        scenarios.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.deadlock_safe) loadScenario(true);
            else if (checkedId == R.id.deadlock_unsafe) loadScenario(false);
        });
        root.findViewById(R.id.deadlock_run).setOnClickListener(v -> startSimulation());
        nextButton.setOnClickListener(v -> nextStep());
        autoButton.setOnClickListener(v -> toggleAutoPlay());
        root.findViewById(R.id.deadlock_reset).setOnClickListener(v -> resetView());

        loadScenario(true);
        return root;
    }

    private void loadScenario(boolean safe) {
        stopAuto();
        int[][] need = safe ? new int[][]{
                {7, 4, 3}, {1, 2, 2}, {6, 0, 0}, {0, 1, 1}, {4, 3, 1}
        } : new int[][]{
                {1, 1, 0}, {1, 1, 1}, {1, 1, 1}, {0, 0, 0}, {0, 0, 0}
        };
        int[] available = safe ? new int[]{3, 3, 2} : new int[]{0, 0, 1};
        int[][] allocation = safe ? SAFE_ALLOCATION : UNSAFE_ALLOCATION;
        fillInputs(need, available);
        algorithm = safe ? DeadlockScenario.safe() : DeadlockScenario.unsafe();
        resetView();
    }

    private void fillInputs(int[][] need, int[] available) {
        for (int i = 0; i < 5; i++) for (int r = 0; r < 3; r++)
            needInputs[i][r].setText(String.valueOf(need[i][r]));
        for (int r = 0; r < 3; r++) availableInputs[r].setText(String.valueOf(available[r]));
    }

    private BankersAlgorithm readUserInput() {
        int[][] need = new int[5][3];
        int[] available = new int[3];
        for (int i = 0; i < 5; i++) for (int r = 0; r < 3; r++)
            need[i][r] = parseInput(needInputs[i][r]);
        for (int r = 0; r < 3; r++) available[r] = parseInput(availableInputs[r]);

        // Allocation stays with the selected preset so the release/resource flow remains visual.
        int[][] allocation = isUnsafePreset() ? UNSAFE_ALLOCATION : SAFE_ALLOCATION;
        int activeProcesses = isUnsafePreset() ? 3 : 5;
        int[][] trimmedNeed = new int[activeProcesses][3];
        int[][] trimmedAllocation = new int[activeProcesses][3];
        for (int i = 0; i < activeProcesses; i++) {
            System.arraycopy(need[i], 0, trimmedNeed[i], 0, 3);
            System.arraycopy(allocation[i], 0, trimmedAllocation[i], 0, 3);
        }
        return new BankersAlgorithm(trimmedAllocation, trimmedNeed, available, true);
    }

    private boolean isUnsafePreset() {
        return getScenarioId() == R.id.deadlock_unsafe;
    }

    private int getScenarioId() {
        View root = getView();
        RadioGroup group = root == null ? null : root.findViewById(R.id.deadlock_scenarios);
        return group == null ? R.id.deadlock_safe : group.getCheckedRadioButtonId();
    }

    private int parseInput(EditText input) {
        String value = input.getText().toString().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("All matrix fields are required.");
        int parsed = Integer.parseInt(value);
        if (parsed < 0) throw new IllegalArgumentException("Values cannot be negative.");
        return parsed;
    }

    private void startSimulation() {
        stopAuto();
        try {
            algorithm = readUserInput();
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        steps.clear();
        steps.addAll(algorithm.generateSteps());
        stepIndex = 0;
        resultText.setText("READY — press Next Step to execute the safety check");
        resultText.setVisibility(View.VISIBLE);
        sequenceText.setText("Safe sequence: —");
        nextButton.setEnabled(true);
        autoButton.setEnabled(true);
        showMatrices();
    }

    private void nextStep() {
        if (steps.isEmpty()) startSimulation();
        if (stepIndex >= steps.size()) return;
        BankersStep step = steps.get(stepIndex++);
        render(step);
        if (stepIndex >= steps.size()) {
            nextButton.setEnabled(false);
            autoPlaying = false;
            autoButton.setText("Auto Play");
        }
    }

    private void render(BankersStep step) {
        stepText.setText("Step " + stepIndex + " / " + steps.size());
        actionText.setText(step.getMessage());
        availableText.setText("Available / Work: " + vector(step.getWork()));
        boolean[] finish = step.getFinish();
        StringBuilder finishBuilder = new StringBuilder("Finish: ");
        for (int i = 0; i < finish.length; i++) finishBuilder.append("P").append(i + 1).append("=").append(finish[i] ? "true" : "false").append(i + 1 < finish.length ? "   " : "");
        finishText.setText(finishBuilder.toString());
        for (int i = 0; i < processStates.length; i++) {
            if (i >= finish.length) processStates[i].setVisibility(View.GONE);
            else {
                processStates[i].setVisibility(View.VISIBLE);
                String state = finish[i] ? "FINISHED ✓" : "WAITING";
                if (i == step.getProcessIndex() && step.getType() == BankersStep.Type.CHECK) state = "CHECKING...";
                processStates[i].setText("P" + (i + 1) + "    " + state);
            }
        }
        if (step.getType() == BankersStep.Type.RELEASE && step.getProcessIndex() >= 0) {
            String current = sequenceText.getText().toString();
            String prefix = "Safe sequence: ";
            if (current.startsWith(prefix)) current = current.substring(prefix.length());
            if (current.equals("—")) current = "";
            sequenceText.setText(prefix + (current.isEmpty() ? "P" + (step.getProcessIndex() + 1) : current + " → P" + (step.getProcessIndex() + 1)));
        }
        if (step.getType() == BankersStep.Type.UNSAFE) resultText.setText("UNSAFE STATE ⚠");
        if (step.getType() == BankersStep.Type.COMPLETE) resultText.setText("SAFE STATE ✓");
    }

    private void showMatrices() {
        int[][] need = algorithm.getNeed();
        StringBuilder b = new StringBuilder("Need Matrix\n");
        for (int i = 0; i < need.length; i++) b.append("P").append(i + 1).append(": ").append(vector(need[i])).append("\n");
        actionText.setText(b.toString().trim());
        availableText.setText("Available / Work: —");
        finishText.setText("Finish: —");
        for (int i = 0; i < processStates.length; i++) {
            processStates[i].setVisibility(i < need.length ? View.VISIBLE : View.GONE);
            if (i < need.length) processStates[i].setText("P" + (i + 1) + "    READY");
        }
    }

    private void resetView() {
        steps.clear();
        stepIndex = 0;
        stepText.setText("Ready");
        actionText.setText("Edit the Need Matrix / Available values, then press Run.");
        availableText.setText("Available / Work: —");
        finishText.setText("Finish: —");
        sequenceText.setText("Safe sequence: —");
        resultText.setText("NOT RUN");
        nextButton.setEnabled(false);
        autoButton.setEnabled(false);
        for (TextView state : processStates) state.setVisibility(View.VISIBLE);
        showMatrices();
    }

    private void toggleAutoPlay() {
        if (autoPlaying) { stopAuto(); return; }
        autoPlaying = true;
        autoButton.setText("Stop Auto Play");
        autoNext();
    }

    private void autoNext() {
        if (!autoPlaying) return;
        if (steps.isEmpty()) startSimulation();
        if (stepIndex >= steps.size()) { stopAuto(); return; }
        nextStep();
        handler.postDelayed(this::autoNext, 700);
    }

    private void stopAuto() {
        autoPlaying = false;
        handler.removeCallbacksAndMessages(null);
        if (autoButton != null) autoButton.setText("Auto Play");
    }

    private static String vector(int[] v) { return "[ " + v[0] + "  " + v[1] + "  " + v[2] + " ]"; }

    @Override public void onDestroyView() { stopAuto(); super.onDestroyView(); }
}
