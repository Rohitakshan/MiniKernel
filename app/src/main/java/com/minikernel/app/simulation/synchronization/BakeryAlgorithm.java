package com.minikernel.app.simulation.synchronization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Step-by-step simulation of Lamport's Bakery mutual exclusion using selected processes. */
public final class BakeryAlgorithm {
    private BakeryAlgorithm() { }
    public static List<SimulationStep> generateDemo(int count,String[] names,int[] pids){
        int n=Math.max(2,Math.min(5,count)); List<SimulationStep> out=new ArrayList<>(); int[] number=new int[n]; String[] state=new String[n]; Arrays.fill(state,"READY");
        add(out,"SYSTEM","Simulation initialized",n+" selected processes are ready to compete for one critical section.",false,number,state);
        for(int i=0;i<n;i++){
            state[i]="CHOOSING"; add(out,label(names,pids,i),"choosing a ticket","choosing["+i+"] = true. "+names[i]+" is selecting a number.",false,number,state);
            int max=0; for(int v:number) max=Math.max(max,v); number[i]=max+1; state[i]="WAITING";
            add(out,label(names,pids,i),"number["+i+"] = "+number[i],"Ticket chosen. Lower (number, process order) gets priority.",false,number,state);
            state[i]="READY";
        }
        for(int i=0;i<n;i++){
            state[i]="CHECKING"; add(out,label(names,pids,i),"compares ticket numbers",names[i]+" checks all other non-zero tickets before entering.",false,number,state);
            state[i]="CRITICAL SECTION"; add(out,label(names,pids,i),"enters critical section",names[i]+" has the smallest remaining ticket and gets exclusive access.",true,number,state);
            number[i]=0; state[i]="READY"; add(out,label(names,pids,i),"number["+i+"] = 0",names[i]+" exits and clears its ticket. The next process can proceed.",false,number,state);
        }
        add(out,"SYSTEM","Simulation complete","All selected processes entered once; the critical section was never shared simultaneously.",false,number,state); return out;
    }
    public static List<SimulationStep> generateDemo(int count){int n=Math.max(2,Math.min(5,count));String[] a=new String[n];int[] p=new int[n];for(int i=0;i<n;i++){a[i]="P"+i;p[i]=i;}return generateDemo(n,a,p);}
    private static void add(List<SimulationStep> out,String actor,String action,String detail,boolean cs,int[] number,String[] state){int[] t=new int[5];String[] s=new String[5];Arrays.fill(s,"HIDDEN");System.arraycopy(number,0,t,0,number.length);System.arraycopy(state,0,s,0,state.length);out.add(new SimulationStep(actor,action,detail,cs,new boolean[5],-1,t,s));}
    private static String label(String[] n,int[] p,int i){return n[i]+" (PID "+p[i]+")";}
}
