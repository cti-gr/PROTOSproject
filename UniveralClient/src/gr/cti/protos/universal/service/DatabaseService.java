/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.service;

import gr.cti.protos.universal.helpers.GeneralHelper;
import gr.cti.protos.universal.model.CalculationBean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author spyko
 */
public class DatabaseService {

    public static ResultSet getBlockedByDate(String fromDate, String toDate) {
       // Long l = System.currentTimeMillis();
        try {
            Connection conn = ConnectionHelper.getConnection();
            ResultSet rs = null;
            if(fromDate!=null && toDate!=null){
                PreparedStatement q = conn.prepareStatement("SELECT * FROM firewallLog WHERE date>=? and date<=? order by date,time;");
                q.setString(1, fromDate);
                q.setString(2, toDate);
                rs = q.executeQuery();
            }
            else{
               Statement stmt = conn.createStatement();
               rs = stmt.executeQuery("SELECT * FROM firewallLog order by date,time;");
            }
           // System.out.println("TIME FOR EXECUTING QUERY:::: " + (System.currentTimeMillis() - l));
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    
    public static ResultSet getIncidentsByDate(String fromDate, String toDate)
    {
        
        
        return null;
        
    }
    public static CalculationBean getBlockedLive(String fromDate, String toDate, String fromTime ,String toTime) {
        Long l = System.currentTimeMillis();
        PreparedStatement stmt = null; 
        PreparedStatement stmt1 =  null; 
        ResultSet rs = null;
        CalculationBean cb = null;
        try {
            stmt = ConnectionHelper.getStmt();
            stmt1 = ConnectionHelper.getStmt1();
            if(fromDate.equals(toDate)){
                stmt.setString(1, fromDate);
                stmt.setString(2, fromTime);
                stmt.setString(3, toTime);
                rs = stmt.executeQuery();
                cb = new CalculationBean();
                cb.setIncidents(rs.getDouble("count"));
            }
            else{
                stmt1.setString(1, fromDate);
                stmt1.setString(2, fromTime);
                stmt1.setString(3, toDate);
                stmt1.setString(4, toTime);
                rs = stmt1.executeQuery();
                cb = new CalculationBean();
                cb.setIncidents(rs.getDouble("count"));
            }
//            System.out.println("TIME FOR EXECUTING QUERY:::: " + (System.currentTimeMillis() - l));
            return cb;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            try {
                if(rs!=null){
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
        
    }
    
        
        
        
    }
    
    
