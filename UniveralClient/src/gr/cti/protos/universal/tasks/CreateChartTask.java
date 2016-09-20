/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import gr.cti.protos.universal.helpers.GeneralHelper;
import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.service.DatabaseService;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import org.joda.time.DateTime;

/**
 *
 * @author wifferson
 */
public class CreateChartTask extends Task<List<XYChart.Series<String, Number>>>{
    
    private final String fromDate;
    private final String toDate;
    private final int granularity;
    private final boolean calculateDerivatives;
    
    private double previousFirstDerivative;
    private double previousIncidents;
    
    public CreateChartTask(String fromDate, String toDate, int granularity,boolean calculateDerivatives) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.granularity = granularity;
        this.calculateDerivatives = calculateDerivatives;
    }
    
    @Override
    protected List<XYChart.Series<String, Number>> call() throws Exception {
        return getIncidents(granularity, fromDate, toDate,calculateDerivatives);//To change body of generated methods, choose Tools | Templates.
    }
    
    private List<XYChart.Series<String, Number>> getIncidents(int granularity, String fromDate, String toDate, boolean calculateDerivatives) {
        Long st = System.currentTimeMillis();
        DateTime start = GeneralHelper.getDateFromString(fromDate, "00:00:00");
        DateTime end = GeneralHelper.getDateFromString(toDate, "23:59:59");
        DateTime current = start.plusSeconds(granularity);
        
        Long times = (end.getMillis() - start.getMillis())/(granularity*1000);
        int counter = 0;
        List<XYChart.Series<String, Number>> allSeries = new ArrayList<>();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        XYChart.Series<String, Number> maliciousActivity = new XYChart.Series<>();
        XYChart.Series<String, Number> epidemicRate = new XYChart.Series<>();
        
        //List<XYChart.Data<String, Number>> allDatas = new ArrayList<>();
        while(current.isBefore(end)){
            String fromTimeStr = GeneralHelper.getTimeToString(start);
            String toTimeStr = GeneralHelper.getTimeToString(current);
            String fromDateStr = GeneralHelper.getDateToString(start);
            String toDateStr = GeneralHelper.getDateToString(current);
            CalculationBean cb = DatabaseService.getBlockedLive(fromDateStr, toDateStr, fromTimeStr, toTimeStr);
            Number currentIncidents;
            Number aNumber;
            currentIncidents = cb.getIncidents();
            if(calculateDerivatives == true){
                if(counter == 0){
                    previousIncidents = cb.getIncidents();
                }
                else if(counter == 1){
                    previousFirstDerivative = calculateDerivative(cb.getIncidents(), previousIncidents);
                    previousIncidents = cb.getIncidents();
                }
                else{
                     double currentFirstDerivative = calculateDerivative(cb.getIncidents(), previousIncidents);
                     double currentSecondDerivative = calculateDerivative(currentFirstDerivative, previousFirstDerivative);
                     previousFirstDerivative = currentFirstDerivative;
                     previousIncidents = cb.getIncidents();
                     cb.setFirstDerivative(currentFirstDerivative);
                     cb.setSecondDerivative(currentSecondDerivative);
                     aNumber = currentFirstDerivative;
                     XYChart.Data<String, Number> datoF = new XYChart.Data<>(GeneralHelper.getDateTimeToString(current),aNumber);
                     maliciousActivity.getData().add(datoF);
                     aNumber = currentSecondDerivative;
                     XYChart.Data<String, Number> datoS = new XYChart.Data<>(GeneralHelper.getDateTimeToString(current),aNumber);
                     epidemicRate.getData().add(datoS);
                }
            }
            XYChart.Data<String, Number> dato = new XYChart.Data<>(GeneralHelper.getDateTimeToString(current),currentIncidents);
            series.getData().add(dato);
            
            series.setName("Incidents");
            
            start = start.plusSeconds(granularity);
            current = current.plusSeconds(granularity);
            counter++;
            updateProgress(counter, times);
//            System.out.println(currentIncidents);
         
            
            
        }
        System.out.println("OVERALLL::" + (System.currentTimeMillis() - st));
      
        if(calculateDerivatives == true){
            maliciousActivity.setName("Malicious Activity");
            epidemicRate.setName("Epidemic Rate");
            allSeries.add(maliciousActivity);
            allSeries.add(epidemicRate);
        }
        else{
            allSeries.add(series);
        }
        return allSeries;
    }
    
    private double calculateDerivative(double current, double previous){
        if(previous != 0.0){
            return (current-previous)/previous;
        }
        else{
            return 0.0;
        }
    }
    
    
}
