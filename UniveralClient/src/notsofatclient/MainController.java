/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package notsofatclient;

import eu.schudt.javafx.controls.calendar.DatePicker;
import gr.cti.protos.universal.calculation.LocalCalculations;
import gr.cti.protos.universal.helpers.GeneralHelper;
import gr.cti.protos.universal.helpers.SeriesHelper;
import gr.cti.protos.universal.map.Maps;
import gr.cti.protos.universal.map.MapEvent;
import gr.cti.protos.universal.model.CalculationBean;
import gr.cti.protos.universal.model.TopBean;
import gr.cti.protos.universal.model.enums.Granularity;
import gr.cti.protos.universal.model.enums.Top;
import gr.cti.protos.universal.service.LocalService;
import gr.cti.protos.universal.service.ServerService;
import gr.cti.protos.universal.tasks.CreateChartTask;
import gr.cti.protos.universal.tasks.CreateDerivativesTimeseriesTask;
import gr.cti.protos.universal.tasks.CreateIncidentsTimeseriesTask;
import gr.cti.protos.universal.tasks.DrawingChartTask;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.joda.time.DateTime;

/**
 *
 * @author spyko
 */
public class MainController implements Initializable {

    @FXML
    private Button sdDiagnosis;

    @FXML
    private Label optionLabel;
    
    @FXML
    private Label label;
    
    @FXML
    private Button malwareActivityButton;
    
    @FXML
    private LineChart<String, Number> malwareChart;
    
    @FXML
    private WebView webView;
    
    @FXML
    private LineChart<String, Number> derivativesChart;
    
    @FXML
    private AnchorPane mainContentPane;
    
    private DatePicker from;
    
    @FXML
    
    private DatePicker to;
    
    @FXML
    private GridPane gridpane;
    
    @FXML
    private GridPane gridgranularity;
    
    @FXML
    private ComboBox combobox;
    
    
    @FXML
    private Button topButton;
    
    @FXML
    private Button mapButton;
    
    @FXML
    private Button derivativeButton;
    
    @FXML
    private Button liveButton;
    
    @FXML
    private Button globalButton;
    
    @FXML
    private PieChart protocolPie;
    
    @FXML
    private PieChart ipPie;
    
    @FXML
    private PieChart portPie;
    
    @FXML
    private Button drawButton;
    
    @FXML
    private GridPane chartbox;
    
    
    @FXML
    private Label gtlabel;
    
//    @FXML
//    private ProgressBar bar;
    
    @FXML
    private ProgressIndicator indicator;
    
    @FXML
    private AnchorPane progressPane;
    
    @FXML
    private AnchorPane progressPane2;
    
    @FXML
    private AnchorPane liveAnchor;
    
    @FXML
    private AnchorPane globalAnchor;
    
    @FXML
    private LineChart<String, Number> malActivityChart;
    
    @FXML
    private LineChart<String, Number> derActivityChart;
    
     @FXML
    private LineChart<String, Number> malActivityGlobalChart;
    
    @FXML
    private LineChart<String, Number> derActivityGlobalChart;
 
    @FXML 
    private AnchorPane mainanchorpane;
    
    @FXML
    private Label messageTask;
    
    private ServerService serverService;
    
    private LocalService localService;
    
    private boolean finished = false;
    
    private ObservableList<XYChart.Series<String, Number>> liveIncidentsSeries ;
      
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    @FXML
    private void showMaps(){

        Maps maps= new Maps();
        System.out.println("MAPS1" );
      // Object obj = new Maps();
        System.out.println("MAPS2" );
        
        
    }
    
    @FXML
    private void showDerivativesChart(){
        progressPane.setVisible(true);
        progressPane2.setVisible(true);
        
        
        
        CreateChartTask task = new CreateChartTask(GeneralHelper.getDateToString(from.getSelectedDate()),GeneralHelper.getDateToString(to.getSelectedDate()), ((Granularity)combobox.getValue()).getSecs(),true);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
               
                malwareChart.getData().clear();
                malwareChart.getData().clear();
                malwareChart.getData().addAll((List<XYChart.Series<String, Number>>)t.getSource().getValue());
                malwareChart.visibleProperty().set(true);
                progressPane.setVisible(false);
                progressPane2.setVisible(false);
            }
        });
    indicator.progressProperty().bind(task.progressProperty());
    messageTask.textProperty().bind(task.messageProperty());
    
    new Thread(task).start();
    }
    
    @FXML
    private void showWeb(){
        displayFalse();
        webView.setVisible(true);
    }
    
    @FXML
    private void showMalwareActivityChart() {
      
        progressPane.setVisible(true);
        progressPane2.setVisible(true);
        System.out.println("APOOOOOOOOOOOOOOOO" + GeneralHelper.getDateToString(from.getSelectedDate()));
        System.out.println("EOSSSSSSSSSSSSSSSS" + GeneralHelper.getDateToString(to.getSelectedDate()));
        CreateChartTask task = new CreateChartTask(GeneralHelper.getDateToString(from.getSelectedDate()),GeneralHelper.getDateToString(to.getSelectedDate()), ((Granularity)combobox.getValue()).getSecs(),false);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                malwareChart.getData().clear();
                
                malwareChart.getData().addAll((List<XYChart.Series<String, Number>>)t.getSource().getValue());
                malwareChart.setAnimated(true);
                malwareChart.visibleProperty().set(true);
                progressPane.setVisible(false);
                progressPane2.setVisible(false);
            }
        });
