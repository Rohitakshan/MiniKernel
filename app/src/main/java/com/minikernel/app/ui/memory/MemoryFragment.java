package com.minikernel.app.ui.memory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.minikernel.app.R;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.memory.AllocationStrategy;
import com.minikernel.app.simulation.memory.MemoryBlock;
import com.minikernel.app.simulation.memory.MemoryManager;

import java.util.List;

public class MemoryFragment extends Fragment {
    private MemoryManager manager;
    private LinearLayout blockContainer;
    private TextView summary;
    private RadioGroup strategyGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_memory, container, false);
        manager = new MemoryManager();
        blockContainer = root.findViewById(R.id.memory_block_container);
        summary = root.findViewById(R.id.memory_summary);
        strategyGroup = root.findViewById(R.id.strategy_group);

        strategyGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.strategy_best) manager.setStrategy(AllocationStrategy.BEST_FIT);
            else if (checkedId == R.id.strategy_worst) manager.setStrategy(AllocationStrategy.WORST_FIT);
            else manager.setStrategy(AllocationStrategy.FIRST_FIT);
            refresh();
        });

        root.findViewById(R.id.allocate_button).setOnClickListener(v -> showAllocateDialog());
        root.findViewById(R.id.compact_button).setOnClickListener(v -> {
            manager.compact();
            refresh();
            Toast.makeText(requireContext(), "Memory compacted", Toast.LENGTH_SHORT).show();
        });
        root.findViewById(R.id.reset_memory_button).setOnClickListener(v -> {
            manager.reset();
            refresh();
        });
        refresh();
        return root;
    }

    private void showAllocateDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        layout.setPadding(pad, 0, pad, 0);

        EditText name = new EditText(requireContext());
        name.setHint("Process name");
        layout.addView(name);

        EditText size = new EditText(requireContext());
        size.setHint("Memory required (KB)");
        size.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(size);

        new AlertDialog.Builder(requireContext())
                .setTitle("Allocate Memory")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Allocate", (dialog, which) -> {
                    String process = name.getText().toString().trim();
                    int kb;
                    try { kb = Integer.parseInt(size.getText().toString().trim()); }
                    catch (Exception e) { Toast.makeText(requireContext(), "Enter a valid size", Toast.LENGTH_SHORT).show(); return; }
                    int pid = findNextPid();
                    if (!manager.allocate(process, pid, kb)) {
                        Toast.makeText(requireContext(), "Allocation failed: no suitable free block", Toast.LENGTH_SHORT).show();
                    }
                    refresh();
                }).show();
    }

    private int findNextPid() {
        int pid = 1001;
        List<com.minikernel.app.model.SimulatedProcess> processes = ProcessManager.getInstance().getProcesses();
        for (com.minikernel.app.model.SimulatedProcess p : processes) pid = Math.max(pid, p.getPid() + 1);
        return pid;
    }

    private void refresh() {
        if (summary == null || blockContainer == null) return;
        int used = manager.getUsedMemory();
        int free = manager.getFreeMemory();
        summary.setText("1024 KB total  •  " + used + " KB used  •  " + free + " KB free\nStrategy: " + manager.getStrategy().getLabel());
        blockContainer.removeAllViews();

        for (int i = 0; i < manager.getBlocks().size(); i++) {
            MemoryBlock block = manager.getBlocks().get(i);
            MaterialCardView card = new MaterialCardView(requireContext());
            card.setRadius(dp(14));
            card.setCardElevation(dp(2));
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(16), dp(12), dp(16), dp(12));

            TextView title = new TextView(requireContext());
            title.setText(block.isAllocated() ? block.getProcessName() + "  (PID " + block.getPid() + ")" : "FREE BLOCK");
            title.setTextSize(16);
            title.setTextColor(requireContext().getColor(com.google.android.material.R.color.design_default_color_on_background));
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(title);

            TextView details = new TextView(requireContext());
            details.setText("Start: " + block.getStart() + " KB    Size: " + block.getSize() + " KB");
            details.setTextSize(13);
            row.addView(details);

            if (block.isAllocated()) {
                MaterialButton freeButton = new MaterialButton(requireContext());
                freeButton.setText("Free Block");

                freeButton.setOnClickListener(v -> {
                    manager.freeByPid(block.getPid());
                    refresh();
                });

                row.addView(freeButton);
            }
            card.addView(row);
            blockContainer.addView(card, new LinearLayout.LayoutParams(-1, -2) {{ bottomMargin = dp(8); }});
        }
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
