/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.model.enums;

/**
 *
 * @author wifferson
 */
public enum Granularity {
    DAILY(86400, "Daily"),
    HOURLY(3600, "Hourly"),
    SEC15(15,"15 Seconds"),
    SEC30(30,"30 Seconds");
    
    private int secs;
    private String desc;
    
    private Granularity(int secs, String desc){
        this.secs = secs;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
    
    public int getSecs(){
        return secs;
    }
    
    
}
