package com.minikernel.app.ui.disk;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.minikernel.app.simulation.disk.DiskScheduler;
import com.minikernel.app.simulation.disk.DiskSchedulingResult;

import java.util.ArrayList;
import java.util.List;

public class DiskSchedulingFragment extends Fragment {
    private EditText requestsInput, headInput, sizeInput;
    private Spinner algorithmSpinner, directionSpinner;
    private LinearLayout resultContainer;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        int pad = dp(18);
        ScrollView scroll = new ScrollView(requireContext());
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root);

        TextView heading = text("Disk Scheduling Simulator", 24, true);
        root.addView(heading);
        root.addView(text("Compare the six standard algorithms by their head movement.", 14, false));

        algorithmSpinner = spinner(new String[]{"FCFS", "SSTF", "SCAN", "C-SCAN", "LOOK", "C-LOOK"});
        addField(root, "Algorithm", algorithmSpinner);
        requestsInput = field("Example: 98, 183, 37, 122, 14, 124, 65, 67", InputType.TYPE_CLASS_TEXT);
        requestsInput.setText("98, 183, 37, 122, 14, 124, 65, 67");
        addField(root, "Disk request queue (track numbers)", requestsInput);
        headInput = field("Example: 53", InputType.TYPE_CLASS_NUMBER);
        headInput.setText("53");
        addField(root, "Initial head position", headInput);
        sizeInput = field("Example: 200", InputType.TYPE_CLASS_NUMBER);
        sizeInput.setText("200");
        addField(root, "Number of tracks (0 to size - 1)", sizeInput);
        directionSpinner = spinner(new String[]{"Move towards higher tracks (Right)", "Move towards lower tracks (Left)"});
        addField(root, "Initial direction (SCAN / C-SCAN / LOOK / C-LOOK)", directionSpinner);

        MaterialButton run = new MaterialButton(requireContext());
        run.setText("Run Disk Scheduling");
        root.addView(run, matchWrap());
        resultContainer = new LinearLayout(requireContext());
        resultContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(resultContainer);
        run.setOnClickListener(v -> runSimulation());
        return scroll;
    }

    private void runSimulation() {
        try {
            String raw = requestsInput.getText().toString().trim();
            if (raw.isEmpty()) throw new IllegalArgumentException("Enter at least one disk request.");
            List<Integer> requests = new ArrayList<>();
            for (String token : raw.split("[,\\s]+")) requests.add(Integer.parseInt(token));
            int head = Integer.parseInt(headInput.getText().toString().trim());
            int size = Integer.parseInt(sizeInput.getText().toString().trim());
            String algorithm = algorithmSpinner.getSelectedItem().toString();
            boolean right = directionSpinner.getSelectedItemPosition() == 0;
            DiskSchedulingResult result = DiskScheduler.schedule(algorithm, requests, head, size, right);
            showResult(algorithm, head, result);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Enter valid numeric values.", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showResult(String algorithm, int initialHead, DiskSchedulingResult result) {
        resultContainer.removeAllViews();
        resultContainer.addView(text("RESULT — " + algorithm, 20, true));
        resultContainer.addView(text("Head movement path", 15, true));
        StringBuilder path = new StringBuilder().append(initialHead);
        for (int track : result.getOrder()) path.append("  →  ").append(track);
        resultContainer.addView(card(path.toString()));
        resultContainer.addView(text("Step-by-step movement", 16, true));
        int previous = initialHead;
        for (int i = 0; i < result.getOrder().size(); i++) {
            int track = result.getOrder().get(i);
            int distance = result.getMovements().get(i);
            resultContainer.addView(text((i + 1) + ". Track " + previous + " → " + track + "   |   " + distance + " tracks", 14, false));
            previous = track;
        }
        resultContainer.addView(card("TOTAL HEAD MOVEMENT: " + result.getTotalHeadMovement() + " tracks"));
    }

    private void addField(LinearLayout root, String label, View input) {
        root.addView(text(label, 14, true));
        LinearLayout.LayoutParams p = matchWrap(); p.bottomMargin = dp(12);
        root.addView(input, p);
    }
    private EditText field(String hint, int type) {
        EditText e = new EditText(requireContext()); e.setSingleLine(true); e.setHint(hint); e.setInputType(type); return e;
    }
    private Spinner spinner(String[] values) {
        Spinner s = new Spinner(requireContext());
        s.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, values)); return s;
    }
    private TextView text(String value, int size, boolean bold) {
        TextView t = new TextView(requireContext()); t.setText(value); t.setTextSize(size);
        t.setTextColor(requireContext().getColor(android.R.color.primary_text_light));
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, dp(8), 0, dp(8)); return t;
    }
    private View card(String value) {
        MaterialCardView card = new MaterialCardView(requireContext());
        card.setRadius(dp(12)); card.setCardElevation(dp(2)); card.setUseCompatPadding(true);
        TextView t = text(value, 16, true); t.setGravity(Gravity.CENTER_VERTICAL); t.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.addView(t); LinearLayout.LayoutParams p = matchWrap(); p.bottomMargin = dp(12); card.setLayoutParams(p); return card;
    }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(-1, -2); }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
}
