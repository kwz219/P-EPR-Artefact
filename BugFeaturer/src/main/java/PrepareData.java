import Util.BuggyContext;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class PrepareData {
    public static void main(String[] args){
        String buggy_position_file = "E:\\APR\\FL-VS-APR-master\\FL-VS-APR-master\\data\\BugPositions.txt";
        String output_file_dir = "D:/文档/APR-Ensemble/Defects4JData";
        String buggy_file_dir = "E:/APR/FL-VS-APR-master/FL-VS-APR-master/data/BuggyFiles";
        ArrayList<String> failed_parse_bugs = new ArrayList();
        ArrayList<String> success_parse_bugs = new ArrayList();
        ArrayList<HashMap<String,Integer>> bugId_line = new ArrayList();
        String success_id_path = output_file_dir+"/success_ids.txt";
        String failed_id_path = output_file_dir+"/failed_ids.txt";
        try {
            Stream<String> lines = Files.lines(Paths.get(buggy_position_file));
            lines.forEachOrdered(ele ->{
                        String[] infos = ele.split("@");
                        String bug_id=infos[0];
                        String file_path = infos[1];
                        String buggy_lines=infos[2];
                        String[] file_infos = file_path.split("/");
                        String file_name= file_infos[file_infos.length-1].replace(".java","");

                        // get buggy line ids
                        ArrayList<Integer> buggy_line_ids=new ArrayList();
                        if(buggy_lines.contains(",")){
                            String[] ids= buggy_lines.split(",");
                            for(String id: ids){
                                if(id.contains("-")){
                                    String[] start_end=id.split("-");
                                    int start=Integer.valueOf(start_end[0]);
                                    int end = Integer.valueOf(start_end[1]);
                                    while(start<=end){
                                        buggy_line_ids.add(start);
                                        start+=1;
                                    }
                                }else{
                                    buggy_line_ids.add(Integer.valueOf(id));
                                }
                            }
                        }else{

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
                        }
                        assert buggy_line_ids.size()>0;
                        String buggy_class_path = buggy_file_dir+"/"+bug_id+"/"+file_path;
                        Launcher launcher = new Launcher();
                        launcher.addInputResource(buggy_class_path);
                        CtModel model = launcher.buildModel();
                        //System.out.println(model.getClass())
                        List<CtClass> Classes = model.getElements(new TypeFilter<>(CtClass.class));
                        assert Classes.size() == 1;
                        CtClass buggy_class = Classes.get(0);

                        // parse buggy line and find the buggy method
                        for(int buggy_line_id: buggy_line_ids){

                            String signature = bug_id+"_"+file_name+"_"+String.valueOf(buggy_line_id);
                            System.out.println(signature);


                            String target_line_path = output_file_dir+"/buggy_lines/"+bug_id+"_"+file_name+"_"+String.valueOf(buggy_line_id)+".txt";
                            String target_method_path = output_file_dir+"/buggy_methods/"+bug_id+"_"+file_name+"_"+String.valueOf(buggy_line_id)+".txt";
                            String err_line_number_path = output_file_dir+"/abs_metas/"+bug_id+"_"+file_name+"_"+String.valueOf(buggy_line_id)+".txt";

                            BuggyContext buggy_context = null;
                            /**
                            try {
                                buggy_context = main2.parseJavaClass(buggy_class_path,buggy_line_id);
                                System.out.println(buggy_context.getBuggyline());
                                writeFile(buggy_context.getBuggyline(),target_line_path);

                                List<CtMethod> buggy_methods = buggy_class.getElements(new TypeFilter<>(CtMethod.class));
                                CtMethod buggy_method = null;
                                for(CtMethod method: buggy_methods){
                                    if(method.getPosition().getLine()<=buggy_line_id && method.getPosition().getEndLine()>=buggy_line_id){
                                        buggy_method = method;
                                        break;
                                    }
                                }
                                if(buggy_method == null){
                                    List<CtConstructor> constructors = buggy_class.getElements(new TypeFilter<>(CtConstructor.class));
                                    CtConstructor buggy_constructor=null;
                                    for(CtConstructor cons: constructors){
                                        if(cons.getPosition().isValidPosition()) {
                                            if (cons.getPosition().getLine() <= buggy_line_id && cons.getPosition().getEndLine() >= buggy_line_id) {
                                                buggy_constructor = cons;
                                                break;
                                            }
                                        }
                                    }
                                    if(buggy_constructor==null){
                                        failed_parse_bugs.add(signature);
                                    }else{
                                        String source_code = buggy_constructor.getOriginalSourceFragment().getSourceCode();
                                        writeFile(source_code, target_method_path);
                                        int relative_pos = buggy_line_id - buggy_constructor.getPosition().getLine();
                                        String buggy_position = String.valueOf(relative_pos) + ":" + String.valueOf(relative_pos + 1);
                                        writeFile(buggy_position, err_line_number_path);
                                    }
                                }else{
                                    //write buggy_method
                                    String source_code = buggy_method.getOriginalSourceFragment().getSourceCode();
                                    writeFile(source_code, target_method_path);
                                    success_parse_bugs.add(signature);
                                    int relative_pos = buggy_line_id - buggy_method.getPosition().getLine();
                                    String buggy_position = String.valueOf(relative_pos) + ":" + String.valueOf(relative_pos + 1);
                                    writeFile(buggy_position, err_line_number_path);
                                }
                            } catch (IOException e) {
                                failed_parse_bugs.add(signature);
                            }**/

                            /**
                            try {
                                BuggyContext buggy_context = main.parseJavaClass(buggy_class_path,buggy_line_id);
                                System.out.println(buggy_context.getBuggyline());
                                writeFile(buggy_context.getBuggyline(),target_line_path);

                                if(buggy_context.getBuggyElements().size()>0){
                                    CtElement buggy_element = buggy_context.getBuggyElements().get(0);
                                    CtMethod buggy_method = buggy_element.getParent(new TypeFilter<>(CtMethod.class));
                                    if(buggy_method==null){
                                        CtConstructor buggy_constructor = buggy_element.getParent(new TypeFilter<>(CtConstructor.class));
                                        if(buggy_constructor==null) {
                                            failed_parse_bugs.add(signature);
                                            writeFile("<EMP>", target_method_path);
                                        }else{
                                            String source_code = buggy_constructor.getOriginalSourceFragment().getSourceCode();
                                            writeFile(source_code, target_method_path);
                                            int relative_pos = buggy_element.getPosition().getLine() - buggy_constructor.getPosition().getLine();
                                            String buggy_position = String.valueOf(relative_pos) + ":" + String.valueOf(relative_pos + 1);
                                            writeFile(buggy_position, err_line_number_path);

                                            success_parse_bugs.add(bug_id + "_" + file_name + "_" + String.valueOf(buggy_line_id));
                                        }
                                    }else {
                                        String source_code = buggy_method.getOriginalSourceFragment().getSourceCode();
                                        writeFile(source_code, target_method_path);


                                        int relative_pos = buggy_element.getPosition().getLine() - buggy_method.getPosition().getLine();
                                        String buggy_position = String.valueOf(relative_pos) + ":" + String.valueOf(relative_pos + 1);
                                        writeFile(buggy_position, err_line_number_path);

                                        success_parse_bugs.add(bug_id + "_" + file_name + "_" + String.valueOf(buggy_line_id));
                                    }

                                }else{
                                    failed_parse_bugs.add(bug_id+"_"+file_name+"_"+String.valueOf(buggy_line_id));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                             **/
                        }
                    }
                    );
            writeListToFile(success_parse_bugs,success_id_path);
            writeListToFile(failed_parse_bugs,failed_id_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String file_content, String output_path){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(output_path));
            out.write(file_content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeListToFile(ArrayList<String> list,String output_path){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(output_path));
            for(String content:list){
                out.write(content+'\n');
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
