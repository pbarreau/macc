#include <esp_event.h>
#include <esp_event_loop.h>
#include <WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>

int cli1= 13100;

//const char* ssidWIFI     = "CANALBOX-428F75";
//const char* passwordWIFI = "FAAC104AA8";
const char* ssidWIFI     = "DESKTOP-GLPMSI";
const char* passwordWIFI = "97197170";

const char* nomHOST = "MACC-V13";
const char* assid = "SALLE-V13";
const char* asecret = "hello97170";

IPAddress server_addr(192, 168, 137, 127); // IP of the MySQL *server* here
//IPAddress server_addr(192, 168, 137, 127); // IP of the MySQL *server* here
char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password

char* CLIM117; 
char* CLIM118; char* CLIM119; char* CLIM120; char* CLIM121; char* CLIM122; char* CLIM123; 
char* CLIM124; char* CLIM125; char* CLIM126; char* CLIM127; char* CLIM128; char* CLIM129; char* CLIM130; char* CLIM1OFF;

char* CLIM217; char* CLIM218; char* CLIM219; char* CLIM220; char* CLIM221; char* CLIM222; char* CLIM223; 
char* CLIM224; char* CLIM225; char* CLIM226; char* CLIM227; char* CLIM228; char* CLIM229; char* CLIM230; char* CLIM2OFF;


char EXEC_SQL[512]; // Buffer de creation requete
char SELECT_SQL[] = "SELECT CODE  FROM MACCRemake.TRAME_IR WHERE ID_TRAMEIR=%d";
WiFiClient client;
WiFiServer server(1060); // SOCKET Server TCP sur port 1060

MySQL_Connection conn((Client *)&client);
byte mac_addr[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// Create an instance of the cursor passing in the connection

void setup() {

  WiFi.mode(WIFI_AP_STA);
  //CONNECTION AU WiFi
  Serial.begin(115200);
  delay(10);

  WiFi.begin(ssidWIFI, passwordWIFI);
  WiFi.setHostname(nomHOST);
  Serial.print("Connection a:");
  Serial.println(ssidWIFI);

  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  

  
  Serial.println("");
  Serial.println("WiFi connectée");
  Serial.print(",mon ip est:");
  Serial.println(WiFi.localIP());

  //access point part

  Serial.println("Creation point d'access");
  WiFi.softAP(assid, asecret);
  Serial.print("IP address:\t");
  Serial.println(WiFi.softAPIP());
  Serial.println("Point d'acces:");

  //TRAME1(cli1);
  server.begin();// Lancement du Serveur
}

char* TRAME1(int BDD)
{
  delay(200);
  sprintf(EXEC_SQL, SELECT_SQL, BDD);
  CLIM1OFF = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);  
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+17);
  CLIM117 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+18);
  CLIM118 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+19);
  CLIM119 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+20);
  CLIM120 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+21);
  CLIM121 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+22);
  CLIM122 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+23);
  CLIM123 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+24);
  CLIM124 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+25);
  CLIM125 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+26);
  CLIM126 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+27);
  CLIM127 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+28);
  CLIM128 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+29);
  CLIM129 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  sprintf(EXEC_SQL, SELECT_SQL, BDD+30);
  CLIM130 = SELECT(EXEC_SQL); // exécute la fonction SELECT
  Serial.println(EXEC_SQL);
  delay(20);
  conn.close();
  Serial.println(CLIM117);
  Serial.println(CLIM118);
  Serial.println(CLIM119);
  Serial.println(CLIM120);
  Serial.println(CLIM121);
  Serial.println(CLIM122);
  Serial.println(CLIM123);
  Serial.println(CLIM124);
  Serial.println(CLIM125);
  Serial.println(CLIM126);
  Serial.println(CLIM127);
  Serial.println(CLIM128);
  Serial.println(CLIM129);
  Serial.println(CLIM130);
}

void loop()
{
  if (client)
  {
    int climDispo = (WiFi.softAPgetStationNum()); // detecte le nombre d'appareil connectée
    //climDispo--;    //l'application ne compte pas
    while (client.connected())
    {
      if (client.available())
      {
        int command = client.read(); //recupération des Valeurs
        Serial.print(command); //affichage dans le moniteur serie

        //a partir de 10 clim 1
        //a partir de 50 clim 2
        //a partir de 100 clim 3

        switch (command)
        {
          case 0: client.write(WiFi.softAPgetStationNum());
            Serial.println("Nombre de climatiseur envoyée"); break;          
          case 11:
            SendClim1(CLIM1OFF); break;
          case 10:
            SendClim1(CLIM120); break;
          case 12:
            SendClim1(CLIM117); break;
          case 13:
            SendClim1(CLIM117); break;
          case 14:
            SendClim1(CLIM118); break;
          case 15:
            SendClim1(CLIM119); break;
          case 16:
            SendClim1(CLIM120); break;
          case 17:
            SendClim1(CLIM121); break;
          case 18:
            SendClim1(CLIM122); break;
          case 19:
            SendClim1(CLIM123); break;
          case 20:
            SendClim1(CLIM124); break;
          case 21:
            SendClim1(CLIM125); break;
          case 22:
            SendClim1(CLIM126); break;
          case 23:
            SendClim1(CLIM127); break;
          case 24:
            SendClim1(CLIM128); break;
          case 25:
            SendClim1(CLIM129); break;
          case 26:
            SendClim1(CLIM130); break;
        }
      }

  }
  }
  else
  {
    client = server.available();
  }
      

}

char* SendClim1(char* trame_ir){
IPAddress CLIM1 (192, 168, 4, 101);
            if (client.connect(CLIM1, 1070)) {
              Serial.println("Connexion Etabli");
              client.println(trame_ir);
              Serial.println("Commande Envoye");
              client.stop();
            }
            else
            delay(10);
}


char* SendClim2(char* trame_ir){
IPAddress CLIM2 (192, 168, 4, 102);
            if (client.connect(CLIM2, 1070)) {
              Serial.println("Connexion Etabli");
              client.println(trame_ir);
              Serial.println("Commande Envoye");
              client.stop();
            }
            else
            delay(10);
}


char* SELECT(char* recup) //Fonction de Récupération de valeur dans la BDD
{
  //      Attente de connexion a la BDD
  while (conn.connect(server_addr, 3306, user, password) != true) {
    delay(500);
    Serial.print ( "." );
  }

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
