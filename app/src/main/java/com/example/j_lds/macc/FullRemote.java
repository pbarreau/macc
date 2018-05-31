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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class FullRemote extends AppCompatActivity {

    private GestureDetectorCompat gestureObjet;
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;
    private Socket s;
    private PrintWriter pw;

    private TextView acNameStatusP2;

    private FloatingActionButton fab_power,fab_graph,fab_exit;
    private Button button_plus,button_minus,button_temp,button_timer;

    private String nameTextPassStr,ACname;

    private int ACidInfo;
    private int lastTempInstruction;
    private int power = 0;
    private int intTempValue;
    private int startTemp = 26;
    private int commandMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_remote);

        //Hides MACC bar at the top.................................................................
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set full remote text textview more visible
        TextView homeScreenTitle = (TextView) findViewById(R.id.textView_screenTitle2_FullRemote);
        homeScreenTitle.setTextColor(Color.MAGENTA);

        //get and set username
        String name = getIntent().getStringExtra("user");
        nameTextPassStr = name;
        TextView nameText = (TextView) findViewById(R.id.textView_name_FullRemote);
        nameText.setText("Bonjour\n"+name);

        //get and set the class location
        TextView className = (TextView) findViewById(R.id.textView_className_FullRemote);
        String salle = e4Csg1MACC_getWifiSSID();
        className.setText("Pièce:\n"+ salle);

        //get selected AC name
        ACname = getIntent().getStringExtra("ACName");
        TextView ACNameStatusP1 = (TextView)findViewById(R.id.textView_acNameStatusP1);
        ACNameStatusP1.setText(ACname + ": ");

        //get the set temp instruction
        Intent integer = getIntent();
        lastTempInstruction = integer.getIntExtra("tempSetInstruction",0);
        lastTempInstruction = 20;
        //get AC id which will help to send the increment or decrement temperature to the server(ESP32)




        //change how the commandeMessage will be sended !!!!!!!!!!!!!!!!!!!!!!!
        Intent intent = getIntent();
        ACidInfo = intent.getIntExtra("climIdInfo",0);
        ACidInfo = ACidInfo * 100;

        gestureObjet = new GestureDetectorCompat(this, new FullRemote.LearnGesture());

        //declare my buttons........................................................................
        fab_power = findViewById(R.id.floatingActionButton_power);
        fab_graph = findViewById(R.id.floatingActionButton_homeGraph);
        fab_exit = findViewById(R.id.floatingActionButton_FullRemoteExit);
        button_temp = findViewById(R.id.button_temp);
        button_timer = findViewById(R.id.button_timer);
        button_plus = findViewById(R.id.button_plus);
        button_minus = findViewById(R.id.button_minus);

        acNameStatusP2 = (TextView)findViewById(R.id.textView_acNameStatusP2);

        progressDialog=new ProgressDialog(this);
        connectionClass = new ConnectionClass();

        fab_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_power();
            }
        });

        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_plus();
            }
        });

        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e4Csg1Macc_minus();
            }
        });

        fab_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the graph screen.....................................................................
                Intent intent = new Intent(FullRemote.this, Graph.class);
                intent.putExtra("user", nameTextPassStr);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FullRemote.this,LoginHome.class);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        button_temp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                button_timer.setPressed(false);
                button_temp.setPressed(true);
                return true;
            }
        });

        button_timer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                button_temp.setPressed(false);
                button_timer.setPressed(true);
                return true;
            }
        });

        intTempValue = startTemp;

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

    private void e4Csg1MACC_toDecode(){
        //if its clim 1
        if (ACidInfo == 100){
            switch (commandMessage){
                case 100 : commandMessage = 10; break;  //on
                case 101 : commandMessage = 11; break;  //off
                case 116 : commandMessage = 12; break;  //16
                case 117 : commandMessage = 13; break;  //17
                case 118 : commandMessage = 14; break;  //18
                case 119 : commandMessage = 15; break;  //19
                case 120 : commandMessage = 16; break;  //20
                case 121 : commandMessage = 17; break;  //21
                case 122 : commandMessage = 18; break;  //22
                case 123 : commandMessage = 19; break;  //23
                case 124 : commandMessage = 20; break;  //24
                case 125 : commandMessage = 21; break;  //25
                case 126 : commandMessage = 22; break;  //26
                case 127 : commandMessage = 23; break;  //27
                case 128 : commandMessage = 24; break;  //28
                case 129 : commandMessage = 25; break;  //29
                case 130 : commandMessage = 26; break;  //30
            }
        }
        //if its clim2
        if (ACidInfo == 200){
            switch (commandMessage){
                case 200 : commandMessage = 50; break;  //on
                case 201 : commandMessage = 51; break;  //off
                case 216 : commandMessage = 52; break;  //16
                case 217 : commandMessage = 53; break;  //17
                case 218 : commandMessage = 54; break;  //18
                case 219 : commandMessage = 55; break;  //19
                case 220 : commandMessage = 56; break;  //20
                case 221 : commandMessage = 57; break;  //21
                case 222 : commandMessage = 58; break;  //22
                case 223 : commandMessage = 59; break;  //23
                case 224 : commandMessage = 60; break;  //24
                case 225 : commandMessage = 61; break;  //25
                case 226 : commandMessage = 62; break;  //26
                case 227 : commandMessage = 63; break;  //27
                case 228 : commandMessage = 64; break;  //28
                case 229 : commandMessage = 65; break;  //29
                case 230 : commandMessage = 66; break;  //30
            }
        }
        //if its all clims
        if (ACidInfo == 300){
            switch (commandMessage){
                case 300 : commandMessage = 100; break;  //on
                case 301 : commandMessage = 101; break;  //off
                case 316 : commandMessage = 102; break;  //16
                case 317 : commandMessage = 103; break;  //17
                case 318 : commandMessage = 104; break;  //18
                case 319 : commandMessage = 105; break;  //19
                case 320 : commandMessage = 106; break;  //20
                case 321 : commandMessage = 107; break;  //21
                case 322 : commandMessage = 108; break;  //22
                case 323 : commandMessage = 109; break;  //23
                case 324 : commandMessage = 110; break;  //24
                case 325 : commandMessage = 111; break;  //25
                case 326 : commandMessage = 112; break;  //26
                case 327 : commandMessage = 113; break;  //27
                case 328 : commandMessage = 114; break;  //28
                case 329 : commandMessage = 115; break;  //29
                case 330 : commandMessage = 116; break;  //30
            }
        }
    }

    private void e4Csg1Macc_power() {
        TextView tv = (TextView) findViewById(R.id.textView_tempNumber_FullRemote);
        if(button_temp.isPressed()){
            if (power == 0) {
                //button on.........................................................................
                if (intTempValue == startTemp) {
                    power = 1;
                    acNameStatusP2.setText("On");

                    //prepare message
                    commandMessage = ACidInfo /*+ startTemp*/;

                    e4Csg1MACC_toDecode();

                    //send socket command to ESP32
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    //show starter temperature when app first launches
                    tv.setText(""+startTemp);
                    Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                }else{
                    power = 1;
                    acNameStatusP2.setText("On");

                    //prepare message
                    commandMessage = ACidInfo /*+ intTempValue*/;

                    e4Csg1MACC_toDecode();

                    //send socket command to ESP32
                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    //show starter temperature when app already launched
                    tv.setText(""+intTempValue);
                    Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                }
            } else {
                //button off............................................................................
                power = 0;
                acNameStatusP2.setText("Off");

                commandMessage = ACidInfo+1;

                e4Csg1MACC_toDecode();

                E4sendSocket ss = new E4sendSocket();
                ss.execute();

                tv.setText(" ");
                Toast.makeText(getBaseContext(),"Command "+commandMessage,Toast.LENGTH_SHORT).show();
            }
        }else{
            //button Temp or timer is not pressed
            Toast.makeText(getBaseContext(),"Bouton TEMP ou TIMER n'est pas appuyer",Toast.LENGTH_LONG).show();
        }
    }

    private void e4Csg1Macc_plus(){
        TextView tv = (TextView) findViewById(R.id.textView_tempNumber_FullRemote);
        if(button_temp.isPressed()){
            if (power == 1) {
                if (intTempValue < 30) {
                    //prepare command
                    intTempValue++;
                    commandMessage = ACidInfo+intTempValue;

                    e4Csg1MACC_toDecode();

                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    tv.setText(""+intTempValue);
                    Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(),"AC : "+ACname+" is off",Toast.LENGTH_SHORT).show();
            }
        }else{
            //button Temp or timer is not pressed
            Toast.makeText(getBaseContext(),"Bouton TEMP ou TIMER n'est pas appuyer",Toast.LENGTH_LONG).show();
        }
    }

    private void e4Csg1Macc_minus(){
        TextView tv = (TextView) findViewById(R.id.textView_tempNumber_FullRemote);
        if(button_temp.isPressed()){
            if (power == 1) {
                if (intTempValue > 16) {
                    if (intTempValue > lastTempInstruction) {
                        //prepare command
                        intTempValue--;
                        commandMessage = ACidInfo + intTempValue;

                        e4Csg1MACC_toDecode();

                        E4sendSocket ss = new E4sendSocket();
                        ss.execute();

                        tv.setText("" + intTempValue);
                        Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "La temperature est fixer a "+lastTempInstruction+"°C", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(getBaseContext(),"AC : "+ACname+" is off",Toast.LENGTH_SHORT).show();
            }
        }else {
            //button Temp or timer is not pressed
            Toast.makeText(getBaseContext(),"Bouton TEMP ou TIMER n'est pas appuyer",Toast.LENGTH_LONG).show();
        }
    }

    //this sub class is serve to execute e4Csg1MACC_sendSocket() in the background..................
    private class E4sendSocket extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            Character b = (char) commandMessage;
                try {
                String host = "93.121.180.47"; //93.121.180.74  192.168.4.1
                int port = 1060;
                s = new Socket(host, port);
                pw = new PrintWriter(s.getOutputStream());
                pw.write(b);
                pw.flush();
                pw.close();
                s.close();
                Log.e("SOCKET int:", String.valueOf(commandMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //swipe felt or right...........................................................................
    @Override
    public boolean onTouchEvent (MotionEvent event){
        this.gestureObjet.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //create the gesture Objet class
    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        //SimpleOnGestureListener is a lister for what we want to do

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float veloccityX, float veloccityY) {
            if (event2.getX() > event1.getX()) {
                //left
                Intent intent = new Intent(FullRemote.this, Home.class);
                intent.putExtra("user", nameTextPassStr);
                startActivity(intent);
                finish();
                System.exit(0);

            } else if (event2.getX() < event1.getX()) {
                //right
            }
            return true;

        }
    }
}
