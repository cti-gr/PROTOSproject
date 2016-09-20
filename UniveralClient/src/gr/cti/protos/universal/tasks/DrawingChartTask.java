/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import org.joda.time.DateTime;

/**
 *
 * @author wifferson
 */
public class DrawingChartTask extends Task<ObservableList<XYChart.Series<String, Number>>>{
    private final Map<DateTime, Number> timeseriesMap;
    private final String name;

    public DrawingChartTask(Map<DateTime, Number> timeseriesMap, String name) {
        this.timeseriesMap = timeseriesMap;
        this.name = name;
    }
    
    @Override
    protected ObservableList<XYChart.Series<String, Number>> call() throws Exception {
       return createXyChart(timeseriesMap, name); //To change body of generated methods, choose Tools | Templates.
    }
    
    private ObservableList<XYChart.Series<String, Number>> createXyChart(
            Map<DateTime, Number> timeseriesMap, String name) {
        Long st = System.currentTimeMillis();
        List<XYChart.Data<String, Number>> allDatas = new ArrayList<>(); //= new Property
        //series.setName(name);
        int i = 0;
        for (Map.Entry<DateTime, Number> entry : timeseriesMap.entrySet()) {
            i++;
            double progress = 75.0 + (((double)i/(double)timeseriesMap.size())*25);
            updateProgress(progress,100.00);
            XYChart.Data dato = new XYChart.Data<>(entry.getKey().toString(), entry.getValue());
            allDatas.add(dato);
            //System.out.println("EDOOOO");
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>(name,FXCollections.observableArrayList(allDatas));
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(series); 
        System.out.println("OVERALL:::" + (System.currentTimeMillis()-st));
        return chartData;
    }
    
}
