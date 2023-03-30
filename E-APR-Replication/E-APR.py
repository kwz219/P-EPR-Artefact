import codecs
import itertools
import json
import os
from sklearn.ensemble import RandomForestClassifier

features_dict={"AECSL":"LE10_ATOMIC_EXPRESSION_COMPARISION_SAME_LEFT",
               "SPTWNG":"M5_SIMILAR_PRIMITIVE_TYPE_WITH_NORMAL_GUARD",
               "CVNI":"LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED",
               "VCTC":"V11_VAR_COMPATIBLE_TYPE_IN_CONDITION",
               "PUIA":"S8_PRIMITIVE_USED_IN_ASSIGNMENT"}
value_dict={"false":0,"true":1,"True":1,"False":0}
systems={"Arja":0,"Cardumen":1,"DynaMoth":2,"GenProg":3,"Kali":4,"Nopol":5,"RSRepair":6,
         "jGenProg":7,"jKali":8,"jMutRepair":9}
tool_dict={'ACS': 0, 'ARJA': 1, 'AVATAR': 2, 'Cardumem': 3, 'DynaMoth': 4, 'FixMiner': 5, 'GenProg-A': 6, 'jGenProg': 7, 'jKali': 8, 'jMutRepair': 9, 'Kali-A': 10, 'kPAR': 11, 'Nopol': 12, 'RSRepair-A': 13, 'SimFix': 14, 'TBar': 15, 'SequenceR': 16, 'Recoder': 17, 'RewardRepair': 18, 'CodeBERT': 19, 'TransplantFix': 20}

def search_repair_result(result_dict,key):
    if True:
        for proj in result_dict.keys():
            proj_dict=result_dict[proj]
            if key in proj_dict.keys():
                return proj_dict[key]

    return None
def calculate_label(result_dict):
    label=[1,1,1,1,1,1,1,1,1,1]
    for sys in systems.keys():
        if sys in result_dict.keys():
            label[systems[sys]]=value_dict[str(result_dict[sys])]
        else:
            label[systems[sys]]=0
    assert len(label)==10
    return label


def build_train_features(features_dir,result_f,qbsresult_f):
    samples = os.listdir(features_dir)
    repair_results = json.load(codecs.open(result_f,'r',encoding='utf8'))
    train_features=[]
    train_labels=[]
    for sample in samples:
        sample_path=os.path.join(features_dir,sample)
        sample_features = json.load(codecs.open(os.path.join(sample_path),'r',encoding='utf8'))
        feature_value=[0,0,0,0,0]
        sample_label=[1,1,1,1,1,1,1,1,1,1]
        bench=sample_features["id"]
        if len(sample_features["files"])==0:
            continue
        else:
            features = sample_features["files"][0]["features"][0]
            filename = sample_features["files"][0]["file_name"]
            #print(filename)
            if "bears" in bench:
                sample_repair_result=repair_results["Bears"]
                name_infos = filename.split("-")
                key = name_infos[-3]+'-'+name_infos[-2]

                result_list= search_repair_result(sample_repair_result,key)
                #print(key)
                #print(sample_repair_result.keys())

                if not result_list==None:
                    sample_label = calculate_label(result_list)
                else:
                    print("not find",bench)
            elif "bugsdotjar" in bench:
                sample_repair_result=repair_results["Bugs.jar"]
                name_infos = filename.split("-")
                key = name_infos[-2]

                result_list= search_repair_result(sample_repair_result,key)
                if not result_list==None:
                    sample_label = calculate_label(result_list)
                else:
                    print("not find",bench)
            else:
                #quixbugs
                qbs_repair_results=json.load(codecs.open(qbsresult_f,'r',encoding='utf8'))
                sample_repair_result=qbs_repair_results["QuixBugs"]
                key = filename.replace('-','_')
                if key in sample_repair_result.keys():
                    sample_label = calculate_label(sample_repair_result[key])
                else:
                    print("not find",bench)
            #print(features)
            # find AESCL and CVNI
            # print(list(features.keys()))
            if "FEATURES_LOGICAL_EXPRESSION" in features.keys():
                FLE_features = features["FEATURES_LOGICAL_EXPRESSION"]
                for item in FLE_features.keys():
                    item_FLE= FLE_features[item]
                    aescl_find=False
                    cvni_find=False
                    for fle in item_FLE.keys():
                        if fle=="LE10_ATOMIC_EXPRESSION_COMPARISION_SAME_LEFT":
                            feature_value[0]=value_dict[item_FLE[fle]]
                            aescl_find=True
                        if fle == "LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED":
                            feature_value[2] = value_dict[item_FLE[fle]]
                            cvni_find=True
                        if aescl_find and cvni_find:
                            break
            # find SPTWNG
            if "FEATURES_METHODS" in features.keys():
                FM_features= features["FEATURES_METHODS"]
                for item in FM_features.keys():
                    item_FM=FM_features[item]
                    for fme in item_FM.keys():
                        if fme == "M5_SIMILAR_PRIMITIVE_TYPE_WITH_NORMAL_GUARD":
                            feature_value[1]=value_dict[item_FM[fme]]
                            break
            # find VCTC
            if "FEATURES_VARS" in features.keys():

                FV_features = features["FEATURES_VARS"]
                for item in FV_features.keys():
                    item_FV=FV_features[item]
                    for fme in item_FV.keys():
                        if fme == "V11_VAR_COMPATIBLE_TYPE_IN_CONDITION":
                            feature_value[3]=value_dict[item_FV[fme]]
                            break
            # find PUIA
            if "S8_PRIMITIVE_USED_IN_ASSIGNMENT" in features.keys():

                PUIA = features["S8_PRIMITIVE_USED_IN_ASSIGNMENT"]
                feature_value[4]=value_dict[PUIA]
        train_features.append(feature_value)
        train_labels.append(sample_label)
        print(feature_value,sample_label)

    return train_features,train_labels

