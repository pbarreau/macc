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
import java.util.ArrayList;

public class ClimHome extends AppCompatActivity {

    int on_Off = 0 ;
    private String unite = "° C";
    private Button button_ON_OFF;
    private Button button_PLUS;
    private Button button_MINUS;
    private Button button_GRAPH;
    private Button button_BACK;

    TextView nameText, nameSalle;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;

    private Socket s;
    private PrintWriter pw;
    //hostname local 192.168.13.91 RPI RESEAUX V13
    //hostname distant 93.121.229.118 CHEZ MATHIAS
    //hostname local 192.168.137.127
    private String hostTest= "172.20.10.2"; //192.168.1.21
    private String host = "192.168.4.1";
    private int port = 1060;
    private int portTest = 8024;

    private int intTempValue;
    private int startTemp = 26;
    private String clim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clim_home);

        getSupportActionBar().hide(); //Hides MyFirsApp.............................................
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String name = getIntent().getStringExtra("user");
        nameText = (TextView) findViewById(R.id.textView_nameHome);
        nameText.setText("Hello\n"+name);
        ///////////////////////////////////////////////////////////////////////////////////////////////
        nameSalle = (TextView)findViewById(R.id.textView_salle);
        String salle = e4Csg1MACC_getWifiSSID();
        nameSalle.setText("Salle:\n"+ salle);
        //pass from loginHome the class(la sale) number where the user is suppose to be.

        //declare my buttons........................................................................
        button_ON_OFF = (Button) findViewById(R.id.button_on_off);
        button_PLUS = (Button) findViewById(R.id.button_Plus);
        button_MINUS = (Button) findViewById(R.id.button_minus);
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

        intTempValue = startTemp;
        showClim();

    }

    /*get the "Salle" name by the wifi ssid
    shows the user where he is connceted (in which "Salle" he is still connected)...................
     */
    public String e4Csg1MACC_getWifiSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid  = info.getSSID();
        return ssid;
    }

    //this methode is for the alternative button on off.............................................
    protected void e4Csg1Macc_on_Off() {
        TextView tv = (TextView) findViewById(R.id.textView_tempView);
        if (clim.equals("Please press to select a AC")){
            Toast.makeText(getBaseContext(), "Please select a AC !!!", Toast.LENGTH_LONG).show();
        }else{
            if (on_Off == 0) {
                //button on.............................................................................
                if (intTempValue == startTemp){
                    on_Off = 1;
                    button_ON_OFF.setText("Off"); // Arrêt
                    button_ON_OFF.setBackgroundColor(Color.parseColor("#ff99cc00"));
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();
                    tv.setText("" + startTemp);
                }else{
                    on_Off = 1;
                    button_ON_OFF.setText("Off"); // Arrêt
                    button_ON_OFF.setBackgroundColor(Color.parseColor("#ff99cc00"));
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();
                    tv.setText("" + intTempValue);
                }
            } else {
                //button off............................................................................
                on_Off = 0;
                button_ON_OFF.setText("On"); //Marche
                button_ON_OFF.setBackgroundColor(Color.parseColor("#ffcc0000"));
                tv.setText(" ");
            }
        }
    }

    //this methode is for up button.................................................................
    protected void e4Csg1Macc_plus(){
        //button up.................................................................................
        TextView tv = (TextView) findViewById(R.id.textView_tempView);
        String stringTempValue = tv.getText().toString();
        if (clim.equals("Please press to select a AC")){
            Toast.makeText(getBaseContext(), "Please select a AC !!!", Toast.LENGTH_SHORT).show();
        }else {
            if (on_Off == 1) {
                if (intTempValue < 30) {
                    intTempValue = Integer.parseInt(stringTempValue);   //convert int to string
                    intTempValue++;
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    tv.setText("" + intTempValue);
                    button_MINUS.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            } else {
                Toast.makeText(getBaseContext(), "AC is off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this methode is for down button...............................................................
    protected void e4Csg1Macc_minus(){
        //button down...............................................................................
        TextView tv = (TextView) findViewById(R.id.textView_tempView);
        String stringTempValue = tv.getText().toString();

        if (clim.equals("Please press to select a AC")){
            Toast.makeText(getBaseContext(), "Please select a AC !!!", Toast.LENGTH_SHORT).show();
        }else {
            if (on_Off == 1) {
                if (intTempValue > 16) {
                    intTempValue = Integer.parseInt(stringTempValue);
                    intTempValue--;
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    tv.setText("" + intTempValue);
                    button_MINUS.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            } else {
                Toast.makeText(getBaseContext(), "AC is off", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this methode is for graph button to access the graph temperature..............................
    protected void e4Csg1Macc_tempGraph(){
        //open the graph screen.....................................................................
        Intent intent = new Intent(this, TemperatureGraph.class);
        startActivity(intent);
    }

    //this methode is for exit button to go back to the identification screen.......................
    protected void e4Csg1Macc_exitToLogin(){
        //back......................................................................................
        Intent intent = new Intent(this, LoginHome.class);
        startActivity(intent);
    }

    //this sub class is serve to execute e4Csg1MACC_sendSocket() in the background..................
    private class E4sendSocket extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = new Socket(host, port);
                pw = new PrintWriter(s.getOutputStream());
                pw.write(intTempValue);
                pw.flush();
                pw.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //this methode is ...
    private void showClim(){

        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_clim);

        //create a list of items for the spinner.
        String[] languages = {"Please press to select a AC", "Clim","Clim1","Clim3","All AC's"};
        ArrayList<String> language = new ArrayList<String>();

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);

        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), "AC selected : " +parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                clim = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });
    }
}
