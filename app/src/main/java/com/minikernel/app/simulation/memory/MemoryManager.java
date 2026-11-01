package com.minikernel.app.simulation.memory;

import java.util.ArrayList;
import java.util.List;

/** Educational contiguous-memory allocator using First Fit, Best Fit and Worst Fit. */
public class MemoryManager {
    public static final int DEFAULT_TOTAL_MEMORY = 1024;

    private final int totalMemory;
    private final List<MemoryBlock> blocks = new ArrayList<>();
    private AllocationStrategy strategy = AllocationStrategy.FIRST_FIT;

    public MemoryManager() { this(DEFAULT_TOTAL_MEMORY); }

    public MemoryManager(int totalMemory) {
        if (totalMemory <= 0) throw new IllegalArgumentException("Total memory must be positive");
        this.totalMemory = totalMemory;
        blocks.add(new MemoryBlock(0, totalMemory));
    }

    public void setStrategy(AllocationStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("Strategy cannot be null");
        this.strategy = strategy;
    }

    public AllocationStrategy getStrategy() { return strategy; }
    public int getTotalMemory() { return totalMemory; }
    public List<MemoryBlock> getBlocks() { return new ArrayList<>(blocks); }

    public int getUsedMemory() {
        int used = 0;
        for (MemoryBlock b : blocks) if (b.isAllocated()) used += b.getSize();
        return used;
    }

    public int getFreeMemory() { return totalMemory - getUsedMemory(); }

    public boolean allocate(String processName, int pid, int requestedSize) {
        if (processName == null || processName.trim().isEmpty() || requestedSize <= 0) return false;
        if (requestedSize > getFreeMemory()) return false;

        int index = chooseBlock(requestedSize);
        if (index < 0) return false;

        MemoryBlock block = blocks.get(index);
        int remainder = block.getSize() - requestedSize;
        MemoryBlock allocated = new MemoryBlock(block.getStart(), requestedSize);
        allocated.allocate(processName.trim(), pid);
        blocks.set(index, allocated);
        if (remainder > 0) {
            blocks.add(index + 1, new MemoryBlock(block.getStart() + requestedSize, remainder));
        }
        return true;
    }

    public boolean freeByPid(int pid) {
        for (MemoryBlock block : blocks) {
            if (block.isAllocated() && block.getPid() == pid) {
                block.free();
                mergeFreeBlocks();
                return true;
            }
        }
        return false;
    }

    public boolean freeByIndex(int index) {
        if (index < 0 || index >= blocks.size() || !blocks.get(index).isAllocated()) return false;
        blocks.get(index).free();
        mergeFreeBlocks();
        return true;
    }

    /** Moves allocated blocks together, preserving order, then creates one free block. */
    public void compact() {
        List<MemoryBlock> compacted = new ArrayList<>();
        int nextStart = 0;
        for (MemoryBlock old : blocks) {
            if (!old.isAllocated()) continue;
            MemoryBlock moved = new MemoryBlock(nextStart, old.getSize());
            moved.allocate(old.getProcessName(), old.getPid());
            compacted.add(moved);
            nextStart += old.getSize();
        }
        if (nextStart < totalMemory) compacted.add(new MemoryBlock(nextStart, totalMemory - nextStart));
        blocks.clear();
        blocks.addAll(compacted);
    }

    public void reset() {
        blocks.clear();
        blocks.add(new MemoryBlock(0, totalMemory));
    }

    private int chooseBlock(int requestedSize) {
        int selected = -1;
        if (strategy == AllocationStrategy.FIRST_FIT) {
            for (int i = 0; i < blocks.size(); i++) {
                if (!blocks.get(i).isAllocated() && blocks.get(i).getSize() >= requestedSize) return i;
            }
            return -1;
        }

        int selectedSize = strategy == AllocationStrategy.BEST_FIT ? Integer.MAX_VALUE : -1;
        for (int i = 0; i < blocks.size(); i++) {
            MemoryBlock b = blocks.get(i);
            if (b.isAllocated() || b.getSize() < requestedSize) continue;
            if (strategy == AllocationStrategy.BEST_FIT && b.getSize() < selectedSize) {
                selected = i; selectedSize = b.getSize();
            } else if (strategy == AllocationStrategy.WORST_FIT && b.getSize() > selectedSize) {
                selected = i; selectedSize = b.getSize();
            }
        }
        return selected;
    }

    private void mergeFreeBlocks() {
        for (int i = blocks.size() - 2; i >= 0; i--) {
            MemoryBlock current = blocks.get(i);
            MemoryBlock next = blocks.get(i + 1);
            if (!current.isAllocated() && !next.isAllocated()) {
                MemoryBlock merged = new MemoryBlock(current.getStart(), current.getSize() + next.getSize());
                blocks.set(i, merged);
                blocks.remove(i + 1);
            }
        }
    }
}
