package Preference;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtLocalVariableImpl;

import java.util.Set;

public class ClassReferTypePreference implements Preference {
    CtElement check_type;
    CtElement child_check_type;
    String preference_name;

    public ClassReferTypePreference(String keyword) {
        this.preference_name = keyword;
        if (keyword.contentEquals("NumberType")) {
            this.check_type = new CtLocalVariableImpl<>();
            this.preference_name = keyword;
        } else if (keyword.contentEquals("DivisionOperator")) {

        }
    }

    @Override
    public boolean matches(CtElement ce) {
        if (this.preference_name.equals("NumberType")) {
            if (ce.getClass().equals(this.check_type.getClass())) {
                Set<CtTypeReference<?>> types = ce.getReferencedTypes();
                for (CtTypeReference type : types) {
                    if (type.getSimpleName().equals("int") || type.getSimpleName().equals("float") ||
                            type.getSimpleName().equals("double") || type.getSimpleName().equals("short") ||
                            type.getSimpleName().equals("byte") || type.getSimpleName().equals("char")) {
                        return true;
                    }
                }
            }
        } else if (this.preference_name.equals("")) {
            return false;
        }
        return false;
    }

    @Override
    public String getPreference_name() {
        return null;
    }
}
