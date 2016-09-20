/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.cti.protos.universal.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author spyko
 */
public class ConnectionHelper {

    private static Connection connection = null;
    //private static String jdbcPath = "jdbc:sqlite:logdb.db";
    private static String jdbcPath = "jdbc:sqlite:C:\\Program Files\\Firelog\\firelog.db";
    private static PreparedStatement stmt = null;
    private static PreparedStatement stmt1 = null;
    
    public static Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(jdbcPath);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading driver: " + e);
        }
        return connection;
    }
    
    public static Connection getConnection(){
        if(connection==null){
           createConnection();
        }
        return connection;
    }
    
    public static void closeConnection(){
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PreparedStatement getStmt() throws SQLException {
        if(stmt==null){
           stmt = getConnection().prepareStatement("select count(date) as count from firewallLog where date = ? and time>=? and time<? order by date,time;");
        }
        return stmt;
    }

    public static PreparedStatement getStmt1() throws SQLException {
        if(stmt1==null){
           stmt1 = getConnection().prepareStatement("select count(date) as count from firewallLog where (date = ? and time>=?) or (date = ? and time<?) order by date,time;");
        }
        return stmt1;
    }
    
    public static void closeStatement(PreparedStatement ps) throws SQLException{
        if(ps!=null){
            ps.close();
        }
    }

}
