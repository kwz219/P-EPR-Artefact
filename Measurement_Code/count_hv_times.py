import codecs
import json
import os

def get_fix_status(bug,re_json,tool):
    if bug in re_json[tool]["correct"]:
        return "Correct"
    elif bug in re_json[tool]["overfit"]:
        return "Overfit"
    else:
        return "Fail"

def count_hv_times_4EAPR(result_json,truth_json_f):
    results = json.load(codecs.open(result_json,'r',encoding='utf8'))
    ground_truth = json.load(codecs.open(truth_json_f,'r',encoding='utf8'))
    bug_valid_times={}
    for bug in results.keys():
        tool_scores=results[bug]
        tool_score_list = []
        for tool in tool_scores.keys():
            tool_score_list.append((tool,tool_scores[tool]))
        ranked_tool_scores = sorted(tool_score_list,reverse=True,key=lambda x:x[1])
        if bug not in bug_valid_times.keys():
            bug_valid_times[bug]={"times":0,"continue":True}
        for tool_score in ranked_tool_scores:
            tool=tool_score[0]
            status = get_fix_status(bug,ground_truth,tool)
            if status == "Correct":
                if bug_valid_times[bug]["continue"]:
                    bug_valid_times[bug]["times"] = bug_valid_times[bug]["times"] + 1
                    bug_valid_times[bug]["continue"] = False
            elif status == "Overfit":
                if bug_valid_times[bug]["continue"]:
                    bug_valid_times[bug]["times"] = bug_valid_times[bug]["times"] + 1

#count_hv_times_4EAPR("D:\APR-Ensemble\eapr_results\Chart_Closure_Lang.json","D:\文档\APR-Ensemble\APR_fixes_all.json")

def count_hv_times(file_path):
    result_lines=[]
    with open(file_path,'r',encoding='utf8')as f:
        for line in f:
            result_lines.append(line.strip())
        f.close()
    bug_valid_times={}
    correct_bugs=set()
    for line in result_lines:
        infos = line.split("<SEP>")
        bug_name = infos[0]
        status = infos[-1]
        if bug_name not in bug_valid_times.keys():
            bug_valid_times[bug_name]={"times":0,"continue":True}
        if status == "Correct":
            correct_bugs.add(bug_name)
            if bug_valid_times[bug_name]["continue"]:
                bug_valid_times[bug_name]["times"]=bug_valid_times[bug_name]["times"]+1
                bug_valid_times[bug_name]["continue"]=False
        elif status == "Overfit":
            if bug_valid_times[bug_name]["continue"]:
                bug_valid_times[bug_name]["times"]=bug_valid_times[bug_name]["times"]+1
    valid_times_sum = 0
    for bug_name in bug_valid_times.keys():
        valid_times_sum+=bug_valid_times[bug_name]["times"]
    return correct_bugs,valid_times_sum

def count_hv_times_all(files_dir):
    files=os.listdir(files_dir)
    for file in files:
        if file.endswith(".json"):
            dic,times = count_hv_times(os.path.join(files_dir,file))
            print(dic)
            print(file.replace('.json',''),times)


#count_hv_times_all("D:/文档/APR-Ensemble/Logs/EAPR_tools")
#_,t = count_hv_times("D:\文档\APR-Ensemble\Logs\EAPR_tools\\10_0.5_10_selectmode.json")
#print(t)


