//#include <string.h>
#include <WiFi.h>

// Prototypes
void pb_traiterClientWifi(void);

const char* ssid = "HUAWEI-E5186-5D41";
const char* password =  "NR7RFA0MA2J";

#define MAX_CLIENTS 10
#define MAX_LINE_LENGTH 50

WiFiServer wifiServer(1024);
WiFiClient *clients[MAX_CLIENTS] = { NULL };
char inputs[MAX_CLIENTS][MAX_LINE_LENGTH] = { 0 };

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

//----------------------------------------------------------
void loop() {
    pb_traiterClientWifi();
}
//----------------------------------------------------------



//https://arduino.stackexchange.com/questions/31256/multiple-client-server-over-wifi
void pb_traiterClientWifi(void)
{
    WiFiClient unClient = wifiServer.available(); // nouveau client ?
    
    if (unClient) {
        // oui
        Serial.print("Nouveau client wifiId:");
        // memoriser son idWifi qqpart
        for (int i=0 ; i<MAX_CLIENTS ; ++i) {
            if (clients[i]==NULL) {
                clients[i] = new WiFiClient(unClient);
                Serial.println(i);
                break;
            }
        }
    }
    
    // est ce qu'un client nous a contacte ?
    for (int i=0 ; i<MAX_CLIENTS ; ++i) {
        // ce client est toujours connecte ?
        // avec des infos a lire
        if (NULL != clients[i] && clients[i]->available() ) {
            //lire la donnee du client jusqu'a "\r"
            Serial.println("Reception msg client");
            
            char newChar;
            int posChar = 0;
            do{             
                newChar = clients[i]->read();
                inputs[i][posChar]=newChar;
                posChar++;         
            }while((newChar != '\r') && (posChar<MAX_LINE_LENGTH-1) && (clients[i]->available()>0));
            
            // terminer la chaine
            inputs[i][posChar]='\0';
            Serial.print("Un msg reply a client wifiId:");
            Serial.println(i);
            
            Serial.print("Msg to send:");
            Serial.println(inputs[i]);
            clients[i]->write(inputs[i]);
        }
        
        // Le client est deconnecte
        if (NULL != clients[i] && (! clients[i]->connected()) ) {
            Serial.print("Un client deconnecte wifiId:");
            Serial.println(i);
            clients[i]->flush();
            delete clients[i];
            clients[i] = NULL;
        }
        
    }
}
