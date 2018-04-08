package com.example.j_lds.macc;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClimHome extends AppCompatActivity {

    int on_off = 0 ;
    String unite = "° C";
    Button button_ON_OFF;
    Button button_PLUS;
    Button button_MINUS;
    Button button_MODE;
    Button button_GRAPH;
    Button button_BACK;
    String z = "";

    TextView nametext;

    private static Socket s;
    private static PrintWriter pw;
    private static String host = "192.168.1.21";
    private static String hostTest = "172.20.10.2";
    private static int port = 1060;
    private static int portTest = 1342;
    private static String dataPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clim_home);

        getSupportActionBar().hide(); //Hides MyFirsApp.............................................
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String name=getIntent().getStringExtra("user");
        nametext= (TextView) findViewById(R.id.textView_name);
        nametext.setText("Hello "+name);
        //pass from loginHome the class(la sale) number where the user is suppose to be.

        //declare my buttons........................................................................
        button_ON_OFF = (Button) findViewById(R.id.button_on_off);
        button_PLUS = (Button) findViewById(R.id.button_Plus);
        button_MINUS = (Button) findViewById(R.id.button_minus);
        button_MODE = (Button) findViewById(R.id.button_mode);
        button_GRAPH = (Button) findViewById(R.id.button_Graph);
        button_BACK = (Button) findViewById(R.id.button_backLogin);

        //set button listeners......................................................................
        button_ON_OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_Off();
            }
        });
        button_PLUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus();
            }
        });
        button_MINUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minus();
            }
        });
        button_MODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode();
            }
        });
        button_GRAPH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempGraph();
            }
        });
        button_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backLogin();
            }
        });
    }

    protected void on_Off() {
        Button b = (Button) findViewById(R.id.button_on_off);
        TextView tv = (TextView) findViewById(R.id.textView_tempView);
        int color;

        if (on_off == 0) {
            //button on.............................................................................
            on_off = 1;
            b.setText("Off"); // Arrêt
            color = Color.parseColor("#ff99cc00");
            b.setBackgroundColor(color);

            dataPass = "A";
            tv.setText("26" + unite);

        } else {
            //button off............................................................................
            on_off = 0;
            b.setText("On"); //Marche
            color = Color.parseColor("#ffcc0000");
            b.setBackgroundColor(color);

            dataPass = "B";
            tv.setText("0" + unite);

        }
    }

    protected void plus(){
        //button up.................................................................................
        dataPass = "C";
        myTask mt = new myTask();
        mt.execute();
    }

    protected void minus(){
        //button down...............................................................................
        dataPass = "D";
        myTask mt = new myTask();
        mt.execute();
    }

    protected String mode(){
        //button mode...............................................................................
        String z = "This button is not yet available";
        return z;
/*
        dataPass = "D";
        myTask mt = new myTask();
        mt.execute();
        */
    }

    protected void tempGraph(){
        //open the graph screen.....................................................................
        Intent intent = new Intent(this, TemperatureGraph.class);
        startActivity(intent);
    }

    protected void backLogin(){
        //back......................................................................................
        Intent intent = new Intent(this, LoginHome.class);
        startActivity(intent);
    }

    private class myTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = new Socket(hostTest, portTest);
                pw = new PrintWriter(s.getOutputStream());
                pw.write(dataPass);
                pw.flush();
                pw.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



}
