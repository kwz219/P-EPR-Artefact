import Util.Defects4JDataInfo;
public class main2 {
    public static void main(String[] args){
        EPRP_Launcher launcher =new EPRP_Launcher();


        launcher.loadExecutor("D:/文档/APR-Ensemble/APR_fixes_all.json");

        Defects4JDataInfo d4j_info = new Defects4JDataInfo();
        d4j_info.init_information("D:/文档/APR-Ensemble/Defects4JData/Defects4J_blocks_info.json");
        double[] ex_alpha = new double[]{0.1,0.3,0.5,0.7,0.9};
        String output_dir="D:/文档/APR-Ensemble/Logs/rand2";
        String all_log_file = output_dir+"/EPRP_selectmode.log";
        for(int i=3;i<9;i++){
            for(double alpha:ex_alpha){
                for (int k=10;k<21;k++) {

                    launcher.configTools(new String[]{""});
                    launcher.fixDefects4J(d4j_info.infos, "E:/APR/FL-VS-APR-master/FL-VS-APR-master/data/BuggyFiles", "select",
                            output_dir + "/" + String.valueOf(i) + "_" + String.valueOf(alpha)+ "_" + String.valueOf(k) + "_selectmode.json",
                            i, alpha, all_log_file,k,new String[]{});
                }
            }
        }

    }
}
