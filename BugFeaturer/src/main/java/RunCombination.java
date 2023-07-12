import APRTool.APRTool;
import Util.Defects4JDataInfo;

import java.util.ArrayList;

public class RunCombination {
    public static void main(String[] args) {


        Defects4JDataInfo d4j_info = new Defects4JDataInfo();
        d4j_info.init_information("D:/文档/APR-Ensemble/Defects4JData/Defects4J_blocks_info.json");
        double[] ex_alpha = new double[]{0.5};
        String output_dir = "E:\\P-EPR-Test\\Math_predict_all_0.3_a";
        String all_log_file = output_dir + "/EPRP_selectmode.log";
        ArrayList<APRTool> tools = new ArrayList();

        //tools.add(new APRTool("Recoder", new String[]{"None"}));
        //tools.add(new APRTool("TBar", new String[]{"Cast", "Operator", "Super", "Array", "Invocation", "Literal", "DataType", "Return"}));

        //tools.add(new APRTool("RewardRepair", new String[]{"None"}));
        //tools.add(new APRTool("TransplantFix", new String[]{"None"}));
        //tools.add(new APRTool("FixMiner", new String[]{"DataType", "Operator"}));
        //tools.add(new APRTool("kPAR", new String[]{"Cast", "Array", "DataType", "Operator", "Invocation"}));
        //tools.add(new APRTool("AVATAR", new String[]{"Cast", "Array", "Super", "Operator", "DataType"}));
        //tools.add(new APRTool("SimFix", new String[]{"Operator", "DataType", "Literal", "Invocation"}));

        //tools.add(new APRTool("CodeBERT", new String[]{"None"}));
        //tools.add(new APRTool("SequenceR", new String[]{"None"}));
        //tools.add(new APRTool("ACS", new String[]{"Operator"}));

        //tools.add(new APRTool("ARJA", new String[]{"None"}));
        //tools.add(new APRTool("RSRepair-A", new String[]{"None"}));
        //tools.add(new APRTool("GenProg-A", new String[]{"None"}));
        //tools.add(new APRTool("jGenProg", new String[]{"None"}));
        //tools.add(new APRTool("Kali-A", new String[]{"None"}));
        //tools.add(new APRTool("jMutRepair", new String[]{"Operator"}));
        //tools.add(new APRTool("DynaMoth", new String[]{"Operator"}));
        //tools.add(new APRTool("Nopol", new String[]{"Operator"}));
        //tools.add(new APRTool("jMutRepair", new String[]{"None"}));
        //tools.add(new APRTool("DynaMoth", new String[]{"None"}));
        //tools.add(new APRTool("Nopol", new String[]{"None"}));
        //tools.add(new APRTool("Cardumen ", new String[]{"None"}));
        //tools.add(new APRTool("jKali", new String[]{"None"}));
        /**
         tools.add(new APRTool("TransplantFix",new String[]{"None"}));
         tools.add(new APRTool("Recoder",new String[]{"None"}));
         tools.add(new APRTool("CodeBERT",new String[]{"None"}));
         tools.add(new APRTool("SequenceR",new String[]{"None"}));
         tools.add(new APRTool("RewardRepair",new String[]{"None"}));
         tools.add(new APRTool("ARJA",new String[]{"None"}));
         tools.add(new APRTool("GenProg-A",new String[]{"None"}));
         tools.add(new APRTool("jGenProg",new String[]{"None"}));
         tools.add(new APRTool("jKali",new String[]{"None"}));
         tools.add(new APRTool("DynaMoth",new String[]{"Operator"}));
         tools.add(new APRTool("Cardumem",new String[]{"None"}));
         tools.add(new APRTool("Kali-A",new String[]{"None"}));
         tools.add(new APRTool("Nopol",new String[]{"Operator"}));
         tools.add(new APRTool("RSRepair-A",new String[]{"None"}));
         tools.add(new APRTool("ACS", new String[]{"Operator"}));
         tools.add(new APRTool("AVATAR",new String[]{"Cast","Array","Super","Operator","DataType"}));
         tools.add(new APRTool("FixMiner",new String[]{"DataType","Operator"}));
         tools.add(new APRTool("jMutRepair",new String[]{"Operator"}));
         tools.add(new APRTool("kPAR",new String[]{"Cast","Array","DataType","Operator","Invocation"}));
         tools.add(new APRTool("SimFix",new String[]{"Operator","DataType","Literal","Invocation"}));
         tools.add(new APRTool("TBar",new String[]{"Cast","Operator","Super","Array","Invocation","Literal","DataType","Return"}));
         **/
        for (int ind = 21; ind < 22; ind++) {
            //ArrayList<APRTool> selected_tools = new ArrayList<APRTool>();
            //for (int i = 0; i < ind; i++) {
                //selected_tools.add(tools.get(tools.size() - 1 - i));
            //}

            for (int k = 1; k <= ind; k++) {
                /**
                 String[] projects=new String[]{"Chart","Closure","Lang","Math","Mockito","Time"};
                 ArrayList<Integer[]> combs = new ArrayList<>();
                 combs.add(new Integer[]{0,1,2});
                 combs.add(new Integer[]{0,1,3});
                 combs.add(new Integer[]{0,1,4});
                 combs.add(new Integer[]{0,1,5});
                 combs.add(new Integer[]{0,2,3});
                 combs.add(new Integer[]{0,2,4});
                 combs.add(new Integer[]{0,2,5});
                 combs.add(new Integer[]{0,3,4});
                 combs.add(new Integer[]{0,3,5});
                 combs.add(new Integer[]{0,4,5});
                 combs.add(new Integer[]{1,2,3});
                 combs.add(new Integer[]{1,2,4});
                 combs.add(new Integer[]{1,2,5});
                 combs.add(new Integer[]{1,3,4});
                 combs.add(new Integer[]{1,3,5});
                 combs.add(new Integer[]{1,4,5});
                 combs.add(new Integer[]{2,3,4});
                 combs.add(new Integer[]{2,3,5});
                 combs.add(new Integer[]{2,4,5});
                 combs.add(new Integer[]{3,4,5});
                 combs.add(new Integer[]{0,1,3});

                 for(Integer[] comb: combs){
                 EPRP_Launcher launcher =new EPRP_Launcher();
                 for (APRTool tool:selected_tools){
                 tool.emptyHistory();
                 }
                 launcher.loadExecutor("D:/文档/APR-Ensemble/APR_fixes_all.json");
                 launcher.configTools(selected_tools);
                 launcher.fixDefects4J(d4j_info.infos, "E:/APR/FL-VS-APR-master/FL-VS-APR-master/data/BuggyFiles", "select",
                 output_dir + "/" +comb[0]+"_"+comb[1]+"_"+comb[2]+"_"+ String.valueOf(selected_tools.size()) + "_" + String.valueOf(0.5)+ "_" + String.valueOf(k) + "_selectmode.json",
                 1, 0.5, all_log_file,k,new String[]{projects[comb[0]],projects[comb[1]],projects[comb[2]]});

                 }
                 **/

                for (int rseed = 1; rseed < 2; rseed++) {
                    EPRP_Launcher launcher = new EPRP_Launcher();
                    launcher.loadExecutor("D:/文档/APR-Ensemble/APR_fixes_all.json");
                    launcher.configToolsFromFiles("E:\\P-EPR-Test\\Math_predict");
                    //launcher.configTools("");
                    launcher.fixDefects4J(d4j_info.infos, "E:/APR/FL-VS-APR-master/FL-VS-APR-master/data/BuggyFiles", "select",
                            output_dir + "/" + "21" + "_" + String.valueOf(21) + "_" + String.valueOf(0.5) + "_" + String.valueOf(k) + "_selectmode.json",
                            rseed, 0.3, all_log_file, k, new String[]{"Math"});
                }


            }
        }


    }
}
