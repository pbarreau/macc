#include <WiFi.h>

//const char* ssidWIFI = "DESKTOP-GLPMSI";
//const char* passwordWIFI =  "97197170";

const char* nomHOST = "TestAP+STA";
const char* assid = "SALLE-V13";
const char* asecret = "hello97170";
int clientId = 0;
//IPAddress server(192,168,4,2);
WiFiClient client;

void setup() {
Serial.begin(115200);
  delay(10);
  WiFi.mode(WIFI_AP);
  Serial.println("Creation point d'access");
  WiFi.softAP(assid, asecret);
  Serial.print("IP address:\t");
  Serial.println(WiFi.softAPIP());
  Serial.println("Point d'acces:");

}

void loop() {
  delay(5000);
IPAddress R (192, 168, 4, 5);
              if (client.connect(R, 80)) {
    Serial.println("connected to server");
    // Make a HTTP request:
    client.println("GET /ir?code=2 HTTP/1.1");
    client.println("Host: 192.168.4.5");
    client.println("Connection: close");
    client.println();

}}
