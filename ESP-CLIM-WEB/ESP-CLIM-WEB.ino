////CODE FINAL ESP8266-CLIM

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
uint64_t dataoff = 0xB27BE0;
uint64_t dataon = 0xB23F20;
//se code passe ESP en IP Statique
IPAddress ip(192, 168, 4, 101);
IPAddress gateway(192, 168, 4, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress dns(192, 168, 4, 1);

MDNSResponder mdns;

ESP8266WebServer server(80);

IRsend irsend(4);  // DEL IR sur GPIO4 (D2)Â²


void handleIr() {
  for (uint8_t i = 0; i < server.args(); i++) {
    if (server.argName(i) == "code") {
      uint32_t code = strtoul(server.arg(i).c_str(), NULL, 10);
      Serial.println(code);
      SENDIR(code);



      //switch(code)
      //{
      //  case 1:
      //  irsend.sendCOOLIX(dataon, 24);
      //  Serial.println("allume le climatiseur");
      //  break;
      //  case 2:
      //  Serial.println("Eteindre le climatiseur");
      //  irsend.sendCOOLIX(dataoff, 24);
      //  break;
      //  default:
      //  Serial.print(code);
      //  Serial.println(" a Ã©tÃ© envoyÃ©");
      //}



    }
  }
}
  void SENDIR(uint32_t trame) {
#if SEND_COOLIX
    irsend.sendCOOLIX(trame, 24);
    Serial.println(trame);
#endif  // FIN de l'emission IR

  }


  void setup(void) {                        //premier fonction exÃ©cutÃ© au demarrage
    irsend.begin();

    Serial.begin(115200);
    WiFi.config(ip, dns, gateway, subnet);
    WiFi.begin(ssid, password);
    WiFi.hostname(host);
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


