/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.tasks.ServerTask;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author spyko
 */
public class ServerService extends Service<CalculationBean> {
    
    public static URL url;
    public static ObjectMapper mapper;
    
    public ServerService(){
        mapper = new ObjectMapper();
        try {
            url = new URL("http://protos.cti.gr/promis/returnLiveData.php");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected Task<CalculationBean> createTask() {
            return new ServerTask();
    }

}
