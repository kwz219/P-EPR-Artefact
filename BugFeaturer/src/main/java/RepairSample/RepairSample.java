package RepairSample;

import spoon.reflect.declaration.CtElement;

public class RepairSample {
    CtElement buggy_element;
    String test_error_type;
    String bugname;

    public RepairSample(String bugname, CtElement ce, String test_err) {
        this.buggy_element = ce;
        this.bugname = bugname;
        this.test_error_type = test_err;

    }

}
