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
// Create an instance of the cursor passing in the connection

//sample query
char query[] = "SELECT message FROM testsSnir_2018.test_arduino WHERE client=0;";


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
  

void loop() {row_values *row = NULL;
  long head_count = 0;

  delay(1000);

  Serial.println("1) Demonstrating using a cursor dynamically allocated.");
  // Initiate the query class instance
  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
  // Execute the query
  cur_mem->execute(query);
  // Fetch the columns (required) but we don't use them.
  column_names *columns = cur_mem->get_columns();

  // Read the row (we are only expecting the one)
  do {
    row = cur_mem->get_next_row();
    if (row != NULL) {
      head_count = atol(row->values[0]);
    }
  } while (row != NULL);
  // Deleting the cursor also frees up memory used
  delete cur_mem;

  // Show the result
  Serial.print("  NYC pop = ");
  Serial.println(head_count);

  delay(500);

  Serial.println("2) Demonstrating using a local, global cursor.");
  // Execute the query
  cur_mem->execute(query);
  // Fetch the columns (required) but we don't use them.
  cur_mem->get_columns();
  // Read the row (we are only expecting the one)
  do {
    row = cur_mem->get_next_row();
    if (row != NULL) {
      head_count = atol(row->values[0]);
    }
  } while (row != NULL);
  // Now we close the cursor to free any memory
  cur_mem->close();

  // Show the result but this time do some math on it
  Serial.print("  NYC pop = ");
  Serial.println(head_count);
  Serial.print("  NYC pop increased by 12 = ");
  Serial.println(head_count+12);
}
