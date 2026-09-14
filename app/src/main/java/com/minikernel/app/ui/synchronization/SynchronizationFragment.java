package com.minikernel.app.ui.synchronization;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.minikernel.app.R;
import com.minikernel.app.model.SimulatedProcess;
import com.minikernel.app.simulation.ProcessManager;
import com.minikernel.app.simulation.synchronization.BakeryAlgorithm;
import com.minikernel.app.simulation.synchronization.PetersonAlgorithm;
import com.minikernel.app.simulation.synchronization.SimulationStep;
import java.util.ArrayList;
import java.util.List;

/** Synchronization simulator that uses the actual simulated processes from Process Manager. */
public class SynchronizationFragment extends Fragment {
    private final ProcessManager pm=ProcessManager.getInstance();
    private Spinner algorithmSpinner,countSpinner; private Spinner[] selectors=new Spinner[5]; private TextView[] cards=new TextView[5];
    private TextView info,step,action,detail,status,shared,order; private Button next,auto,reset; private final List<SimulationStep> steps=new ArrayList<>();
    private int current=-1; private boolean playing; private final Handler handler=new Handler(Looper.getMainLooper());
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle state){
        View v=inflater.inflate(R.layout.fragment_synchronization,container,false); algorithmSpinner=v.findViewById(R.id.spinner_sync_algorithm); countSpinner=v.findViewById(R.id.spinner_sync_process_count); info=v.findViewById(R.id.text_sync_selected_info); step=v.findViewById(R.id.text_sync_step); action=v.findViewById(R.id.text_sync_action); detail=v.findViewById(R.id.text_sync_detail); status=v.findViewById(R.id.text_sync_status); shared=v.findViewById(R.id.text_sync_shared_state); order=v.findViewById(R.id.text_sync_order); next=v.findViewById(R.id.button_sync_next); auto=v.findViewById(R.id.button_sync_auto); reset=v.findViewById(R.id.button_sync_reset);
        for(int i=0;i<5;i++){selectors[i]=v.findViewById(getResources().getIdentifier("spinner_sync_process_"+i,"id",requireContext().getPackageName()));cards[i]=v.findViewById(getResources().getIdentifier("sync_process_"+i,"id",requireContext().getPackageName()));}
        algorithmSpinner.setAdapter(adapter(new String[]{"Peterson Algorithm","Bakery Algorithm"})); countSpinner.setAdapter(adapter(new String[]{"2 processes","3 processes","4 processes","5 processes"}));
        algorithmSpinner.setOnItemSelectedListener(new L(this::configurationChanged)); countSpinner.setOnItemSelectedListener(new L(this::configurationChanged)); for(Spinner s:selectors)s.setOnItemSelectedListener(new L(this::selectionChanged));
        next.setOnClickListener(x->advance()); auto.setOnClickListener(x->toggleAuto()); reset.setOnClickListener(x->resetSimulation()); refreshChoices(); return v;
    }
    @Override public void onResume(){super.onResume();if(algorithmSpinner!=null)refreshChoices();}
    private ArrayAdapter<String> adapter(String[] a){ArrayAdapter<String>x=new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_item,a);x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);return x;}
    private void refreshChoices(){List<SimulatedProcess> list=pm.getProcesses();String[] a=new String[Math.max(1,list.size())];if(list.isEmpty())a[0]="No processes created yet";else for(int i=0;i<list.size();i++)a[i]=label(list.get(i));for(Spinner s:selectors)s.setAdapter(adapter(a));configurationChanged();}
    private void configurationChanged(){if(algorithmSpinner==null)return;int n=participants();for(int i=0;i<5;i++){selectors[i].setVisibility(i<n?View.VISIBLE:View.GONE);cards[i].setVisibility(i<n?View.VISIBLE:View.GONE);}info.setText(pm.getProcesses().size()<n?"Not enough processes. Create at least "+n+" in Process Manager.":"These selectors use the processes you created in Process Manager.");resetSimulation();}
    private void selectionChanged(){if(algorithmSpinner!=null)resetSimulation();}
    private int participants(){return algorithmSpinner.getSelectedItemPosition()==0?2:countSpinner.getSelectedItemPosition()+2;}
    private boolean makeSteps(){List<SimulatedProcess> all=pm.getProcesses();int n=participants();if(all.size()<n)return false;String[] names=new String[n];int[] pids=new int[n];int[] chosen=new int[n];for(int i=0;i<n;i++){int pos=selectors[i].getSelectedItemPosition();if(pos<0||pos>=all.size())return false;chosen[i]=pos;for(int j=0;j<i;j++)if(chosen[j]==pos){info.setText("Select different processes for each participant.");return false;}names[i]=all.get(pos).getName();pids[i]=all.get(pos).getPid();}steps.clear();if(algorithmSpinner.getSelectedItemPosition()==0)steps.addAll(PetersonAlgorithm.generateDemo(names[0],pids[0],names[1],pids[1]));else steps.addAll(BakeryAlgorithm.generateDemo(n,names,pids));return true;}
    private void resetSimulation(){stopAuto();current=-1;boolean ready=makeSteps();render(ready);}
    private void advance(){if(steps.isEmpty()){Toast.makeText(requireContext(),"Create/select enough processes first.",Toast.LENGTH_SHORT).show();return;}if(current>=steps.size()-1){Toast.makeText(requireContext(),"Simulation complete.",Toast.LENGTH_SHORT).show();return;}current++;render(true);}
    private void toggleAuto(){if(playing)stopAuto();else{if(steps.isEmpty()){Toast.makeText(requireContext(),"Create/select enough processes first.",Toast.LENGTH_SHORT).show();return;}playing=true;auto.setText("Pause");autoNext();}}
    private void autoNext(){if(!playing)return;if(current>=steps.size()-1){stopAuto();return;}advance();handler.postDelayed(this::autoNext,900);}
    private void stopAuto(){playing=false;handler.removeCallbacksAndMessages(null);if(auto!=null)auto.setText("Auto Play");}
    private void render(boolean ready){int n=participants();if(!ready){step.setText("Waiting for processes");action.setText("Create/select processes in Process Manager");detail.setText("This module is connected to your Process Manager. Create at least "+n+" processes, then select them above.");status.setText("Critical section: unavailable");shared.setText(algorithmSpinner.getSelectedItemPosition()==0?"flag[0] = false    flag[1] = false    turn = -":"number[] = unavailable");order.setText("Execution order: not started");next.setText("Start");for(int i=0;i<n;i++)cards[i].setText("Participant "+(i+1)+"\nWaiting for process");return;}if(current<0){step.setText("Ready to execute");action.setText("Press Start to execute the first algorithm event");detail.setText("One button press = one simulated execution event. Watch the selected processes and shared synchronization variables change.");status.setText("Critical section: IDLE");shared.setText(algorithmSpinner.getSelectedItemPosition()==0?"flag[0] = false    flag[1] = false    turn = 0":"number[] = all 0");order.setText("Execution order: not started");next.setText("Start");}else{SimulationStep s=steps.get(current);step.setText("Execution Step "+(current+1)+" / "+steps.size());action.setText(s.getActor()+"  •  "+s.getAction());detail.setText(s.getDetail());status.setText(s.isInCriticalSection()?"CRITICAL SECTION: OCCUPIED":"CRITICAL SECTION: FREE");renderShared(s,n);order.setText("Last executed: "+s.getActor());next.setText(current==steps.size()-1?"Done":"Next Step");String[] st=s.getStates();for(int i=0;i<n;i++)cards[i].setText(selectedLabel(i)+"\n"+((st[i]==null||"HIDDEN".equals(st[i]))?"READY":st[i]));}}
    private void renderShared(SimulationStep s,int n){if(algorithmSpinner.getSelectedItemPosition()==0){boolean[]f=s.getFlags();shared.setText("flag[0] = "+f[0]+"    flag[1] = "+f[1]+"    turn = "+s.getTurn());}else{int[]t=s.getTickets();StringBuilder b=new StringBuilder("number[]: ");for(int i=0;i<n;i++){if(i>0)b.append("    ");b.append("P").append(i).append("=").append(t[i]);}shared.setText(b.toString());}}
    private String selectedLabel(int i){List<SimulatedProcess>l=pm.getProcesses();int p=selectors[i].getSelectedItemPosition();return p>=0&&p<l.size()?label(l.get(p)):"Participant "+(i+1);}
    private String label(SimulatedProcess p){return p.getName()+" (PID "+p.getPid()+")";}
    @Override public void onDestroyView(){stopAuto();super.onDestroyView();}
    private static class L implements android.widget.AdapterView.OnItemSelectedListener{private final Runnable r;L(Runnable x){r=x;}public void onItemSelected(android.widget.AdapterView<?>p,View v,int pos,long id){r.run();}public void onNothingSelected(android.widget.AdapterView<?>p){}}
}