//        CreateIncidentsTimeseriesTask task = new CreateIncidentsTimeseriesTask(((Granularity)combobox.getValue()).getSecs(), GeneralHelper.getDateToString(from.getSelectedDate()),GeneralHelper.getDateToString(to.getSelectedDate()));
//        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            
//            @Override
//            public void handle(WorkerStateEvent t) {
//                HashMap<DateTime,Number> incidents = (HashMap<DateTime, Number>)t.getSource().getValue();
//                if(incidents!=null){
//                    DrawingChartTask task1 = new DrawingChartTask(incidents, "Malware Activity");
//                    indicator.progressProperty().bind(task1.progressProperty());
//
//                    task1.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//
//                        @Override
//                        public void handle(WorkerStateEvent t) {
//                            malwareChart.getData().clear();
//                            malwareChart.getData().addAll((ObservableList<XYChart.Series<String, Number>>)t.getSource().getValue());
//                            malwareChart.visibleProperty().set(true);
//                            progressPane.setVisible(false);
//                            progressPane2.setVisible(false);
//                        }
//                    });
//                     new Thread(task1).start();
//                }
//                else{
//                     progressPane.setVisible(false);
//                     progressPane2.setVisible(false);
//                }
//            }
//        });
        
        indicator.progressProperty().bind(task.progressProperty());
        messageTask.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
    
    public void closeprocesse(KeyEvent t) throws InterruptedException
    {
        if (t.getCode() == KeyCode.ENTER) {
            progressPane.setVisible(false);
   
        }
       
    }
    
    @FXML
    public void showgridpane(ActionEvent e)
    {
        //System.out.println(e.getSource().g);
         if(e.getSource() == malwareActivityButton){
            ObservableList<Granularity> granularityList = FXCollections.observableArrayList(Granularity.values());
            combobox.setItems(granularityList);
            combobox.getSelectionModel().selectFirst();
            optionLabel.setText("Malware Activity");
            gtlabel.setText("Select granularity");
            malwareChart.setTitle("Malware Activity");
            drawButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    showMalwareActivityChart(); //To change body of generated methods, choose Tools | Templates.
                }
            });
