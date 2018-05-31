package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Graph extends AppCompatActivity {
    private LineGraphSeries<DataPoint> seriesTemp,seriesHumi;
    private SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm");

    //my attributes
    //values which i will get the last 4 temperatures from the db...................................
    private static double temperature1;
    private static double temperature2;
    private static double temperature3;
    private static double temperature4;
    //
    private static double humidite1,humidite2,humidite3,humidite4;
    //values which i will get the last 4 times of each temperatures from the db.....................
    private static Time tempTime1;
    private static Time tempTime2;
    private static Time tempTime3;
    private static Time tempTime4;
    //declaration of my constructors................................................................
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;
    //declare my button_annuler........................................................................
    private Button button_annuler;
    //the constructors..............................................................................
    private ArrayList<Double> tableTemp = new ArrayList<Double>();
    private ArrayList<Double> tableHumi = new ArrayList<Double>();
    private ArrayList<Time> tableTime = new ArrayList<Time>();
    //a string date that will have the current day..................................................
    private String timeStamp;
    //a string message that will inform the user....................................................
    private String message = "";
    //a variable that i can verify the success before the graph creation............................
    private boolean isSuccess = false;
    //a string to store the name of the c\lass.......................................................
    private String salleName; //= "BATV";

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userName = getIntent().getStringExtra("user");

        //initialize my constructors................................................................
        connectionClass = new ConnectionClass();
        progressDialog=new ProgressDialog(this);
        button_annuler = (Button)findViewById(R.id.button_annulerGraph);
        //set a click listener to my back...........................................................
        button_annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1MACC_annulerGraph();
            }
        });

        //get current date and time and store it in string timeStemp................................
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        //get the "Salle" by ssid fom ClimHome.class................................................
        salleName = e4Csg1MACC_getWifiSSID();

        //(execute) find temperature, time values in db and show the graph..........................
        E4cBackground e4Task = new E4cBackground();
        e4Task.execute();
    }

    public String e4Csg1MACC_getWifiSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid  = info.getSSID();
        return ssid;
    }

    private void e4Csg1MACC_sqlQueryTemperature() throws SQLException {
        //open a connection.........................................................................
        Connection con = connectionClass.e4Csg1MACC_CONN();
        //test the connection.......................................................................
        if (con == null) {
            message = "Veuillez vérifier votre connexion internet";
        } else {
            //prepare a query and a statement.......................................................
            //i need to get four temperatures and their hours and place them from recent to dated...
            //String query = "select TEMPERATURE,HUMIDITE,DATE_JOUR from SALLE_BAT where NOM_BAT = '"+salleName+"' and DATE_JOUR = '"+timeStamp+"' order by DATE_JOUR"; //2018-04-10
            String queryTest = "select TEMPERATURE,HUMIDITE,DATE_JOUR from SALLE_BAT where NOM_BAT = 'BTV' ORDER BY DATE_JOUR DESC";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(queryTest);

            //
            while(rs.next()){
                tableTemp.add(rs.getDouble(1));
                tableHumi.add(rs.getDouble(2));
                tableTime.add(rs.getTime(3));

            }
            isSuccess = true;
            //affect the four recent values to my temperature variables from the ArrayList..........
            temperature1 = tableTemp.get(0);
            temperature2 = tableTemp.get(1);
            temperature3 = tableTemp.get(2);
            temperature4 = tableTemp.get(3);

            ////affect the four recent values to my humidity variables from the ArrayList...........
            humidite1 = tableHumi.get(0);
            humidite2 = tableHumi.get(1);
            humidite3 = tableHumi.get(2);
            humidite4 = tableHumi.get(3);

            //affect the four recent values to my time variables from the ArrayList.................
            tempTime1 = tableTime.get(0);
            tempTime2 = tableTime.get(1);
            tempTime3 = tableTime.get(2);
            tempTime4 = tableTime.get(3);
        }
    }

    class E4cBackground extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Chargement des graphes...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                e4Csg1MACC_sqlQueryTemperature();
            }catch (Exception ex){
                message = "Exceptions......" + ex;
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s){
            //if isSuccess is true than represent the graph and warn the user.......................
            //or warn the user the graph did not loaded.............................................
            if (isSuccess) {
                //find my Graph id from the xml file................................................
                //create an object series ..........................................................
                //add the points using series and show on graph.....................................
                GraphView graphTemp = (GraphView)findViewById(R.id.Graph);
                GraphView graphHumidity = (GraphView)findViewById(R.id.Graph1);
                seriesTemp = new LineGraphSeries<DataPoint>(e4Csg1MACC_getDataPointTemp());
                seriesHumi = new LineGraphSeries<DataPoint>(e4Csg1MACC_getDataPointHumi());
                graphTemp.addSeries(seriesTemp);
                graphHumidity.addSeries(seriesHumi);


                //show the time in a time format hh:mm
                graphTemp.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX){
                            return sdf.format(new Date((long)value));
                        }
                        return super.formatLabel(value, isValueX);
                    }
                });
                graphHumidity.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX){
                            return sdf.format(new Date((long)value));
                        }
                        return super.formatLabel(value, isValueX);
                    }
                });

                graphTemp.getGridLabelRenderer().setNumHorizontalLabels(4);
                graphHumidity.getGridLabelRenderer().setNumHorizontalLabels(4);
                graphTemp.getViewport().setScalable(true);
                graphHumidity.getViewport().setScalable(true);
                Viewport viewportTemp = graphTemp.getViewport();
                Viewport viewportHumi = graphHumidity.getViewport();
                viewportTemp.setYAxisBoundsManual(true);
                viewportHumi.setYAxisBoundsManual(true);

                e4Csg1MACC_getDataPointTemp();
                e4Csg1MACC_getDataPointHumi();

                message = "Graphique chargé";
            }else{
                message = "Les graphiques n'ont pas pu être chargé";
            }
            Toast.makeText(getBaseContext(),""+ message,Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }
    }

    public DataPoint[] e4Csg1MACC_getDataPointTemp() {
        DataPoint[] dp = new DataPoint[]{
                new DataPoint(tempTime1, temperature1),    //new DataPoint(new Date().getTime(),1),
                new DataPoint(tempTime2, temperature2),    //new DataPoint(heur,valeur),
                new DataPoint(tempTime3, temperature3),
                 new DataPoint(tempTime4, temperature4),
        };
        return (dp);
    }
    public DataPoint[] e4Csg1MACC_getDataPointHumi() {
        DataPoint[] dp = new DataPoint[]{
                new DataPoint(tempTime1,humidite1),   //new DataPoint(heur,valeur),
                new DataPoint(tempTime2,humidite2),
                new DataPoint(tempTime3,humidite3),
                new DataPoint(tempTime4,humidite4)
        };
        return (dp);
    }

    public void e4Csg1MACC_annulerGraph(){Intent intent;
            intent = new Intent(Graph.this, Home.class);
            intent.putExtra("user", userName);
            startActivity(intent);
    }
}
