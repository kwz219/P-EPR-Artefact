package Preference;

import Util.BuggyContext;
import spoon.reflect.declaration.CtElement;

public class HistoryPreferenceMatcher {
    HistoryPreference history_preference=new HistoryPreference();
    public int[] getPreferScore(BuggyContext buggy_context){
        return history_preference.getPreferenceScore(buggy_context);
    }
}
