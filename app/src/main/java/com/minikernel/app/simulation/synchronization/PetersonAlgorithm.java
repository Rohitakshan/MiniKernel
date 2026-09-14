package com.minikernel.app.simulation.synchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Step-by-step simulation of Peterson mutual exclusion using two selected processes. */
public final class PetersonAlgorithm {
    private PetersonAlgorithm() { }
    public static List<SimulationStep> generateDemo(String name0, int pid0, String name1, int pid1) {
        String a=safe(name0,"P0"), b=safe(name1,"P1"); int[] pids={pid0,pid1};
        List<SimulationStep> out=new ArrayList<>(); boolean[] flag={false,false}; String[] state={"READY","READY"}; int turn=0;
        add(out,"SYSTEM","Simulation initialized","Using "+label(a,pids[0])+" and "+label(b,pids[1]) + ".",false,flag,turn,state);
        flag[0]=true; state[0]="REQUESTING"; add(out,label(a,pids[0]),"flag[0] = true",a+" requests the critical section.",false,flag,turn,state);
        turn=1; add(out,label(a,pids[0]),"turn = 1",a+" gives "+b+" priority if both are requesting.",false,flag,turn,state);
        flag[1]=true; state[1]="REQUESTING"; add(out,label(b,pids[1]),"flag[1] = true",b+" also requests the critical section. Contention is visible.",false,flag,turn,state);
        turn=0; add(out,label(b,pids[1]),"turn = 0",b+" gives "+a+" priority. The turn variable breaks the tie.",false,flag,turn,state);
        state[0]="CHECKING"; add(out,label(a,pids[0]),"checks flag[1] && turn == 1","flag[1] is true, but turn is 0, so "+a+" may enter.",false,flag,turn,state);
        state[0]="CRITICAL SECTION"; add(out,label(a,pids[0]),"enters critical section","Only "+a+" is inside. "+b+" must wait.",true,flag,turn,state);
        state[1]="WAITING"; add(out,label(b,pids[1]),"waits","The while condition keeps "+b+" out while "+a+" is inside.",true,flag,turn,state);
        flag[0]=false; state[0]="READY"; add(out,label(a,pids[0]),"flag[0] = false",a+" exits and releases the critical section.",false,flag,turn,state);
        state[1]="CHECKING"; add(out,label(b,pids[1]),"checks condition again","flag[0] is now false, so "+b+" can proceed.",false,flag,turn,state);
        state[1]="CRITICAL SECTION"; add(out,label(b,pids[1]),"enters critical section",b+" now gets exclusive access.",true,flag,turn,state);
        flag[1]=false; state[1]="READY"; add(out,label(b,pids[1]),"flag[1] = false",b+" exits. Mutual exclusion was maintained.",false,flag,turn,state);
        add(out,"SYSTEM","Simulation complete","Neither selected process occupied the critical section at the same time as the other.",false,flag,turn,state);
        return out;
    }
    public static List<SimulationStep> generateDemo(){return generateDemo("P0",0,"P1",1);}
    private static void add(List<SimulationStep> out,String actor,String action,String detail,boolean cs,boolean[] flag,int turn,String[] state){
        boolean[] f=new boolean[5]; System.arraycopy(flag,0,f,0,flag.length); int[] t=new int[5]; String[] s=new String[5]; Arrays.fill(s,"HIDDEN"); s[0]=state[0]; s[1]=state[1]; out.add(new SimulationStep(actor,action,detail,cs,f,turn,t,s));
    }
    private static String safe(String s,String f){return s==null||s.trim().isEmpty()?f:s.trim();}
    private static String label(String n,int pid){return n+" (PID "+pid+")";}
}
