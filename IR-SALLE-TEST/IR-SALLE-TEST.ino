#ifndef UNIT_TEST
#include <Arduino.h>
#endif
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <IRremoteESP8266.h>
#include <IRsend.h>
#include <WiFiClient.h>

const char* ssid = "DESKTOP-GLPMSI";//Nom du réseau a se connectée
const char* password = "97197170"; //Mot de passe
uint64_t dataoff = 0xB27BE0;
uint64_t dataon = 0xB23F20;

MDNSResponder mdns;

ESP8266WebServer server(80);

IRsend irsend(4);  // DEL IR sur GPIO4 (D2)


void handleIr() {
  for (uint8_t i = 0; i < server.args(); i++) {
    if (server.argName(i) == "code") {
      uint32_t code = strtoul(server.arg(i).c_str(), NULL, 10);  
  #if SEND_COOLIX
  
switch(code)
{
  case 1:
  irsend.sendCOOLIX(dataon, 24);
  Serial.println("allume le climatiseur"); 
  break;
  case 2:  
  Serial.println("Eteindre le climatiseur");
  irsend.sendCOOLIX(dataoff, 24);
  break;
  default:
  Serial.print(code);
  Serial.println(" a été envoyé");
}         


  #endif  // FIN de l'emission IR
    }
  }
}



void setup(void) {                        //premier fonction exécuté au demarrage
  irsend.begin();

  Serial.begin(115200);
  WiFi.begin(ssid, password);
  WiFi.hostname("CLIM1");
  Serial.println("");
  

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  
  Serial.print("Votre addresse IP est: ");
  Serial.println(WiFi.localIP());
  if (mdns.begin("esp8266", WiFi.localIP())) {
    Serial.println("MDNS responder started");
  }
  server.on("/ir", handleIr);
  server.begin();
  Serial.println("HTTP server started");
}
void loop(void) {
  server.handleClient();   
}
