import codecs
import csv
import json
import os
import random

from count_hv_times import count_hv_times

bugnum={"Chart":26,"Closure":133,"Lang":65,"Math":106,"Mockito":38,"Time":27}
all_projects=["Chart","Closure","Lang","Math","Mockito","Time"]
def getRepairResult(bugname,tool_name,result_dict):
    if tool_name=="Arja":
        tool_name="ARJA"
    if tool_name == "Cardumen":
        tool_name = "Cardumem"
    if tool_name in ["GenProg","Kali","RSRepair"]:
        tool_name = tool_name+'-A'
    #print(bugname,result_dict[tool_name]["correct"])
    if bugname in result_dict[tool_name]["correct"]:
        return "correct"
    elif bugname in result_dict[tool_name]["overfit"]:
        return "overfit"
    else:
        return "fail"

def get_hvall4comb(results_dir):
    results = os.listdir(results_dir)
    comb_hvall={}
    for file in results:
        if file.endswith("21_selectmode.json"):
            _,hv_times = count_hv_times(os.path.join(results_dir,file))
            infos = file.split('_')
            projects=all_projects[int(infos[0])]+'_'+all_projects[int(infos[1])]+'_'+all_projects[int(infos[2])]
            comb_hvall[projects]=hv_times
    return comb_hvall
def find_hvall(projects,re_dict):
    for key in re_dict.keys():
        if projects[0] in key and projects[1] in key and projects[2] in key:
            return re_dict[key]
    return None
def count_ensemble(repair_result_f,projects):
    repair_result = json.load(codecs.open(repair_result_f,'r',encoding='utf8'))
    correct_fixed_bugs = set()
    overfit_fixed_bugs = set()
    for proj in projects:
        for tool in repair_result.keys():
            correct_results = repair_result[tool]["correct"]
            for bug in correct_results:
                if proj in bug:
                    correct_fixed_bugs.add(bug)
            overfit_results = repair_result[tool]["overfit"]
            for bug in overfit_results:
                if proj in bug:
                    overfit_fixed_bugs.add(bug)
    return correct_fixed_bugs,overfit_fixed_bugs

def statistics_EAPR(repair_result_f,result_dir,output_f):
    results=os.listdir(result_dir)
    ground_truth = json.load(codecs.open(repair_result_f,'r',encoding='utf8'))
    outout_lines=[]
    re_file = open(output_f,'a+',encoding='utf8',newline='')
    writer=csv.writer(re_file)
    for re in results:
        result_json = json.load(codecs.open(os.path.join(result_dir,re)))
        projects=re.replace('.json','').split('_')
        for k in range(21):
            correct_num = 0
            plausible_num = 0
            call_num=0
            bug_valid_times={}
            correct_bugs=set()
            for bug in result_json.keys():
                bug_name = bug
                tool_scores = result_json[bug]
                sorted_scores = sorted(tool_scores.values(), reverse=True)
                bug_name = bug_name.replace('.json', '')
                if bug_name not in bug_valid_times.keys():
                    bug_valid_times[bug_name] = {"times": 0, "continue": True}
                if True:
                    min_score = sorted_scores[k]
                    selected_tools = []
                    for tool in tool_scores.keys():
                        if tool_scores[tool] >= min_score:
                            selected_tools.append(tool)
                    # print(selected_tools)
                    plausible_already = False
                    #selected_tools=random.sample(list(tool_scores.keys()),k+1)
                    call_num+=len(selected_tools)
                    for tool in selected_tools:
                        repair_result = getRepairResult(bug_name, tool, ground_truth)
                        # print(repair_result)
                        if repair_result == "correct":
                            if bug_valid_times[bug_name]["continue"]:
                                bug_valid_times[bug_name]["times"] = bug_valid_times[bug_name]["times"] + 1
                                bug_valid_times[bug_name]["continue"] = False
                        elif repair_result == "overfit":
                            if bug_valid_times[bug_name]["continue"]:
                                bug_valid_times[bug_name]["times"] = bug_valid_times[bug_name]["times"] + 1
                        if repair_result == "correct":
                            correct_num += 1
                            correct_bugs.add(bug_name)
                            if plausible_already:
                                plausible_num -= 1
                            break
                        elif repair_result == "overfit":
                            if not plausible_already:
                                plausible_num += 1
                                plausible_already = True
            #print(projects)
            test_projects=list(set(all_projects)-set(projects))
            ensemble_perf,_=count_ensemble(repair_result_f,set(all_projects)-set(projects))
            #print(len(ensemble_perf),correct_num,call_num,21*(bugnum[test_projects[0]]+bugnum[test_projects[1]]+bugnum[test_projects[2]]))
            correct_num = len(correct_bugs)
            call_num=call_num*(1+0.025)
            eff_gain=round(correct_num*100/len(ensemble_perf)-call_num*100/(21*(bugnum[test_projects[0]]+bugnum[test_projects[1]]+bugnum[test_projects[2]])),2)
            #print(re,k,eff_gain)
            print(set(ensemble_perf)-correct_bugs)
            eff_gain=max(0.5,eff_gain)

            hv_times=0
            for key in bug_valid_times.keys():
                hv_times+=bug_valid_times[key]["times"]

            hv_dict=get_hvall4comb("D:\文档\APR-Ensemble\Logs\EPRP_comb")
            HVSP = round(correct_num*100/len(ensemble_perf)-hv_times*100/find_hvall(test_projects,hv_dict),2)
            print(test_projects,k,correct_num,len(ensemble_perf),hv_times,find_hvall(test_projects,hv_dict),HVSP)
            writer.writerow([HVSP])

