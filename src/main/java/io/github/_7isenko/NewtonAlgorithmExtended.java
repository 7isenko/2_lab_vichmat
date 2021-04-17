package io.github._7isenko;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 7isenko
 */
public class NewtonAlgorithmExtended {

    private final PolynomialFunction[] functions;
    private final PolynomialFunction[][] derivatives;
    private final double accuracy;
    private final ArrayList<Map<Double, Double>> points;
    private int iterations = 0;

    public NewtonAlgorithmExtended(PolynomialFunction[] functions, PolynomialFunction[][] derivatives, double accuracy) {
        this.functions = functions;
        this.derivatives = derivatives;
        this.accuracy = accuracy;
        this.points = new ArrayList<>();
    }

    public ArrayList<Map<Double, Double>> solve(double[] estimate) {

        double[] xVector = estimate;
        for (int i = 0; i < xVector.length; i++) {
            points.add(new LinkedHashMap<>());
        }

        boolean quit = false;
        do {
            iterations++;
            double[] newVector = new double[xVector.length];
            for (int i = 0; i < xVector.length; i++) {
                double summaryDerivatives = 0;
                for (int j = 0; j < functions.length; j++) {
                    summaryDerivatives += derivatives[j][i].solve(xVector);
                }
                if (summaryDerivatives == 0) {
                    summaryDerivatives += accuracy / 2;
                }
                double newX = xVector[i] - functions[i].solve(xVector) / summaryDerivatives;
                newVector[i] = newX;
            }
            for (int i = 0; i < newVector.length; i++) {
                double funcRez = functions[i].solve(newVector);
                points.get(i).put(newVector[i], funcRez);
            }
            double max = 0;
            for (int i = 0; i < xVector.length; i++) {
                max = Math.max(Math.abs(xVector[i] - newVector[i]), max);
            }
            if (max < accuracy) {
                quit = true;
            }
            xVector = newVector;
        } while (!quit && iterations < 10000);

        return points;
    }

    public int getIterations() {
        return iterations;
    }
}
