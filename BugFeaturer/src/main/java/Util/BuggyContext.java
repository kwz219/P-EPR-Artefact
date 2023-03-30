package Util;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.ArrayList;
import java.util.Set;

public class BuggyContext {
    ArrayList<CtElement> buggy_elements=new ArrayList<>();
    String buggy_line;
    String speical_type="";
    public String getBuggyline() {
        return buggy_line;
    }

    public void setBuggyline(String buggy_line) {
        this.buggy_line = buggy_line;
    }

    public void setSpeical_type(String type){
        this.speical_type=type;
    }

    public String getSpecialType(){
        return this.speical_type;
    }

    public boolean hasSpecialType(){
        if(this.speical_type==""){
            return false;
        }
        return true;
    }
    String getSpeical_type(){
        return this.speical_type;
    }

    public void add_element(CtElement buggy_element){
        this.buggy_elements.add(buggy_element);
    }

    public ArrayList<CtElement> getBuggyElements(){
        return this.buggy_elements;
    }
    public void printInfo(){
        System.out.println(this.buggy_line);
        int id=1;
        if(this.buggy_elements.size()>0) {
            for (CtElement ce : buggy_elements) {
                System.out.println("buggy element " + String.valueOf(id) + " " + ce.toString());
                System.out.println("buggy element type "+String.valueOf(id)+" "+ce.getClass());

                if(ce.getClass().equals(CtBinaryOperatorImpl.class)){

                    System.out.println("Operator type: "+((CtBinaryOperatorImpl<?>) ce).getKind().toString());
                }
                /**
                Set<CtTypeReference<?>> types=ce.getReferencedTypes();
                for(CtTypeReference type: types){
                    System.out.println(type.getSimpleName());
                }**/

                System.out.println("buggy parent " + String.valueOf(id) + " " + ce.getParent().toString());
                System.out.println("buggy parent type "+String.valueOf(id)+" "+ce.getClass().toString());
                id = id + 1;
            }
        }
    }
}
