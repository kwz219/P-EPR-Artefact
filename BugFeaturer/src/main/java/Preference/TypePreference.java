package Preference;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.reflect.reference.CtWildcardReferenceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypePreference implements Preference{
    String preference_name;
    boolean element_check; // if false, only check childs
    boolean child_check; // if false, only check the input element
    CtElement check_type;
    ArrayList<CtElement> child_check_types=new ArrayList();
    ArrayList<String> str_child_check_types=new ArrayList();

    public TypePreference(String keyword){
        this.preference_name=keyword;
        if(keyword.equals("Operator")){
            this.element_check=true;
            this.child_check=false;
            this.check_type=new CtBinaryOperatorImpl();
        }else if(keyword.equals("Cast")){
            this.element_check=true;
            this.child_check=true;
            this.check_type=new CtLocalVariableImpl();
            this.child_check_types.add(new CtWildcardReferenceImpl());
        }else if(keyword.equals("Array")){
            this.element_check=false;
            this.child_check=true;
            this.child_check_types.add(new CtArrayWriteImpl());
            this.child_check_types.add(new CtArrayReadImpl());
        }else if(keyword.equals("Return")){
            this.element_check=true;
            this.child_check=false;
            this.check_type=new CtReturnImpl();
        }else if(keyword.equals("Literal")){
            this.element_check=false;
            this.child_check=true;
            this.child_check_types.add(new CtLiteralImpl());
        }else if(keyword.equals("DataType")){
            this.element_check=false;
            this.child_check=true;
            this.str_child_check_types.add("char");
            this.str_child_check_types.add("int");
            this.str_child_check_types.add("float");
            this.str_child_check_types.add("double");
            this.str_child_check_types.add("long");
            this.str_child_check_types.add("short");
            this.str_child_check_types.add("byte");
        }else if(keyword.equals("Invocation")){
            this.element_check=true;
            this.child_check=true;
            this.check_type=new CtInvocationImpl();
            this.child_check_types.add(new CtInvocationImpl());
        }else if(keyword.equals("Super")){
            this.element_check=false;
            this.child_check=true;
            this.child_check_types.add(new CtSuperAccessImpl());
        }else if(keyword.equals("None")){
            this.element_check=false;
            this.child_check=false;
        }
        else{
            System.err.println("Do not support preference type: "+keyword);
        }
    }
    @Override
    public boolean matches(CtElement ce) {
        assert element_check||child_check == true;
        boolean result=false;
        if(element_check==true){
            result=result||ce.getClass().equals(this.check_type.getClass());
            if(child_check==true&&result==true){
                boolean child_check_result=false;
                for(CtElement child_check_type:this.child_check_types){
                    List<CtElement> satis_childs=ce.getElements(new TypeFilter(child_check_type.getClass()));
                    child_check_result=child_check_result||satis_childs.size()>0;
                }
                if(this.str_child_check_types.size()>0){
                    Set<CtTypeReference<?>> types=ce.getReferencedTypes();
                    for(CtTypeReference type:types){
                        if(this.str_child_check_types.contains(type.toString())){
                            child_check_result=true;
                            break;
                        }
                    }
                }
                result=result&&child_check_result;
            }
        }else{
            boolean child_check_result=false;
            for(CtElement child_check_type:this.child_check_types){
                List<CtElement> satis_childs=ce.getElements(new TypeFilter(child_check_type.getClass()));
                child_check_result=child_check_result||satis_childs.size()>0;
            }
            if(this.str_child_check_types.size()>0){
                Set<CtTypeReference<?>> types=ce.getReferencedTypes();
                for(CtTypeReference type:types){
                    if(this.str_child_check_types.contains(type.toString())&&ce.toString().contains(type.toString())){
                        child_check_result=true;
                        break;
                    }
                }
            }
            result=child_check_result;
        }

        return result;
    }

    @Override
    public String getPreference_name(){
        return this.preference_name;
    }
}
