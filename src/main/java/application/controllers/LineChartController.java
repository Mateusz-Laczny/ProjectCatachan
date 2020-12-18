package application.controllers;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineChartController {
    private final Map<String, XYChart.Series<String, Number>> seriesMap;
    private final LineChart<String, Number> chart;

    public LineChartController(LineChart<String, Number> chart, String XAxisLabel, List<String> seriesNames) {
        seriesMap = new HashMap<>();
        this.chart = chart;
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        chart.getXAxis().setAnimated(false);
        chart.getXAxis().setLabel(XAxisLabel);

        chart.getYAxis().setAnimated(false);

        for(String name : seriesNames) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(name);
            chart.getData().add(series);
            seriesMap.put(name, series);
        }
    }

    public void addSeriesEntry(String seriesName, Number xValue, Number yValue) throws NullPointerException {
        XYChart.Series<String, Number> series = seriesMap.get(seriesName);
        if(series.getData().size() > chart.getWidth()) {
            series.getData().remove(0);
        }

        series.getData().add(new XYChart.Data<>(xValue.toString(), yValue));
    }
}
