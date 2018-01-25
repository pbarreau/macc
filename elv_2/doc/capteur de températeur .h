
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
#include <OneWire.h>
 
// ---------- Initialisation des variables ---------------------
 
// Variables propres au DS18B20
const int DS18B20_PIN=10;
const int DS18B20_ID=0x28;
// Déclaration de l'objet ds
OneWire ds(DS18B20_PIN); // on pin DS18B20_PIN (a 4.7K resistor is necessary)
 
// Variables générales
float DS18B20_temperature;
const int SERIAL_PORT=9600;
 
void setup() {
// Initialisation du port de communication avec le PC
Serial.begin(SERIAL_PORT);
Serial.println("Initialisation du programme");
}
 
void loop() { 
DS18B20_temperature = getTemperatureDS18b20(); // On lance la fonction d'acquisition de T°
// on affiche la T°
Serial.print("(DS18B20) =>\t temperature: "); 
Serial.println(DS18B20_temperature);
}
 
/* --------------- Acquisition de la température ----------------------------------- */
float getTemperatureDS18b20(){
byte i;
byte data[12];
byte addr[8];
float temp =0.0;
 
//Il n'y a qu'un seul capteur, donc on charge l'unique adresse.
ds.search(addr);
 
// Cette fonction sert à surveiller si la transmission s'est bien passée
if (OneWire::crc8( addr, 7) != addr[7]) {
Serial.println("getTemperatureDS18b20 : <!> CRC is not valid! <!>");
return false;
}
 
// On vérifie que l'élément trouvé est bien un DS18B20
if (addr[0] != DS18B20_ID) {
Serial.println("L'équipement trouvé n'est pas un DS18B20");
return false;
}
 
// Demander au capteur de mémoriser la température et lui laisser 850ms pour le faire (voir datasheet)
ds.reset();
ds.select(addr);
ds.write(0x44);
delay(850);
// Demander au capteur de nous envoyer la température mémorisé
ds.reset();
ds.select(addr);
ds.write(0xBE);
 
// Le MOT reçu du capteur fait 9 octets, on les charge donc un par un dans le tableau data[]
for ( i = 0; i < 9; i++) {
data[i] = ds.read();
}
// Puis on converti la température (*0.0625 car la température est stockée sur 12 bits)
temp = ( (data[1] << 8) + data[0] )*0.0625;
 
return temp;
}