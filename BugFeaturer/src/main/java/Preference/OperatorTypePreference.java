package Preference;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

import java.util.List;

public class OperatorTypePreference implements Preference{
    String preference_name="";
    BinaryOperatorKind operator_kind;

    public OperatorTypePreference(String operator_name){
        this.preference_name=operator_name;
        if (operator_name.equals("Division")){
            this.operator_kind=BinaryOperatorKind.DIV;
        }
    }
    @Override
    public boolean matches(CtElement e) {
        List<CtBinaryOperatorImpl> operators=e.getElements(new TypeFilter(CtBinaryOperatorImpl.class));
        for(CtBinaryOperatorImpl operator:operators){
            if(operator.getKind().equals(this.operator_kind)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPreference_name() {
        return this.preference_name;
    }
}
