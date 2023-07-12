package Util;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreNormalizer {
    public static HashMap<String, Double> ScoreNorm(ArrayList<String> matched_tools, HashMap<String, Double[]> scores, double alpha) {
        HashMap<String, Double> normed_scores = new HashMap<>();
        for (String tool_name : scores.keySet()) {
            Double[] tool_score = scores.get(tool_name);
            Double score = tool_score[0] / (tool_score[0] + tool_score[1]);
            if (matched_tools.contains(tool_name)) {
                normed_scores.put(tool_name, BonusFunction.multAplha(score, alpha));
            } else {
                normed_scores.put(tool_name, score);
            }
        }
        return normed_scores;
    }

    public static HashMap<String, Double> ScoreNorm(ArrayList<String> matched_tools, HashMap<String, Double[]> scores, HashMap<String, Double[]> test_scores, double alpha) {
        HashMap<String, Double> normed_scores = new HashMap<>();
        for (String tool_name : scores.keySet()) {
            Double[] tool_score = scores.get(tool_name);
            Double[] test_score_dual = test_scores.get(tool_name);
            Double score = tool_score[0] / (tool_score[0] + tool_score[1]);
            Double test_score = test_score_dual[0] / (test_score_dual[0] + test_score_dual[1]);

            Double final_score = (score + test_score) / 2;
            //Double final_score = test_score;
            if (matched_tools.contains(tool_name)) {
                normed_scores.put(tool_name, BonusFunction.multAplha(final_score, alpha));
            } else {
                normed_scores.put(tool_name, final_score);
            }
        }
        return normed_scores;
    }
}
