package com.minikernel.app.ui.process;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.minikernel.app.R;
import com.minikernel.app.model.ProcessState;
import com.minikernel.app.model.SimulatedProcess;

import java.util.List;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ProcessViewHolder> {
    private final List<SimulatedProcess> processes;
    private final ProcessActionListener listener;

    public ProcessAdapter(List<SimulatedProcess> processes, ProcessActionListener listener) {
        this.processes = processes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProcessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_process, parent, false);
        return new ProcessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessViewHolder holder, int position) {
        SimulatedProcess process = processes.get(position);
        holder.bind(process);
    }

    @Override
    public int getItemCount() {
        return processes.size();
    }

    class ProcessViewHolder extends RecyclerView.ViewHolder {
        private final TextView pid;
        private final TextView name;
        private final Chip state;
        private final Button ready;
        private final Button run;
        private final Button wait;
        private final Button terminate;
        private final Button delete;

        ProcessViewHolder(@NonNull View itemView) {
            super(itemView);
            pid = itemView.findViewById(R.id.text_process_pid);
            name = itemView.findViewById(R.id.text_process_name);
            state = itemView.findViewById(R.id.chip_process_state);
            ready = itemView.findViewById(R.id.button_process_ready);
            run = itemView.findViewById(R.id.button_process_run);
            wait = itemView.findViewById(R.id.button_process_wait);
            terminate = itemView.findViewById(R.id.button_process_terminate);
            delete = itemView.findViewById(R.id.button_process_delete);
        }

        void bind(SimulatedProcess process) {
            pid.setText(itemView.getContext().getString(R.string.process_pid_format, process.getPid()));
            name.setText(process.getName());
            state.setText(process.getState().name());

            ready.setVisibility(process.getState() == ProcessState.NEW
                    || process.getState() == ProcessState.WAITING ? View.VISIBLE : View.GONE);
            run.setVisibility(process.getState() == ProcessState.READY ? View.VISIBLE : View.GONE);
            wait.setVisibility(process.getState() == ProcessState.RUNNING ? View.VISIBLE : View.GONE);
            terminate.setVisibility(process.getState() == ProcessState.RUNNING ? View.VISIBLE : View.GONE);
            delete.setVisibility(process.getState() == ProcessState.TERMINATED ? View.VISIBLE : View.GONE);

            ready.setOnClickListener(v -> listener.onReady(process));
            run.setOnClickListener(v -> listener.onRun(process));
            wait.setOnClickListener(v -> listener.onWait(process));
            terminate.setOnClickListener(v -> listener.onTerminate(process));
            delete.setOnClickListener(v -> listener.onDelete(process));
        }
    }
}
