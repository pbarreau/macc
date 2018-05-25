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

    private TextView acNameStatusP1,acNameStatusP2;

    private FloatingActionButton fab_power,fab_graph,fab_exit;
    private Button button_plus,button_minus,button_temp,button_timer;

    private String nameTextPassStr,ACname;

    private int ACidInfo;
    private int power = 0;
    private int intTempValue;
    private int startTemp = 26;
    private int commandMessage;

    private boolean switcher =true;

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
        nameText.setText("Hello\n"+name);

        //get and set the class location
        TextView className = (TextView) findViewById(R.id.textView_className_FullRemote);
        String salle = e4Csg1MACC_getWifiSSID();
        className.setText("Salle:\n"+ salle);

        //get selected AC name
        ACname = getIntent().getStringExtra("ACName");
        TextView ACNameStatusP1 = (TextView)findViewById(R.id.textView_acNameStatusP1);
        ACNameStatusP1.setText(ACname + " : ");

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

        acNameStatusP1 = (TextView)findViewById(R.id.textView_acNameStatusP1);
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

        //mettre nom de la clim
        acNameStatusP1.setText("clim ");
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

    private void e4Csg1Macc_power() {
        TextView tv = (TextView) findViewById(R.id.textView_tempNumber_FullRemote);
        if(button_temp.isPressed()){
            if (power == 0) {
                //button on.........................................................................
                if (intTempValue == startTemp) {
                    power = 1;
                    acNameStatusP2.setText("On");

                    //prepare message
                    commandMessage = ACidInfo + startTemp;

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
                    commandMessage = ACidInfo + intTempValue;

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

                commandMessage = ACidInfo+0;

                E4sendSocket ss = new E4sendSocket();
                ss.execute();

                tv.setText(" ");
                Toast.makeText(getBaseContext(),"Command "+commandMessage,Toast.LENGTH_SHORT).show();
            }
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

                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    tv.setText(""+intTempValue);
                    Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(),"AC : "+ACname+" is off",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void e4Csg1Macc_minus(){
        TextView tv = (TextView) findViewById(R.id.textView_tempNumber_FullRemote);
        if(button_temp.isPressed()){
            if (power == 1) {
                if (intTempValue > 16) {
                    //prepare command
                    intTempValue--;
                    commandMessage = ACidInfo+intTempValue;

                    E4sendSocket ss = new E4sendSocket();
                    ss.execute();

                    tv.setText(""+intTempValue);
                    Toast.makeText(getBaseContext(), "Command " + commandMessage, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(),"AC : "+ACname+" is off",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this sub class is serve to execute e4Csg1MACC_sendSocket() in the background..................
    private class E4sendSocket extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
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
