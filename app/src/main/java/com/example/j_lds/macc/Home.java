package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Home extends AppCompatActivity {

    private GestureDetectorCompat gestureObjet;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private FloatingActionButton fabExit;
    private FloatingActionButton fabGraph;
    private ConnectionClass connectionClass;

    private TextView className;
    private TextView tempText;
    private TextView humidityPercentageText;

    //a string message that will inform the user....................................................
    private String message = "";
    //a string date that will have the current day and time that will have the current time.........
    private String nameTextPassStr,timeStamp,lastCalledTemp,selectedAC;
    private ArrayList<String> arrayList;
    private int humidity, temperature,lastTempInstruction;
    private int nubSelectedAC;
    private int nbClim;

    private boolean humidSuccess = false;
    private boolean tempSuccess = false;
    private boolean InstructionSuccess = false;
    private boolean getClim = false;
    private boolean goodClimInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Hides MACC bar at the top.................................................................
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set home text in textview more visible
        TextView homeScreenTitle = (TextView) findViewById(R.id.textView_screenTitle1_Home);
        homeScreenTitle.setTextColor(Color.MAGENTA);

        //
        String name = getIntent().getStringExtra("user");
        nameTextPassStr = name;
        TextView nameText = (TextView) findViewById(R.id.textView_nameHome);
        nameText.setText("Bonjour\n"+name);

        //
        className = (TextView)findViewById(R.id.textView_classNameHome);
        String salle = e4Csg1MACC_getWifiSSID();
        className.setText("Pièce :\n"+ salle);


        //
        gestureObjet = new GestureDetectorCompat(this, new Home.LearnGesture());
        progressDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.progressBar_humidity);
        connectionClass = new ConnectionClass();
        tempText = (TextView) findViewById(R.id.textView_tempNumber_Home);
        humidityPercentageText = (TextView) findViewById(R.id.textView_humidityPercentage);

        //get current date and store it in string date..............................................
        //date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //get current date and time and store it in string timeStemp................................
        timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        fabExit = (FloatingActionButton)findViewById(R.id.floatingActionButton_homeExit);
        fabGraph = (FloatingActionButton)findViewById(R.id.floatingActionButton_homeGraph);

        fabExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,LoginHome.class);
                startActivity(intent);
                finish();
                System.exit(0);

            }
        });
        fabGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activity = "Home";
                Intent intent = new Intent(Home.this, Graph.class);
                intent.putExtra("Previous Activity", activity);
                intent.putExtra("user", nameTextPassStr);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });
