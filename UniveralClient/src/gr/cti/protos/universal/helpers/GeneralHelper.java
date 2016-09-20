/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author spyko
 */
public class GeneralHelper {
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    //private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");
    
    
    public static Date getStringAsDate(String strDate) {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(strDate);
        } catch (ParseException ex) {
            Logger.getLogger(GeneralHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    
    public static DateTime getDateFromString(String dateStr, String timeStr) {
        String dateTime = dateStr + " " + timeStr;
        DateTime dateTimeObj = null;
       
           //  Long l = System.nanoTime();
            dateTimeObj = DATE_TIME_FORMATTER.parseDateTime(dateTime);
           //  System.out.println("GET DATE FROM STRING TIME:" + (System.nanoTime() - l));
        
        return dateTimeObj;
    }
    
     public static DateTime getDateFromString(String dateTimeStr) {
        String dateTime = dateTimeStr;
        DateTime dateTimeObj = DATE_TIME_FORMATTER.parseDateTime(dateTime);
        return dateTimeObj;
    }
    
    public static List<DateTime> createDatesFromRS(ResultSet rs) {
        List<DateTime> timeseries = new ArrayList<>();
        try {
            while (rs.next()) {
                DateTime dateTime = getDateFromString(rs.getString("date"), rs.getString("time"));
                //DateTime dateTime = new DateTime(date);
                timeseries.add(dateTime);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeneralHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return timeseries;
    }
    
    public static <K ,V> void displayMap(Map<K, V> mapToDisplay){
        for(Map.Entry<K, V> entry: mapToDisplay.entrySet()){
            System.out.println(entry.getKey() + ":::" + entry.getValue());
        }
        System.out.println("--------------------------------------------------");
    }
    
    public static Map<String, Integer> getTopByValue(Map<String, Integer> mapToSort, int top){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(mapToSort.entrySet());
        
        Collections.sort(list,new ValueComparator());
        Collections.reverse(list);
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        
        for(int i=0;i<top;i++){
            if(i<list.size()){
                sortedMap.put(list.get(i).getKey(), list.get(i).getValue());
            }
            else{
                break;
            }
            
        }
        
        return sortedMap;
    }
    
    public static String getDateToString(DateTime dateTime){
        if(dateTime==null){
            return null;
        }
        else{
            return DATE_FORMATTER.print(dateTime);
        }
    }
    
     public static String getDateToString(Date date){
        if(date==null){
            return null;
        }
        else{
            return DATE_FORMAT.format(date);
        }
    }
    
    public static String getTimeToString(DateTime dateTime){
         if(dateTime==null){
            return null;
        }
        else{
            return TIME_FORMATTER.print(dateTime);
        } 
    }
    
    public static String getDateTimeToString(DateTime dateTime){
         if(dateTime==null){
            return null;
        }
        else{
            return DATE_TIME_FORMATTER.print(dateTime);
        } 
    }
}
