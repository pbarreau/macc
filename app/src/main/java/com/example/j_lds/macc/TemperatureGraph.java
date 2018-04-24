package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.Date;
import java.util.Locale;

public class TemperatureGraph extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm");

    //my attributes
    //values which i will get the last 4 temperatures from the db...................................
    private static double temperature1;
    private static double temperature2;
    private static double temperature3;
    private static double temperature4;
    //values which i will get the last 4 times of each temperatures from the db.....................
    private static Time tempTime1;
    private static Time tempTime2;
    private static Time tempTime3;
    private static Time tempTime4;
    //declaration of my constructors................................................................
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;
    //declare my button_BACK........................................................................
    private Button button_BACK;
    //the constructors..............................................................................
    private ArrayList<Double> tableTemp = new ArrayList<Double>();
    private ArrayList<Time> tableTime = new ArrayList<Time>();
    //a string date that will have the current day..................................................
    private String date;
    //a string message that will inform the user....................................................
    private String message = "";
    //a variable that i can verify the success before the graph creation............................
    private boolean isSuccess = false;
    //a string to store the name of the c\lass.......................................................
    private String salleName; //= "BATV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_graph);

        getSupportActionBar().hide(); //Hides MyFirsApp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initialize my constructors................................................................
        connectionClass = new ConnectionClass();
        progressDialog=new ProgressDialog(this);
        button_BACK = (Button)findViewById(R.id.button_backHome);
        //set a click listener to my back...........................................................
        button_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1MACC_backHome();
            }
        });

        //get current date and store it in string date..............................................
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //get the "Salle" by ssid fom ClimHome.class................................................
        ClimHome climHome = new ClimHome();
        salleName = climHome.e4Csg1MACC_getWifiSSID();

        //(execute) find temperature, time values in db and show the graph..........................
        E4cBackground e4Task = new E4cBackground();
        e4Task.execute();
    }

    private void e4Csg1MACC_sqlQuery() throws SQLException {
        //open a connection.........................................................................
        Connection con = connectionClass.e4Csg1MACC_CONN();
        //test the connection.......................................................................
        if (con == null) {
            message = "Please check your internet connection";
        } else {
            //prepare a query and a statement.......................................................
            //i need to get four temperatures and their hours and place them from recent to dated...
            String query = "select TEMPERATURE,HEURE from SALLE where NOM_SALLE = '"+salleName+"' and DATE_JOUR = '"+date+"' order by HEURE"; //2018-04-10
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //
            while(rs.next()){
                tableTemp.add(rs.getDouble(1));
                tableTime.add(rs.getTime(2));

            }
            isSuccess = true;
            //affect the four recent values to my temperature variables from the ArrayList..........
            temperature1 = tableTemp.get(0);
            temperature2 = tableTemp.get(1);
            temperature3 = tableTemp.get(2);
            temperature4 = tableTemp.get(3);

            //affect the four recent values to my temperature variables from the ArrayList..........
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

            progressDialog.setMessage("Loading graph...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                e4Csg1MACC_sqlQuery();
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
                GraphView graph = (GraphView)findViewById(R.id.Graph);
                series = new LineGraphSeries<DataPoint>(e4Csg1MACC_getDataPoint());
                graph.addSeries(series);

                //show the time in a time format hh:mm
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    return sdf.format(new Date((long)value));
                }
                return super.formatLabel(value, isValueX);
            }
        });

                // graph.getGridLabelRenderer().setNumHorizontalLabels(10);
                graph.getViewport().setScalable(true);
                Viewport viewport = graph.getViewport();
                viewport.setYAxisBoundsManual(true);
//                viewport.setMinY(0);
//                viewport.setMaxY(30);

                e4Csg1MACC_getDataPoint();

                message = "Graph loaded";
            }else{
                message = "Graph failed to loaded";
            }
            Toast.makeText(getBaseContext(),""+ message,Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }
    }

    public DataPoint[] e4Csg1MACC_getDataPoint() {
        DataPoint[] dp = new DataPoint[]{
                new DataPoint(tempTime1, temperature1),    //new DataPoint(new Date().getTime(),1),
                new DataPoint(tempTime2, temperature2),    //new DataPoint(heur,valeur),
                new DataPoint(tempTime3, temperature3),
                new DataPoint(tempTime4, temperature4)
        };
        return (dp);
    }

    public void e4Csg1MACC_backHome(){
        Intent intent;
        intent = new Intent(this, ClimHome.class);
        startActivity(intent);
    }
}
