import Util.Defects4JDataInfo;
import Util.IO;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.cli.*;

import java.util.LinkedHashMap;

public class CLI_Command {

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("mode", true, "mode: train or inference");
        options.addOption("hd_path", true, "history data path for training P-EPR");
        options.addOption("input_file", true, "the path of the input file ");
        options.addOption("save_dir", true, "the path to save trained or updated tool configuration of P-EPR");
        options.addOption("log_dir", true, "directory to save execution logs of P-EPR");
        options.addOption("repair_history_info",true, "a file that stores the meta information of repair history");
        options.addOption("tool_configuration", true, "configured tools in E-APR");
        options.addOption("tool_config_dir", true, "the directory that stores the configuration of tools");
        options.addOption("test_err_type", true, "exception type");
        options.addOption("faulty_line_ids", true, "faulty line ids of the buggy file. eg1: 19,21 eg2: 10-14");
        options.addOption("result_file", true, "the scoring result of the input bug");

        CommandLineParser parser = new DefaultParser();
        CommandLine commandline = parser.parse(options, args);

        if (commandline.hasOption("mode")) {
            String mode = commandline.getOptionValue("mode");
            System.out.println(mode);
            if (mode.equals("initialize")) {
                String history_data = commandline.getOptionValue("hd_path");
                System.out.println(history_data);

                //String configuration_file = commandline.getOptionValue("tool_configuration");
                String save_dir = commandline.getOptionValue("save_dir");
                String config_dir = commandline.getOptionValue("tool_config_dir");
                String repair_history_data = commandline.getOptionValue("repair_history_info");
                //System.out.println(save_dir);
                String log_dir = commandline.getOptionValue("log_dir");
                EPRP_Launcher launcher = new EPRP_Launcher();

                //launcher.configTools(new String(""));
                //launcher.configToolsFromFile(configuration_file);
                launcher.configToolsFromFiles(config_dir);
                //launcher.loadExecutor(history_data);
                //Defects4JDataInfo d4j_info = new Defects4JDataInfo();

                JSONObject init_data_info = IO.readJsonFile(repair_history_data);

                for (String tool:init_data_info.keySet()){
                    System.out.print("Starting to initialize "+tool);
                    JSONArray list = (JSONArray) init_data_info.get(tool);
                    for(int i = 0; i<list.size();i++){
                        JSONObject infos = list.getJSONObject(i);
                        String file_path = infos.getString("file_path");
                        String fault_location = infos.getString("fault_location");
                        String repair_result = infos.getString("repair_result");
                        String test_error_type = infos.getString("test_error_type");
                        try {
                            launcher.initializeRepairHistory(tool, file_path, fault_location, repair_result, test_error_type);
                        }catch (Exception e){
                            e.printStackTrace();
                            continue;
                        }

                    }
                }

                //"Chart","Closure","Lang","Mockito","Time","Math"
                //d4j_info.init_information("D:/文档/APR-Ensemble/Defects4JData/Defects4J_blocks_info.json");
                //launcher.fixDefects4J(d4j_info.infos, "E:/APR/FL-VS-APR-master/FL-VS-APR-master/data/BuggyFiles", "select",
                        //log_dir + "/" + String.valueOf(21) + "_" + String.valueOf(0.5) + "_" + String.valueOf(21) + "_selectmode.json",
                        //10, 0.5, log_dir + "/result.log", 21, new String[]{"Chart","Closure","Lang","Mockito","Time",});
                launcher.saveToolConfigs(save_dir);

            } else if (mode.equals("inference")) {
                String config_dir = commandline.getOptionValue("tool_config_dir");
                EPRP_Launcher launcher = new EPRP_Launcher();
                launcher.configToolsFromFiles(config_dir);
                String input_file_path = commandline.getOptionValue("input_file");
                String log_dir = commandline.getOptionValue("log_dir");
                String buggy_line_numbers = commandline.getOptionValue("faulty_line_ids");
                String test_err_type = commandline.getOptionValue("test_err_type");
                String result_path = commandline.getOptionValue("result_file");

                LinkedHashMap<String, Double> tool_scores = launcher.scoreOneBug(input_file_path, buggy_line_numbers, test_err_type, 0.5);
                JSONObject tool_score_json = new JSONObject();
                for (String tool : tool_scores.keySet()) {
                    tool_score_json.put(tool, tool_scores.get(tool));
                }
                IO.writeJsonFile(tool_score_json, result_path);
            }
        }

    }
}
