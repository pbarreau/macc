#ifndef UNIT_TEST
#include <Arduino.h>
#endif
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <IRremoteESP8266.h>
#include <IRsend.h>
#include <WiFiClient.h>

const char* ssid = "sengwada";        //Nom du réseau a se connectée
const char* password = "97197170"; //Mot de passe
uint64_t dataoff = 0xB27BE0;
uint64_t dataon = 0xB23F20;

WiFiServer wifiServer(1060);
int clientId = 1;
IRsend irsend(4);  // DEL IR sur GPIO4 (D2)

  
  
  #if SEND_COOLIX
  
switch(c)
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
  wifiServer.begin();
  Serial.println("SocketServer ");
}

void loop(void) {
  server.handleClient();   
}
