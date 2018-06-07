/*EN ESP8266
   #include <ESP8266WiFi.h>
  #include <WiFiClient.h>
  #include <ESP8266WebServer.h>

  char ssidWIFI[]     = "DESKTOP-GLPMSI";
  char passwordWIFI[] = "97197170";
  char nomHOST[] = "MACC IR";
*/


#include <WiFi.h>

//const char* ssidWIFI = "DESKTOP-GLPMSI";
//const char* passwordWIFI =  "97197170";

const char* nomHOST = "TestAP+STA";
const char* assid = "SALLE-V13";
const char* asecret = "hello97170";
int clientId = 0;
//IPAddress server(192,168,4,2);
WiFiClient client;

WiFiServer server(1060); // SOCKETServer TCP sur port 1060

void setup( ) {

  Serial.begin(115200);
  delay(10);
  /*
    // Connect to WiFi network
    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssidWIFI);

    WiFi.mode(WIFI_STA);
    WiFi.begin(ssidWIFI, passwordWIFI);
    // WiFi.hostname(nomHOST);

    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }
    Serial.println("");
    Serial.println("WiFi connected");
  */


  WiFi.mode(WIFI_AP);
  Serial.println("Creation point d'access");
  WiFi.softAP(assid, asecret);
  Serial.print("IP address:\t");
  Serial.println(WiFi.softAPIP());
  Serial.println("Point d'acces:");


  server.begin();// Lancement du Serveur
}

//WiFiClient client;
/*
  void loop() {
  client.connect(server, 106);
  client.println("Hello server! Are you sleeping?\r");  // sends the message to the server
  String answer = client.readStringUntil('\r');   // receives the answer from the sever
  Serial.println("from server: " + answer);
  client.flush();
  }
*/



void loop( )
{
  if (client)
  {
    //      Serial.println("actuellement connectée:");
    int clim = (WiFi.softAPgetStationNum());
    clim--;
   Serial.println(clim);
//    Serial.println(clientId);
    while (client.connected()) {
      
      
      
      if (client.available())
      {
        int command = client.read(); //recupération des Valeurs
        Serial.print(command); //affichage dans le moniteur serie
        //client.write(clientId);
        
      }

    
  }
  }
else
{
  client = server.available();
}

}
