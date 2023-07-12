package Util;

import APRTool.APRTool;

import java.util.*;

public class ToolSelection {
    public static ArrayList<String> selectByAVG(HashMap<String, Double> tool_scores) {
        ArrayList<String> tool_names = new ArrayList();
        double score = 0;
        for (String tool : tool_scores.keySet()) {
            score += tool_scores.get(tool);
        }
        double avg_score = score / tool_scores.size();
        for (String tool : tool_scores.keySet()) {
            if (tool_scores.get(tool) >= avg_score) {
                tool_names.add(tool);
            }
        }
        return tool_names;

    }

    //choose k tools ranked by the preference score
    public static ArrayList<String> selectByTopK(HashMap<String, Double> tool_scores, int k) {
        ArrayList<String> selected_tools = new ArrayList();
        Object[] score = tool_scores.values().stream().sorted(Comparator.reverseOrder()).toArray();
        for (String tool : tool_scores.keySet()) {
            if (tool_scores.get(tool) >= (double) (score[k - 1])) {
                selected_tools.add(tool);
            }
        }
        return selected_tools;
    }

    public static ArrayList<String> explicitFirst(HashMap<String, Double> tool_scores, ArrayList<String> matches_tools) {
        ArrayList<String> selected_tools = new ArrayList();
        if (matches_tools.size() > 0) {
            for (String tool_name : tool_scores.keySet()) {
                if (matches_tools.contains(tool_name) && tool_scores.get(tool_name) > 0) {
                    selected_tools.add(tool_name);
                }
            }
        }
        Object[] score = tool_scores.values().stream().sorted(Comparator.reverseOrder()).toArray();
        System.out.println(score[0]);
        System.out.println(score[1]);
        for (String tool : tool_scores.keySet()) {
            if (tool_scores.get(tool) >= (double) (score[0])) {
                selected_tools.add(tool);
            }
        }

        return selected_tools;
    }

    public static ArrayList<String> purelyHistoryScore(ArrayList<APRTool> tools) {
        ArrayList<String> selected_tools = new ArrayList<>();
        double all_score = 0.0;
        for (APRTool tool : tools) {
            double his_score = tool.getHistoryFixesScore();
            all_score += his_score;
        }
        double avg_score = all_score / tools.size();
        for (APRTool tool : tools) {
            double his_score = tool.getHistoryFixesScore();
            if (his_score > avg_score) {
                selected_tools.add(tool.getName());
            }
        }
        return selected_tools;
    }

    public static LinkedHashMap<String, Double> sortMap(HashMap<String, Double> tool_scores) {
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(tool_scores.entrySet());
        LinkedHashMap<String, Double> ranked_scores = new LinkedHashMap<>();
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> t1, Map.Entry<String, Double> t2) {
                return t2.getValue().compareTo(t1.getValue());
            }
        });

        for (int i = 0; i < list.size(); i++) {
            ranked_scores.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return ranked_scores;
    }
}
