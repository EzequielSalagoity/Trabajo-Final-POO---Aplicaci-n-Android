/*
  Este sketch permite la conexión a un servidor MQTT que:
    - publica los datos de temperatura y humedad recopilados del sensor DHT11 en los topics
    casa/temperatura y casa/humedad, respectivamente.
    - recibe información de los topics casa/luz y casa/luz_reg y actua en consecuencia

    Topic: casa/luz. Message: On --> Luz encendida
    Topic: casa/luz. Message: Off --> Luz apagada
    Topic: casa/luz_reg. Message: entero de 0 a 100 --> Intensidad del LED

 Se reconectará al servidor si la conexión se pierde usando una función de reconexión(reconnect).
*/
#include <Arduino.h>
// Incluimos librería para utilizar ESP8266
#include <ESP8266WiFi.h>
// Incluimos librería para utilizar un cliente MQTT
#include <PubSubClient.h>
// Incluimos librería para utilizar sensor de temperatura y humedad
#include "DHT.h"  //Adafruit
#include <ESP8266WebServer.h>
#include <EEPROM.h>
 
// Definimos el pin digital donde se conecta el sensor de temperaturan y humedad
#define DHT_PIN 14
// Definimos que tipo de sensor se utiliza ( DHT11 ó DHT22 )
#define DHTTYPE DHT11
// Definimos el pin digital donde se conecta el LED REGULABLE
#define LED_REG_PIN 2
// Definimos el pin digital donde se conecta el LED 
#define LED_PIN 4

/* ----------> DECLARACIÓN DE VARIABLE <---------- */ 
// Variables MQTT y WiFi
WiFiClient espClient;
PubSubClient client(espClient);

// Variables donde se guardan los datos de configuración ingresados
// en la web
char ssid[40]={'\0'};      
char pass[40]={'\0'};
char broker[40]={'\0'};  
char broker_port[40]={'\0'}; 
char broker_user[40]={'\0'};     
char broker_pass[40]={'\0'};

//  Datos del ACCESS POINT
const char *SSIDConf = "NodeMCU_AP";
const char *PASSWORDConf = "12345678";

String mensaje = "";

ESP8266WebServer server(80);

/* ----------> CÓDIGO HTML PÁGINA DE CONFIGURACÓN <---------- */
String pagina = "<!DOCTYPE html>"
"<html>"
"<head>"
"<title>Configuración NodeMCU</title>"
"<meta charset='UTF-8'>"
"</head>"
"<body>"
"</form>"
"<form action='guardar_conf' method='get'>"
"SSID:<br><br>"
"<input class='input1' name='ssid' type='text'><br>"
"PASSWORD:<br><br>"
"<input class='input1' name='pass' type='password'><br><br>"
"SERVER MQTT:<br><br>"
"<input class='input1' name='broker' type='text'><br><br>"
"PORT:<br><br>"
"<input class='input1' name='broker_port' type='text'><br><br>"
"USER MQTT:<br><br>"
"<input class='input1' name='broker_user' type='text'><br><br>"
"PASSWORD:<br><br>"
"<input class='input1' name='broker_pass' type='password'><br><br>"
"<input class='boton' type='submit' value='GUARDAR'/><br><br>"
"</form>"
"<a href='escanear'><button class='boton'>ESCANEAR</button></a><br><br>";

String paginafin = "</body>"
"</html>";

// Inicializamos el sensor DHT11
DHT dht(DHT_PIN, DHTTYPE);

// Variables para el delay
long previousMillis = 0;        // almacena la última vez que fue actualizada 
// La siguiente variable es un long porque el tiempo, medido en milisegundos,
// se convertirá rápidamente en un número más grande del que puede ser almacenado en un int.
long interval = 2000;           // intervalo de delay (milliseconds)

/* ----------> DECLARACIÓN DE FUNCIONES <---------- */ 

// Función para llamar a la página de configuración
void paginaconf() 
{
  server.send(200, "text/html", pagina + mensaje + paginafin); 
}

