/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.model.enums;

/**
 *
 * @author wifferson
 */
public enum Top {
    TOP5(5, "Top 5"),
    TOP10(10, "Top 10"),
    TOP100(100, "Top 100");
    
    private int top;
    private String desc;
    
    private Top(int top, String desc){
        this.top = top;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

    public int getTop() {
        return top;
    }
    
}
