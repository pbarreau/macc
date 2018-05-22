package com.example.j_lds.macc;


import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//hostname local 192.168.13.91
// local db 192.168.137.127
//hostname distant 93.121.180.47
// db user MACC
//user pi
//passDB Simconolat

public class ConnectionClass{
    //mes attribustes
    private String classs = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://93.121.180.47/MACC";
    private String userName = "pi";
    private String password = "Simconolat";

    public Connection e4Csg1MACC_CONN() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        try {
            //trying to connect to the database

            Class.forName(classs);
            conn = DriverManager.getConnection(url, userName, password);
            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {
            Log.e("ERRO_1...", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO_2...", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO_3...", e.getMessage());
        }
        return conn;
    }
}
