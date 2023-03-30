package APRTool;

import Preference.HistoryPreference;
import RepairSample.RepairSample;
import RepairSample.FixStatus;
import Util.BuggyContext;
import spoon.reflect.declaration.CtElement;
import Preference.TypePreferenceMatcher;

import java.util.ArrayList;

public class APRTool {
    String name;
    TypePreferenceMatcher type_matcher;
    public HistoryPreference history_matcher =new HistoryPreference();

    public String getName() {
        return name;
    }

    public APRTool(String tool_name, String[] type_keywords){
        this.name=tool_name;
        this.type_matcher=new TypePreferenceMatcher(type_keywords);
    }

    public boolean type_matches(CtElement ce){
        ArrayList<String> types=this.type_matcher.matchTypes(ce);
        if(types.size()>0){
            return true;
        }
        return false;
    }



    public int[] getPreferenceScore(BuggyContext ce){
        return this.history_matcher.getPreferenceScore(ce);
    }

    public int[] getTestPreferenceScore(String test_err_type){
        return this.history_matcher.getTestPreferenceScore(test_err_type);
    }
    public double getHistoryFixesScore(){
        return this.history_matcher.getHistoryFixesScore();
    }
    public void recordFix(CtElement ce, FixStatus fs){
        //this.history_matcher.recordFix(ce,fs);
    }

    public void recordFix(String bugname,CtElement ce, FixStatus fs,String test_error_type){
        this.history_matcher.recordFix(bugname,ce,fs,test_error_type);
    }

    public void emptyHistory(){
        this.history_matcher = new HistoryPreference();
    }
}
