package com.minikernel.app.ui.process;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.minikernel.app.R;
import com.minikernel.app.model.ProcessState;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.simulation.ProcessManager;

import java.util.ArrayList;
import java.util.List;

public class ProcessFragment extends Fragment implements ProcessActionListener {

    private final ProcessManager processManager = new ProcessManager();
    private final List<SimulatedProcess> displayedProcesses = new ArrayList<>();

    private ProcessAdapter adapter;
    private TextView processCount;
    private TextView emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        processCount = view.findViewById(R.id.text_process_count);
        emptyState = view.findViewById(R.id.text_process_empty);
        Button createButton = view.findViewById(R.id.button_create_process);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_processes);

        adapter = new ProcessAdapter(displayedProcesses, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        createButton.setOnClickListener(v -> showCreateProcessDialog());
        updateUi();
        return view;
    }

    private void showCreateProcessDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.process_name_hint);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        int horizontalPadding = (int) (24 * getResources().getDisplayMetrics().density);
        input.setPadding(horizontalPadding, 0, horizontalPadding, 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.create_process_dialog_title)
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    processManager.createProcess(input.getText().toString());
                    updateUi();
                })
                .show();
    }

    private void updateUi() {
        if (adapter == null) {
            return;
        }

        displayedProcesses.clear();
        displayedProcesses.addAll(processManager.getProcesses());
        adapter.notifyDataSetChanged();

        int count = displayedProcesses.size();
        processCount.setText(getResources().getQuantityString(R.plurals.process_count, count, count));
        emptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    private void showTransitionResult(boolean changed, String message) {
        if (changed) {
            updateUi();
        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReady(SimulatedProcess process) {
        showTransitionResult(processManager.transitionToReady(process), getString(R.string.invalid_transition));
    }

    @Override
    public void onRun(SimulatedProcess process) {
        showTransitionResult(processManager.transitionToRunning(process), getString(R.string.invalid_transition));
    }

    @Override
    public void onWait(SimulatedProcess process) {
        showTransitionResult(processManager.transitionToWaiting(process), getString(R.string.invalid_transition));
    }

    @Override
    public void onTerminate(SimulatedProcess process) {
        showTransitionResult(processManager.terminate(process), getString(R.string.invalid_transition));
    }

    @Override
    public void onDelete(SimulatedProcess process) {
        boolean deleted = processManager.deleteTerminated(process);
        if (deleted) {
            updateUi();
        } else {
            Toast.makeText(requireContext(), R.string.delete_only_terminated, Toast.LENGTH_SHORT).show();
        }
    }
}
