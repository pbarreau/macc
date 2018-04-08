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
import java.sql.Statement;
import java.util.Date;

public class TemperatureGraph extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm:ss");

    //values which will have the db last four temperatures..........................................
    private static int one;
    private static int two;
    private static int three;
    private static int four;

    private static int heur1;
    private static int heur2;
    private static int heur3;
    private static int heur4;


    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    Button button_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_graph);

        getSupportActionBar().hide(); //Hides MyFirsApp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        connectionClass = new ConnectionClass();
        progressDialog=new ProgressDialog(this);
        button_BACK = (Button)findViewById(R.id.button_backHome);
        button_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backHome();
            }
        });
    }

    class myTask extends AsyncTask<Void, Void, String>
    {
        String z = "";
        boolean show = false;
        boolean isSuccess = false;
        boolean isSuccess1 = false;

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading graph...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {

                    String query = " select TEMPERATURE from SALLE";
                    Statement stmt = con.createStatement();
                    //stmt.executeUpdate(query);
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        one = rs.getInt(0);
                        two = rs.getInt(1);
                        three = rs.getInt(2);
                        four = rs.getInt(3);

                        isSuccess = true;
                    }

                    String query1 = "select HEURE from SALLE";
                    Statement stmt1 = con.createStatement();
                    ResultSet rs1 = stmt.executeQuery(query1);

                    while (rs1.next()){
                        heur1 = rs1.getInt(0);
                        heur2 = rs1.getInt(1);
                        heur3 = rs1.getInt(2);
                        heur4 = rs1.getInt(3);

                        isSuccess1 = true;
                    }

                    if (isSuccess && isSuccess1) {
                        show = true;
                        z = "Graph loaded";
                    } else {
                        show = false;
                    }
                }
            }catch (Exception ex){
                z = "Exceptions......" + ex;
            }
            return z;
        }
        @Override
        protected void onPostExecute(String s){
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if (show){
                GraphView graph = (GraphView)findViewById(R.id.Graph);
                series = new LineGraphSeries<DataPoint>(getDataPoint());
                graph.addSeries(series);
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
                viewport.setMinY(0);
                viewport.setMaxY(30);

                //getDataPoint();
            }
        }
    }

    //
    public DataPoint[] getDataPoint() {
        DataPoint[] dp = new DataPoint[]{
                new DataPoint(heur1,one),      //new DataPoint(new Date().getTime(),1),
                new DataPoint(heur2,two),    //new DataPoint(heur,valeur),
                new DataPoint(heur3,three),
                new DataPoint(heur4,four)
        };
        return (dp);
    }

    public void backHome(){
        Intent intent;
        intent = new Intent(this, ClimHome.class);
        startActivity(intent);
    }

}
