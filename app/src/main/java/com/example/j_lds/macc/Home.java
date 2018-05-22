package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
    private FloatingActionButton fab;
    private FloatingActionButton fabExit;
    private FloatingActionButton fabGraph;
    private ConnectionClass connectionClass;

    private TextView className;
    private TextView tempText;
    private TextView humidityPercentageText;

    //a string message that will inform the user....................................................
    private String message = "";
    private String serverEncryptMessage="";
    //a string date that will have the current day and time that will have the current time.........
    private String nameTextPassStr,timeStamp,lastCalledTemp,selectedAC;
    private ArrayList<String> arrayList;
    private int humidity, temperature;
    private int nubSelectedAC;

    private boolean humidSuccess = false;
    private boolean tempSuccess = false;
    private boolean askServerInfoSuccess = false;
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
        nameText.setText("Hello\n"+name);

        //
        className = (TextView)findViewById(R.id.textView_classNameHome);
        String salle = e4Csg1MACC_getWifiSSID();
        className.setText("Salle:\n"+ salle);

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

        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton_settings);
        fabExit = (FloatingActionButton)findViewById(R.id.floatingActionButton_homeExit);
        fabGraph = (FloatingActionButton)findViewById(R.id.floatingActionButton_homeGraph);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Home.this, Settings.class);
                startActivity(intent);
                finish();
                System.exit(0);*/
            }
        });
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

                getInformation getInfo = new getInformation();
        getInfo.execute();

        //start thread to receive data from the server
        new Thread(receiveServerData).start();
    }

    //create Runnable to receive a socket from the server
    Runnable receiveServerData = new Runnable() {
        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader br;
        Handler h = new Handler();

        @Override
        public void run() {
            try {
                //listening on port 1061 for a serverSocket
                ss = new ServerSocket(1061);

                //infinity
                while (true) {
                    s = ss.accept();
                    isr = new InputStreamReader(s.getInputStream());
                    br = new BufferedReader(isr);

                    serverEncryptMessage = br.readLine();

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            //decrypt the message
                            decryptServerData();
                            showClim();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /*get the class name by the wifi ssid
    shows the user where he is connceted (in which class he is still connected).....................
     */
    private String e4Csg1MACC_getWifiSSID(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid  = info.getSSID();
        return ssid;
    }

    public void decryptServerData(){
        //decrypting the message by splitting it
        //the server must use ";" to separate the ACs name
        String[] serverDataStr = serverEncryptMessage.split(";");
        //get the fist separate info that is the number of ACs + 1 (itself)
        int totalACs = Integer.parseInt(serverDataStr[0]);
        //get the second separate info that is the last called temperature
        //a user send to the server

        //create a list of items for the spinner.
        arrayList = new ArrayList<String>();
        arrayList.add("Please press to select an AC");

        //store in the list all the Air Conditioner names the serve send me
        for (int i = 1; i < totalACs; i++ ){
            arrayList.add(serverDataStr[i]);
        }
    }

    private void askServerInfo(){
        Socket s;
        PrintWriter pw;
        String msg = "Server send ACs";

        try {
            s = new Socket("192.168.1.74", 1060);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(msg);
            pw.flush();
            pw.close();
            s.close();
            askServerInfoSuccess = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void showClim(){
        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_clim);

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

                if(selectedAC.equals("Please press to select an AC")){
                    Toast.makeText(getBaseContext(), "Please select an AC", Toast.LENGTH_SHORT).show();
                }
                if(!selectedAC.equals("Please press to select an AC")){
                    goodClimInfo = true;
                    Toast.makeText(getBaseContext(), "AC selected : " + selectedAC, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });
    }

    private void e4Csg1MACC_sqlQueryHumidity() throws SQLException {
        //open a connection.........................................................................
        Connection con = connectionClass.e4Csg1MACC_CONN();
        //test the connection.......................................................................
        if (con == null) {
            message = "Please check your internet connection";
        } else {
            //prepare a query and a statement.......................................................
            //i need to get four temperatures and their hours and place them from recent to dated...
            String query = "select HUMIDITE from SALLE_BAT where NOM_BAT = '"+className+"' and DATE_JOUR = '"+timeStamp+"' order by ID";//2018-05-14 10:46:26
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            //
            while(rs.next()){
                humidity = rs.getInt(1);
                humidSuccess = true;
            }

            String query1 = "select TEMPERATURE from SALLE_BAT where NOM_BAT = '"+className+"' and DATE_JOUR = '"+timeStamp+"' order by DATE_JOUR";
            Statement stmt1 = con.createStatement();
            ResultSet rs1 = stmt1.executeQuery(query1);

            while(rs1.next()){
                temperature = rs1.getInt(1);
                tempSuccess = true;
            }
        }
    }

    private class getInformation extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Retrieving data...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            /*try {
                e4Csg1MACC_sqlQueryHumidity();
            } catch (SQLException e) {
                e.printStackTrace();
                message = "Exceptions......" + e;
            }*/
            askServerInfo();
            return message;
        }

        @Override
        protected void onPostExecute(String s){
            //if isSuccess is true than represent the graph and warn the user.......................
            //or warn the user the graph did not loaded.............................................
            if (humidSuccess && tempSuccess && askServerInfoSuccess) {
                tempText.setText(temperature);
                progressBar.setProgress(humidity);
                humidityPercentageText.setText(humidity+"%");
                message = "data found";
            }else{
                tempText.setText("0");
                progressBar.setProgress(0);
                humidityPercentageText.setText(0+"%");
                message = "data not found";
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
                if (goodClimInfo) {
                    Intent intent = new Intent(Home.this, FullRemote.class);
                    intent.putExtra("user", nameTextPassStr);
                    intent.putExtra("ACName",selectedAC);
                    intent.putExtra("climIdInfo", nubSelectedAC);
                    startActivity(intent);
                    finish();
                    System.exit(0);
                }else{
                    Toast.makeText(getBaseContext(), "Please select an AC\nbefore proceeding...", Toast.LENGTH_SHORT).show();
                }
            }
            return true;

        }
    }

}
