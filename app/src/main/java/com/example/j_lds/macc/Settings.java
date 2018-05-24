package com.example.j_lds.macc;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    private ArrayList<String> arrayListLanguages = new ArrayList<String>();

    private FloatingActionButton fab_graph,fab_exit;
    private Button button_cancel,button_apply;

    private String userName,selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Hides MACC bar at the top.................................................................
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        button_cancel = (Button)findViewById(R.id.button_cancelSettings);
        button_apply = (Button)findViewById(R.id.button_applySettings);
        fab_graph = findViewById(R.id.floatingActionButton_graphSettings);
        fab_exit = findViewById(R.id.floatingActionButton_exitSettings);

        userName = getIntent().getStringExtra("user");

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(Settings.this, Home.class);
                intent.putExtra("user", userName);
                startActivity(intent);
            }
        });

        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the graph screen.....................................................................
                Intent intent = new Intent(Settings.this, Graph.class);
                intent.putExtra("user", userName);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        fab_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this,LoginHome.class);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });
    }

    void getLanguage(){
        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner_language);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayListLanguages);

        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        //when spinner selected an item in the list
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = parent.getItemAtPosition(position).toString();

                if(selectedLanguage.equals("Please press to select an AC")){
                    Toast.makeText(getBaseContext(), "Please select an AC", Toast.LENGTH_SHORT).show();
                }
                if(!selectedLanguage.equals("Please press to select an AC")){
                    Toast.makeText(getBaseContext(), "AC selected : " + selectedLanguage, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }

        });
    }

}
