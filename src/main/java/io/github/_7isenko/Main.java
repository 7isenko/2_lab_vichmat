package io.github._7isenko;

import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.*;

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
            Function secondDerivative = x -> 0;
            String strFunc = "y = x";

            switch (chosenAlgorithm) {
                case 1:
                    chosenFunction = x -> a * pow(x, b) + c;  // y = a*(x^b) + c
                    derivative = x -> a * b * pow(x, b - 1);
                    secondDerivative = x -> a * b * (b - 1) * pow(x, b - 2);
                    strFunc = String.format("y = %.3f*(x^%.3f) + %.3f", a, b, c);
                    break;
                case 2:
                    chosenFunction = x -> a * pow(x, 2) + b * x + c;  // y = a*x^2 + bx + c
                    derivative = x -> a * 2 * x + b;
                    secondDerivative = x -> a * 2;
                    strFunc = String.format("y = %.3f*x^2 + %.3f*x + %.3f", a, b, c);
                    break;
                case 3:
                    chosenFunction = x -> a * sin(b * x) / (c * x);  // y = ( a*sin(bx) / cx )
                    derivative = x -> (b * cos(b * x) / (x) - sin(b * x) / pow(x, 2)) * (a / c); // y' = ((b*cos(b*x)/x - sin(b*x)/(x^2)) * a/c)
                    secondDerivative = x -> a * (-b * b * sin(b * x) - 2 * b * cos(b * x) / x + 2 * sin(b * x) / pow(x, 2)) / (c * x);
                    strFunc = String.format("y = %.3f*sin(%.3f*x) / %.3f*x", a, b, c);
                    break;
            }

            System.out.println("Введённая функция: " + strFunc);

            double xLeft = 0, xRight = 0;

            boolean ok = false;
            while (!ok) {
                System.out.println("Задайте отрезок для метода бисекции");
                System.out.println("Левая граница: ");
                xLeft = inputReader.readDoubleFromConsole();
                System.out.println("Правая граница: ");
                xRight = inputReader.readDoubleFromConsole();

                if (signum(chosenFunction.solve(xLeft)) == signum(chosenFunction.solve(xRight))) {
                    System.out.println("Знаки функции на границах равны. Метод бисекции может не сойтись.");
                }

                GraphBuilder.createExampleGraph(chosenFunction, xLeft, xRight);
                System.out.println("Вы хотите изменить выбранные границы? y/n");
                if (!inputReader.parseYesOrNo()) {
                    ok = true;
                }
            }

            double initialEstimate = 0;
            if (signum(secondDerivative.solve(xLeft)) == signum(chosenFunction.solve(xLeft))) {
                initialEstimate = xLeft;
                System.out.println("Начальным приближением для метода Ньютона была выбрана левая граница отрезка");
            } else {
                if (signum(secondDerivative.solve(xRight)) == signum(chosenFunction.solve(xRight))) {
                    initialEstimate = xRight;
                    System.out.println("Начальным приближением для метода Ньютона была выбрана правая граница отрезка");
                } else {
                    System.out.println("Ни один из концов отрезка не удовлетворяет условию сходимости для метода Ньютона.");
                    System.out.println("Введите начальное приближение: ");
                    initialEstimate = inputReader.readDoubleFromConsole();
                    if (signum(secondDerivative.solve(initialEstimate)) != signum(chosenFunction.solve(initialEstimate))) {
                        System.out.println("Знаки функции и второй производной различаются. Сходимость не гарантируется");
                    }
                }
            }

            BisectionAlgorithm bisectionAlgorithm = new BisectionAlgorithm(chosenFunction, accuracy);
            Map<Double, Double> resultForBisection = bisectionAlgorithm.solve(xLeft, xRight);
            int bisectionIterations = bisectionAlgorithm.getIterations();
            GraphBuilder.createBisectionChart(resultForBisection, chosenFunction, xLeft, xRight);
            NewtonAlgorithm newtonAlgorithm = new NewtonAlgorithm(chosenFunction, derivative, accuracy);
            Map<Double, Double> resultForNewton = newtonAlgorithm.solve(initialEstimate);
            int newtonIterations = newtonAlgorithm.getIterations();
            GraphBuilder.createNewtonChart(resultForNewton, chosenFunction, xLeft, xRight);

            System.out.println("Разница в методах:");
            double bX = resultForBisection.keySet().toArray(new Double[0])[resultForBisection.size() - 1];
            double bY = resultForBisection.values().toArray(new Double[0])[resultForBisection.size() - 1];

            double nX = resultForNewton.keySet().toArray(new Double[0])[resultForNewton.size() - 1];
            double nY = resultForNewton.values().toArray(new Double[0])[resultForNewton.size() - 1];

            System.out.printf("Метод бисекции: x = %.4f; y = %.4f, количество итераций: %d\n", bX, bY, bisectionIterations);
            System.out.printf("Метод Ньютона_: x = %.4f; y = %.4f, количество итераций: %d", nX, nY, newtonIterations);
        } else {
            System.out.println("Выберите систему, решения которой хотите найти");
            System.out.println("1 - { sin(a*x1) − x2 + b*x1 + c = 0");
            System.out.println("    { d*x1^2 + e*x2^2 + f = 0\n");
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
            PolynomialFunction[] chosenSolvedFunctions = new PolynomialFunction[0];
            PolynomialFunction[][] derivatives = new PolynomialFunction[0][0];
            double[] estimate = new double[0];
            String[] strFunctions = new String[0];
            switch (chosenAlgorithm) {
                case 1:
                    // { sin(a*x1) − x2 + b*x1 + c = 0
                    // { d*x1^2 + e*x2^2 + f = 0
                    chosenFunctions = new PolynomialFunction[]{
                            xVec -> sin(a * xVec[0]) - xVec[1] + b * xVec[0] + c,
                            xVec -> d * (xVec[0] * xVec[0]) + e * (xVec[1] * xVec[1]) + f};
                    chosenSolvedFunctions = new PolynomialFunction[]{
                            xVec -> sin(a * xVec[0]) + b * xVec[0] + c, // y =c+bx+sin(ax)
                            xVec -> sqrt(-d * (xVec[0] * xVec[0]) - f) * sqrt(e), // y = sqrt(-f-dx^2)*sqrt(e)
                            xVec -> -sqrt(-d * (xVec[0] * xVec[0]) - f) * sqrt(e)  // y = -sqrt(-f-dx^2)*sqrt(e)
                    };
                    derivatives = new PolynomialFunction[][]{
                            {xVec -> a * cos(a * xVec[0]) + b, xVec -> d * 2 * xVec[0]},
                            {xVec -> -1, xVec -> 2 * e * xVec[1]}};
                    estimate = new double[2];
                    strFunctions = new String[2];
                    strFunctions[0] = String.format("sin(%.4f*x1) - x2 + %.4f*x1 + %.4f", a, b, c);
                    strFunctions[1] = String.format("%.4f*x1^2 + %.4f*x2^2 + %.4f", d, e, f);
                    break;
                case 2:
                    // { a*cos(x1) - b = 0
                    // { c*cos(x2) - d = 0
                    // { e*cos(x3) - f = 0
                    chosenFunctions = new PolynomialFunction[]{
                            xVec -> a * cos(xVec[0]) - b,
                            xVec -> c * cos(xVec[1]) - d,
                            xVec -> e * cos(xVec[2]) - f};
                    derivatives = new PolynomialFunction[][]{
                            {xVec -> -a * sin(xVec[0]), xVec -> 0, xVec -> 0},
                            {xVec -> 0, xVec -> -c * sin(xVec[1]), xVec -> 0},
                            {xVec -> 0, xVec -> 0, xVec -> -e * sin(xVec[2])}};
                    estimate = new double[3];
                    strFunctions = new String[3];
                    strFunctions[0] = String.format("%.4f*cos(x1) - %.4f", a, b);
                    strFunctions[1] = String.format("%.4f*cos(x2) - %.4f", c, d);
                    strFunctions[2] = String.format("%.4f*cos(x3) - %.4f", e, f);
                    break;
                default:
                    System.out.println("я такое на знаю!");
                    return;
            }
            XYChart chart = null;
            if (chosenAlgorithm == 1)
               chart = GraphBuilder.createTwoDimensionalSystemGraph(chosenSolvedFunctions, true);
            else {

            }

            System.out.println("Введите начальное приближение");
            for (int i = 0; i < estimate.length; i++) {
                System.out.printf("x%d: ", i);
                estimate[i] = inputReader.readDoubleFromConsole();
            }

            NewtonAlgorithmExtended newtonAlgorithmExtended = new NewtonAlgorithmExtended(chosenFunctions, derivatives, accuracy);
            ArrayList<Map<Double, Double>> result = newtonAlgorithmExtended.solve(estimate);
            GraphBuilder.createTwoDimensionalSystemChart(chart, result);

            System.out.println("Ответ, полученный методом Ньютона: ");
            for (int i = 0, resultSize = result.size(); i < resultSize; i++) {
                Map<Double, Double> map = result.get(i);
                double x = map.keySet().toArray(new Double[0])[map.size() - 1];
                double y = map.values().toArray(new Double[0])[map.size() - 1];
                System.out.printf("x%d = %f ; f%d(x) = %f\n", i, x, i, y);
            }
            System.out.println("Количество итераций: " + newtonAlgorithmExtended.getIterations());

        }


    }

}
