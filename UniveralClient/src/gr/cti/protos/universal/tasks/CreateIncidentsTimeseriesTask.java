/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import gr.cti.protos.universal.helpers.GeneralHelper;
import gr.cti.protos.universal.service.DatabaseService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import notsofatclient.MainController;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 *
 * @author wifferson
 */
public class CreateIncidentsTimeseriesTask extends Task<HashMap<DateTime, Number>>{

     private final String fromDate;
    private final String toDate;
    private final int granularity;

    public CreateIncidentsTimeseriesTask(int granularity, String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.granularity = granularity;
    }
    
    @Override
    protected HashMap<DateTime, Number> call() throws Exception {
        return getIncidentsTimeSeries(granularity, fromDate, toDate); //To change body of generated methods, choose Tools | Templates.
    }
    
     private  HashMap<DateTime,Number> getIncidentsTimeSeries(int granularity, String fromDate, String toDate)
     {
        //System.out.println(getProgress());
        Long startT = System.currentTimeMillis();
        updateMessage("Fetching from DB...");
        
        ResultSet rs = DatabaseService.getBlockedByDate(fromDate, toDate);
        updateProgress(25.0, 100.00);
      //  System.out.println("TIME TAKEN: " + (System.currentTimeMillis() - startT));
       // startT = System.currentTimeMillis();
        
        updateMessage("Calculating Malware Activity Timeseries...");
        List<String> datetimesString = new ArrayList<>();
        try {
            while(rs.next()){
                String datetimeString = rs.getString("date")+ " " +  rs.getString("time");
                datetimesString.add(datetimeString);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CreateIncidentsTimeseriesTask.class.getName()).log(Level.SEVERE, null, ex);
        }
         List<DateTime> timeseries = new ArrayList<>();
         
         double counter = 0;
        for(String datetimeString:datetimesString){
            timeseries.add(GeneralHelper.getDateFromString(datetimeString));
            counter++;
            double progress = 25.0 + ((counter/(double)datetimesString.size())*25);
            updateProgress(progress  , 100.00);
        }
        
        if(timeseries.isEmpty()){
            updateMessage("No available data for this period.");
             try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
            return null;
        }
        else{
             DateTime firstDateTime = timeseries.get(0);
        DateTime lastDateTime = timeseries.get(timeseries.size() - 1);
        DateTime endDateTime = new DateTime(lastDateTime.getYear(), lastDateTime.getMonthOfYear(), lastDateTime.getDayOfMonth(), 23, 59, 59);
        DateTime start = new DateTime(firstDateTime.getYear(), firstDateTime.getMonthOfYear(), firstDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime end = (new DateTime(firstDateTime.getYear(), firstDateTime.getMonthOfYear(), firstDateTime.getDayOfMonth(), 0, 0, 0)).plusSeconds(granularity);
        Interval interval = new Interval(start, end);
       
        
        //ArrayList<Integer> incidentsPerGranularity = new ArrayList<Integer>();
        HashMap<DateTime,Number> incidentsWithTime = new LinkedHashMap<>();
             
        int count = 0;
        int i = 0;
         int thecounter = 0;
       // startT = System.currentTimeMillis();
        updateMessage("Calculating Malware Activity...");
      
        while (end.isBefore(endDateTime) && i < timeseries.size()) {
            while (i < timeseries.size() && interval.contains(timeseries.get(i))) {
                count++;
                i++;
                double progress = 50.0 + (((double)i/(double)timeseries.size())*25);
                updateProgress(progress,100.00);
            }
            incidentsWithTime.put(end, count);
            count = 0;
            start = start.plusSeconds(granularity);
            end = end.plusSeconds(granularity);
            interval = new Interval(start, end);
                
           thecounter++;
        }
      System.out.println("thecounter" + thecounter);
        System.out.println("TIME TAKEN WHILES: " + (System.currentTimeMillis() - startT));
       // updateProgress(0, 0);
        
        
        //SeriesHelper.createXyChart((Map<DateTime, Number>)t.getSource().getValue(),"incidents");
        updateMessage("Drawing Chart...");
        //return incidentsWithTime
        return incidentsWithTime;
        }
   // return null;
}
}

