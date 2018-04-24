package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClimHome extends AppCompatActivity {

    int on_Off = 0 ;
    String unite = "° C";
    Button button_ON_OFF;
    Button button_PLUS;
    Button button_MINUS;
    Button button_MODE;
    Button button_GRAPH;
    Button button_BACK;

    String message = "";

    TextView nameText, nameSalle;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;

    private static Socket s;
    private static PrintWriter pw;
    //hostname local 192.168.13.91 RPI RESEAUX V13
    //hostname distant 93.121.229.118 CHEZ MATHIAS
    //hostname local 192.168.137.127
    private static String host = ""; //192.168.1.21
    private static String hostTest = "192.168.137.127";
    private static int port = 1060;
    private static int portTest = 1060;
    private static String dataPass = "";

    private String salle,clim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clim_home);

        getSupportActionBar().hide(); //Hides MyFirsApp.............................................
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String name = getIntent().getStringExtra("user");
        nameText = (TextView) findViewById(R.id.textView_name);
        nameText.setText("Hello "+name);
        ///////////////////////////////////////////////////////////////////////////////////////////////
        nameSalle = (TextView)findViewById(R.id.textView_salle);
        salle = e4Csg1MACC_getWifiSSID();
        nameSalle.setText("Salle: "+salle);
        //pass from loginHome the class(la sale) number where the user is suppose to be.

        //declare my buttons........................................................................
        button_ON_OFF = (Button) findViewById(R.id.button_on_off);
        button_PLUS = (Button) findViewById(R.id.button_Plus);
        button_MINUS = (Button) findViewById(R.id.button_minus);
        button_MODE = (Button) findViewById(R.id.button_mode);
        button_GRAPH = (Button) findViewById(R.id.button_ok);
        button_BACK = (Button) findViewById(R.id.button_back);

        //set button listeners......................................................................
        button_ON_OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_on_Off();
            }
        });
        button_PLUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_plus();
            }
        });
        button_MINUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_minus();
            }
        });
        button_MODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_mode();
            }
        });
        button_GRAPH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_tempGraph();
            }
        });
        button_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_exitToLogin();
            }
        });

        progressDialog=new ProgressDialog(this);
        connectionClass = new ConnectionClass();

        
    }

    /*get the "Salle" name by the wifi ssid
    shows the user where he is connceted (in which "Salle" he is still connected)...................
     */
    public String e4Csg1MACC_getWifiSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();
        String ssid  = info.getSSID();
        return ssid;
    }

    private void e4Csg1MACC_sendSocket(){

        try {
            s = new Socket(hostTest, portTest);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(dataPass);
            pw.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void e4Csg1Macc_on_Off() {
        Button b = (Button) findViewById(R.id.button_on_off);
        TextView tv = (TextView) findViewById(R.id.textView_tempView);
        int color;

        if (on_Off == 0) {
            //button on.............................................................................
            on_Off = 1;
            b.setText("Off"); // Arrêt
            color = Color.parseColor("#ff99cc00");
            b.setBackgroundColor(color);

            dataPass = "A";
            E4sendSocket ss = new E4sendSocket();
            ss.execute();
            tv.setText("26" + unite);

        } else {
            //button off............................................................................
            on_Off = 0;
            b.setText("On"); //Marche
            color = Color.parseColor("#ffcc0000");
            b.setBackgroundColor(color);

            dataPass = "B";
            E4sendSocket ss = new E4sendSocket();
            ss.execute();
            tv.setText("0" + unite);

        }
    }

    protected void e4Csg1Macc_plus(){
        //button up.................................................................................
        dataPass = "C";
        E4sendSocket ss = new E4sendSocket();
        ss.execute();
    }

    protected void e4Csg1Macc_minus(){
        //button down...............................................................................
        dataPass = "D";
        E4sendSocket ss = new E4sendSocket();
        ss.execute();
    }

    protected String e4Csg1Macc_mode(){
        //button e4Csg1Macc_mode...............................................................................
        String z = "This button is not yet available";
        return z;
/*
        dataPass = "D";
        E4sendSocket mt = new E4sendSocket();
        mt.execute();
        */
    }

    protected void e4Csg1Macc_tempGraph(){
        //open the graph screen.....................................................................
        Intent intent = new Intent(this, TemperatureGraph.class);
        startActivity(intent);
    }

    protected void e4Csg1Macc_exitToLogin(){
        //back......................................................................................
        try {
            pw.close();
            s.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(this, LoginHome.class);
        startActivity(intent);
    }

    private class E4sendSocket extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            e4Csg1MACC_sendSocket();
            return null;
        }
    }

    //find climatiseur..............................................................................
    private void showClim(){

        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_clim);
        //create a list of items for the spinner.
        String[] languages = {" ", "English","Français",};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "Text : " +parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                clim = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });
    }

}
