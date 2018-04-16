#include <WiFi.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>


// https://github.com/esp8266/Arduino/issues/2100
#include <esp_event.h>
#include <esp_event_loop.h>

#define ledPin  2
#define ledSetOff LOW
#define ledSetOn HIGH

/*
 * AUTEUR : P.BARREAU
 * DATE   : 14/04/2018
 * 
 * Programme de tests connexion tcp + connexion MariaDB
 * A utiliser avec Application QT dispo
 * Adapter les @ du serveur de BDD + login et mot de passe
 * Adapter ssid et pass pour se connecter au reseau qui permet d'atteindre la BDD
 * prendre une clef wifi pc et se connecter au ssid de l'esp ("espAccessPoint")
 * 
 * L'appli QT pc se connecte a l'esp par :
 * addresse en 192.168.4.1 ET par addresse que l'esp a obtenu en se connectant a votre SSID "maison"
 * 
 * SQL : On peut faire des INSERT et des SELECT
 * Le code permet d'avoir 10 clients TCP maxi (tester si reellement possible)
 * chaque client TCP utilise LA connexion MariaDb pour y mettre ses donnees
 */

// Voir exemple MySQL Connector
/*
 * Pour utiliser les acces a la base de donnees: sur le serveur de bases :
 CREATE DATABASE testsSnir_2018;
 CREATE TABLE testsSnir_2018.test_arduino (
    num integer primary key auto_increment,
    client integer,
    message char(40),
    value float,
    recorded timestamp
  );
  
 */

// ========= MariaDB Data ===============
WiFiClient client; // Use this for WiFi instead of EthernetClient
MySQL_Connection conn((Client *)&client);
byte mac_addr[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };

IPAddress server_addr(192,168,0,22);  // IP of the MySQL *server* here
char mysql_user[] = "pascal";         // MySQL user login username
char mysql_pass[] = "pascal";         // MySQL user login password

char INSERT_SQL[] = "INSERT INTO testsSnir_2018.test_arduino (client,message,value) VALUES (%d,'%s',%s);";
char SELECT_SQL[] = "SELECT message,value FROM testsSnir_2018.test_arduino WHERE client=%d ORDER BY value DESC;";
char EXEC_SQL[128]; // Buffer de creation requete
char capteur[10]; // convertion des floats
//----------------------------

// A suivre
// https://42bots.com/tutorials/esp8266-example-wi-fi-access-point-web-server-static-ip-remote-control/

// ==========================================
// Local Prototypes
// ==========================================
void pb_traiterClientWifi(void);
void pb_traiterSql(int cId, char *msg);
double pb_obtenirUneValeurDeCapteur(void);
void pb_trouverMsgUtilisateur(int user);
void pb_determinerClients(void);
boolean deviceIP(char* mac_device, String &cb);
// ==========================================
static void initialiserHandler(void);
static esp_err_t event_handler(void *ctx, system_event_t *event);
static esp_err_t event_UneStationSeConnecte(void *ctx, system_event_t *event);
static esp_err_t event_onNewStation(void *ctx, system_event_t *event);

// ==== Config de la borne Maison ===========
//const char* ssid = "HUAWEI-E5186-5D41";
//const char* password =  "NR7RFA0MA2J";
const char* ssid = "Eminent";
const char* password =  "";

// ==== Config De Access Point de cet ESP ===
const char* assid = "espAccessPoint";
const char* asecret = "hello";

// ==== Config TCP Server
#define MAX_CLIENTS 10
#define MAX_LINE_LENGTH 50 
#define USE_PORT  1024

WiFiServer wifiServer(USE_PORT);
WiFiClient *clients[MAX_CLIENTS] = { NULL };
char inputs[MAX_CLIENTS][MAX_LINE_LENGTH] = { 0 };

// ================ Gestion des events =================
// http://blog.podkalicki.com/esp32-wifi-sniffer/
// http://www.instructables.com/id/ESP32-Remote-Control-With-Sockets/
boolean waitingDHCP=false;
char last_mac[18];

void initialiserHandler(void)
{
    //ESP_ERROR_CHECK( esp_event_loop_init(event_handler, NULL) );
    esp_event_loop_set_cb(event_onNewStation,NULL);
}

// =====
esp_err_t event_handler(void *ctx, system_event_t *event)
{ 
    return ESP_OK;
}

// =====
// Manage incoming device connection on ESP access point
esp_err_t event_onNewStation(void *ctx, system_event_t *event) {
    system_event_ap_staconnected_t sta_info;
    
    if(event->event_id == SYSTEM_EVENT_AP_STACONNECTED || event->event_id == SYSTEM_EVENT_AP_STADISCONNECTED){
        sta_info = event->event_info.sta_connected;

        if(event->event_id == SYSTEM_EVENT_AP_STACONNECTED){
        Serial.print("Nouveau bail wifi, ");
        }
        else{
        Serial.print("Deconnexion, ");
        }
        sprintf(last_mac,"%02X:%02X:%02X:%02X:%02X:%02X", MAC2STR(sta_info.mac));
        Serial.printf("address MAC: %s,",last_mac);
        Serial.printf("id: %d\n\r", sta_info.aid);
        waitingDHCP=true;
    }
    return ESP_OK;
}
// =====================================================
void setup() {
    Serial.begin(115200);
    delay(1000);

    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, ledSetOn);
    //delay(500);
     
    // Carte en Point d'acces ET en station (vers un autre point d'acces)
    WiFi.mode(WIFI_AP_STA);
    
    //access point part
    Serial.println("Creation point d'access");
    WiFi.softAP(assid);
    Serial.print("IP address:\t");
    Serial.println(WiFi.softAPIP());
    
    //station part
    Serial.print("Connection a:");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    
    while (WiFi.status() != WL_CONNECTED) {
        delay(1000);
        Serial.println("Recherche...");
    }
    
    Serial.print("Connection ok a:");
    Serial.print(ssid);
    Serial.print(",mon ip est:");
    Serial.println(WiFi.localIP());
    
    // Serveur TCP demarre
    wifiServer.begin();
    
    // Connexion a Maria DB
    Serial.println("Mysql test...");
    if (conn.connect(server_addr, 3306, mysql_user, mysql_pass)) {
        delay(1000);
        Serial.println("Connection reussie...Traitement possible");
        //conn.close();
    }
    else
        Serial.println("Connection failed.");
    
    // initialisation d'un seed pour les nombres aleatoires
    randomSeed(analogRead(0));

    // Mise en place du handler
    initialiserHandler();

    digitalWrite(ledPin, ledSetOff);
}

