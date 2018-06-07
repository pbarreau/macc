#include <WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <esp_event.h>
#include <esp_event_loop.h>


const char* ssidWIFI = "DESKTOP-GLPMSI";
const char* pWIFI    = "97197170";
int SALLE = 14;   // NUMERO DE LA SALLE
char* BATIMENT ="BTV"; // BATIMENT 

uint8_t MAC_array[6];
char MAC_char[18];


IPAddress server_addr(192, 168, 137, 127); // IP du serveur MySQL
char user[] = "pi";              // utilisateur BDD
char password[] = "Simconolat";        // mot de passe BDD
char EXEC_SQL[128]; // Buffer de creation requete
char EXE_SQL[128];
char INSERT_SQL[] = "INSERT INTO `MACC1`.`ESPSALLE` (`NUM_SALLE`, `IP_CLIM`,`ADRESSE_MACESP`, `NOM_BAT`) VALUES ('%d', '%s', '%s', '%s');";
char UPDATE_SQL[] = "UPDATE `MACC1`.`ESPSALLE` SET `IP_CLIM`='%s' WHERE  `ADRESSE_MACESP`='%s';";
WiFiClient client;
MySQL_Connection conn((Client *)&client);




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
 
 WiFi.macAddress(MAC_array);
    for (int i = 0; i < sizeof(MAC_array); ++i){
      sprintf(MAC_char,"%s%02x:",MAC_char,MAC_array[i]);
    }
Serial.println(MAC_char);

Serial.println(WiFi.localIP());
sprintf(EXEC_SQL, INSERT_SQL, SALLE, WiFi.localIP().toString().c_str(), MAC_char, BATIMENT);
Serial.println(EXEC_SQL);
INSERT(EXEC_SQL);  //rajoute dans table ESPSALLE la carte si cela est sa première connexion


delay(500);
Serial.println(MAC_char);
sprintf(EXE_SQL, UPDATE_SQL, WiFi.localIP().toString().c_str(), MAC_char);
Serial.println(EXE_SQL);

}

void loop() {
  //delay(900000); // attendre 15 min
INSERT(EXE_SQL); //met a jour l'adresse IP dans la base de donnée
}



void INSERT(char* requete)
{
  // Initialise l'instance de la classe de requête
  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
  // Execute the query
  cur_mem->execute(requete);
  delete cur_mem;
//comme il n'y a aucun résultat, nous n'avons pas besoin de lire de données
 // La suppression du curseur libère également la mémoire utilisée
}
