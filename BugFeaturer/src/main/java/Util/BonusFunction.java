package Util;

public class BonusFunction {
    public static double noNegetive(int preference_score) {
        return Math.min(0, preference_score);
    }

    public static double noChange(int preference_score) {
        return (double) preference_score;
    }

    public static double multAplha(double preference_score, double alpha) {
        return (1 + alpha) * preference_score;
    }
}
