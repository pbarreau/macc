//CODE FINAL ESP8266!
#include <ESP8266WiFi.h>
#include <ESP8266WiFiAP.h>
#include <ESP8266WiFiGeneric.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266WiFiScan.h>
#include <ESP8266WiFiSTA.h>
#include <ESP8266WiFiType.h>
#include <WiFiClient.h>
#include <WiFiClientSecure.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <ESP8266WebServer.h>

//information de connection

char ssidWIFI[]     = "DESKTOP-GLPMSI";
char passwordWIFI[] = "97197170";
char nomHOST[] = "TestAP+STA";
const char* assid = "espPoint";
const char* asecret = "hello971";

IPAddress local_IP(192,168,137,137);
IPAddress gateway(192,168,137,136);
IPAddress subnet(255,255,255,0);

void setup() {
Serial.begin(115200);
  delay(10);
                
// Connect to WiFi network
  Serial.println();
-    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssidWIFI);
    
    WiFi.mode(WIFI_AP_STA); 
  WiFi.begin(ssidWIFI, passwordWIFI);
  WiFi.hostname(nomHOST);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  
  Serial.println("Creation point d'access");
    WiFi.softAP(assid, asecret);
    WiFi.softAPConfig(local_IP, gateway, subnet);
    Serial.print("IP address:\t");
    Serial.println(WiFi.softAPIP());
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
}

void loop() {
  // put your main code here, to run repeatedly:

}