#train_features,train_labels=build_train_features("D:/文档/APR-Ensemble/EAPR/features","E:/RepairThemAll_experiment-master/RepairThemAll_experiment-master/repair_results.json","E:\RepairThemAll_experiment-master\RepairThemAll_experiment-master\quixbugs_results.json")
#clf=RandomForestClassifier(n_estimators=10)

#clf.fit(train_features,train_labels)

def load_test_samples(dir):
    bug_features = os.listdir(dir)
    test_samples={}
    for bug in bug_features:
        if "Chart" in bug or "Time" in bug or "Closure" in bug or "Lang" in bug or "Math" in bug or "Mockito" in bug:
            repairresults = json.load(codecs.open(os.path.join(dir,bug)))
            feature_value=[0,0,0,0,0]
            test_samples[bug]=feature_value
            if len(repairresults["files"])==0:
                continue
            else:
                # find AESCL and CVNI
                # print(list(features.keys()))
                features=repairresults["files"][0]["features"][0]
                if "FEATURES_LOGICAL_EXPRESSION" in features.keys():
                    FLE_features = features["FEATURES_LOGICAL_EXPRESSION"]
                    for item in FLE_features.keys():
                        item_FLE = FLE_features[item]
                        aescl_find = False
                        cvni_find = False
                        for fle in item_FLE.keys():
                            if fle == "LE10_ATOMIC_EXPRESSION_COMPARISION_SAME_LEFT":
                                feature_value[0] = value_dict[item_FLE[fle]]
                                aescl_find = True
                            if fle == "LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED":
                                feature_value[2] = value_dict[item_FLE[fle]]
                                cvni_find = True
                            if aescl_find and cvni_find:
                                break
                # find SPTWNG
                if "FEATURES_METHODS" in features.keys():
                    FM_features = features["FEATURES_METHODS"]
                    for item in FM_features.keys():
                        item_FM = FM_features[item]
                        for fme in item_FM.keys():
                            if fme == "M5_SIMILAR_PRIMITIVE_TYPE_WITH_NORMAL_GUARD":
                                feature_value[1] = value_dict[item_FM[fme]]
                                break
                # find VCTC
                if "FEATURES_VARS" in features.keys():

                    FV_features = features["FEATURES_VARS"]
                    for item in FV_features.keys():
                        item_FV = FV_features[item]
                        for fme in item_FV.keys():
                            if fme == "V11_VAR_COMPATIBLE_TYPE_IN_CONDITION":
                                feature_value[3] = value_dict[item_FV[fme]]
                                break

                # find PUIA
                if "S8_PRIMITIVE_USED_IN_ASSIGNMENT" in features.keys():
                    PUIA = features["S8_PRIMITIVE_USED_IN_ASSIGNMENT"]
                    feature_value[4] = value_dict[PUIA]
            test_samples[bug]=feature_value
    return test_samples

"""
test_samples=load_test_samples("D:/文档/APR-Ensemble/EAPR/d4jfeatures")
predict_results={}
"""

def getlabelvalue(bugname,repair_result_dict):
    label_value=[0] * 21
    for tool_name in tool_dict.keys():
        result = repair_result_dict[tool_name]
        if bugname in result["correct"]:
            label_value[tool_dict[tool_name]]=1
    return label_value
