package Util;

import RepairSample.FixStatus;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.FileUtils;
import com.alibaba.fastjson.JSON;

import java.io.*;

public class IO {

    public static JSONObject readJsonFile(String file_path) {
        JSONObject jsonObj = null;
        try {
            File file = new File(file_path);
            String content = FileUtils.readFileToString(file, "UTF-8");
            jsonObj = JSON.parseObject(content);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObj;

    }

    public static void writeJsonFile(JSONObject jsonObj, String file_path) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file_path));
            bw.write(jsonObj.toString(SerializerFeature.PrettyFormat));//转化成字符串再写
            bw.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static FixStatus getFixStatusByString(String name){
        if(name.equals("Correct")||name.equals("correct")){
            return FixStatus.Correct;
        }else if(name.equals("Overfit")){
            return FixStatus.Overfit;
        }else if (name.equals("Failed")){
            return FixStatus.Failed;
        }else{
            return FixStatus.UnExecuted;
        }
    }
}
