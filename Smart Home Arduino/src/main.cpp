/*
 Este sketch permite la conexión a un servidor MQTT que:
    - publica los datos de temperatura y humedad recopilados del sensor DHT11 en los topics
    casa/temperatura y casa/humedad, respecitivamente.
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
 
// Definimos el pin digital donde se conecta el sensor de temperaturan y humedad
#define DHT_PIN 14
// Definimos que tipo de sensor se utiliza ( DHT11 ó DHT22 )
#define DHTTYPE DHT11
// Definimos el pin digital donde se conecta el LED REGULABLE
#define LED_REG_PIN 2
// Definimos el pin digital donde se conecta el LED 
#define LED_PIN 4

/* ----------> DECLARACIÓN DE VARIABLE <---------- */ 
// DATOS de la conexión WiFi
//const char* ssid = "WiFi408 2.4GHz Cocina";
//const char* password = "0041613798";
const char* ssid = "Honor 6X";
const char* password = "8bb91cc04be7";

// DATOS de la conexión MQTT
//const char* mqtt_server = "192.168.0.145";
//const int mqtt_port = 1883;
const char* mqtt_server = "hairdresser.cloudmqtt.com";  // Servidor
const int mqtt_port = 16435;  // Puerto
const char* mqtt_user = "psngmpoh";  // Usuario
const char *mqtt_pass = "1WwW6kwnBrSk";  // Contraseña

// Variables MQTT y WiFi
WiFiClient espClient;
PubSubClient client(espClient);
// Inicializamos el sensor DHT11
DHT dht(DHT_PIN, DHTTYPE);

// Variables para el delay
long previousMillis = 0;        // almacena la última vez que fue actualizada 
// La siguiente variable es un long porque el tiempo, medido en milisegundos,
// se convertirá rápidamente en un número más grande del que puede ser almacenado en un int.
long interval = 2000;           // intervalo de delay (milliseconds)

/* ----------> DECLARACIÓN DE FUNCIONES <---------- */ 
void setup_wifi() 
{
  WiFi.disconnect();
  delay(3000);
  
  // Comenzamos conectándonos a una red WiFi
  Serial.println();
  Serial.println("---> START <---");  
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(300);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("Your IP address is ");
  Serial.println(WiFi.localIP().toString());
  client.setServer(mqtt_server, mqtt_port);  
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
    
    // Intenro de conexión
    //if (client.connect(clientId.c_str())) // sin usuario y contraseña
    if (client.connect(clientId.c_str(),mqtt_user,mqtt_pass))  // con usuario y contraseña     
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
  pinMode(LED_REG_PIN, OUTPUT); //D4 -> LED_REG
  pinMode(LED_PIN, OUTPUT);  //D2 -> LED
  pinMode(DHT_PIN, INPUT);  //D5 -> TEMPERATURA Y HUMEDAD
  Serial.begin(9600);
  dht.begin();
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
      Serial.println(F("Failed to read from DHT sensor!"));
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