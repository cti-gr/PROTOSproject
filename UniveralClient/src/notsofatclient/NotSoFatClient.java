/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notsofatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

 /**
 *
 * @author spyko
 */
public class NotSoFatClient extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
       // LocalCalculations.getIncidentsTimeSeries(30,"2013-02-18", "2013-02-19");
//        System.setProperty("javafx.userAgentStylesheetUrl", "CASPIAN");
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
       
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
