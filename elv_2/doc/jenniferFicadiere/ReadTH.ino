#include "DHT.h" 
#include <esp_event.h>
#include <esp_event_loop.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <WiFi.h>

//______________________________________________________________
// ici on utilise la broche IO2 de ESP32 pour lire les données 
#define DHTPIN 2
// notre capteur est DHT22 type 
#define DHTTYPE DHT22 
// crée une instance de 
DHT dht(2, DHT22);
//______________________________________________________________

/*
const char* ssidWIFI = "Jenni";// nom du réseau 
const char* pWIFI    = "25111998";      // Mot de passe du réseau 
const char*  nomHOST = "MACC IR";*/

const char* ssidWIFI = "DESKTOP-GLPMSI";// nom du réseau // connexion wifi
const char* pWIFI    = "97197170";      // Mot de passe du réseau 
const char*  nomHOST = "MACC IR";

/*IPAddress server_addr(93,121,180,47);  // IP of the MySQL *server* here // CONNECTION en local 
char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password*/

IPAddress server_addr(192,168,13,91);  // IP of the MySQL *server* here
char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password

WiFiClient client;
MySQL_Connection conn((Client *)&client);

   
//sample query 
char INSERT_SQL[] = " USE `MACC`; INSERT INTO `MACC`.`SALLE_BAT` (`ID_SALLE`,`NOM_BAT`, `TEMPERATURE`, `HUMIDITE`) VALUES ( '14','BTV', '%f', '%f')";
char query[256];
char temperature[10];
char humidity[10];
char DELETE_SQL[] =" FROM SALLE1` WHERE timestamp < UNIX_TIMESTAMP(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))"; // supprimer les données  de un mois 
 // Buffer de creation requete

void setup() {
  Serial.begin(115200);
  Serial.println ("Hello je suis le capteur DHT22!" );
  // appel à commencer à démarrer le capteur
  dht.begin ();

  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to  ");
  Serial.println(ssidWIFI);

 pinMode(2, INPUT);
  //pinMode(sensorPin2, INPUT);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssidWIFI, pWIFI);
  WiFi.setHostname(nomHOST);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("DB - Connecting...");

//Attente de connexion a la BDD
  while(conn.connect(server_addr, 3306, user, password)!= true) {
    delay(200);
    Serial.println ( "." );
  }

  
}
void loop() {
Temp();

}




void Temp(){
   // utilise les fonctions fournies par la bibliothèque. 
  float h = dht.readHumidity ();
  // Lit la température comme Celsius (la valeur par défaut) 
  float t = dht.readTemperature ();
  // Vérifie si les lectures ont échoué et quitte tôt (pour réessayer). 
  if (isnan (h) || isnan (t)) {
    Serial.println ( "Impossible de lire le capteur DHT!" );
    delay(1000);
    return ;
  }
  // afficher le résultat dans Terminal 
  Serial.print ( "Humidite:" );
  Serial.print (h);
  Serial.print ( "% \ " );
  Serial.print ( "Temperature:" );
  Serial.print (t);
  Serial.println ( " *C" );
  delay(1000);

//convertir les float en tableaux de char
 //dtostrf(t, 1, 2, temperature);
 //dtostrf(h, 1, 2, humidity);

// create the DHT22 query
sprintf(query, INSERT_SQL, t, h);
INSERT(query);
  
  Serial.println("DHT22 data recorded");
  Serial.println("Recording data.");

}





void INSERT(char* requete)
{
  // Initiate the query class instance
  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
  // Execute the query
  cur_mem->execute(requete);
  delete cur_mem;}