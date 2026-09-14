package com.minikernel.app.ui.home;

public interface ModuleClickListener {

    String MODULE_PROCESS = "process";
    String MODULE_THREAD = "thread";
    String MODULE_SYNCHRONIZATION = "synchronization";
    String MODULE_IPC = "ipc";
    String MODULE_SCHEDULER = "scheduler";
    String MODULE_MEMORY = "memory";
    String MODULE_SYSCALL = "syscall";
    String MODULE_ABOUT = "about";

    void onModuleClicked(String moduleId);
}
