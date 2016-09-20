/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.model;

import java.util.HashMap;

/**
 *
 * @author wifferson
 */
public class TopBean {
    
    private HashMap<String, Integer> protocols;
    private HashMap<String, Integer> ports;
    private HashMap<String, Integer> ips;

    public HashMap<String, Integer> getProtocols() {
        return protocols;
    }

    public void setProtocols(HashMap<String, Integer> protocols) {
        this.protocols = protocols;
    }

    public HashMap<String, Integer> getPorts() {
        return ports;
    }

    public void setPorts(HashMap<String, Integer> ports) {
        this.ports = ports;
    }

    public HashMap<String, Integer> getIps() {
        return ips;
    }

    public void setIps(HashMap<String, Integer> ips) {
        this.ips = ips;
    }
    
    
}
