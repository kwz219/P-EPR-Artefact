import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.HashMap;

public class BuggyCode {
    CtMethod buggy_method;
    CtConstructor buggy_constructor;
    ArrayList<CtElement> buggy_elements = new ArrayList<>();
    CtElement buggy_parent;

    public CtConstructor getBuggy_constructor() {
        return buggy_constructor;
    }

    public void setBuggy_constructor(CtConstructor buggy_constructor) {
        this.buggy_constructor = buggy_constructor;
    }

    public ArrayList<CtElement> getBuggyElements() {
        return buggy_elements;
    }

    public CtElement getBuggyParent() {
        return buggy_parent;
    }

    public void setBuggyParent(CtElement buggy_parent) {
        this.buggy_parent = buggy_parent;
    }

    public void addBuggyElement(CtElement buggy_element){
        this.buggy_elements.add(buggy_element);
    }
    public void setBuggyMethod(CtMethod buggy_method) {
        this.buggy_method = buggy_method;
    }

    public CtMethod getBuggyMethod() {
        return this.buggy_method;
    }

    public void printInfo(){
        if(this.buggy_method==null){
            System.out.println("buggy_constructor: " + this.buggy_constructor.getSignature());
        }else {
            System.out.println("buggy_method: " + this.buggy_method.getSimpleName());
        }
        if(!this.buggy_parent.equals(null)){
            System.out.println("buggy_parent: "+this.buggy_parent.toString());
        }
        for(CtElement ele:this.buggy_elements){
            System.out.println("buggy_element: "+ele.toString());
        }
    }

}
