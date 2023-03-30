package Preference;

import spoon.reflect.declaration.CtElement;

public interface Preference {
    String preference_name = "";
    boolean matches(CtElement e);

    public String getPreference_name();
}