def load_all_labels(features_dir,repair_result_f):
    repair_labels = json.load(codecs.open(repair_result_f,'r',encoding='utf8'))
    files=os.listdir(features_dir)
    all_label_dict={}
    for file in files:
        repairresults = json.load(codecs.open(os.path.join(features_dir,file),'r',encoding='utf8'))
        feature_value=[0,0,0,0,0]

        bugname = (file.split("_")[1] + '_' + file.split('_')[2]).replace('.json', '')
        label_value = getlabelvalue(bugname,repair_labels)

        if len(repairresults["files"]) == 0:
            continue
        else:
            # find AESCL and CVNI
            # print(list(features.keys()))
            features = repairresults["files"][0]["features"][0]
            if "FEATURES_LOGICAL_EXPRESSION" in features.keys():
                FLE_features = features["FEATURES_LOGICAL_EXPRESSION"]
                for item in FLE_features.keys():
                    item_FLE = FLE_features[item]
                    aescl_find = False
                    cvni_find = False
                    for fle in item_FLE.keys():
                        if fle == "LE10_ATOMIC_EXPRESSION_COMPARISION_SAME_LEFT":
                            feature_value[0] = value_dict[item_FLE[fle]]
                            aescl_find = True
                        if fle == "LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED":
                            feature_value[2] = value_dict[item_FLE[fle]]
                            cvni_find = True
                        if aescl_find and cvni_find:
                            break
            # find SPTWNG
            if "FEATURES_METHODS" in features.keys():
                FM_features = features["FEATURES_METHODS"]
                for item in FM_features.keys():
                    item_FM = FM_features[item]
                    for fme in item_FM.keys():
                        if fme == "M5_SIMILAR_PRIMITIVE_TYPE_WITH_NORMAL_GUARD":
                            feature_value[1] = value_dict[item_FM[fme]]
                            break
            # find VCTC
            if "FEATURES_VARS" in features.keys():

                FV_features = features["FEATURES_VARS"]
                for item in FV_features.keys():
                    item_FV = FV_features[item]
                    for fme in item_FV.keys():
                        if fme == "V11_VAR_COMPATIBLE_TYPE_IN_CONDITION":
                            feature_value[3] = value_dict[item_FV[fme]]
                            break

            # find PUIA
            if "S8_PRIMITIVE_USED_IN_ASSIGNMENT" in features.keys():
                PUIA = features["S8_PRIMITIVE_USED_IN_ASSIGNMENT"]
                feature_value[4] = value_dict[PUIA]
            all_label_dict[bugname]={"feature":feature_value,"label":label_value}
    return all_label_dict
def split_train_test(features_dir,label_f,output_dir):
    projects=["Chart","Closure","Math","Lang","Mockito","Time"]
    combs=itertools.combinations(projects,r=3)
    all_label_dict=load_all_labels(features_dir,label_f)
    print(list(all_label_dict.keys()))

    for idx,comb in enumerate(combs):
        print(comb)
        train_features = []
        train_labels = []
        train_ids = []
        test_features = []
        test_ids = []
        for bugname in all_label_dict.keys():
            def valid_comb(bugname,comb):
                for project in comb:
                    if project in bugname:
                        if project=="Closure" and int(bugname.split('_')[-1])>133:
                            return False
                        return True
                return False
            if valid_comb(bugname,comb):
                train_features.append(all_label_dict[bugname]["feature"])
                train_labels.append(all_label_dict[bugname]["label"])
                #print(len(all_label_dict[bugname]["label"]))
                train_ids.append(bugname+'<SEP>'+'<SEP>'.join(comb))
            else:

                proj = bugname.split('_')[0]
                if proj in projects:
                    if not ((proj=="Closure") and int(bugname.split('_')[-1])>133):
                        #print(bugname+'<SEP>'+'<SEP>'.join(comb))
                        test_features.append(all_label_dict[bugname]["feature"])
                        test_ids.append(bugname+'<SEP>'+'<SEP>'.join(comb))

        print(idx,comb,"starts training")
        print("train",len(train_ids),"test ",len(test_ids))

        rfc_model =RandomForestClassifier()
        rfc_model.fit(train_features,train_labels)

        print("starts predicting")
        predict_results={}
        for ind,feature in zip(test_ids,test_features):
            test_x = [feature]
            pred_prob = rfc_model.predict_proba(test_x)
            probs = []
            #print(pred_prob)
            #print(len(pred_prob))
            for prob in pred_prob:
                if len(prob[0])>1:
                    probs.append(prob[0][1])
                else:
                    probs.append(0.0)
            sys_pred = {}
            for sys in tool_dict.keys():
                sys_pred[sys] = probs[tool_dict[sys]]
            bugname=ind.split("<SEP>")[0]
            predict_results[bugname] = sys_pred
        with open(output_dir+'/'+'_'.join(comb)+'.json','w',encoding='utf8')as f:
            json.dump(predict_results,f,indent=4)
        print(idx,comb,"write finished",len(list(predict_results.keys())))



split_train_test("D:/文档/APR-Ensemble/EAPR/d4jfeatures","D:\文档\APR-Ensemble\APR_fixes_all.json","./E-APR-log")
"""
for sample in test_samples.keys():
    feature=test_samples[sample]
    test_x = [feature]
    print(test_x)
    pred_prob = clf.predict_proba(test_x)
    probs = []
    for prob in pred_prob:
        probs.append(prob[0][1])
    sys_pred={}
    for sys in systems.keys():
        sys_pred[sys]=probs[systems[sys]]
    predict_results[sample]=sys_pred
with open("./EAPR_predict.json",'w',encoding='utf-8')as f:
    json.dump(predict_results,f,indent=4)

"""

