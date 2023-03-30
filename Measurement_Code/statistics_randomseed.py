import codecs
import csv
import math
import os

from count_hv_times import count_hv_times


def calculate_TISP_all (files,output_file):
    result_dict={"1":[],"2":[],"3":[],"4":[],"5":[],"6":[],"7":[],"8":[]}
    writer=csv.writer(codecs.open(output_file,'a+',encoding='utf8'))
    for file in files:
        with open(file,'r',encoding='utf8')as f:
            for line in f:
                if line.startswith("21 "):
                    line=line.replace("21 ",'',1)
                infos = line.split()
                rand_seed = infos[0]
                em_alpha = float(infos[1])

                if em_alpha==0.5:
                    tit = int(infos[-1])
                    correct_num = int(infos[3])
                    TISP = round(correct_num*100/122 - tit*100/8295,2)
                    result_dict[rand_seed].append(TISP)
    for r_seed in result_dict.keys():
        TISP_list = result_dict[r_seed]
        writer.writerow(TISP_list)
#calculate_TISP_all([r"D:\文档\APR-Ensemble\Logs\selectmode\EPRP_selectmode.log",r"D:\文档\APR-Ensemble\Logs\rand2\EPRP_selectmode.log"],
                   #"./results/TISP_seeds.csv")

def calculate_HVSP_all(dirs,output_file):
    result_dict = {"1": [], "2": [], "3": [], "4": [], "5": [], "6": [], "7": [], "8": []}
    writer = csv.writer(codecs.open(output_file, 'a+', encoding='utf8'))
    for dir in dirs:
        files = os.listdir(dir)
        for file in files:
            if file.endswith('.json') and '_0.5' in file:
                t,hv_times = count_hv_times(os.path.join(dir,file))
                HVSP=round(len(t)*100/122-hv_times*100/393,2)
                rand_seed = file.split('_')[0]
                result_dict[rand_seed].append(HVSP)
        for r_seed in result_dict.keys():
            TISP_list = result_dict[r_seed]
            writer.writerow(TISP_list)
#calculate_HVSP_all(["D:\文档\APR-Ensemble\Logs\selectmode","D:\文档\APR-Ensemble\Logs\\rand2"],'./results/HVSP_seeds.csv')

def calculate_all_HVSP(file_dir):
    files = os.listdir(file_dir)
    correct_all={"1":56,"2":79,"3":86,"4":102,"5":103,"6":103,"7":103,"8":109,"9":111,"10":114,"11":120,
                 "12":122,"13":122,"14":122,"15":122,"16":122,"17":122,"18":122,"19":122,"20":122,'21':122}
    hv_all={'10': 283, '11': 289, '12': 307, '13': 321, '14': 335, '15': 342, '16': 363, '17': 377, '18': 381, '19': 386,
     '1': 74, '20': 389, '21': 393, '2': 127, '3': 147, '4': 184, '5': 204, '6': 231, '7': 246, '8': 257, '9': 271}

    for file in files:
        if file.endswith('.json'):
            infos = file.split('_')
            tool_num = file.split('_')[0]
            t,hv_times = count_hv_times(os.path.join(file_dir,file))
            print(file,str(math.ceil(len(t)*100/correct_all[tool_num]-hv_times*100/hv_all[tool_num]))+'%')


#calculate_all_HVSP("D:\文档\APR-Ensemble\Logs\combination")

def calculate_HVSP_special_group(file_dir,all_file):
    all_correct,all_hv = count_hv_times(all_file)
    print(len(all_correct),all_hv)
    files = os.listdir(file_dir)
    for file in files:
        correct,hv_times = count_hv_times(os.path.join(file_dir,file))
        print(file,str(math.ceil(len(correct)*100/len(all_correct)-hv_times*100/all_hv)))
