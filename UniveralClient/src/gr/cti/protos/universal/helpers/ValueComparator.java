/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.helpers;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author wifferson
 */
public class ValueComparator implements Comparator<Map.Entry<String, Integer>>{

    @Override
    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
        return o1.getValue().compareTo(o2.getValue());
        
    }
    
    
}
