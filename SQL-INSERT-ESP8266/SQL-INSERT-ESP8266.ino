/*
  MySQL Connector/Arduino Example : basic insert

  This example demonstrates how to issue an INSERT query to store data in a
  table. For this, we will create a special database and table for testing.
  The following are the SQL commands you will need to run in order to setup
  your database for running this sketch.

  CREATE DATABASE test_arduino;
  CREATE TABLE test_arduino.hello_arduino (
    num integer primary key auto_increment,
    message char(40),
    recorded timestamp
  );

  Here we see one database and a table with three fields; a primary key that
  is an auto_increment, a string, and a timestamp. This will demonstrate how
  to save a date and time of when the row was inserted, which can help you
  determine when data was recorded or updated.

  INSTRUCTIONS FOR USE

  1) Create the database and table as shown above.
  2) Change the address of the server to the IP address of the MySQL server
  3) Change the user and password to a valid MySQL user and password
  4) Connect a USB cable to your Arduino
  5) Select the correct board and port
  6) Compile and upload the sketch to your Arduino
  7) Once uploaded, open Serial Monitor (use 115200 speed) and observe
  8) After the sketch has run for some time, open a mysql client and issue
     the command: "SELECT * FROM test_arduino.hello_arduino" to see the data
     recorded. Note the field values and how the database handles both the
     auto_increment and timestamp fields for us. You can clear the data with
     "DELETE FROM test_arduino.hello_arduino".

  Note: The MAC address can be anything so long as it is unique on your network.

  Created by: Dr. Charles A. Bell
*/

#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>

char ssidWIFI[]     = "mediaserv3429";
char passwordWIFI[] = "EFCAC56ED2E1DEE43AF73A7142";
char nomHOST[] = "MACC IR";

IPAddress server_addr(192,168,1,8);  // IP of the MySQL *server* here
char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password


WiFiClient client;
MySQL_Connection conn((Client *)&client);

//sample query
char INSERT_SQL[] = "CREATE DATABASE ESP82662";


void setup() {
Serial.begin(115200);
  delay(10);

// Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssidWIFI);
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssidWIFI, passwordWIFI);
  WiFi.hostname(nomHOST);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("DB - Connecting...");

  //Attente de connexion a la BDD
while (conn.connect(server_addr, 3306, user, password) != true) {
delay(500);
Serial.print ( "." );
}
};
  
void loop() {
   delay(2000);

  Serial.println("Recording data.");

  // Initiate the query class instance
  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
  // Execute the query
  cur_mem->execute(INSERT_SQL);
  // Note: since there are no results, we do not need to read any data
  // Deleting the cursor also frees up memory used
  delete cur_mem;
}
