package APRTool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import RepairSample.FixStatus;
import org.json.JSONArray;
import org.json.JSONObject;
public class ToolExecutor {
    JSONObject executor_results;
    public void init_results(String path){
        try {
            String result = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject json_result = new JSONObject(result);
            this.executor_results=json_result;
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public FixStatus executor(String tool_name, String bug_id){
        //System.out.println(tool_name);

        if (this.executor_results.has(tool_name)) {

            JSONObject tool_results=this.executor_results.getJSONObject(tool_name);
            List<Object> correct_results = tool_results.getJSONArray("correct").toList();
            List<Object> overfit_results = tool_results.getJSONArray("overfit").toList();

            if (correct_results.contains(bug_id)) {
                return FixStatus.Correct;
            } else if (overfit_results.contains(bug_id)) {
                return FixStatus.Overfit;
            } else {
                return FixStatus.Failed;
            }
        }else{
            throw new NullPointerException("No this tool"+" "+tool_name);
        }
    }
}
