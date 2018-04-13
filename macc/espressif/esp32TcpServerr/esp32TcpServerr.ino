#include <WiFi.h>
 
const char* ssid = "HUAWEI-E5186-5D41";
const char* password =  "NR7RFA0MA2J";


WiFiServer wifiServer(1024);
int clientId = 1;
 
void setup() {
 
  Serial.begin(115200);
 
  delay(1000);
 
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi..");
  }
 
  Serial.println("Connected to the WiFi network");
  Serial.println(WiFi.localIP());
 
  wifiServer.begin();
}
 
void loop() {
 
  WiFiClient client = wifiServer.available();
 
  if (client) {
  Serial.print("Nouveau client id:");
  Serial.println(clientId);
  clientId++;
    while (client.connected()) {
 
      while (client.available()>0) {
        char c = client.read();
        client.write(c);
      }
 
      delay(10);
    }
 
    client.stop();
    Serial.println("Client disconnected");
 
  }
}
