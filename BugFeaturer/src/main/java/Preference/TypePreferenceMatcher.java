package Preference;

import Preference.TypePreference;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;

public class TypePreferenceMatcher {
    ArrayList<Preference> type_preferences=new ArrayList();
    public TypePreferenceMatcher(String[] keywords){
        for(String keyword:keywords){
            if(keyword.equals("Division")){
                this.type_preferences.add(new OperatorTypePreference(keyword));
            }else {
                this.type_preferences.add(new TypePreference(keyword));
            }
        }

    }
    public ArrayList<String> matchTypes(CtElement ce){
        ArrayList<String> matched_types=new ArrayList();
        for(Preference tp:this.type_preferences){
            if(tp.matches(ce)){
                matched_types.add(tp.getPreference_name());
            }
        }
        return matched_types;
    }
}