def calculate_TISP_special_group(file):
    with open(file,'r',encoding='utf8')as f:
        for line in f:
            if line.startswith("21 "):
                line = line.replace("21 ", '', 1)
            infos = line.split()
            em_alpha = float(infos[1])
            if em_alpha == 0.5:
                tit = int(infos[-1])
                correct_num = int(infos[3])
                TISP = round(correct_num * 100 / 122 - tit * 100 / 8295, 2)
                print(file,TISP)
calculate_TISP_special_group("D:\文档\APR-Ensemble\Logs\\removeTest\\EPRP_selectmode.log")
#calculate_HVSP_special_group("D:\文档\APR-Ensemble\Logs\poorgroup","D:\文档\APR-Ensemble\Logs\poorgroup\\21_11_0.5_11_selectmode.json")
#calculate_HVSP_special_group("D:\文档\APR-Ensemble\Logs\\removeTest","D:\文档\APR-Ensemble\Logs\\removeTest\\21_0.5_21_selectmode.json")
def calculate_TISP_eximpact(no_ex_file,files,output_file):
    ex_results_dict={"No Pattern Preference":[], "0.1":[],"0.3":[],"0.5":[],"0.7":[],"0.9":[]}
    writer = csv.writer(codecs.open(output_file,'w',encoding='utf8'))
    with open(no_ex_file,'r',encoding='utf8')as f:
        for line in f:
            infos = line.strip().split()
            correct_num = int(infos[4])
            hv_times = int(infos[-1])
            TISP = round(correct_num * 100 / 122 - hv_times * 100 / 8295, 2)
            ex_results_dict["No Pattern Preference"].append(TISP)
    for file in files:
        with open(file,'r',encoding='utf8')as f:
            for line in f:
                line=line.replace("21 ",'',1)
                infos=line.strip().split()
                if infos[0] == "1":
                    em_alpha = infos[1]
                    correct_num = int(infos[3])
                    hv_times = int(infos[-1])
                    TISP = round(correct_num*100/122-hv_times*100/8295,2)
                    ex_results_dict[em_alpha].append(TISP)
    for ex in ex_results_dict.keys():
        writer.writerow(ex_results_dict[ex])

#calculate_TISP_eximpact("D:\文档\APR-Ensemble\Logs\\removeEx\EPRP_selectmode.log",
                        #["D:\文档\APR-Ensemble\Logs\selectmode\EPRP_selectmode.log","D:\文档\APR-Ensemble\Logs\\rand2\EPRP_selectmode.log"],
                        #"D:\文档\APR-Ensemble\ex_impact.csv")

def calculate_HVSP_eximpact(files_dir_1,files_dir_2,output_file):
    ex_results_dict = {"No Pattern Preference": [], "0.1": [], "0.3": [], "0.5": [], "0.7": [], "0.9": []}
    #writer = csv.writer(codecs.open(output_file,'w',encoding='utf8'))
    no_ex_files = os.listdir(files_dir_1)
    for file in no_ex_files:
        if file.endswith('.json'):
            correct_bugs,hv_times = count_hv_times(os.path.join(files_dir_1,file))
            HVSP = round(len(correct_bugs)*100/122 - hv_times*100/393,2)
            ex_results_dict["No Pattern Preference"].append(HVSP)
            print(file)
    ex_files = os.listdir(files_dir_2)
    for file in ex_files:
        if file.endswith('.json'):
            correct_bugs, hv_times = count_hv_times(os.path.join(files_dir_2, file))
            ex_alpha = file.split('_')[1]
            HVSP = round(len(correct_bugs) * 100 / 122 - hv_times * 100 / 393, 2)
            ex_results_dict[ex_alpha].append(HVSP)
            print(file)
    #for ex in ex_results_dict.keys():
    #    writer.writerow(ex_results_dict[ex])
#calculate_HVSP_eximpact("D:\文档\APR-Ensemble\Logs\\removeEx","D:\文档\APR-Ensemble\Logs\ex_difference",
                        #"D:\文档\APR-Ensemble\ex_impact_HVSP.csv")