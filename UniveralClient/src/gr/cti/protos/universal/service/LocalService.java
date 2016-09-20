/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.service;

import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.tasks.LiveTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author wifferson
 */
public class LocalService extends Service<CalculationBean> {
    
    public static Double previousIncidents = null;
    public static Double previousFirstDerivative = null;

    @Override
    protected Task<CalculationBean> createTask() {
         return new LiveTask();
    }
    
}
