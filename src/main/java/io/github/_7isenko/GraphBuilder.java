package io.github._7isenko;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 7isenko
 */
public class GraphBuilder {

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
        chart.getStyler().setMarkerSize(16);
        new SwingWrapper<>(chart).displayChart();
    }

    public static void createChart(Map<Double, Double> result, String name) {
        int size = result.size();
        double[] xData = new double[size];
        double[] yData = new double[size];
        int i = 0;
        for (Map.Entry<Double, Double> entry : result.entrySet()) {
            xData[i] = entry.getKey();
            yData[i] = entry.getValue();
            i++;
        }

        XYChart chart = QuickChart.getChart(name, "X", "Y", "y(x)", xData, yData);
        for (XYSeries xySeries : chart.getSeriesMap().values()) {
            xySeries.setMarker(SeriesMarkers.CIRCLE);
        }

        chart.addSeries("Final answer", Arrays.copyOfRange(xData, size - 1, size), Arrays.copyOfRange(yData, size - 1, size)).setMarkerColor(Color.RED);

        chart.getStyler().setMarkerSize(16);
        new SwingWrapper<>(chart).displayChart();
    }
}
