package RepairSample;

import java.util.ArrayList;

public class ResultRecorder {
    public ArrayList <RepairStatus> repair_results=new ArrayList();

    public void addRepairResult(RepairStatus repair_status){
        this.repair_results.add(repair_status);
    }

    public int[] CountCorrectOverfit_SmartEnsemble(){
        int correct_bugs=0;
        int overfit_bugs=0;
        int correct_patches=0;
        int overfit_patches=0;
        for(RepairStatus rs: this.repair_results){
            int[] fix_results=rs.countFixStatuses();
            correct_patches+=fix_results[1];
            overfit_patches+=fix_results[2];
            if (fix_results[1]>0){
                correct_bugs+=1;
            }else if(fix_results[1]==0&&fix_results[2]>0){
                overfit_bugs+=1;
            }
        }
        return new int[]{correct_bugs,overfit_bugs,correct_patches,overfit_patches};
    }

    public int countInvocationTimes(){
        int reduction_times=0;
        for(RepairStatus rs:this.repair_results){
            reduction_times+=rs.countModelInvocationTimes();
        }
        return reduction_times;
    }

}
