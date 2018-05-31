#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include <MySQL_Connection.h>
#include <MySQL_Cursor.h>
#include <MySQL_Encrypt_Sha1.h>
#include <MySQL_Packet.h>
#include <Time.h>
#include <TimeLib.h>
const char ssid[] = "DESKTOP-GLPMSI";  //  your network SSID (name)
const char pass[] = "97197170";       // your network password

//IPAddress server_addr(192, 168, 137,127); // IP of the MySQL *server* here
IPAddress server_addr(93, 120, 180,47);
char user[] = "pi";              // MySQL user login username
char password[] = "Simconolat";        // MySQL user login password
char EXEC_SQL[128]; // Buffer de creation requete
char SELECT_SQL[] = "SELECT horaire_allumage FROM MACC.FIXER_CONSIGNE WHERE ID_JOUR=%d";
int cli=11;
char* sel;
// NTP Servers:
static const char ntpServerName[] = "us.pool.ntp.org";
//static const char ntpServerName[] = "time.nist.gov";
//static const char ntpServerName[] = "time-a.timefreq.bldrdoc.gov";
//static const char ntpServerName[] = "time-b.timefreq.bldrdoc.gov";
//static const char ntpServerName[] = "time-c.timefreq.bldrdoc.gov";

const int timeZone = -5;     // Central European Time
//const int timeZone = -5;  // Eastern Standard Time (USA)
//const int timeZone = -4;  // Eastern Daylight Time (USA)
//const int timeZone = -8;  // Pacific Standard Time (USA)
//const int timeZone = -7;  // Pacific Daylight Time (USA)


WiFiUDP Udp;
unsigned int localPort = 8888;  // local port to listen for UDP packets

time_t getNtpTime();
void digitalClockDisplay();
void printDigits(int digits);
void sendNTPpacket(IPAddress &address);


WiFiClient client;
MySQL_Connection conn((Client *)&client);
byte mac_addr[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// Create an instance of the cursor passing in the connection
void setup() {
  // put your setup code here, to run once:
Serial.begin(115200);
  while (!Serial) ; // Needed for Leonardo only
  delay(250);
  Serial.println("TimeNTP Example");
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, pass);
  WiFi.setHostname("ESP_Salle");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
   Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("DB - Connecting...");
   while (conn.connect(server_addr, 3306, user, password) != true) {
    delay(500);
    Serial.print ( "." );
  }
  delay(1000);
 sprintf(EXEC_SQL, SELECT_SQL, cli);
 sel = SELECT(EXEC_SQL); // exécute la fonction SELECT
 conn.close();
 Serial.println(sel);
}
time_t prevDisplay = 0; // when the digital clock was displayed
void loop() {
  // put your main code here, to run repeatedly:
  if (timeStatus() != timeNotSet) {
    if (now() != prevDisplay) { //update the display only if time has changed
      prevDisplay = now();
      digitalClockDisplay();
    }
  }

}

char* SELECT(char* recup) 
{
  
  //    Serial.println("> Execution de SELECT with dynamically supplied parameter");

  MySQL_Cursor *cur_mem = new MySQL_Cursor(&conn);

  // preparer le code sql
  

  // Execute the query
  cur_mem->execute(recup);
  //    Serial.println(EXEC_SQL);
  // Fetch the columns and print them
  column_names *cols = cur_mem->get_columns();
  //    Serial.print("ColName:");
  for (int f = 0; f < cols->num_fields; f++) {
    //        Serial.print(cols->fields[f]->name);
    if (f < cols->num_fields - 1) {
      //            Serial.print(',');
    }
  }
  //    Serial.println();
  // Read the rows and print them
  row_values *row = NULL;
  
 do {
    row = cur_mem->get_next_row(); 
    if (row != NULL) {
      for (int f = 0; f < cols->num_fields; f++) {
        if (f == 0) {
          //                   Serial.print("\t");
        }
        char* test = row->values[f];
        //Serial.println(test);
        return test;

        
        if (f < cols->num_fields - 1) {
          //                    Serial.print(',');
        }
      }
      //          Serial.println();
    }
  } 
  while (row != NULL);
  // Deleting the cursor also frees up memory used
  delete cur_mem;
}


void digitalClockDisplay()
{
  // digital clock display of the time
  Serial.print(hour());
  printDigits(minute());
  printDigits(second());
  Serial.print(" ");
}
void printDigits(int digits)
{
  // utility for digital clock display: prints preceding colon and leading 0
  Serial.print(":");
  if (digits < 10)
    Serial.print('0');
  Serial.print(digits);
}
/*-------- NTP code ----------*/

const int NTP_PACKET_SIZE = 48; // NTP time is in the first 48 bytes of message
byte packetBuffer[NTP_PACKET_SIZE]; //buffer to hold incoming & outgoing packets

time_t getNtpTime()
{
  IPAddress ntpServerIP; // NTP server's ip address

  while (Udp.parsePacket() > 0) ; // discard any previously received packets
  Serial.println("Transmit NTP Request");
  // get a random server from the pool
  WiFi.hostByName(ntpServerName, ntpServerIP);
  Serial.print(ntpServerName);
  Serial.print(": ");
  Serial.println(ntpServerIP);
  sendNTPpacket(ntpServerIP);
  uint32_t beginWait = millis();
  while (millis() - beginWait < 1500) {
    int size = Udp.parsePacket();
    if (size >= NTP_PACKET_SIZE) {
      Serial.println("Receive NTP Response");
      Udp.read(packetBuffer, NTP_PACKET_SIZE);  // read packet into the buffer
      unsigned long secsSince1900;
      // convert four bytes starting at location 40 to a long integer
      secsSince1900 =  (unsigned long)packetBuffer[40] << 24;
      secsSince1900 |= (unsigned long)packetBuffer[41] << 16;
      secsSince1900 |= (unsigned long)packetBuffer[42] << 8;
      secsSince1900 |= (unsigned long)packetBuffer[43];
      return secsSince1900 - 2208988800UL + timeZone * SECS_PER_HOUR;
    }
  }
  Serial.println("No NTP Response :-(");
  return 0; // return 0 if unable to get the time
}
11
// send an NTP request to the time server at the given address
void sendNTPpacket(IPAddress &address)
{
  // set all bytes in the buffer to 0
  memset(packetBuffer, 0, NTP_PACKET_SIZE);
  // Initialize values needed to form NTP request
  // (see URL above for details on the packets)
  packetBuffer[0] = 0b11100011;   // LI, Version, Mode
  packetBuffer[1] = 0;     // Stratum, or type of clock
  packetBuffer[2] = 6;     // Polling Interval
  packetBuffer[3] = 0xEC;  // Peer Clock Precision
  // 8 bytes of zero for Root Delay & Root Dispersion
  packetBuffer[12] = 49;
  packetBuffer[13] = 0x4E;
  packetBuffer[14] = 49;
  packetBuffer[15] = 52;
  // all NTP fields have been given values, now
  // you can send a packet requesting a timestamp:
  Udp.beginPacket(address, 123); //NTP requests are to port 123
  Udp.write(packetBuffer, NTP_PACKET_SIZE);
  Udp.endPacket();
}
