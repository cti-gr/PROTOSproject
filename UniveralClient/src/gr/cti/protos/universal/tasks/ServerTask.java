/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.tasks;

import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.model.LiveServerBean;
import gr.cti.protos.universal.service.ServerService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import org.joda.time.DateTime;

/**
 *
 * @author spyko
 */
public class ServerTask extends Task<CalculationBean>{

    @Override
    protected CalculationBean call() throws Exception {
        return retrieveActivity();
    }
    
    private CalculationBean retrieveActivity(){
        CalculationBean cb = new CalculationBean();
        try {
            System.out.println("TREHO");
            LiveServerBean lsb = ServerService.mapper.readValue(ServerService.url.openStream(), LiveServerBean.class);
            System.out.println("TREHO1111111");
            
            if(lsb.getRate1().get(0).get(1)!= null){
                cb.setFirstDerivative(Double.parseDouble((String)(lsb.getRate1().get(0).get(1))));
            }
            else{
                cb.setFirstDerivative(0.0);
            }
            if(lsb.getRate2().get(0).get(1)!=null){
                 cb.setSecondDerivative(Double.parseDouble((String)(lsb.getRate2().get(0).get(1))));
            }
            else{
                cb.setSecondDerivative(0.0);
            }
            if(lsb.getSumTotal().get(0).get(1)!=null){
                  cb.setIncidents(Double.parseDouble((String)(lsb.getSumTotal().get(0).get(1))));
            }
            else{
                cb.setIncidents(0.0);
            }
            cb.setDateTime(new DateTime());
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ServerTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cb;
    }

    
}
