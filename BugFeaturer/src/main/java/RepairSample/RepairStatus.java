package RepairSample;

import java.util.HashMap;

public class RepairStatus {
    String bug_id;
    HashMap<String,FixStatus> tool_fix_status = new HashMap<>();
    public RepairStatus(String bug_id){
        this.bug_id=bug_id;
    }
    public void record_fix(String tool_name,FixStatus status){
        this.tool_fix_status.put(tool_name,status);
    }

    public int[] countFixStatuses(){

        int[] result={0,0,0,0};
        for(String tool_name:this.tool_fix_status.keySet()){
            FixStatus fix_status=this.tool_fix_status.get(tool_name);
            //System.out.println(fix_status);
            if(fix_status.equals(FixStatus.UnExecuted)){
                result[0]=result[0]+1;
            }else if(fix_status.equals(FixStatus.Correct)){
                result[1]=result[1]+1;
            }else if(fix_status.equals(FixStatus.Overfit)){
                result[2]=result[2]+1;
            }else if(fix_status.equals(FixStatus.Failed)){
                result[3]=result[3]+1;
            }
        }
        assert tool_fix_status.size()==(result[0]+result[1]+result[2]+result[3]);
        return result;
    }

    public int countModelInvocationTimes(){
        return this.tool_fix_status.size();
    }


}
