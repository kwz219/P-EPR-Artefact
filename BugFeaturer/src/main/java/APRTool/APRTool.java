package APRTool;

import Preference.HistoryPreference;
import RepairSample.FixStatus;
import Util.BuggyContext;
import Util.IO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import spoon.reflect.declaration.CtElement;
import Preference.TypePreferenceMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APRTool {
    String name;

    TypePreferenceMatcher type_matcher;
    public HistoryPreference history_matcher = new HistoryPreference();

    public String getName() {
        return name;
    }

    public JSONObject getToolInfo() {
        JSONObject tool_info = new JSONObject();
        tool_info.put("tool_name", this.name);
        tool_info.put("explicit_preferences", this.type_matcher.keywords);
        tool_info.put("total_fixed_count", this.history_matcher.getTotalFixedCount());
        tool_info.put("total_failed_count", this.history_matcher.getUnableFixedCount());
        JSONObject history = new JSONObject();

        HashMap<String, int[]> type_pre = this.history_matcher.type_preferences;
        HashMap<String, int[]> test_pre = this.history_matcher.test_err_preferences;

        history.put("type_history", transferHistory(type_pre));
        history.put("test_history", transferHistory(test_pre));
        tool_info.put("history_preferences", history);

        return tool_info;
    }

    public static JSONObject transferHistory(HashMap<String, int[]> map) {
        JSONObject history_data = new JSONObject();
        for (String type : map.keySet()) {
            int[] fix_fail = map.get(type);
            history_data.put(type, fix_fail);
        }
        return history_data;
    }


    public APRTool(String config_path) {
        JSONObject tool_config = IO.readJsonFile(config_path);
        this.name = tool_config.getString("tool_name");
        List<String> ex_keywords = tool_config.getJSONArray("explicit_preferences").toJavaList(String.class);
        this.type_matcher = new TypePreferenceMatcher(ex_keywords);

        this.history_matcher.initFromConfig(tool_config);
    }

    public APRTool(String tool_name, String[] type_keywords) {
        this.name = tool_name;
        this.type_matcher = new TypePreferenceMatcher(type_keywords);
    }

    public APRTool(String tool_name, List<String> type_keywords) {
        this.name = tool_name;
        this.type_matcher = new TypePreferenceMatcher(type_keywords);
    }


    public boolean type_matches(CtElement ce) {
        ArrayList<String> types = this.type_matcher.matchTypes(ce);
        if (types.size() > 0) {
            return true;
        }
        return false;
    }


    public int[] getPreferenceScore(BuggyContext ce) {

        return this.history_matcher.getPreferenceScore(ce);
    }

    public int[] getTestPreferenceScore(String test_err_type) {
        return this.history_matcher.getTestPreferenceScore(test_err_type);
    }

    public double getHistoryFixesScore() {
        return this.history_matcher.getHistoryFixesScore();
    }


    public void recordFix(String bugname, CtElement ce, FixStatus fs, String test_error_type) {
        this.history_matcher.recordFix(bugname, ce, fs, test_error_type);
    }

    public void emptyHistory() {
        this.history_matcher = new HistoryPreference();
    }
}
