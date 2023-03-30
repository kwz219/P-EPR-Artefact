import APRTool.APRTool;
import APRTool.ToolExecutor;
import RepairSample.FixStatus;
import RepairSample.RepairStatus;
import RepairSample.ResultRecorder;
import Util.BonusFunction;
import Util.BuggyContext;
import Util.ScoreNormalizer;
import Util.ToolSelection;
import org.json.JSONArray;
import org.json.JSONObject;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class EPRP_Launcher {
    ToolExecutor executor=new ToolExecutor();
    ArrayList<APRTool> availble_tools = new ArrayList<APRTool>();

    public void loadExecutor(String results_path){
        this.executor.init_results(results_path);
    }

    /**
     * add available APR tools
     * @param config_pathes
     * @return
     */
    public void  configTools(String[] config_pathes){
        ArrayList<APRTool> tools=new ArrayList();
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
        tools.add(new APRTool("Cardumen",new String[]{"None"}));
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

        this.availble_tools=tools;

    }

    public void configTools(ArrayList<APRTool> all_tools){
        this.availble_tools=all_tools;

    }
    /**
     * parse the buggy statement with given faulty line id
     * @param file_path :
     * @param suspicious_line
     * @return the buggy statement and the buggy context
     * @throws IOException
     */
    public static BuggyContext parseJavaClass(String file_path, int suspicious_line) throws IOException{

        List<String> lines = Files.readAllLines(Paths.get(file_path),
                StandardCharsets.UTF_8);
        String str_buggy_line=lines.get(suspicious_line-1);

        Launcher launcher = new Launcher();
        launcher.addInputResource(file_path);
        CtModel model = launcher.buildModel();
        //System.out.println(model.getClass())
        List<CtClass> Classes = model.getElements(new TypeFilter<>(CtClass.class));
        assert Classes.size() == 1;
        CtClass buggy_class = Classes.get(0);
        List<CtElement> buggy_elements=buggy_class.filterChildren(ctElement ->
                (ctElement.getPosition().isValidPosition()&&ctElement.getPosition().getLine()==suspicious_line&&ctElement.getPosition().getEndLine()==suspicious_line)).list();
        BuggyContext buggy_context=new BuggyContext();
        buggy_context.setBuggyline(str_buggy_line);
        for (CtElement ce: buggy_elements){
            if(ce.getClass().equals(CtTypeReferenceImpl.class)){
                buggy_context.setSpeical_type("ClassDeclaration");

            }else if(!ce.getClass().equals(CtBlock.class)){
                buggy_context.add_element(ce);
            }
        }
        //buggy_context.printInfo();
        /**
         HistoryPreference HP=new HistoryPreference();
         List<CtElement> stringList =HP.getStrings(buggy_context.getBuggyElements().get(0));
         for(CtElement str: stringList){
         System.out.print(str.toString()+"!!!");
         }
         System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$");
         **/
        return buggy_context;
    }

    /**
     * calculate scores of all tools (one line)
     *
     * @param tools
     * @param buggy_context
     * @param test_err_type
     * @param strategy
     * @return
     */
    public static HashMap<String,Double> ScoreOneLine(ArrayList<APRTool> tools,BuggyContext buggy_context,String test_err_type,String strategy,double ex_alpha) {

        ArrayList<String> explicit_matches_tools=new ArrayList();
        HashMap<String,Double> normalized_scores = new HashMap<String,Double>();
        boolean context_extractable = false;
        boolean test_trivial = true;
        HashMap<String, Double[]> type_history_score=new HashMap<>();
        HashMap<String, Double[]> test_history_score= new HashMap<>();
        HashMap<String, Double> tool_history_sum_score = new HashMap<>();



        //calculate the pure history fix score
        for (APRTool tool:tools){
            tool_history_sum_score.put(tool.getName(),tool.getHistoryFixesScore());
        }

        //calculate the test error type score
        if(!test_err_type.equals("junit.framework.AssertionFailedError")){
            for (APRTool tool : tools) {
                int[] test_prefer_score = tool.getTestPreferenceScore(test_err_type);

                test_history_score.put(tool.getName(),new Double[]{(double) test_prefer_score[0],(double) test_prefer_score[1]});
            }
            test_trivial=false;
        }

        //bug context could be extracted
        if(buggy_context.getBuggyElements().size()>0) {
            CtElement buggy_element = buggy_context.getBuggyElements().get(0);
            for (APRTool tool : tools) {
                boolean explicit_match = tool.type_matches(buggy_element);
                int[] prefer_score = tool.getPreferenceScore(buggy_context);
                if (explicit_match) {
                    explicit_matches_tools.add(tool.getName());
                }

                type_history_score.put(tool.getName(),new Double[]{(double)prefer_score[0],(double)prefer_score[1]});
            }
            context_extractable=true;
        }
        context_extractable=false;
        if(context_extractable){
            //selected_tools=ToolSelection.explicitFirst(tool_history_type_score,explicit_matches_tools);
            HashMap<String,Double> final_scores = new HashMap<>();
            //selected_tools=ToolSelection.selectByTopK(final_scores,6);
            //System.out.println("explicit first");
            if (test_trivial){
                normalized_scores = ScoreNormalizer.ScoreNorm(explicit_matches_tools,type_history_score,ex_alpha);

            }
            else{
                //normalized_scores = ScoreNormalizer.ScoreNorm(explicit_matches_tools,type_history_score,ex_alpha);
                normalized_scores = ScoreNormalizer.ScoreNorm(explicit_matches_tools,type_history_score,test_history_score,ex_alpha);

            }
        }else if (!test_trivial){

            for(String tool:test_history_score.keySet()){
                Double[] score = test_history_score.get(tool);
                normalized_scores.put(tool,score[0]/(score[0]+score[1]));
            }

            //normalized_scores=tool_history_sum_score;
            //System.out.println("test type "+test_err_type);
        }else{
            normalized_scores=tool_history_sum_score;
            //System.out.println("history score ");
        }




        return normalized_scores;
    }


    public static ArrayList<String> matchOneLine(ToolExecutor executor,ArrayList<APRTool> tools,BuggyContext buggy_context,String test_err_type,String strategy,double ex_alpha) {
        ArrayList<String> selected_tools=new ArrayList();
        ArrayList<String> explicit_matches_tools=new ArrayList();
        HashMap<String,Double> normalized_scores = new HashMap<String,Double>();
        boolean context_extractable = false;
        boolean test_trivial = true;
        HashMap<String, Double[]> type_history_score=new HashMap<>();
        HashMap<String, Double[]> test_history_score= new HashMap<>();
        HashMap<String, Double> tool_history_type_score = new HashMap();
        HashMap<String, Double> tool_history_testerror_score = new HashMap();
        HashMap<String, Double> tool_history_sum_score = new HashMap<>();


        if(strategy.equals("random")){
            for(APRTool tool: tools) {
                double p = Math.random();
                if(p>=0.5){
                    selected_tools.add(tool.getName());
                }
            }
            return selected_tools;
        }
        //calculate the pure history fix score
        for (APRTool tool:tools){
            tool_history_sum_score.put(tool.getName(),tool.getHistoryFixesScore());
        }

        //calculate the test error type score
        if(!test_err_type.equals("junit.framework.AssertionFailedError")){
            for (APRTool tool : tools) {
                int[] test_prefer_score = tool.getTestPreferenceScore(test_err_type);
                tool_history_testerror_score.put(tool.getName(), (double) test_prefer_score[0]);
                test_history_score.put(tool.getName(),new Double[]{(double) test_prefer_score[0],(double) test_prefer_score[1]});
            }
            test_trivial=false;
        }

        //bug context could be extracted
        if(buggy_context.getBuggyElements().size()>0) {
            CtElement buggy_element = buggy_context.getBuggyElements().get(0);
            for (APRTool tool : tools) {
                boolean explicit_match = tool.type_matches(buggy_element);
                int[] prefer_score = tool.getPreferenceScore(buggy_context);
                if (explicit_match) {
                    explicit_matches_tools.add(tool.getName());
                }
                tool_history_type_score.put(tool.getName(), (double) prefer_score[0]);
                type_history_score.put(tool.getName(),new Double[]{(double)prefer_score[0],(double)prefer_score[1]});
            }
            context_extractable=true;
        }

        if(context_extractable){
            //selected_tools=ToolSelection.explicitFirst(tool_history_type_score,explicit_matches_tools);
            HashMap<String,Double> final_scores = new HashMap<>();
            //selected_tools=ToolSelection.selectByTopK(final_scores,6);
            //System.out.println("explicit first");
            if (test_trivial){
                normalized_scores = ScoreNormalizer.ScoreNorm(explicit_matches_tools,type_history_score,ex_alpha);
                selected_tools=ToolSelection.selectByTopK(normalized_scores,7);
            }
            else{
                normalized_scores = ScoreNormalizer.ScoreNorm(explicit_matches_tools,type_history_score,test_history_score,ex_alpha);
                selected_tools=ToolSelection.selectByTopK(normalized_scores,7);
            }
        }else if (!test_trivial){
            selected_tools=ToolSelection.selectByTopK(tool_history_testerror_score,5);
            //System.out.println("test type "+test_err_type);
        }else{
            selected_tools=ToolSelection.selectByTopK(tool_history_sum_score,6);
            //System.out.println("history score ");
        }




        return selected_tools;
    }

    /**
     * calculate preference scores for all APR tools
     * @param tools
     * @param file_path
     * @param buggy_lines
     * @param line_ele
     * @param test_err_type
     * @param ex_alpha
     * @return
     */
    public static LinkedHashMap<String,Double> ScoreAllTools(ArrayList<APRTool> tools, String file_path, ArrayList<Integer> buggy_lines, HashMap<Integer, CtElement> line_ele, String test_err_type,double ex_alpha){

        HashMap<String,Double> final_preference_scores = new HashMap<>();
        LinkedHashMap<String,Double> ranked_final_scores = new LinkedHashMap<>();
        try{


            // judge each bug line
            for(int susp_line: buggy_lines){
                BuggyContext buggy_context = parseJavaClass(file_path,susp_line);
                if(buggy_context.getBuggyElements().size()>0) {
                    line_ele.put(susp_line, buggy_context.getBuggyElements().get(0));
                }
                HashMap<String,Double> preference_scores= ScoreOneLine(tools,buggy_context,test_err_type,"default",ex_alpha);
                for(String tool:preference_scores.keySet()){
                    if(final_preference_scores.containsKey(tool)){
                        final_preference_scores.put(tool,final_preference_scores.get(tool)+preference_scores.get(tool));
                    }else{
                        final_preference_scores.put(tool,preference_scores.get(tool));
                    }
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        ranked_final_scores=ToolSelection.sortMap(final_preference_scores);
        return ranked_final_scores;
    }
    public HashSet<String> MatchingTools(ArrayList<APRTool> tools, String file_path, ArrayList<Integer> buggy_lines, HashMap<Integer, CtElement> line_ele, String test_err_type,double ex_alpha,int top_k){
        ArrayList<String> explicit_matches_tools=new ArrayList();
        HashSet<String> selected_tools = new HashSet<>();
        HashMap<String,Double> final_preference_scores = new HashMap<>();
        try{
            HashMap<String,ArrayList<Integer>> matched_info= new HashMap<>();

            // judge each bug line
            for(int susp_line: buggy_lines){
                BuggyContext buggy_context = parseJavaClass(file_path,susp_line);
                CtElement buggy_element;
                if(buggy_context.getBuggyElements().size()>0) {
                    buggy_element = buggy_context.getBuggyElements().get(0);
                }else{
                    continue;
                }
                for(APRTool tool:tools){
                    boolean explicit_match = tool.type_matches(buggy_element);
                    int[] prefer_score = tool.getPreferenceScore(buggy_context);
                    if (explicit_match) {
                        explicit_matches_tools.add(tool.getName());
                    }
                }
                /**
                if(buggy_context.getBuggyElements().size()>0) {
                    line_ele.put(susp_line, buggy_context.getBuggyElements().get(0));
                }
                HashMap<String,Double> preference_scores= ScoreOneLine(tools,buggy_context,test_err_type,"default",ex_alpha);
                for(String tool:preference_scores.keySet()){
                    if(final_preference_scores.containsKey(tool)){
                        final_preference_scores.put(tool,final_preference_scores.get(tool)+preference_scores.get(tool));
                    }else{
                        final_preference_scores.put(tool,preference_scores.get(tool));
                    }
                }
                **/

            }
            for(String tool: explicit_matches_tools){
                selected_tools.add(tool);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("Final Selected Tools number "+String.valueOf(selected_tools.size()));
        return selected_tools;
    }

    /**
     * test EPRP on Defects4J v1.2
     * @param d4j_info
     */
    public void fixDefects4J(JSONObject d4j_info,String buggy_file_dir,String mode,String output_file,long random_seed,double ex_alpha,String log_f,int top_k, String[] train_projects){
        JSONObject EPRP_Log = new JSONObject();
        ResultRecorder recorder=new ResultRecorder();
        HashMap<String,Integer> mapTool = new HashMap<>();
        for (int i=0;i<this.availble_tools.size();i++){
            mapTool.put(availble_tools.get(i).getName(),i);
        }
        int all_bugs_count=0;

        List<String> bugSequence = new ArrayList<>();
        for (String bug_name: d4j_info.keySet()){
            bugSequence.add(bug_name);
        }
        Random rand_seed = new Random(random_seed);
        Collections.shuffle(bugSequence,rand_seed);
        ArrayList<String> recorder_list= new ArrayList<>();

        List<String> new_bug_sequence=new ArrayList<>();
        List<String> test_bug_sequence= new ArrayList<>();
        if (train_projects.length>0){
            for (String bug_name: bugSequence){
                boolean add = false;
                for (String proj: train_projects){
                    if (bug_name.contains(proj)){
                        add=true;
                    }
                }
                if(add){
                    new_bug_sequence.add(bug_name);
                }else{
                    test_bug_sequence.add(bug_name);
                }

            }
            new_bug_sequence.addAll(test_bug_sequence);
            bugSequence=new_bug_sequence;
        }
        for(String bug_name: bugSequence){
            all_bugs_count+=1;
            JSONObject bug_infos= (JSONObject) d4j_info.get(bug_name);
            JSONObject blocks = (JSONObject) bug_infos.get("blocks");
            String test_error_type = (String) bug_infos.get("first_test_error");

            // check each hunk
            HashMap<String, HashSet<String>> matched_infos = new HashMap<>();
            HashMap<String, HashMap<Integer,CtElement>> matched_elements = new HashMap<>();
            HashMap<String,Double> final_score_rank = new HashMap<>();
            for(String blk_id: blocks.keySet()){

                JSONObject blk_info = (JSONObject) blocks.get(blk_id);

                String buggy_class_path = (String) blk_info.get("buggy_class_path");
                buggy_class_path=buggy_file_dir+'/'+bug_name+'/'+buggy_class_path;

                // extract buggy lines
                String buggy_lines = (String) blk_info.get("absolute_position");
                ArrayList<Integer> buggy_line_ids=new ArrayList();
                if(buggy_lines.contains("-")) {
                    String[] start_end = buggy_lines.split("-");
                    int start = Integer.valueOf(start_end[0]);
                    int end = Integer.valueOf(start_end[1]);
                    while (start <= end) {
                        buggy_line_ids.add(start);
                        start += 1;
                    }
                }else{
                    buggy_line_ids.add(Integer.valueOf(buggy_lines));
                }

                //Selecting Tools
                HashMap<Integer,CtElement> context_cache = new HashMap<>();
                LinkedHashMap<String,Double> tool_scores = ScoreAllTools(this.availble_tools,buggy_class_path,buggy_line_ids,context_cache,test_error_type,ex_alpha);

                //score normalization
                for (String tool: tool_scores.keySet()){
                    tool_scores.put(tool,tool_scores.get(tool)/buggy_line_ids.size());
                }
                for(String tool:tool_scores.keySet()){
                    if(final_score_rank.containsKey(tool)){
                        final_score_rank.put(tool,final_score_rank.get(tool)+tool_scores.get(tool));
                    }else{
                        final_score_rank.put(tool,tool_scores.get(tool));
                    }
                }
                HashSet<String> selected_tools = MatchingTools(this.availble_tools,buggy_class_path,buggy_line_ids,context_cache,test_error_type,ex_alpha,top_k);
                matched_elements.put(blk_id,context_cache);
                matched_infos.put(blk_id,selected_tools);

            }

            //score normalization
            for(String tool:final_score_rank.keySet()){
                final_score_rank.put(tool,final_score_rank.get(tool)/blocks.keySet().size());
            }

            // for multi-block bugs, a tool should match every block
            HashSet<String> resSet =new HashSet<>();
            for (String blk_id: matched_infos.keySet()){
                if (resSet.size()==0){
                    resSet.addAll(matched_infos.get(blk_id));
                }else {
                    resSet.retainAll(matched_infos.get(blk_id));
                }
            }

            // executing the tool
            RepairStatus rs =new RepairStatus(bug_name);
            System.out.println(bug_name);
            System.out.println("------------------------------------------------");
            if(mode.equals("rank")){
                LinkedHashMap<String,Double> tool_ranks = ToolSelection.sortMap(final_score_rank);
                System.out.println(final_score_rank.size());
                System.out.println(tool_ranks.size());
                JSONArray tool_score_status = new JSONArray();
                for(String tool: resSet){
                    System.out.println(tool+" "+String.valueOf(tool_ranks.get(tool)));
                    FixStatus fs = executor.executor(tool, bug_name);
                    rs.record_fix(tool, fs);
                    recorder_list.add(bug_name+"<SEP>"+tool+"<SEP>"+String.valueOf(tool_ranks.get(tool))+"<SEP>"+fs.toString());
                    tool_score_status.put(tool+"<SEP>"+String.valueOf(tool_ranks.get(tool))+"<SEP>"+fs.toString());
                    /**
                    for (String blk_id : matched_elements.keySet()) {
                        HashMap<Integer, CtElement> matched_eles = matched_elements.get(blk_id);
                        for (int sus_line : matched_eles.keySet()) {
                            this.availble_tools.get(mapTool.get(tool)).recordFix(bug_name, matched_eles.get(sus_line), fs, test_error_type);
                        }
                    }
                     **/

                }
                EPRP_Log.put(bug_name,tool_score_status);
            }else if (mode == "random"){

                double prob = top_k/21.0;
                System.out.println(prob);
                for(APRTool tool:this.availble_tools){
                    double rand = Math.random();
                    String tool_name = tool.getName();
                    if(rand <= prob){
                        System.out.println(tool_name);
                        FixStatus fs = executor.executor(tool_name, bug_name);
                        recorder_list.add(bug_name+"<SEP>"+tool_name+"<SEP>"+fs.toString());
                        rs.record_fix(tool_name, fs);
                        for (int ind = 0; ind < this.availble_tools.size(); ind++) {
                            if (this.availble_tools.get(ind).getName().equals(tool_name)) {
                                for (String blk_id : matched_elements.keySet()) {
                                    HashMap<Integer, CtElement> matched_eles = matched_elements.get(blk_id);
                                    for (int sus_line : matched_eles.keySet()) {
                                        this.availble_tools.get(ind).recordFix(bug_name, matched_eles.get(sus_line), fs, test_error_type);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mode == "select"){
                LinkedHashMap<String,Double> tool_ranks = ToolSelection.sortMap(final_score_rank);
                Object[] t_score =tool_ranks.values().stream().sorted(Comparator.reverseOrder()).toArray();

                for (String tool_name : tool_ranks.keySet()) {
                    if(tool_ranks.get(tool_name)<(double)t_score[top_k-1]){
                        FixStatus fs = executor.executor(tool_name, bug_name);
                        recorder_list.add(bug_name+"<SEP>"+tool_name+"<SEP>"+String.valueOf(tool_ranks.get(tool_name))+"<SEP>"+"UnSelected_"+fs.toString());
                        continue;
                    }
                    System.out.println(tool_name+" "+String.valueOf(tool_ranks.get(tool_name)));
                    FixStatus fs = executor.executor(tool_name, bug_name);
                    recorder_list.add(bug_name+"<SEP>"+tool_name+"<SEP>"+String.valueOf(tool_ranks.get(tool_name))+"<SEP>"+fs.toString());
                    rs.record_fix(tool_name, fs);
                    for (int ind = 0; ind < this.availble_tools.size(); ind++) {
                        if (this.availble_tools.get(ind).getName().equals(tool_name)) {
                            for (String blk_id : matched_elements.keySet()) {
                                HashMap<Integer, CtElement> matched_eles = matched_elements.get(blk_id);
                                for (int sus_line : matched_eles.keySet()) {
                                    this.availble_tools.get(ind).recordFix(bug_name, matched_eles.get(sus_line), fs, test_error_type);
                                }
                            }
                        }
                    }
                }

            }
            recorder.addRepairResult(rs);
            System.out.println("===========================================");


        }
        if(true){
            File file=new File(output_file);
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                System.out.println("Writing JSON object to file");
                System.out.println("-----------------------");

                for (String result:recorder_list) {
                    fileWriter.write(result+'\n');

                }
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int[] result=recorder.CountCorrectOverfit_SmartEnsemble();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(log_f,true));
            out.write(String.valueOf(availble_tools.size())+" "+String.valueOf(random_seed)+" "+String.valueOf(ex_alpha)+" "+String.valueOf(top_k)+" "+String.valueOf(result[0])+" "+String.valueOf(result[1])
            + " "+String.valueOf(result[2])+" "+String.valueOf(result[3])+" "+String.valueOf(recorder.countInvocationTimes())+'\n');
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured"+ e);
        }

        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);
        System.out.println(result[3]);
        System.out.println(all_bugs_count);
        System.out.println(recorder.countInvocationTimes());

    }

}
