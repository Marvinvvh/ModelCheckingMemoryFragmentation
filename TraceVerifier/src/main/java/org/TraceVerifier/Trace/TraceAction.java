package org.TraceVerifier.Trace;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the functionality of TraceActions, and represent the TraceAction in a trace.
 */
public abstract class TraceAction {
    @Expose
    private final Type type;
    @Expose
    private int pointer;
    @Expose
    private int size = 0;

    public TraceAction(Type type, int pointer) {
        this.type = type;
        this.pointer = pointer;
    }

    public TraceAction(Type type, int pointer, int size) {
        this(type, pointer);
        this.size = size;
    }

    public boolean isAlloc() {
        return type == Type.eAlloc;
    }

    public boolean isFree() {
        return type == Type.eFree;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 0) {
            return;
        }
        this.size = size;
    }

    public Type getType() {
        return type;
    }

    /**
     * Prepares the trace action so that it can be injected into the model.
     */
    public String toTraceActionString(int order) {
        int allocPointer = 0;
        int allocSize = 0;
        int freePointer = 0;

        String strOrder = String.format("%8s", Integer.toString(order)).replace(" ", "0");
        String strType;
        switch (type) {
            case Type.eAlloc:
                strType = "Alloc";
                allocPointer = pointer;
                allocSize = size;
                break;
            case Type.eFree:
                strType = "Free";
                freePointer = pointer;
                break;
            case Type.eInvalidAction:
                strType = "InvalidAction";
                freePointer = pointer;
                allocSize = size;
                break;
            default:
                throw new java.lang.Error("Not all trace actions are handled when converting to string.");
        }
        // FORMAT:
        //     {00000000, eAllocatorActionAlloc, {00000000, 00000123}, {00000000}},
        //     {00000002, eAllocatorActionFree , {00000000, 00000000}, {00000001}},
        strType = String.format("%-21s", "eAllocatorAction" + strType);
        String strAllocPtr = String.format("%8s", Integer.toString(allocPointer)).replace(" ", "0");
        String strAllocSize = String.format("%8s", Integer.toString(allocSize)).replace(" ", "0");
        String strAlloc = "{" + strAllocPtr + ", " + strAllocSize + "}";
        String strFree = String.format("{%8s}", Integer.toString(freePointer)).replace(" ", "0");
        String separator = ", ";
        return "    {" + strOrder + separator + strType + separator + strAlloc + separator + strFree + "}";
    }

    public List<TraceAction> GetTraceActions() {
        ArrayList<TraceAction> actions = new ArrayList<>();
        actions.add(this);
        return actions;
    }

    public enum Type {
        eFree, // 0
        eAlloc, // 1
        eInvalidAction // 2
    }
}