//             System.out.println("RUNS?????::::" + serverService.isRunning());
        }
        else if(e.getSource() == topButton){
            ObservableList<Top> topList = FXCollections.observableArrayList(Top.values());
            combobox.setItems(topList);
            combobox.getSelectionModel().selectFirst();
            optionLabel.setText("Top");
            gtlabel.setText("Select top");
            drawButton.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent t) {
                    showPieChart(); //To change body of generated methods, choose Tools | Templates.
                }
            });
                 // drawBu
        }
        else if(e.getSource() == derivativeButton){
            ObservableList<Granularity> granularityList = FXCollections.observableArrayList(Granularity.values());
            combobox.setItems(granularityList);
            combobox.getSelectionModel().selectFirst();
            optionLabel.setText("Derivatives");
            malwareChart.setTitle("Derivatives");
            
            drawButton.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent t) {
                    showDerivativesChart(); //To change body of generated methods, choose Tools | Templates.
                }
            });
        }
        
        displayFalse();
        gridpane.setVisible(true);
        gridgranularity.setVisible(true);
        optionLabel.setVisible(true);
        
    }
    @FXML
    public void test(){
        Task task = new Task<Void>(){
            
            @Override
            protected Void call() throws Exception {
                final int max = 1000000;
                for (int i=1; i<=max; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(i, max);
                }
                return null;
            }
            
        };
        
     //   bar.progressProperty().bind(task.progressProperty());
        
        new Thread(task).start();
        // System.out.println(combobox.getValue().getSecs());
        //    malwareChart.getData().get(0).getData().add(new XYChart.Data<String, Number>((new DateTime()).toString(), Double.valueOf(Math.random()*10000.00).intValue()));
        //    malwareChart.getData().get(0).getData().remove(0);
        //   malwareChart.getData().add(new XYChart.Data<String, Number>("2013-04-18", 100));
        //  DatabaseService.getBlockedByDate(null, null);
    }
    
    
    public void showPieChart(){
        TopBean topBean = LocalCalculations.getTop(GeneralHelper.getDateToString(from.getSelectedDate()), GeneralHelper.getDateToString(to.getSelectedDate()));
        System.out.println("EEEEEEEEEEEEEEEE" + topBean.getIps());
        protocolPie.setData(SeriesHelper.createPieData(GeneralHelper.getTopByValue(topBean.getProtocols(), ((Top) combobox.getValue()).getTop())));
        ipPie.setData(SeriesHelper.createPieData(GeneralHelper.getTopByValue(topBean.getIps(), ((Top)combobox.getValue()).getTop())));
        portPie.setData(SeriesHelper.createPieData(GeneralHelper.getTopByValue(topBean.getPorts(), ((Top)combobox.getValue()).getTop())));
        //                        chart effect
        //        SeriesHelper.addMouseHoverAnimation(protocolPie);
        //        SeriesHelper.addMouseHoverAnimation(ipPie);
        //        SeriesHelper.addMouseHoverAnimation(portPie);
        
        
        
        //protocolPie.effect(PerspectiveTransfor());
        // protocolPie.opacityProperty();
        protocolPie.setVisible(true);
        // protocolPie.
        portPie.setVisible(true);
        ipPie.setVisible(true);
        chartbox.setVisible(true);
        malwareChart.setVisible(false);
        
    }
    
    public void startLiveService(){
     
   //  serverService.cancel();
     
       // System.out.println("TELEIUOSE");
        
    }
    
    public void showLiveCharts()
    {
        liveAnchor.setVisible(true);
        globalAnchor.setVisible(false);
        mainanchorpane.setVisible(false);
    }

    public void showGlobalCharts()
    {
        globalAnchor.setVisible(true);
        liveAnchor.setVisible(false);
        mainanchorpane.setVisible(false);
    }

 
 
 @Override
    public void initialize(URL url, ResourceBundle rb) {
       // ServerTask task = new ServerTask();
        System.out.println(malwareChart.getData());
        sdDiagnosis.setVisible(false);
        mapButton.setVisible(false);
       
        final XYChart.Series<String, Number> incidentsSeries = new XYChart.Series<>();
        incidentsSeries.setName("Incidents");
        final XYChart.Series<String, Number> firstDerivativeSeries = new XYChart.Series<>();
        firstDerivativeSeries.setName("Malware Activity");
        final XYChart.Series<String, Number> secondDerivativeSeries = new XYChart.Series<>();
        secondDerivativeSeries.setName("Epidemic Rate");
        
        final XYChart.Series<String, Number> incidentsSeriesGlobal = new XYChart.Series<>();
        incidentsSeriesGlobal.setName("Incidents");
        final XYChart.Series<String, Number> firstDerivativeSeriesGlobal = new XYChart.Series<>();
        firstDerivativeSeriesGlobal.setName("Malware Activity");
        final XYChart.Series<String, Number> secondDerivativeSeriesGlobal = new XYChart.Series<>();
        secondDerivativeSeriesGlobal.setName("Epidemic Rate");

        malActivityChart.getData().add(incidentsSeries);
        derActivityChart.getData().add(firstDerivativeSeries);
        derActivityChart.getData().add(secondDerivativeSeries);
        
        malActivityGlobalChart.getData().add(incidentsSeriesGlobal);
        derActivityGlobalChart.getData().add(firstDerivativeSeriesGlobal);
        derActivityGlobalChart.getData().add(secondDerivativeSeriesGlobal);
        
        serverService = new ServerService();
        localService = new LocalService();
        
        localService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("BBBBBBBBBBB");
                CalculationBean der = localService.getValue();
                System.out.println(der.getDateTime());
                System.out.println(der.getIncidents());
                System.out.println(der.getFirstDerivative());
                System.out.println(der.getSecondDerivative());
                incidentsSeries.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(), der.getIncidents()));
                firstDerivativeSeries.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(),der.getFirstDerivative()));
                secondDerivativeSeries.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(),der.getSecondDerivative()));
                localService.restart();
            }
        });
        
       localService.start();
         
        serverService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("AAAAAAA");
                CalculationBean der = serverService.getValue();
                System.out.println(der.getDateTime());
                System.out.println(der.getIncidents());
                System.out.println(der.getFirstDerivative());
                System.out.println(der.getSecondDerivative());
                incidentsSeriesGlobal.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(), der.getIncidents()));
                firstDerivativeSeriesGlobal.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(),der.getFirstDerivative()));
                secondDerivativeSeriesGlobal.getData().add(new XYChart.Data<String, Number>(der.getDateTime().toString(),der.getSecondDerivative()));
                serverService.restart();
            }
        });
        
        serverService.start();

        final WebEngine webEngine = webView.getEngine();
        webEngine.load("http://protos.cti.gr");
        
        from = new DatePicker(Locale.ENGLISH);
        from.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        from.getCalendarView().todayButtonTextProperty().set("Today");
        from.getCalendarView().setShowWeeks(false);
        from.getStylesheets().add("notsofatclient/DatePicker.css");
        
        gridpane.add(from, 1,0);
        
        to = new DatePicker(Locale.ENGLISH);
        to.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        to.getCalendarView().todayButtonTextProperty().set("Today");
        to.getCalendarView().setShowWeeks(false);
        to.getStylesheets().add("notsofatclient/DatePicker.css");
        
        gridpane.add(to, 1,1);
        liveAnchor.setVisible(true);
    }
 
 
    private void displayFalse(){
        malwareChart.setVisible(false);
        webView.setVisible(false);
        chartbox.setVisible(false);
        gridgranularity.setVisible(false);
        gridpane.setVisible(false);
        optionLabel.setVisible(false);
        liveAnchor.setVisible(false);
        globalAnchor.setVisible(false);
        mainanchorpane.setVisible(true);
        
    }
    
}

