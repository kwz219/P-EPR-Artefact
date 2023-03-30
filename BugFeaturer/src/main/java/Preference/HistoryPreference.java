package Preference;

import RepairSample.RepairSample;
import Util.BuggyContext;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import RepairSample.FixStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class HistoryPreference implements Preference{

    ArrayList<RepairSample> fixed_bugs = new ArrayList();
    ArrayList<RepairSample> unable_fixed_bugs = new ArrayList();

    public HashMap<String,int[]> type_preferences=new HashMap<>();

    public HashMap<String,int[]> test_err_preferences = new HashMap<>();

    @Override
    public boolean matches(CtElement ce) {
        return false;
    }

    @Override
    public String getPreference_name() {
        return null;
    }

    public List<CtElement> getStrings(CtElement ce){
        List<CtElement> Strings = ce.filterChildren(new AbstractFilter<CtElement>() {
            @Override
            public boolean matches(CtElement element) {
                if(element.getPosition().isValidPosition()){
                    return true;
                }
                return false;
            }
        }).list();
        return Strings;
    }

    public double getHistoryFixesScore(){
        return fixed_bugs.size()/(fixed_bugs.size()+Math.max(unable_fixed_bugs.size(),1));
    }

    /**
     * update the repair history
     * @param bugname
     * @param ce
     * @param fs
     * @param test_error_type
     */
    public void recordFix(String bugname,CtElement ce, FixStatus fs, String test_error_type){
        String type_string=ce.getClass().toString();
        int[] counts;
        int[] test_err_counts;
        // load old history
        if(this.type_preferences.containsKey(type_string)){
            counts = this.type_preferences.get(type_string);
        }else{
            counts=new int[]{1,1};
        }
        if(this.test_err_preferences.containsKey(type_string)){
            test_err_counts = this.test_err_preferences.get(type_string);
        }else{
            test_err_counts=new int[]{1,1};
        }

        //update history
        if(fs.equals(FixStatus.Correct)){
            fixed_bugs.add(new RepairSample(bugname,ce,test_error_type));
            counts[0]+=1;
            test_err_counts[0]= test_err_counts[0]+1;

        }else if(fs.equals(FixStatus.Overfit)||fs.equals(FixStatus.Failed)){
            unable_fixed_bugs.add(new RepairSample(bugname,ce,test_error_type));
            counts[1]+=1;
            test_err_counts[1]= test_err_counts[1]+1;
        }
        this.type_preferences.put(type_string,counts);
        // assertion error is too trival
        if(!test_error_type.equals("junit.framework.AssertionFailedError")){
            this.test_err_preferences.put(test_error_type,test_err_counts);
        }
    }

    public int[] getPreferenceScore(BuggyContext buggy_context){
        String type="";
        if(buggy_context.getBuggyElements().size()==0){
            type=buggy_context.getSpecialType();
        }else {
            type = buggy_context.getBuggyElements().get(0).getClass().toString();
        }
        if(this.type_preferences.containsKey(type)){
            return this.type_preferences.get(type);
        }
        return new int[]{1,1};

    }

    public int[] getTestPreferenceScore(String test_err_type){
        if(this.test_err_preferences.containsKey(test_err_type)){
            return this.test_err_preferences.get(test_err_type);
        }
        return new int[]{1,1};
    }
}
