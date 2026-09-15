package com.minikernel.app.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minikernel.app.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ModuleClickListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ModuleClickListener) {
            listener = (ModuleClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_modules);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(new ModuleAdapter(buildModuleList(), listener));

        return root;
    }

    private List<ModuleItem> buildModuleList() {
        List<ModuleItem> modules = new ArrayList<>();
        modules.add(new ModuleItem(ModuleClickListener.MODULE_PROCESS, "P",
                getString(R.string.module_process_title), getString(R.string.module_process_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_THREAD, "T",
                getString(R.string.module_thread_title), getString(R.string.module_thread_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_SYNCHRONIZATION, "S",
                getString(R.string.module_sync_title), getString(R.string.module_sync_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_IPC, "I",
                getString(R.string.module_ipc_title), getString(R.string.module_ipc_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_SCHEDULER, "C",
                getString(R.string.module_scheduler_title), getString(R.string.module_scheduler_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_MEMORY, "M",
                getString(R.string.module_memory_title), getString(R.string.module_memory_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_SYSCALL, "K",
                getString(R.string.module_syscall_title), getString(R.string.module_syscall_desc)));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_DEADLOCK, "D",
                "Deadlock Simulator", "Explore Banker's Algorithm and safe/unsafe resource states."));
        modules.add(new ModuleItem(ModuleClickListener.MODULE_ABOUT, "?",
                getString(R.string.module_about_title), getString(R.string.module_about_desc)));
        return modules;
    }
}
