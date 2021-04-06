package io.github._7isenko;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author 7isenko
 */
public class Main {

    private static final InputReader inputReader = new InputReader();

    public static void main(String[] args) {

        System.out.println("Вы желаете получить решение НУ или СНУ? НУ - y, СНУ - n");
        double accuracy;
        if (inputReader.parseYesOrNo()) {

            System.out.println("Выберите уравнение, корни которого хотите найти");
            System.out.println("1 - y = a*(x^b) + c");
            System.out.println("2 - y = a*x^2 + bx + c");
            System.out.println("3 - y = ( a*sin(bx) / cx )");
            int chosenAlgorithm = inputReader.readIntFromConsole();

            if (chosenAlgorithm > 3 || chosenAlgorithm <= 0) {
                throw new IllegalArgumentException();
            }

            double a, b, c;
            System.out.println("Введите коэффициент a: ");
            a = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент b: ");
            b = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент c: ");
            c = inputReader.readDoubleFromConsole();

            System.out.println("Введите точность: ");
            accuracy = inputReader.readDoubleFromConsole();

            Function chosenFunction = x -> x;
            Function derivative = x -> 1;
            String strFunc = "y = x";

            switch (chosenAlgorithm) {
                case 1:
                    chosenFunction = x -> a * Math.pow(x, b) + c;  // y = a*(x^b) + c
                    derivative = x -> a * b * Math.pow(x, b - 1);
                    strFunc = String.format("y = %.3f*(x^%.3f) + %.3f", a, b, c);
                    break;
                case 2:
                    chosenFunction = x -> a * Math.pow(x, 2) + b * x + c;  // y = a*x^2 + bx + c
                    derivative = x -> a * 2 * x + b;
                    strFunc = String.format("y = %.3f*x^2 + %.3f*x + %.3f", a, b, c);
                    break;
                case 3:
                    chosenFunction = x -> a * Math.sin(b * x) / (c * x);  // y = ( a*sin(bx) / cx )
                    derivative = x -> (b * Math.cos(b * x) / x - Math.sin(b * x) / Math.pow(x, 2)) * (a / c);
                    strFunc = String.format("y = %.3f*sin(%.3f*x) / %.3f*x", a, b, c);
                    break;
            }

            System.out.println("Введённая функция: " + strFunc);

            System.out.println("Задайте отрезок для метода бисекции");
            System.out.println("Левая граница: ");
            double xLeft = inputReader.readDoubleFromConsole();
            System.out.println("Правая граница: ");
            double xRight = inputReader.readDoubleFromConsole();

            System.out.println("Задайте начальное приближение для метода касательных: ");
            double initialEstimate = inputReader.readDoubleFromConsole();

            Map<Double, Double> resultForBisection = new BisectionAlgorithm(chosenFunction, accuracy).solve(xLeft, xRight);
            GraphBuilder.createChart(resultForBisection, "Bisection Method");
            Map<Double, Double> resultForNewton = new NewtonAlgorithm(chosenFunction, derivative, accuracy).solve(initialEstimate);
            GraphBuilder.createChart(resultForNewton, "Newton's Method");

            System.out.println("Разница в методах:");
            double bX = resultForBisection.keySet().toArray(new Double[0])[resultForBisection.size() - 1];
            double bY = resultForBisection.values().toArray(new Double[0])[resultForBisection.size() - 1];

            double nX = resultForNewton.keySet().toArray(new Double[0])[resultForNewton.size() - 1];
            double nY = resultForNewton.values().toArray(new Double[0])[resultForNewton.size() - 1];

            System.out.printf("Метод бисекции: x = %.4f; y = %.4f\n", bX, bY);
            System.out.printf("Метод Ньютона_: x = %.4f; y = %.4f", nX, nY);
        } else {
            System.out.println("Выберите систему, решения которой хотите найти");
            System.out.println("1 - { sin(a*x1 − x2) + b*x1 + c = 0");
            System.out.println("    { d*x^2 + e*y^2 + f = 0\n");
            System.out.println("2 - { a*cos(x1) - b = 0");
            System.out.println("    { c*cos(x2) - d = 0");
            System.out.println("    { e*cos(x3) - f = 0");

            int chosenAlgorithm = inputReader.readIntFromConsole();

            double a, b, c, d, e, f;

            System.out.println("Введите коэффициент a: ");
            a = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент b: ");
            b = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент c: ");
            c = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент d: ");
            d = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент e: ");
            e = inputReader.readDoubleFromConsole();
            System.out.println("Введите коэффициент f: ");
            f = inputReader.readDoubleFromConsole();

            System.out.println("Введите точность: ");
            accuracy = inputReader.readDoubleFromConsole();

            PolynomialFunction[] chosenFunctions = new PolynomialFunction[0];
            PolynomialFunction[][] derivatives = new PolynomialFunction[0][0];
            double[] estimate = new double[0];
            String[] strFunctions = new String[0];
            switch (chosenAlgorithm) {
                case 1:
                    // { sin(a*x1 − x2) + b*x1 + c = 0
                    // { d*x1^2 + e*x2^2 + f = 0
                    chosenFunctions = new PolynomialFunction[]{
                            xVec -> Math.sin(a * xVec[0] - xVec[1]) + b * xVec[0] + c,
                            xVec -> d * (xVec[0] * xVec[0]) + e * (xVec[1] * xVec[1]) + f};
                    derivatives = new PolynomialFunction[][]{
                            {xVec -> a * Math.cos(a * xVec[0] - xVec[1]) + b, xVec -> d * 2 * xVec[0]},
                            {xVec -> -Math.cos(a * xVec[0] - xVec[1]), xVec -> 2 * e * xVec[1]}};
                    estimate = new double[2];
                    strFunctions = new String[2];
                    strFunctions[0] = String.format("sin(%.4f*x1 - x2) + %.4f*x1 + %.4f", a, b, c);
                    strFunctions[1] = String.format("%.4f*x1^2 + %.4f*x2^2 + %.4f", d, e, f);
                    break;
                case 2:
                    // { a*cos(x1) - b = 0
                    // { c*cos(x2) - d = 0
                    // { e*cos(x3) - f = 0
                    chosenFunctions = new PolynomialFunction[]{
                            xVec -> a * Math.cos(xVec[0]) - b,
                            xVec -> c * Math.cos(xVec[1]) - d,
                            xVec -> e * Math.cos(xVec[2]) - f};
                    derivatives = new PolynomialFunction[][]{
                            {xVec -> -a * Math.sin(xVec[0]), xVec -> 0, xVec -> 0},
                            {xVec -> 0, xVec -> -c * Math.sin(xVec[1]), xVec -> 0},
                            {xVec -> 0, xVec -> 0, xVec -> -e * Math.sin(xVec[2])}};
                    estimate = new double[3];
                    strFunctions = new String[3];
                    strFunctions[0] = String.format("%.4f*cos(x1) - %.4f", a, b);
                    strFunctions[1] = String.format("%.4f*cos(x2) - %.4f", c, d);
                    strFunctions[2] = String.format("%.4f*cos(x3) - %.4f", e, f);
                    break;
            }
            System.out.println("Введите начальное приближение");
            for (int i = 0; i < estimate.length; i++) {
                System.out.printf("x%d: ", i);
                estimate[i] = inputReader.readDoubleFromConsole();
            }

            ArrayList<Map<Double, Double>> result = new NewtonAlgorithmExtended(chosenFunctions, derivatives, accuracy).solve(estimate);
            GraphBuilder.createChart(result, "Newton's Algorithm", strFunctions);

            System.out.println("Ответ, полученный методом Ньютона: ");
            for (int i = 0, resultSize = result.size(); i < resultSize; i++) {
                Map<Double, Double> map = result.get(i);
                double x = map.keySet().toArray(new Double[0])[map.size() - 1];
                double y = map.values().toArray(new Double[0])[map.size() - 1];
                System.out.printf("x%d = %f ; f%d(x) = %f\n", i, x, i, y);
            }

        }


    }

}
