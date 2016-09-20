/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.model;

import org.joda.time.DateTime;

/**
 *
 * @author wifferson
 */
public class CalculationBean {
    private double firstDerivative;
    private double secondDerivative;
    private double incidents;
    private DateTime dateTime;

    public double getFirstDerivative() {
        return firstDerivative;
    }

    public void setFirstDerivative(double firstDerivative) {
        this.firstDerivative = firstDerivative;
    }

    public double getSecondDerivative() {
        return secondDerivative;
    }

    public void setSecondDerivative(double secondDerivative) {
        this.secondDerivative = secondDerivative;
    }

    public double getIncidents() {
        return incidents;
    }

    public void setIncidents(double incidents) {
        this.incidents = incidents;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    
}
