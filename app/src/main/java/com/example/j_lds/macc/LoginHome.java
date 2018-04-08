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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginHome extends AppCompatActivity {

    EditText user,pass;
    Button login;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_home);

        getSupportActionBar().hide(); //Hides MyFirsApp
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        user = (EditText) findViewById(R.id.User);
        pass= (EditText) findViewById(R.id.pass);
        login= (Button) findViewById(R.id.login);

        connectionClass = new ConnectionClass();

        progressDialog=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin login=new DoLogin();
                login.execute();
            }
        });
    }

    private class DoLogin extends AsyncTask<String,String,String>
    {
        String userStr = user.getText().toString();
        String passStr =pass.getText().toString();
        String z="";
        boolean isSuccess=false;

        String userDB, passDB;


        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (userStr.trim().equals("") || passStr.trim().equals(""))
                z = "Please enter all fields....";
            else {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query = " select * from PROFESSEUR where NOM='" + userStr + "' and MDP = '" + passStr + "'";
                        Statement stmt = con.createStatement();
                        //stmt.executeUpdate(query);
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            userDB = rs.getString(2);
                            passDB = rs.getString(5);

                            if (userDB.equals(userStr) && passDB.equals(passStr)) {
                                isSuccess = true;
                                z = "Login successfull";
                            } else {
                                isSuccess = false;
                            }
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions" + ex;
                }
            }
            return z;
        }
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            if(isSuccess) {
                Intent intent=new Intent(LoginHome.this,ClimHome.class);
                intent.putExtra("user", userStr);
                startActivity(intent);
            }
            progressDialog.hide();
        }
    }
}
