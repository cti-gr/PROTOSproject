/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.service.DatabaseService;
import gr.cti.protos.universal.service.LocalService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author wifferson
 */
public class LiveTask extends Task<CalculationBean>{
    
    private final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
    
    @Override
    protected CalculationBean call() throws Exception {
        return calculateLive();
    }
    
    private CalculationBean calculateLive(){
        CalculationBean aBean = new CalculationBean();
        if(LocalService.previousIncidents == null && LocalService.previousFirstDerivative == null){
            DateTime now = new DateTime();
            DateTime nowMinus30 = now.minusSeconds(30);
            DateTime nowMinus60 = now.minusSeconds(60);
            DateTime nowMinus90 = now.minusSeconds(90);
            double previousIncidents = 0;
            CalculationBean rs = DatabaseService.getBlockedLive(dateFormatter.print(nowMinus90),dateFormatter.print(nowMinus60), timeFormatter.print(nowMinus90), timeFormatter.print(nowMinus60));
                previousIncidents = rs.getIncidents();
                rs = DatabaseService.getBlockedLive(dateFormatter.print(nowMinus60),dateFormatter.print(nowMinus30), timeFormatter.print(nowMinus60), timeFormatter.print(nowMinus30));
                double currentIncidents = 0;
                currentIncidents = rs.getIncidents();
                double previousFirstDerivative = (currentIncidents - previousIncidents)/previousIncidents;
                 if(Double.isInfinite(previousFirstDerivative) || Double.isNaN(previousFirstDerivative)){
                    previousFirstDerivative = 0.0;
                }
                previousIncidents = currentIncidents;
                rs = DatabaseService.getBlockedLive(dateFormatter.print(nowMinus30),dateFormatter.print(now), timeFormatter.print(nowMinus30), timeFormatter.print(now));
                currentIncidents = rs.getIncidents();
                double currentFirstDerivative =  (currentIncidents - previousIncidents)/previousIncidents;
                 if(Double.isInfinite(currentFirstDerivative) || Double.isNaN(currentFirstDerivative)){
                    currentFirstDerivative = 0.0;
                }
                double currentSecondDerivative = (currentFirstDerivative - previousFirstDerivative)/previousFirstDerivative;
               
                if(Double.isInfinite(currentSecondDerivative) || Double.isNaN(currentSecondDerivative)){
                    currentSecondDerivative = 0.0;
                }
                LocalService.previousFirstDerivative = currentFirstDerivative;
                LocalService.previousIncidents = currentIncidents;
                aBean.setFirstDerivative(currentFirstDerivative);
                aBean.setSecondDerivative(currentSecondDerivative);
                aBean.setDateTime(now);
                aBean.setIncidents(currentIncidents);
        }
        else{
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LiveTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateTime now = new DateTime();
            DateTime nowMinus30 = now.minusSeconds(30);
            CalculationBean rs = DatabaseService.getBlockedLive(dateFormatter.print(now),dateFormatter.print(nowMinus30), timeFormatter.print(nowMinus30), timeFormatter.print(now));
            double currentIncidents = 0;
                currentIncidents = rs.getIncidents();
                double currentFirstDerivative =  (currentIncidents - LocalService.previousIncidents)/LocalService.previousIncidents;
                if(Double.isInfinite(currentFirstDerivative) || Double.isNaN(currentFirstDerivative)){
                    currentFirstDerivative = 0.0;
                }
                double currentSecondDerivative = (currentFirstDerivative -  LocalService.previousFirstDerivative)/ LocalService.previousFirstDerivative;
                if(Double.isInfinite(currentSecondDerivative) || Double.isNaN(currentSecondDerivative)){
                    currentSecondDerivative = 0.0;
                }
                LocalService.previousFirstDerivative = currentFirstDerivative;
                LocalService.previousIncidents = currentIncidents;
                aBean.setFirstDerivative(currentFirstDerivative);
                aBean.setSecondDerivative(currentSecondDerivative);
                aBean.setDateTime(now);
                aBean.setIncidents(currentIncidents);
        }
        return aBean;
    }
    
}


