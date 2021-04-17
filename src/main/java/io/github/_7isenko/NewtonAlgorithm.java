package io.github._7isenko;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 7isenko
 */
public class NewtonAlgorithm {

    private final Function function;
    private final Function derivative;
    private final double accuracy;
    private final Map<Double, Double> points;
    private int iterations;

    public NewtonAlgorithm(Function function, Function derivative, double accuracy) {
        this.function = function;
        this.derivative = derivative;
        this.accuracy = accuracy;
        this.points = new LinkedHashMap<>();
        this.iterations = 0;
    }

    public Map<Double, Double> solve(double estimate) {

        // В случае деления на ноль пытаюсь получить приближенные значения
        if (Double.isNaN(function.solve(estimate))) {
            estimate += accuracy;
        }

        double prevEst;
        double result = function.solve(estimate);
        points.put(estimate, result);

        if (Math.abs(result) >= accuracy) {
            do {
                iterations++;
                result = function.solve(estimate);
                double derivativeVal = derivative.solve(estimate);
                if (derivativeVal == 0) derivativeVal = accuracy;
                prevEst = estimate;
                estimate = estimate - (result / derivativeVal);
                result = function.solve(estimate);
                points.put(estimate, result);
            } while (Math.abs(result) >= accuracy && Math.abs(estimate - prevEst) >= accuracy);
        }

        return points;
    }

    public int getIterations() {
        return iterations;
    }
}