// Función para grabar en la EEPROM.
// Recibe la dirección de memoria inicial donde se va a guardar el string
// y almacena hasta 40 bytes de tamaño
void grabar(int addr, String a) 
{
  int tamano = a.length(); 
  char inchar[40]; 
  a.toCharArray(inchar, tamano+1);
  for (int i = 0; i < tamano; i++) 
  {
    EEPROM.write(addr+i, inchar[i]);
  }
  for (int i = tamano; i < 40; i++) 
  {
    EEPROM.write(addr+i, 255);
  }
   EEPROM.commit();
}

// Función para guardar los datos ingresados en la página de configuración
// en la EEPROM
void guardar_conf() 
{
  //Recibimos los valores que envia por GET el formulario web
  Serial.println("--------> DATOS INGRESADOS <--------");
  Serial.println("\t-> SSID: "+ server.arg("ssid"));
  grabar(0,server.arg("ssid"));
  Serial.println("\t-> Password: "+ server.arg("pass"));
  grabar(40,server.arg("pass"));
  Serial.println("\t-> Broker: "+ server.arg("broker"));
  grabar(80,server.arg("broker"));
  Serial.println("\t-> Port: "+ server.arg("broker_port"));
  grabar(120,server.arg("broker_port"));
  Serial.println("\t-> User: "+ server.arg("broker_user"));
  grabar(160,server.arg("broker_user"));
  Serial.println("\t-> Password: "+ server.arg("broker_pass"));
  grabar(200,server.arg("broker_pass"));

  mensaje = "---------> Configuracion Guardada <---------";
  paginaconf();
}

// Función para leer la EEPROM.
// Recibe la dirección del primer elemento que queremos leer en memoria y
// lee un bloque de 40 bytes.
// Retorna un string
String leer(int addr) 
{
   byte lectura;
   String strlectura;
   for (int i = addr; i < addr+40; i++) 
   {
      lectura = EEPROM.read(i);
      if (lectura != 255) 
      {
        strlectura += (char)lectura;
      }
   }
   return strlectura;
}

// Función que escanea las redes WiFi disponibles al momento de ingresar 
// los datos en la página de configuración
void escanear() 
{  
  int n = WiFi.scanNetworks(); //devuelve el número de redes encontradas
  Serial.println("-> Escaneo terminado");
  if (n == 0) //si no encuentra ninguna red
  { 
    Serial.println("-> No se encontraron redes <-");
    mensaje = "no se encontraron redes";
  }  
  else
  {
    Serial.print(n);
    Serial.println(" redes encontradas");
    mensaje = "";
    for (int i = 0; i < n; ++i)
    {
      // agrega al STRING "mensaje" la información de las redes encontradas 
      mensaje = (mensaje) + "<p>" + String(i + 1) + ": " + WiFi.SSID(i) + " (" + WiFi.RSSI(i) + ") Ch: " + WiFi.channel(i) + " Enc: " + WiFi.encryptionType(i) + " </p>\r\n";
      //WiFi.encryptionType 5:WEP 2:WPA/PSK 4:WPA2/PSK 7:open network 8:WPA/WPA2/PSK
      delay(10);
    }
    Serial.println(mensaje);
    paginaconf();
  }
}

// Función que inicializa el modo ACCESS POINT que permite la configuración
// del broker MQTT y de la red WiFi de la NodeMCU
void modoconf() 
{   
  Serial.println("");
  Serial.println("---------------> MODO ACCESS POINT <---------------");
  Serial.println("");
  Serial.println("----> DATOS DEL ACCESS POINT <---");
  Serial.println("\t-> SSID: NodeMCU_AP");  
  Serial.println("\t-> Contraseña: 12345678\n");
  WiFi.softAP(SSIDConf, PASSWORDConf);
  IPAddress myIP = WiFi.softAPIP(); 
  Serial.print("IP del access point: ");
  Serial.println(myIP);
  Serial.println("WebServer iniciado...");

  server.on("/", paginaconf); //esta es la pagina de configuracion

  server.on("/guardar_conf", guardar_conf); //Graba en la EEPROM la configuracion

  server.on("/escanear", escanear); //Escanean las redes wifi disponibles
  
  server.begin();

  while (true) 
  {
      server.handleClient();
  }
}

