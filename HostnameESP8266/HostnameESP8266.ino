#include <ESP8266WiFi.h>

const char* ssid     = "TP-LINK-ESP";
const char* password = "97197170";

void setup() {
  Serial.begin(115200);
  delay(10);

// Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
 //se code passe ESP en IP Statique
  IPAddress ip(192, 168, 0, 18);
  IPAddress gateway(192, 168, 0, 254);
  IPAddress subnet(255, 255, 255, 0);
  IPAddress dns(192, 168, 0, 11);
  
  WiFi.config(ip, dns, gateway, subnet);
  WiFi.begin(ssid, password);
  WiFi.mode(WIFI_STA);
  WiFi.hostname("CLIM1");
  
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
};

void loop() {

    };
//se code permet de renommé le nom d'hôte de L'ESP8266 pars WebPool
