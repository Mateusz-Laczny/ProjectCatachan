package application.controllers.charts;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class BarChartController {
    private final XYChart.Series<String, Number> series;

    public BarChartController(BarChart<String, Number> chart) {
        series = new XYChart.Series<>();
        series.setName("Number of genes of given type");
        chart.setAnimated(false);

        chart.getXAxis().setAnimated(false);
        chart.getXAxis().setLabel("Gene");

        chart.getYAxis().setAnimated(false);
        chart.getData().add(series);
    }

    public void updateSeries(Map<String, Number> values) {
        series.getData().clear();


        for(String name : values.keySet()) {
            series.getData().add(new XYChart.Data<>(name, values.get(name)));
        }
    }
}
