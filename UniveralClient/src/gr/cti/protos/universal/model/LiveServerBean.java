/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.model;

import java.util.List;

/**
 *
 * @author spyko
 */
public class LiveServerBean {
    
    private List<List<Object>> rate1;
    private List<List<Object>> rate2;
    private List<String> timeseries;
    private List<List<Object>> sumTotal;
    private List<List<String>> clients;

    public List<List<Object>> getRate1() {
        return rate1;
    }

    public void setRate1(List<List<Object>> rate1) {
        this.rate1 = rate1;
    }

    public List<List<Object>> getRate2() {
        return rate2;
    }

    public void setRate2(List<List<Object>> rate2) {
        this.rate2 = rate2;
    }

    public List<String> getTimeseries() {
        return timeseries;
    }

    public void setTimeseries(List<String> timeseries) {
        this.timeseries = timeseries;
    }

    public List<List<Object>> getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(List<List<Object>> sumTotal) {
        this.sumTotal = sumTotal;
    }

    public List<List<String>> getClients() {
        return clients;
    }

    public void setClients(List<List<String>> clients) {
        this.clients = clients;
    }
}
