/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.helpers;

import javafx.scene.input.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.TranslateTransitionBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.joda.time.DateTime;


/**
 *
 * @author spyko
 */
public class SeriesHelper {

    public static ObservableList<XYChart.Series<String, Number>> createXyChart(
            final Map<DateTime, Number> timeseriesMap, String name) {
        final XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Activity");
        for (final Map.Entry<DateTime, Number> entry : timeseriesMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        final ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();

        chartData.add(series);
        return chartData;
    }
    
     public static XYChart.Series<String, Number> createXyChartNotObservable(
            final Map<DateTime, Number> timeseriesMap, String name) {
        final XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (final Map.Entry<DateTime, Number> entry : timeseriesMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        return series;
    }
     
     public static ObservableList<PieChart.Data> createPieData(Map<String, Integer> data){
         ObservableList<PieChart.Data> datas = FXCollections.observableArrayList();
         for(Map.Entry<String, Integer> entry:data.entrySet()){
             PieChart.Data aData = new PieChart.Data(entry.getKey(),entry.getValue());   
             datas.add(aData);
         }
         return datas;
         
     }
 //                    chartpie effect    
//     public static void addMouseHoverAnimation(PieChart chart){
//         for(PieChart.Data aData:chart.getData()){
//           aData.getNode().setOnMouseEntered(new MouseHoverAnimation(aData, chart));
//           aData.getNode().setOnMouseExited(new MouseExitAnimation());
//         }
//     }
//     
//  static class MouseHoverAnimation implements EventHandler<MouseEvent> {
//  static final Duration ANIMATION_DURATION = new Duration(1000);
//  static final double ANIMATION_DISTANCE = 0.10;
//  private double cos;
//  private double sin;
//  private PieChart chart;
//  
// 
//  public MouseHoverAnimation(PieChart.Data d, PieChart chart) {
//   // this.protocolPie = protocolPie;
//      this.chart = chart;
//    double start = 0;
//    double angle = calcAngle(d);
//    for( PieChart.Data tmp : chart.getData() ) {
//      if( tmp == d ) {
//         break;
//      }
//      start += calcAngle(tmp);
//    }
// 
//    cos = Math.cos(Math.toRadians(0 - start - angle / 2));
//    sin = Math.sin(Math.toRadians(0 - start - angle / 2));
//  }
// 
//  @Override
//  public void handle(MouseEvent arg0) {
//    Node n = (Node) arg0.getSource();
// 
//    double minX = Double.MAX_VALUE;
//    double maxX = Double.MAX_VALUE * -1;
//             
//    for( PieChart.Data d : chart.getData() ) {
//      minX = Math.min(minX, d.getNode().getBoundsInParent().getMinX());
//      maxX = Math.max(maxX, d.getNode().getBoundsInParent().getMaxX());
//    }
// 
//    double radius = maxX - minX;
//    TranslateTransitionBuilder.create().toX((radius *  ANIMATION_DISTANCE) * cos).toY((radius *  ANIMATION_DISTANCE) * sin).duration(ANIMATION_DURATION).node(n).build().play();
//  }
//         
//  private static double calcAngle(PieChart.Data d) {
//    double total = 0;
//    for( PieChart.Data tmp : d.getChart().getData() ) {
//      total += tmp.getPieValue();
//    }
//             
//    return 360 * (d.getPieValue() / total); 
//  }
//}
//     
//     static class MouseExitAnimation implements EventHandler<MouseEvent> {
//			@Override
//			public void handle(MouseEvent event) {
//				TranslateTransitionBuilder.create().toX(0).toY(0).duration(new Duration(1000)).node((Node) event.getSource()).build().play();;
//			}
	}