#statistics_EAPR("D:\文档\APR-Ensemble\APR_fixes_all.json","./eapr_results","./eapr_hvsp.csv")

def statistics_EPRP(result_dir,repair_result_f,output_f):
    files = os.listdir(result_dir)
    re_file = open(output_f,'a+',encoding='utf8',newline='')
    csv_writer = csv.writer(re_file)
    for file in files:
        if file.endswith('.json'):
            call_num=0
            correct_bugs=set()
            result_infos = file.replace('.json','').split('_')

            projects=[all_projects[int(result_infos[0])],all_projects[int(result_infos[1])],all_projects[int(result_infos[2])]]
            bug_valid_times = {}
            with open(os.path.join(result_dir,file),'r',encoding='utf8')as f:
                for line in f:
                    line_infos = line.strip().split("<SEP>")
                    bug=line_infos[0]
                    bug_project = line_infos[0].split('_')[0]
                    if bug not in bug_valid_times.keys():
                        bug_valid_times[bug] = {"times": 0, "continue": True}
                    if not bug_project in projects:
                        #start counting
                        fix_status = line_infos[-1]
                        if not fix_status.startswith("UnSelected"):
                            if fix_status=="Correct":
                               correct_bugs.add(line_infos[0])
                            call_num+=1
                        if fix_status == "Correct":
                            if bug_valid_times[bug]["continue"]:
                                bug_valid_times[bug]["times"] = bug_valid_times[bug]["times"] + 1
                                bug_valid_times[bug]["continue"] = False
                        elif fix_status == "Overfit":
                            if bug_valid_times[bug]["continue"]:
                                bug_valid_times[bug]["times"] = bug_valid_times[bug]["times"] + 1
                f.close()
            test_projects=list(set(all_projects)-set(projects))
            ensemble_perf,_=count_ensemble(repair_result_f,test_projects)
            all_test_num = 21*(bugnum[test_projects[0]]+bugnum[test_projects[1]]+bugnum[test_projects[2]])
            print(file,len(ensemble_perf),len(correct_bugs),call_num,all_test_num)

            print(len(correct_bugs)*100/len(ensemble_perf)-call_num*100/all_test_num)
            effgain=round(len(correct_bugs)*100/len(ensemble_perf)-call_num*100/all_test_num,2)
            hv_times=0
            for key in bug_valid_times.keys():
                hv_times+=bug_valid_times[key]["times"]
            correct_num =len(correct_bugs)
            hv_dict=get_hvall4comb("D:\文档\APR-Ensemble\Logs\EPRP_comb")
            HVSP = round(correct_num*100/len(ensemble_perf)-hv_times*100/find_hvall(test_projects,hv_dict),2)
            print(test_projects,correct_num,len(ensemble_perf),hv_times,find_hvall(test_projects,hv_dict),HVSP)
            if effgain>0:
                csv_writer.writerow([HVSP])

def statistics_PR_EAPR(repair_result_f,result_f):
    ground_truth = json.load(codecs.open(repair_result_f,'r',encoding='utf8'))
    eapr_result = json.load(codecs.open(result_f,'r',encoding='utf8'))
    for bug in eapr_result.keys():
        infos = bug.split('_')
        bug_name = (infos[1]+'_'+infos[2]).replace('.json','')
        tools_rank = eapr_result.get(bug)
        tool_scores = sorted(tools_rank.values(),reverse=True)
        for k in range(10):
            max_score = tool_scores[k]
            correct_num=0
            plausible_num=0
            for tool in tools_rank.keys():
                score = tool_scores.get(tool)
                if score <= max_score:
                    repair_result = getRepairResult(bug_name,tool,ground_truth)
                    if repair_result == "correct":
                        correct_num+=1
                        break
                    elif repair_result == "overfit":
                        plausible_num+=1

            pass
        pass


#statistics_EPRP("D:/文档/APR-Ensemble/Logs/EPRP_comb","D:\文档\APR-Ensemble\APR_fixes_all.json","./eprp_HVSP.csv")