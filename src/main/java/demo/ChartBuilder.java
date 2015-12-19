package demo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ChartBuilder {

    private static final String BACKOFFS_DELAY_CHART_TITLE = "Backoffs delay chart";

    public void processBackoffs(Map<Integer,Long> backoffs) {
        saveChartToFile(createChart(backoffs));
    }

    private JFreeChart createChart(Map<Integer,Long> backoffs){
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(BACKOFFS_DELAY_CHART_TITLE);

        for (Map.Entry<Integer,Long> entry : backoffs.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        dataset.addSeries(series);

        return ChartFactory.createXYLineChart(BACKOFFS_DELAY_CHART_TITLE,
                "Attempt #", "delay", dataset);
    }

    private void saveChartToFile(JFreeChart chart){
        File imageFile = new File("chart.png");
        int width = 640;
        int height = 480;

        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}