void setup_wifi() 
{
  WiFi.disconnect();
  delay(3000);
  
  // Comenzamos conectándonos a una red WiFi
  Serial.println();
  Serial.println("---> START <---");  
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, pass);

  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(300);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("Your IP address is ");
  Serial.println(WiFi.localIP().toString());
  client.setServer(broker, atoi(broker_port));  
}

// Función que recibe los mensajes de MQTT
void callback(char* topic, byte* message, unsigned int length) 
{
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (unsigned int i = 0; i < length; i++) 
  {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();
  // Si un mensaje es recibido sobre el topic casa/luz,se chequerá si el mensaje es "on" ó "off".
  // Se cambia el estado de la salida en función del mensaje.
  if( String(topic) == "casa/luz" )
  {
    Serial.print("Changing output to ");
    if(messageTemp == "on")
    {
      Serial.println("ON");
      digitalWrite(LED_PIN, HIGH);
    }
    else if(messageTemp == "off")
    {
      Serial.println("OFF");
      digitalWrite(LED_PIN, LOW);
    }
  }
  // Si un mensaje es recibido sobre el topic casa/luz_reg,se adaptara el rango recibido( 0 a 100 ) al rango
  // de salida del pin del NodeMCU( 0 a 1023 ).
  // Se cambia el estado de la salida en función del mensaje.  
  if( String(topic) == "casa/luz_reg" )
  {    
    int aux = (int)((messageTemp.toInt())*10.24);
   
    analogWrite (LED_REG_PIN,aux);        
  }
 }
  
void reconnect() {
  // Loop hasta que estemos reconectados
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    
    // Creamos un clientId aleatorio
    String clientId = "ESP8266Client";
    clientId += String(random(0xffff), HEX);
    
    // Intento de conexión
    //if (client.connect(clientId.c_str())) // sin usuario y contraseña
    if (client.connect(clientId.c_str(),broker_user,broker_pass))  // con usuario y contraseña     
    {
      Serial.println("Connected");
      // Una vez conectado, se suscribe a los topics
      client.subscribe("casa/luz");
      client.subscribe("casa/luz_reg");
    } 
    else 
    {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Esperamos 5 segundos antes de probar de nuevo
      delay(5000);
    }
  }
}

// ------> Función de Configuración de la NodeMCU <------
void setup() 
{
  Serial.begin(115200);
  pinMode(LED_REG_PIN, OUTPUT); //D4 -> LED_REG
  pinMode(LED_PIN, OUTPUT);  //D2 -> LED
  pinMode(DHT_PIN, INPUT);  //D5 -> TEMPERATURA Y HUMEDAD
  dht.begin();
  EEPROM.begin(512); // Cantidad de bytes
  if (digitalRead(13) == 0) 
  {
    modoconf();
  }  
  leer(0).toCharArray(ssid, 40);
  leer(40).toCharArray(pass, 40);
  leer(80).toCharArray(broker, 40);
  leer(120).toCharArray(broker_port, 40);
  leer(160).toCharArray(broker_user, 40);
  leer(200).toCharArray(broker_pass, 40);
  setup_wifi();
  client.setCallback(callback);  
}

// ------> Loop principal de la NodeMCU <------
void loop() 
{
  if (!client.connected()) {
    reconnect();
  }
  
  client.loop();

  unsigned long currentMillis = millis();
  
  if(currentMillis - previousMillis > 2000)     //Pasaron 2 segundos?
  {
    previousMillis = currentMillis;           //reseteo previousMillis.

    // Leo los datos de temperatura y humedad del sensor DHT11    
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    
    if (isnan(h) || isnan(t)) 
    {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }    
    char charTemp[10];     
    char charHum[10];         
    
    dtostrf(t, 4, 2, charTemp);  //4 is mininum width, 2 is precision; float value is copied onto buff
    dtostrf(h, 4, 2, charHum);  //4 is mininum width, 2 is precision; float value is copied onto buff
    
    client.publish("casa/temperatura",charTemp);
    client.publish("casa/humedad",charHum);
  }     
}