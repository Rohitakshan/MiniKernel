package com.minikernel.app.ui.thread;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedThread;
import com.minikernel.app.model.ThreadState;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {
    private final List<SimulatedThread> threads;
    private final ThreadActionListener listener;

    public ThreadAdapter(List<SimulatedThread> threads, ThreadActionListener listener) {
        this.threads = threads;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
        return new ThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        holder.bind(threads.get(position));
    }

    @Override
    public int getItemCount() { return threads.size(); }

    class ThreadViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView tid;
        private final TextView parentPid;
        private final Chip stateChip;
        private final Button ready;
        private final Button run;
        private final Button wait;
        private final Button terminate;
        private final Button delete;

        ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_thread_name);
            tid = itemView.findViewById(R.id.text_thread_tid);
            parentPid = itemView.findViewById(R.id.text_thread_parent);
            stateChip = itemView.findViewById(R.id.chip_thread_state);
            ready = itemView.findViewById(R.id.button_thread_ready);
            run = itemView.findViewById(R.id.button_thread_run);
            wait = itemView.findViewById(R.id.button_thread_wait);
            terminate = itemView.findViewById(R.id.button_thread_terminate);
            delete = itemView.findViewById(R.id.button_thread_delete);
        }

        void bind(SimulatedThread thread) {
            name.setText(thread.getName());
            tid.setText(itemView.getContext().getString(R.string.thread_tid_format, thread.getTid()));
            parentPid.setText(itemView.getContext().getString(R.string.thread_parent_pid_format, thread.getParentPid()));
            stateChip.setText(thread.getState().name());

            ready.setVisibility(View.GONE);
            run.setVisibility(View.GONE);
            wait.setVisibility(View.GONE);
            terminate.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);

            ThreadState state = thread.getState();
            if (state == ThreadState.NEW || state == ThreadState.WAITING) {
                ready.setVisibility(View.VISIBLE);
            } else if (state == ThreadState.READY) {
                run.setVisibility(View.VISIBLE);
            } else if (state == ThreadState.RUNNING) {
                wait.setVisibility(View.VISIBLE);
                terminate.setVisibility(View.VISIBLE);
            } else if (state == ThreadState.TERMINATED) {
                delete.setVisibility(View.VISIBLE);
            }

            ready.setOnClickListener(v -> listener.onReady(thread));
            run.setOnClickListener(v -> listener.onRun(thread));
            wait.setOnClickListener(v -> listener.onWait(thread));
            terminate.setOnClickListener(v -> listener.onTerminate(thread));
            delete.setOnClickListener(v -> listener.onDelete(thread));
        }
    }
}
