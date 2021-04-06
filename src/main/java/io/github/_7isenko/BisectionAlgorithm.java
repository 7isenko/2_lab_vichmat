package io.github._7isenko;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 7isenko
 */
public class BisectionAlgorithm {
    private final Function function;
    private final double accuracy;
    private final Map<Double, Double> points;

    public BisectionAlgorithm(Function function, double accuracy) {
        this.function = function;
        this.accuracy = accuracy;
        this.points = new LinkedHashMap<>();
    }

    public Map<Double, Double> solve(double xLeft, double xRight) {
        if (xLeft > xRight) {
            double tmpX = xRight;
            xRight = xLeft;
            xLeft = tmpX;
        }

        double answer;

        answer = function.solve(xLeft);
        if (answer == 0) {
            points.put(xLeft, answer);
            return points;
        }

        answer = function.solve(xRight);
        if (answer == 0) {
            points.put(xRight, answer);
            return points;
        }

        double xMid = xLeft + (xRight - xLeft) / 2;
        if (Double.isNaN(function.solve(xMid))) {
            xRight += xRight / 10; // Если произошло деление на ноль, стоит немного сдвинуть первоначальные границы
        }


        while (xRight - xLeft > accuracy) {

            xMid = xLeft + (xRight - xLeft) / 2;
            answer = function.solve(xMid);

            if (Math.abs(answer) > accuracy) {
                if (Math.signum(function.solve(xLeft)) != Math.signum(answer)) {
                    xRight = xMid;
                } else {
                    xLeft = xMid;
                }
            } else {
                xRight = xLeft; // exit from the cycle on the next iteration
            }
            points.put(xMid, answer);
        }

        return points;
    }
}