/*
        getInformation getInfo = new getInformation();
        getInfo.execute();*/

        e4Csg1MACC_showClim();
    }

    /*get the class name by the wifi ssid
    shows the user where he is connceted (in which class he is still connected).....................
     */
    private String e4Csg1MACC_getWifiSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid  = info.getSSID();
        return ssid;
    }

    private void e4Csg1MACC_getClimFromESP(){
        try {
            int getnbClim =0;
            nbClim = -1;
            while (nbClim == -1) {
                Socket s;
                s = new Socket("192.168.4.1", 1060);
                PrintStream p = new PrintStream(s.getOutputStream());
                Character val = (char) getnbClim;
                p.println(val);

                //accept the resultat
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                nbClim = isr.read();
                nbClim--;
                p.close();
                isr.close();
                s.close();
                getClim = true;
            }
        }catch (IOException  e){
            e.printStackTrace();
        }
        //need to execute this methode twice
    }

    private void e4Csg1MACC_showClim(){
        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_clim);

        //create a list of items for the spinner.
        arrayList = new ArrayList<String>();
        arrayList.add("Veuillez appuyer pour sélectionner un climatiseur");

        //test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
        nbClim = 2;

        //store the number of AC in a list to user after
            for (int i = 1; i <= nbClim; i++) {
                arrayList.add("Climatiseur " + i);
            }
            arrayList.add("Tous les climatiseurs");

            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);

            //set the spinners adapter to the previously created one.
            spinner.setAdapter(adapter);

            //when spinner selected an item in the list
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedAC = parent.getItemAtPosition(position).toString();
                    nubSelectedAC = parent.getSelectedItemPosition();

                    if(selectedAC.equals("Veuillez appuyer pour sélectionner un climatiseur")){
                        Toast.makeText(getBaseContext(), "Veuillez sélectionner un climatiseur", Toast.LENGTH_SHORT).show();
                    }
                    if(!selectedAC.equals("Veuillez appuyer pour sélectionner un climatiseur")){
                        goodClimInfo = true;
                        Toast.makeText(getBaseContext(), "Climatiseur sélectionner : " + selectedAC, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
    }

    private void e4Csg1MACC_sqlQueryGetInfo(){
        try {
            //open a connection.........................................................................
            Connection con = connectionClass.e4Csg1MACC_CONN();
            //test the connection.......................................................................
            if (con == null) {
                message = "Please check your internet connection";
            } else {
                //prepare a query and a statement.......................................................
                //i need to get latest temperatures and it's time
                //i need to get latest humidity and it's time
                //i need to get latest "set temperature instruction" of a room
                String query1 = "select TEMPERATURE from SALLE_BAT where NOM_BAT = '"+className+"' and DATE_JOUR = '"+timeStamp+"' order by DATE_JOUR";
                //String query1Text = "select TEMPERATURE from SALLE_BAT where NOM_BAT = 'BTV' order by ID";
                Statement stmt1 = con.createStatement();
                ResultSet rs1 = stmt1.executeQuery(query1);

                while (rs1.next()) {
                    temperature = rs1.getInt(1);
                    tempSuccess = true;
                }

                String query2 = "select HUMIDITE from SALLE_BAT where NOM_BAT = '"+className+"' and DATE_JOUR = '"+timeStamp+"' order by ID";//2018-05-14 10:46:26
                //String query2Text = "select HUMIDITE from SALLE_BAT where NOM_BAT = 'BTV' order by ID";
                Statement stmt2 = con.createStatement();
                ResultSet rs2 = stmt2.executeQuery(query2);

                //
                while (rs2.next()) {
                    humidity = rs2.getInt(1);
                    humidSuccess = true;
                }

                //
                String query3 = "select HUMIDITE from SALLE_BAT where NOM_BAT = '"+className+"' and DATE_JOUR = '"+timeStamp+"' order by ID";//2018-05-14 10:46:26
                Statement stmt3 = con.createStatement();
                ResultSet rs3 = stmt3.executeQuery(query3);

                //
                while (rs3.next()) {
                    lastTempInstruction = rs3.getInt(1);
                    InstructionSuccess = true;
                }
            }
        }catch (SQLException s){
            s.printStackTrace();
        }
    }

    private class getInformation extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Récupération des données...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            //e4Csg1MACC_sqlQueryGetInfo();

            e4Csg1MACC_getClimFromESP();
            return message;
        }

        @Override
        protected void onPostExecute(String s){
            //if isSuccess is true than represent the graph and warn the user.......................
            //or warn the user the graph did not loaded.............................................
            if (humidSuccess && tempSuccess && InstructionSuccess && getClim) {
                tempText.setText(""+temperature);
                progressBar.setProgress(humidity);
                humidityPercentageText.setText(humidity+"%");
                message = "Données Trouvé";
            }else{
                tempText.setText("0");
                progressBar.setProgress(0);
                humidityPercentageText.setText(0+"%");
                message = "Données non Trouvé";
            }
            Toast.makeText(getBaseContext(),""+ message,Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }
    }

    //swipe felt or right...........................................................................
    @Override
    public boolean onTouchEvent (MotionEvent event){
        this.gestureObjet.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //create the gesture Objet class
    private class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        //SimpleOnGestureListener is a lister for what we want to do

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float veloccityX, float veloccityY) {
            if (event2.getX() > event1.getX()) {
                //left
            } else if (event2.getX() < event1.getX()) {
                //right
                if (goodClimInfo){
                    Intent intent = new Intent(Home.this, FullRemote.class);
                    intent.putExtra("user", nameTextPassStr);
                    intent.putExtra("ACName",selectedAC);
                    intent.putExtra("climIdInfo", nubSelectedAC);
                    intent.putExtra("tempSetInstruction", lastTempInstruction);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }else{
                    Toast.makeText(getBaseContext(), "Veuillez sélectionner un AC\navant de procéder...", Toast.LENGTH_SHORT).show();
                }
            }
            return true;

        }
    }

}
