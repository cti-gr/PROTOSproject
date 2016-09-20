/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.calculation;

import gr.cti.protos.universal.helpers.GeneralHelper;
import gr.cti.protos.universal.model.TopBean;
import gr.cti.protos.universal.service.DatabaseService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 *
 * @author spyko
 */
public class LocalCalculations {

//     public static void getIncidents(int granularity, String fromDate, String toDate) {
//          DateTime start = GeneralHelper.getDateFromString(fromDate, "00:00:00");
//          DateTime end = GeneralHelper.getDateFromString(toDate, "23:59:59");
//          DateTime current = start.plusSeconds(granularity);
//          
//          while(current.isBefore(end)){
//             
//              String fromTimeStr = GeneralHelper.getTimeToString(start);
//              String toTimeStr = GeneralHelper.getTimeToString(current);
//              String fromDateStr = GeneralHelper.getDateToString(start);
//              String toDateStr = GeneralHelper.getDateToString(current);
//              
//              ResultSet rs = DatabaseService.getBlockedLive(fromDateStr, toDateStr, fromTimeStr, toTimeStr);
//              double currentIncidents;
//              try {
//                   currentIncidents = rs.getDouble("count");
//              } catch (SQLException ex) {
//                  Logger.getLogger(LocalCalculations.class.getName()).log(Level.SEVERE, null, ex);
//              }
//              //String 
//              start = start.plusSeconds(granularity);
//              current = current.plusSeconds(granularity);
//          }
//         //DatabaseService.getBlockedLive(toDate, fromDate, toDate)
//         
//     }
    public static HashMap<DateTime, Number> getIncidentsTimeSeries(int granularity, String fromDate, String toDate) {
        Long startT = System.currentTimeMillis();
        ResultSet rs = DatabaseService.getBlockedByDate(fromDate, toDate);
        System.out.println("TIME TAKEN: " + (System.currentTimeMillis() - startT));
        startT = System.currentTimeMillis();
        List<DateTime> timeseries = GeneralHelper.createDatesFromRS(rs);
        System.out.println("TIME TAKEN DATES: " + (System.currentTimeMillis() - startT));

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
 startT = System.currentTimeMillis();
        while (end.isBefore(endDateTime) && i < timeseries.size()) {
            while (i < timeseries.size() && interval.contains(timeseries.get(i))) {
                count++;
                i++;
            }
            //incidentsPerGranularity.add(count);
            incidentsWithTime.put(end, count);
            count = 0;
            start = start.plusSeconds(granularity);
            end = end.plusSeconds(granularity);
            interval = new Interval(start, end);
        }
        System.out.println("TIME TAKEN WHILES: " + (System.currentTimeMillis() - startT));
        return incidentsWithTime;
    }

    public static HashMap<DateTime, Number> getFirstDerivative(HashMap<DateTime, Number> incidents) {
        boolean firstElement = true;
        double previous = 0;
        HashMap<DateTime, Number> firstDerivativeMap = new LinkedHashMap<>();
        for(Map.Entry<DateTime,Number> entry:incidents.entrySet()){
            if(firstElement){
                firstElement = false;
                previous = entry.getValue().doubleValue();
                continue;
            }
            else{
                double current = entry.getValue().doubleValue();
                double firstDerivate = (current - previous)/previous;
                firstDerivativeMap.put(entry.getKey(), firstDerivate);
                previous = current;
            }
        }
        return firstDerivativeMap;
    }
    
    public static HashMap<DateTime, Number> getSecondDerivative(HashMap<DateTime,Number> firstDerivatives){
        boolean firstElement = true;
        double previous = 0.0;
         HashMap<DateTime, Number> secondDerivativeMap = new LinkedHashMap<>();
         for(Map.Entry<DateTime,Number> entry:firstDerivatives.entrySet()){
              if(firstElement){
                firstElement = false;
                previous = entry.getValue().doubleValue();
                continue;
            }
            else{
                double current = entry.getValue().doubleValue();
                double secondDerivative = (current - previous)/previous;
                secondDerivativeMap.put(entry.getKey(), secondDerivative);
                previous = current;
            }
         }
        return secondDerivativeMap;
    }
    
    public static TopBean getTop(String from, String to){
        ResultSet rs = DatabaseService.getBlockedByDate(from, to);
        HashMap<String, Integer> protocolMap = new HashMap<>();
        HashMap<String, Integer> portMap = new HashMap<>();
        HashMap<String, Integer> ipMap = new HashMap<>();
        try {
            while(rs.next()){
                if(protocolMap.containsKey(rs.getString("protocol"))){
                    Integer protocolc = protocolMap.get(rs.getString("protocol"));
                    protocolMap.put(rs.getString("protocol"),++protocolc);
                }
                else{
                    protocolMap.put(rs.getString("protocol"),1);
                }
                
                if(portMap.containsKey(rs.getString("dstport"))){
                    Integer portc = portMap.get(rs.getString("dstport"));
                    portMap.put(rs.getString("dstport"), ++portc);
                }
                else{
                    portMap.put(rs.getString("dstport"),1);
                }
                
                if(ipMap.containsKey(rs.getString("srcip"))){
                    Integer ipc = ipMap.get(rs.getString("srcip"));
                    ipMap.put(rs.getString("srcip"), ++ipc);
                }
                else{
                    ipMap.put(rs.getString("srcip"),1);
                }
                
                
            };
            TopBean topBean = new TopBean();
            topBean.setIps(ipMap);
            topBean.setPorts(portMap);
            topBean.setProtocols(protocolMap);
            return topBean;
        } catch (SQLException ex) {
            Logger.getLogger(LocalCalculations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