//----------------------------------------------------------
void loop() {
    pb_traiterClientWifi();
    //pb_determinerClients();
}
//----------------------------------------------------------



//https://arduino.stackexchange.com/questions/31256/multiple-client-server-over-wifi
void pb_traiterClientWifi(void)
{
    WiFiClient unClient = wifiServer.available(); // nouveau client ?
    
    if (unClient) {
        // oui
        Serial.print("Nouveau client tcp :");
        // memoriser son idWifi qqpart
        for (int i=0 ; i<MAX_CLIENTS ; ++i) {
            if (clients[i]==NULL) {
                clients[i] = new WiFiClient(unClient);
                Serial.println(i);
                break;
            }
        }
        //http://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/soft-access-point-class.html#softapgetstationnum
        Serial.printf("Stations actuellement connecte par soft-AP = %d\n\r", WiFi.softAPgetStationNum());
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
            
            // on laisse "\r"
            clients[i]->write(inputs[i]);
            
            // terminer la chaine (on enleve "\r")
            inputs[i][posChar-1]='\0';
            
            // Gerer la comm avec Maria DB
            pb_traiterSql(i,inputs[i]);
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

// stub pour une recuperation de valeur de capteur
double pb_obtenirUneValeurDeCapteur(void)
{
    float val;
    float a = random(500);
    float b = random(1,99);
    val = a+(b/100);
    
    return val;
}

// un utilisteur wifi a envoye un message
void pb_traiterSql(int cId, char *msg)
{
    Serial.println("Nouvelle insertion dans la base...");
    
    // Initiate the query class instance
    MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
    
    // Recuperer valeur capteur
    // https://horlogeskynet.github.io/blog/programming/dtostrf-lexplication
    double uneReponse = pb_obtenirUneValeurDeCapteur();
    dtostrf(uneReponse,6,2,capteur);
    
    // preparer le code sql
    sprintf(EXEC_SQL, INSERT_SQL, cId, msg, capteur);
    
    // Execute the query
    cur_mem->execute(EXEC_SQL);
    Serial.println(EXEC_SQL);
    
    // Note: since there are no results, we do not need to read any data
    // Deleting the cursor also frees up memory used
    delete cur_mem;
    
    // Recherche des infos deja mises par cet utilisateur
    pb_trouverMsgUtilisateur(cId);
}

void pb_trouverMsgUtilisateur(int user)
{
    Serial.println("> Execution de SELECT with dynamically supplied parameter");
    
    MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);
    
    // preparer le code sql
    sprintf(EXEC_SQL, SELECT_SQL, user);
    
    // Execute the query
    cur_mem->execute(EXEC_SQL);
    Serial.println(EXEC_SQL);
    // Fetch the columns and print them
    column_names *cols = cur_mem->get_columns();
    Serial.print("ColName:");
    for (int f = 0; f < cols->num_fields; f++) {
        Serial.print(cols->fields[f]->name);
        if (f < cols->num_fields-1) {
            Serial.print(',');
        }
    }
    Serial.println();
    // Read the rows and print them
    row_values *row = NULL;
    do {
        row = cur_mem->get_next_row();
        if (row != NULL) {
            for (int f = 0; f < cols->num_fields; f++) {
                if(f==0){
                    Serial.print("\t");
                }
                Serial.print(row->values[f]);
                if (f < cols->num_fields-1) {
                    Serial.print(',');
                }
            }
            Serial.println();
        }
    } while (row != NULL);
    // Deleting the cursor also frees up memory used
    delete cur_mem;    
}

#if 0
// https://github.com/esp8266/Arduino/issues/2100
void pb_determinerClients(void)
{
    if (waitingDHCP) {
        String cb;
        if (deviceIP(last_mac,cb)) {
            Serial.println("Ip address :");
            Serial.println(cb); //do something
        } else {
            Serial.println("Problem during ip address request :");
            Serial.println(cb); //do something else
        }
    }
}


boolean deviceIP(char* mac_device, String &cb) {
    
    struct station_info *station_list = wifi_softap_get_station_info();
    
    while (station_list != NULL) {
        char station_mac[18] = {0}; sprintf(station_mac, "%02X:%02X:%02X:%02X:%02X:%02X", MAC2STR(station_list->bssid));
        String station_ip = IPAddress((&station_list->ip)->addr).toString();
        
        if (strcmp(mac_device,station_mac)==0) {
            waitingDHCP=false;
            cb = station_ip;
            return true;
        } 
        
        station_list = STAILQ_NEXT(station_list, next);
    }
    
    wifi_softap_free_station_info();
    cb = "DHCP not ready or bad MAC address";
    return false;
}
#endif
