package io.github._7isenko;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 7isenko
 */
public class GraphBuilder {


    public static void createBisectionChart(Map<Double, Double> result, Function function, double leftBorder, double rightBorder) {
        int size = result.size();
        double[] xData = new double[size];
        double[] yData = new double[size];
        fillArrays(result, xData, yData);

        XYChart chart = QuickChart.getChart("Bisection algorithm", "X", "Y", "calc points", xData, yData);
        buildChart(function, leftBorder, rightBorder, size, xData, yData, chart);

        for (int i = 0; i < xData.length; i++) {
            XYSeries series = chart.addSeries("vertical" + i, new double[]{xData[i], xData[i]}, new double[]{yData[i] - 100, yData[i] + 100});
            series.setMarker(SeriesMarkers.NONE);
            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            series.setLineColor(Color.RED);
            series.setLineWidth(1);
            series.setShowInLegend(false);
        }

        new SwingWrapper<>(chart).displayChart();
    }

    public static void createNewtonChart(Map<Double, Double> result, Function function, double leftBorder, double rightBorder) {
        int size = result.size();
        double[] xData = new double[size];
        double[] yData = new double[size];
        fillArrays(result, xData, yData);

        XYChart chart = QuickChart.getChart("Newton algorithm", "X", "Y", "calc points", xData, yData);
        buildChart(function, leftBorder, rightBorder, size, xData, yData, chart);

        for (int i = 0; i < xData.length - 1; i++) {
            XYSeries tangent;
            XYSeries vert;
            if (yData[i] >= 0) {
                tangent = chart.addSeries("tangent" + i, new double[]{xData[i + 1], xData[i]}, new double[]{0, yData[i]});
            } else {
                tangent = chart.addSeries("tangent" + i, new double[]{xData[i], xData[i + 1]}, new double[]{yData[i], 0});
            }
            tangent.setMarker(SeriesMarkers.NONE);
            tangent.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            tangent.setLineColor(Color.RED);
            tangent.setLineWidth(1);
            tangent.setShowInLegend(false);

            vert = chart.addSeries("vert" + i, new double[]{xData[i + 1], xData[i + 1]}, new double[]{0, yData[i + 1]});
            vert.setMarker(SeriesMarkers.NONE);
            vert.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
            vert.setLineColor(Color.BLUE);
            vert.setLineWidth(1);
            vert.setShowInLegend(false);
        }

        new SwingWrapper<>(chart).displayChart();
    }

    private static void buildChart(Function function, double leftBorder, double rightBorder, int size, double[] xData, double[] yData, XYChart chart) {
        for (XYSeries xySeries : chart.getSeriesMap().values()) {
            xySeries.setMarker(SeriesMarkers.CIRCLE);
        }

        leftBorder = Math.min(leftBorder, Arrays.stream(xData).min().getAsDouble());
        rightBorder = Math.max(rightBorder, Arrays.stream(xData).max().getAsDouble());
        createGraph(chart, function, leftBorder, rightBorder);

        chart.getStyler().setXAxisMin(leftBorder);
        chart.getStyler().setXAxisMax(rightBorder);

        chart.getStyler().setYAxisMax(Arrays.stream(yData).max().getAsDouble());
        chart.getStyler().setYAxisMin(Arrays.stream(yData).min().getAsDouble());

        chart.addSeries("Final answer", Arrays.copyOfRange(xData, size - 1, size), Arrays.copyOfRange(yData, size - 1, size)).setMarkerColor(Color.RED).setMarker(SeriesMarkers.CROSS);

        chart.getStyler().setMarkerSize(8);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
    }

    public static void createSystemGraph(ArrayList<Map<Double, Double>> mapArrayList, String name, String[] functions) {

    }


    public static void createChart(ArrayList<Map<Double, Double>> mapArrayList, String name, String[] functions) {
        XYChart chart = new XYChartBuilder().width(600).height(500).title(name).xAxisTitle("Xi").yAxisTitle("fi(x)").build();
        for (int j = 0, mapArrayListSize = mapArrayList.size(); j < mapArrayListSize; j++) {
            Map<Double, Double> result = mapArrayList.get(j);
            int size = result.size();
            double[] xData = new double[size];
            double[] yData = new double[size];
            int i = 0;
            for (Map.Entry<Double, Double> entry : result.entrySet()) {
                xData[i] = entry.getKey();
                yData[i] = entry.getValue();
                i++;
            }
            XYSeries series = chart.addSeries(functions[j], xData, yData);
            series.setMarker(SeriesMarkers.CIRCLE);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            series.setLineColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            series = chart.addSeries("Final answer" + j, Arrays.copyOfRange(xData, size - 1, size), Arrays.copyOfRange(yData, size - 1, size));
            series.setMarkerColor(Color.RED);
            series.setMarker(SeriesMarkers.CROSS);
        }
        chart.getStyler().setMarkerSize(8);
        new SwingWrapper<>(chart).displayChart();
    }

    public static void createExampleGraph(Function function, double leftBorder, double rightBorder) {
        XYChart chart = new XYChartBuilder().width(600).height(400).title("Your function").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        createGraph(chart, function, leftBorder, rightBorder);
        new SwingWrapper<>(chart).displayChart().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    private static void createGraph(XYChart chart, Function function, double leftBorder, double rightBorder) {
        ArrayList<Double> xGraph = new ArrayList<>();
        ArrayList<Double> yGraph = new ArrayList<>();
        double xVal = leftBorder;
        while (xVal <= rightBorder) {
            xGraph.add(xVal);
            double yVal = function.solve(xVal);
            if (Double.isFinite(yVal)) {
                yGraph.add(yVal);
            } else {
                yGraph.add(function.solve(xVal + 0.0001));
            }
            xVal += 0.1;
        }

        chart.addSeries("y(x)", xGraph, yGraph).setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line).setMarker(SeriesMarkers.NONE).setLineColor(Color.GREEN);

    }

    private static void fillArrays(Map<Double, Double> result, double[] xData, double[] yData) {
        int i = 0;
        for (Map.Entry<Double, Double> entry : result.entrySet()) {
            xData[i] = entry.getKey();
            yData[i] = entry.getValue();
            i++;
        }
    }
}
