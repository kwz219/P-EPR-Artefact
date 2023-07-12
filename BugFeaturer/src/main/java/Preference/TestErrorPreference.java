package Preference;

import spoon.reflect.declaration.CtElement;

public class TestErrorPreference implements Preference {
    String test_error_type = "";

    @Override
    public boolean matches(CtElement e) {
        return false;
    }

    public boolean matches(String test_error_type) {
        if (test_error_type.equals(this.test_error_type)) {
            return true;
        }
        return false;
    }

    @Override
    public String getPreference_name() {
        return this.test_error_type;
    }
}
