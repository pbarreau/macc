#ifndef UNIT_TEST
#include <Arduino.h>
#endif
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <IRremoteESP8266.h>
#include <IRsend.h>
#include <WiFiClient.h>

const char* ssid = "SALLE-V13"; //Nom du rÃ©seau a se connectÃ©e
const char* password = "hello97170"; //Mot de passe
const char* host   = "CLIM1";

//se code passe ESP en IP Statique
IPAddress ip(192, 168, 4, 101);
IPAddress gateway(192, 168, 4, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress dns(192, 168, 4, 1);
IRsend irsend(4);  // DEL IR sur GPIO4 (D2)Â²

WiFiClient cliente;
WiFiServer server(1070); // SOCKETServer TCP sur port 1060
void setup() {
  Serial.begin(115200);
  delay(10);

  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.config(ip, dns, gateway, subnet);
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);
  WiFi.hostname("CLIM1");


  
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.print("Votre addresse IP est: ");
  Serial.println(WiFi.localIP());
  server.begin();// Lancement du Serveur
}
void loop()
{ 
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  
  WiFiClient cliente = server.available();
  if (cliente)
  {

    //    Serial.println(clientId);
    while (cliente.connected()) {



      while (cliente.available() > 0) {
        char command = cliente.read(); //recupération des Valeurs
        Serial.print(command); //affichage dans le moniteur serie
        //client.write(clientId);
      }
 
      delay(10);
    }
 
    cliente.stop();
    Serial.println("Client disconnected");

}}
