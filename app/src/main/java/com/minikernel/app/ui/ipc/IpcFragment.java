package com.minikernel.app.ui.ipc;

import android.app.AlertDialog;
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

import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.ipc.PipeMessage;
import com.minikernel.app.simulation.ipc.SimulatedPipe;

import java.util.ArrayList;
import java.util.List;

public class IpcFragment extends Fragment {
    private final SimulatedPipe pipe = new SimulatedPipe();
    private ProcessManager processManager;
    private SimulatedProcess writer;
    private SimulatedProcess reader;

    private TextView writerView;
    private TextView readerView;
    private TextView pipeStatusView;
    private TextView bufferView;
    private TextView activityView;
    private EditText messageInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ipc, container, false);
        processManager = ProcessManager.getInstance();

        writerView = view.findViewById(R.id.text_writer);
        readerView = view.findViewById(R.id.text_reader);
        pipeStatusView = view.findViewById(R.id.text_pipe_status);
        bufferView = view.findViewById(R.id.text_pipe_buffer);
        activityView = view.findViewById(R.id.text_ipc_activity);
        messageInput = view.findViewById(R.id.input_message);

        Button writerButton = view.findViewById(R.id.button_writer);
        Button readerButton = view.findViewById(R.id.button_reader);
        Button writeButton = view.findViewById(R.id.button_write);
        Button readButton = view.findViewById(R.id.button_read);
        Button resetButton = view.findViewById(R.id.button_reset);

        writerButton.setOnClickListener(v -> chooseProcess(true));
        readerButton.setOnClickListener(v -> chooseProcess(false));
        writeButton.setOnClickListener(v -> writeMessage());
        readButton.setOnClickListener(v -> readMessage());
        resetButton.setOnClickListener(v -> resetPipe());

        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (processManager != null) refresh();
    }

    private void chooseProcess(boolean chooseWriter) {
        List<SimulatedProcess> processes = processManager.getProcesses();
        if (processes.isEmpty()) {
            Toast.makeText(requireContext(), "Create processes in Process Manager first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] labels = new String[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            SimulatedProcess p = processes.get(i);
            labels[i] = "PID " + p.getPid() + " — " + p.getName();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(chooseWriter ? "Choose Writer Process" : "Choose Reader Process")
                .setSingleChoiceItems(labels, -1, (dialog, which) -> {
                    if (chooseWriter) writer = processes.get(which);
                    else reader = processes.get(which);
                    dialog.dismiss();
                    refresh();
                })
                .show();
    }

    private void writeMessage() {
        if (writer == null || reader == null) {
            Toast.makeText(requireContext(), "Select a writer and reader first.", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) {
            messageInput.setError("Enter a message");
            return;
        }
        if (pipe.write(writer.getPid(), reader.getPid(), text)) {
            messageInput.setText("");
            refresh();
        }
    }

    private void readMessage() {
        if (reader == null) {
            Toast.makeText(requireContext(), "Select a reader first.", Toast.LENGTH_SHORT).show();
            return;
        }
        PipeMessage message = pipe.peek();
        if (message == null) {
            Toast.makeText(requireContext(), "Pipe is empty.", Toast.LENGTH_SHORT).show();
            refresh();
            return;
        }
        if (message.getReaderPid() != reader.getPid()) {
            Toast.makeText(requireContext(), "The next message is addressed to PID " + message.getReaderPid(), Toast.LENGTH_SHORT).show();
            return;
        }
        pipe.read();
        refresh();
    }

    private void resetPipe() {
        pipe.reset();
        refresh();
    }

    private void refresh() {
        if (writerView == null) return;
        writerView.setText(writer == null ? "Writer: Not selected" : "Writer: PID " + writer.getPid() + " — " + writer.getName());
        readerView.setText(reader == null ? "Reader: Not selected" : "Reader: PID " + reader.getPid() + " — " + reader.getName());

        pipeStatusView.setText(pipe.isEmpty() ? "EMPTY" : "DATA AVAILABLE • " + pipe.size() + " message(s)");
        PipeMessage next = pipe.peek();
        if (next == null) {
            bufferView.setText("Buffer is empty\n\nWrite a message from the selected writer.");
        } else {
            StringBuilder b = new StringBuilder();
            b.append("Next message\n");
            b.append("PID ").append(next.getWriterPid()).append(" → PID ").append(next.getReaderPid()).append("\n\n");
            b.append('"').append(next.getText()).append('"');
            bufferView.setText(b.toString());
        }

        List<String> history = pipe.getHistory();
        if (history.isEmpty()) {
            activityView.setText("No IPC activity yet.");
        } else {
            StringBuilder log = new StringBuilder();
            int start = Math.max(0, history.size() - 8);
            for (int i = start; i < history.size(); i++) {
                log.append("• ").append(history.get(i)).append('\n');
            }
            activityView.setText(log.toString().trim());
        }
    }
}
