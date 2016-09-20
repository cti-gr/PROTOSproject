/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.concurrent.Task;
import org.joda.time.DateTime;

/**
 *
 * @author wifferson
 */
public class CreateDerivativesTimeseriesTask extends Task<HashMap<DateTime, Number>>{
    private final HashMap<DateTime, Number> timeSeries;

    public CreateDerivativesTimeseriesTask(HashMap<DateTime, Number> timeSeries) {
        this.timeSeries = timeSeries;
    }

    @Override
    protected HashMap<DateTime, Number> call() throws Exception {
   return getDerivativesTimeseries(timeSeries);
    }
    
  private  HashMap<DateTime,Number> getDerivativesTimeseries(HashMap<DateTime, Number> timeseries)
     {
        //System.out.println(getProgress());
       boolean firstElement = true;
        double previous = 0;
        HashMap<DateTime, Number> derivativeMap = new LinkedHashMap<>();
        for(Map.Entry<DateTime,Number> entry:timeseries.entrySet()){
            if(firstElement){
                firstElement = false;
                previous = entry.getValue().doubleValue();
                continue;
            }
            else{
                double current = entry.getValue().doubleValue();
                double firstDerivate = (current - previous)/previous;
                if(Double.isInfinite(firstDerivate) || Double.isNaN(firstDerivate)){
                    firstDerivate = 0.0;
                }
                derivativeMap.put(entry.getKey(), firstDerivate);
                previous = current;
            }
        }
        return derivativeMap;
    }
  
  public HashMap<DateTime, Number> getTimeSeries(){
      return timeSeries;
  }
    
}

    
