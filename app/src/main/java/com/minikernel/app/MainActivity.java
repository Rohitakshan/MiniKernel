package com.minikernel.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.minikernel.app.ui.about.AboutFragment;
import com.minikernel.app.ui.home.HomeFragment;
import com.minikernel.app.ui.home.ModuleClickListener;
import com.minikernel.app.ui.ipc.IpcFragment;
import com.minikernel.app.ui.deadlock.DeadlockFragment;
import com.minikernel.app.ui.memory.MemoryFragment;
import com.minikernel.app.ui.process.ProcessFragment;
import com.minikernel.app.ui.scheduler.SchedulerFragment;
import com.minikernel.app.ui.synchronization.SynchronizationFragment;
import com.minikernel.app.ui.syscall.SystemCallFragment;
import com.minikernel.app.ui.thread.ThreadFragment;

public class MainActivity extends AppCompatActivity implements ModuleClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getSupportFragmentManager().popBackStack());

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateToolbarState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        updateToolbarState();
    }

    private void updateToolbarState() {
        FragmentManager fm = getSupportFragmentManager();
        boolean atHome = fm.getBackStackEntryCount() == 0;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(!atHome);
            getSupportActionBar().setTitle(atHome ? getString(R.string.app_title) : getCurrentFragmentTitle());
        }
    }

    private String getCurrentFragmentTitle() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (current instanceof ProcessFragment) return getString(R.string.module_process_title);
        if (current instanceof ThreadFragment) return getString(R.string.module_thread_title);
        if (current instanceof SynchronizationFragment) return getString(R.string.module_sync_title);
        if (current instanceof IpcFragment) return getString(R.string.module_ipc_title);
        if (current instanceof SchedulerFragment) return getString(R.string.module_scheduler_title);
        if (current instanceof MemoryFragment) return getString(R.string.module_memory_title);
        if (current instanceof SystemCallFragment) return getString(R.string.module_syscall_title);
        if (current instanceof DeadlockFragment) return "Deadlock Simulator";
        if (current instanceof AboutFragment) return getString(R.string.module_about_title);

        return getString(R.string.app_title);
    }

    @Override
    public void onModuleClicked(String moduleId) {
        Fragment fragment = createFragmentForModule(moduleId);
        if (fragment == null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(moduleId)
                .commit();
    }

    private Fragment createFragmentForModule(@NonNull String moduleId) {
        switch (moduleId) {
            case ModuleClickListener.MODULE_PROCESS:
                return new ProcessFragment();
            case ModuleClickListener.MODULE_THREAD:
                return new ThreadFragment();
            case ModuleClickListener.MODULE_SYNCHRONIZATION:
                return new SynchronizationFragment();
            case ModuleClickListener.MODULE_IPC:
                return new IpcFragment();
            case ModuleClickListener.MODULE_SCHEDULER:
                return new SchedulerFragment();
            case ModuleClickListener.MODULE_MEMORY:
                return new MemoryFragment();
            case ModuleClickListener.MODULE_SYSCALL:
                return new SystemCallFragment();
            case ModuleClickListener.MODULE_DEADLOCK:
                return new DeadlockFragment();
            case ModuleClickListener.MODULE_ABOUT:
                return new AboutFragment();
            default:
                return null;
        }
    }
}
