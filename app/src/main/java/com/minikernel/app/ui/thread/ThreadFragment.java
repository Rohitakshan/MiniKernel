package com.minikernel.app.ui.thread;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.model.SimulatedThread;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.ThreadManager;

import java.util.ArrayList;
import java.util.List;

public class ThreadFragment extends Fragment implements ThreadActionListener {
    private final ProcessManager processManager = ProcessManager.getInstance();
    private final ThreadManager threadManager = ThreadManager.getInstance();
    private final List<SimulatedProcess> processes = new ArrayList<>();
    private final List<SimulatedThread> displayedThreads = new ArrayList<>();

    private Spinner processSpinner;
    private ArrayAdapter<String> processSpinnerAdapter;
    private ThreadAdapter adapter;
    private TextView selectedProcessText;
    private TextView threadCount;
    private TextView emptyState;
    private boolean refreshingSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);

        processSpinner = view.findViewById(R.id.spinner_thread_process);
        selectedProcessText = view.findViewById(R.id.text_selected_process);
        threadCount = view.findViewById(R.id.text_thread_count);
        emptyState = view.findViewById(R.id.text_thread_empty);
        Button createButton = view.findViewById(R.id.button_create_thread);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_threads);

        processSpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        processSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        processSpinner.setAdapter(processSpinnerAdapter);
        processSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View itemView, int position, long id) {
                if (!refreshingSpinner) updateUi();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { updateUi(); }
        });

        adapter = new ThreadAdapter(displayedThreads, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        createButton.setOnClickListener(v -> showCreateThreadDialog());
        refreshProcesses();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (processSpinner != null) refreshProcesses();
    }

    private void refreshProcesses() {
        processes.clear();
        processes.addAll(processManager.getProcesses());

        refreshingSpinner = true;
        processSpinnerAdapter.clear();
        for (SimulatedProcess process : processes) {
            processSpinnerAdapter.add(getString(R.string.thread_process_option, process.getPid(), process.getName()));
        }
        processSpinnerAdapter.notifyDataSetChanged();
        if (!processes.isEmpty()) processSpinner.setSelection(0);
        refreshingSpinner = false;
        updateUi();
    }

    @Nullable
    private SimulatedProcess getSelectedProcess() {
        int position = processSpinner.getSelectedItemPosition();
        if (position < 0 || position >= processes.size()) return null;
        return processes.get(position);
    }

    private void showCreateThreadDialog() {
        SimulatedProcess process = getSelectedProcess();
        if (process == null) {
            Toast.makeText(requireContext(), R.string.thread_create_requires_process, Toast.LENGTH_SHORT).show();
            return;
        }

        EditText input = new EditText(requireContext());
        input.setHint(R.string.thread_name_hint);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        int horizontalPadding = (int) (24 * getResources().getDisplayMetrics().density);
        input.setPadding(horizontalPadding, 0, horizontalPadding, 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.create_thread_dialog_title)
                .setMessage(getString(R.string.thread_create_for_process, process.getPid(), process.getName()))
                .setView(input)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    threadManager.createThread(process, input.getText().toString());
                    updateUi();
                })
                .show();
    }

    private void updateUi() {
        if (adapter == null || processSpinner == null) return;
        SimulatedProcess process = getSelectedProcess();
        displayedThreads.clear();
        if (process != null) displayedThreads.addAll(threadManager.getThreads(process));
        adapter.notifyDataSetChanged();

        if (process == null) {
            selectedProcessText.setText(R.string.thread_no_process_selected);
            threadCount.setText(getResources().getQuantityString(R.plurals.thread_count, 0, 0));
            emptyState.setText(R.string.thread_create_process_first);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            selectedProcessText.setText(getString(R.string.thread_selected_process, process.getPid(), process.getName()));
            int count = displayedThreads.size();
            threadCount.setText(getResources().getQuantityString(R.plurals.thread_count, count, count));
            emptyState.setText(R.string.thread_empty);
            emptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void showTransitionResult(boolean changed, String message) {
        if (changed) updateUi();
        else Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void onReady(SimulatedThread thread) {
        showTransitionResult(threadManager.transitionToReady(thread), getString(R.string.invalid_thread_transition));
    }
    @Override public void onRun(SimulatedThread thread) {
        showTransitionResult(threadManager.transitionToRunning(thread), getString(R.string.invalid_thread_transition));
    }
    @Override public void onWait(SimulatedThread thread) {
        showTransitionResult(threadManager.transitionToWaiting(thread), getString(R.string.invalid_thread_transition));
    }
    @Override public void onTerminate(SimulatedThread thread) {
        showTransitionResult(threadManager.terminate(thread), getString(R.string.invalid_thread_transition));
    }
    @Override public void onDelete(SimulatedThread thread) {
        boolean deleted = threadManager.deleteTerminated(thread);
        if (deleted) updateUi();
        else Toast.makeText(requireContext(), R.string.thread_delete_only_terminated, Toast.LENGTH_SHORT).show();
    }
}
