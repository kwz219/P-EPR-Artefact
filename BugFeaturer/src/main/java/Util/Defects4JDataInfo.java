package Util;


import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Defects4JDataInfo {
    public ArrayList<String> multi_file_bug_ids = new ArrayList();
    public JSONObject infos;

    public Defects4JDataInfo() {
        this.multi_file_bug_ids.add("Chart_14");
        this.multi_file_bug_ids.add("Chart_18");
        this.multi_file_bug_ids.add("Closure_30");
        this.multi_file_bug_ids.add("Closure_34");
        this.multi_file_bug_ids.add("Closure_37");
        this.multi_file_bug_ids.add("Closure_47");
        this.multi_file_bug_ids.add("Closure_54");
        this.multi_file_bug_ids.add("Closure_72");
        this.multi_file_bug_ids.add("Closure_79");
        this.multi_file_bug_ids.add("Closure_89");
        this.multi_file_bug_ids.add("Closure_90");
        this.multi_file_bug_ids.add("Closure_103");
        this.multi_file_bug_ids.add("Closure_106");
        this.multi_file_bug_ids.add("Closure_110");
        this.multi_file_bug_ids.add("Math_1");
        this.multi_file_bug_ids.add("Math_4");
        this.multi_file_bug_ids.add("Math_6");
        this.multi_file_bug_ids.add("Math_14");
        this.multi_file_bug_ids.add("Math_22");
        this.multi_file_bug_ids.add("Math_71");
        this.multi_file_bug_ids.add("Math_77");
        this.multi_file_bug_ids.add("Math_98");
        this.multi_file_bug_ids.add("Mockito_14");
        this.multi_file_bug_ids.add("Mockito_16");
        this.multi_file_bug_ids.add("Mockito_17");
        this.multi_file_bug_ids.add("Mockito_30");
        this.multi_file_bug_ids.add("Time_1");
        this.multi_file_bug_ids.add("Time_2");
        this.multi_file_bug_ids.add("Time_12");


    }

    /**
     * loads the basic information of Defects4j (including buggy line ids, failed test results....)
     *
     * @param info_json_f
     */
    public void init_information(String info_json_f) {
        this.infos = IO.readJsonFile(info_json_f);

    }

}
