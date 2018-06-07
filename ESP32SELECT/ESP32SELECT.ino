 #include <WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <esp_event.h>
#include <esp_event_loop.h>

const char* ssidWIFI = "DESKTOP-GLPMSI";
const char* pWIFI    = "97197170";

//IPAddress server_addr(192, 168, 1, 8); // IP of the MySQL *server* here
IPAddress server_addr(192, 168, 137, 127); // IP of the MySQL *server* here

char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password
char EXEC_SQL[1024]; // Buffer de creation requete
char SELECT_SQL[] = "SELECT RAWDATA from MACC1.TRAME_IR WHERE  IDTRAMEIR=%d";
//char SELECT_SQL[] = "SELECT message FROM testsSnir_2018.test_arduino WHERE client=%d";
int cli=0;
char* sel;
//String a(sel);

WiFiClient client;
MySQL_Connection conn((Client *)&client);
byte mac_addr[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// Create an instance of the cursor passing in the connection

void setup() {
  Serial.begin(115200);
  delay(10);

  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssidWIFI);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssidWIFI, pWIFI);
  WiFi.setHostname("ESP_Salle");

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
  delay(1000);
 sprintf(EXEC_SQL, SELECT_SQL, cli);
 sel = SELECT(EXEC_SQL); // exécute la fonction SELECT
 
 uint16_t My_RawData[400];
 Serial.println(sel);
 
 char *p=strtok(sel, ",");
 int item =0;
 
    while(p != NULL)
   {
      /* 
       * Ici p est un pointeur sur une chaine
       * qui contient exactement l'argument i
       */
      if(item < 400)
      {
         My_RawData[item] = atoi(p);
         item++;
      }
      else
         break;
      /* 
       * On lance un nouvel appel a strtok
       * par contre on lui donne en argument NULL
       * pour qu'il sache que c'est celle du dernier
       * appel, on peut aussi changer les caractères
       * pour parser...
       */
      p = strtok(NULL, " ");
   }
   int i;
   for(i=0; i< item;i++)
   {
    Serial.print(My_RawData[i],DEC);
    Serial.print(" ");
   }
   Serial.println(i);
   }

void loop() {
delay(10000);
}

char* SELECT(char* recup) 
{
  
  //    Serial.println("> Execution de SELECT with dynamically supplied parameter");

  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);

  // preparer le code sql
  

  // Execute the query
  cur_mem->execute(recup);
  //    Serial.println(EXEC_SQL);
  // Fetch the columns and print them
  column_names *cols = cur_mem->get_columns();
  //    Serial.print("ColName:");
  for (int f = 0; f < cols->num_fields; f++) {
    //        Serial.print(cols->fields[f]->name);
    if (f < cols->num_fields - 1) {
      //            Serial.print(',');
    }
  }
  //    Serial.println();
  // Read the rows and print them
  row_values *row = NULL;
  
 do {
    row = cur_mem->get_next_row(); 
    if (row != NULL) {
      for (int f = 0; f < cols->num_fields; f++) {
        if (f == 0) {
          //                   Serial.print("\t");
        }
        char* test = row->values[f];
        //Serial.println(test);
        return test;

        
        if (f < cols->num_fields - 1) {
          //                    Serial.print(',');
        }
      }
      //          Serial.println();
    }
  } 
  while (row != NULL);
  // Deleting the cursor also frees up memory used
  delete cur_mem;
}

