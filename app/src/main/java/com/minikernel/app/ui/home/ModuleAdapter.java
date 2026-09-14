package com.minikernel.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.minikernel.app.R;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private final List<ModuleItem> items;
    private final ModuleClickListener listener;

    public ModuleAdapter(List<ModuleItem> items, ModuleClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module_card, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        ModuleItem item = items.get(position);
        holder.iconLetter.setText(item.getIconLetter());
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onModuleClicked(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        final TextView iconLetter;
        final TextView title;
        final TextView description;

        ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            iconLetter = itemView.findViewById(R.id.text_module_icon);
            title = itemView.findViewById(R.id.text_module_title);
            description = itemView.findViewById(R.id.text_module_desc);
        }
    }
}
