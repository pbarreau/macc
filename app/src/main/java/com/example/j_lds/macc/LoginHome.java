package com.example.j_lds.macc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginHome extends AppCompatActivity {
    //Attributes....................................................................................
    private EditText user,pass;
    private Button valider;
    private ProgressDialog progressDialog;
    private ConnectionClass connectionClass;

    //My variables..................................................................................
    private String userStr;
    private String passStr;
    private String message ="";
    private boolean isSuccess=false;

    private String userDB = "", passDB = "";

    /*void onCreate(Bundle savedInstanceState) is a predefine method of Android Studio
      that will execute everything inside before the user gets the possibility to interact
      with the screen...............................................................................
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_home);

        //Hides MACC bar at the top.................................................................
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Affect my EditText and Button xml objects to my constructor objects.......................
        user = (EditText) findViewById(R.id.User);
        pass = (EditText) findViewById(R.id.pass);
        valider = (Button) findViewById(R.id.valider);

        userStr = user.getText().toString();
        passStr = pass.getText().toString();

        //Initialize my constructor.................................................................
        connectionClass = new ConnectionClass();
        progressDialog=new ProgressDialog(this);

        //set a click listener to my Login button...................................................
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                E4doLogin login=new E4doLogin();
                login.execute();
            }
        });

    }

    private void e4Csg1MACC_get_db_Data(){/*
        try {
            //call my e4Csg1MACC_CONN() method situated in my ConnectionClass file
            // to establish a connexion with the db.................................................
            //if my connexion is null (does not exit) show a message................................
            Connection con = connectionClass.e4Csg1MACC_CONN();
            if (con == null) {
                message = "Veuillez vérifier votre connexion internet";
            } else {
                //if the connexion exist then :
                //prepare a query
                //execute the query
                //find and match the credentials entered and stored in the db
                //      -if found, show a message saying "valider successfully"
                //      -if not, show a message "Error credential...not match!!!"...................

                String query = " select * from PROFESSEUR where NOM = '"+userStr+"' and MDP = '"+passStr+"'";
                Statement stmt = con.createStatement();
                //stmt.executeUpdate(query);
                ResultSet rs = stmt.executeQuery(query);


                while (rs.next()) {
                    userDB = rs.getString(2);
                    passDB = rs.getString(5);

                    if (userDB.equals(userStr) && passDB.equals(passStr)) {
                        isSuccess = true;
                        message = "Bienvenu";
                    }
                }

                if(!userDB.equals(userStr)|| !passDB.equals(passStr)){
                    message = "Erreur d'identité… Utilisateur et ou mot de passe incorrect!!!";
                }
            }
        }catch (Exception ex) {
            //if the db does not exist
            //if the table does not exist
            //show a message explaining it to the user..............................................
            isSuccess = false;
            message = "Exceptions....." +ex;
            Log.e("Exceptions.....",ex.getMessage());
        }*/
                //if the db is off service
                if(userStr.equals("Barreau")&& passStr.equals("Hello10")){
                    isSuccess = true;
                    message = "Login successfull";
                }
    }

    private class E4doLogin extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            /*while the doInBackground(String... params) : protected is executed
            * show a search symbol and mark "Loading..."............................................*/
            progressDialog.setMessage("Chargement...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //if one or two fields are empty, advice the user.......................................
            if (userStr.trim().equals("") || passStr.trim().equals(""))
                message = "Merci de compléter tous les champs....";
            else {
                //if not execute my "e4Csg1MACC_get_db_Data()"......................................
                e4Csg1MACC_get_db_Data();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            // show the message stored in a variable................................................
            Toast.makeText(getBaseContext(),""+ message,Toast.LENGTH_SHORT).show();

            //if successfully the input data and saved data is the same,
            // give access to the next screen and passe the username................................
            if(isSuccess) {
                Intent intent=new Intent(LoginHome.this,Home.class);    //Home.class
                intent.putExtra("user", userStr);
                startActivity(intent);
            }
            //hide my loading message and symbol....................................................
            progressDialog.hide();
        }
    }
}