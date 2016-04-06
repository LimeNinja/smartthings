/*
 *  HTTP over TLS (HTTPS) example sketch
 *
 *  This example demonstrates how to use
 *  WiFiClientSecure class to access HTTPS API.
 *  We fetch and display the status of
 *  esp8266/Arduino project continuous integration
 *  build.
 *
 *  Created by Ivan Grokhotkov, 2015.
 *  This example is in public domain.
 */

#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <TimedAction.h>        // http://www.arduino.cc/playground/Code/TimedAction
#include <DHT.h>

//Constants
#define ledPin D4
#define dhtPin D2     // what pin we're connected to
#define dhtType DHT22   // DHT 22  (AM2302)

// Init DHT22
DHT dht(dhtPin, dhtType); //// Initialize DHT sensor for normal 16mhz Arduino

// Smartthings API
const char* host = "graph-eu01-euwest1.api.smartthings.com";
//const char* host = "graph.api.smartthings.com";

const char* ssid = "";
const char* password = "";

// Smartthings ID's
const char* sensorId = "d51344f1-d629-4214-a629-eff823ea12a0";
const char* lightId = "d7742919-5d32-4e79-8445-0c5a53397010";
const char* appID = "15bd70bf-1947-4aa1-ae7a-0b2d55962101";         //"f2080844-6749-496f-8175-9047f67407fb";
const char* accessToken = "e1192b15-1272-4474-8baf-2fb45cfbfd44";   //"6533fb98-2c5e-4184-88f0-3743f91e47fb";

const int httpsPort = 443;

//Variables
float hum;  //Stores humidity value
float temp; //Stores temperature value

void setup() {
  Serial.begin(115200);
  
  dht.begin();
  Serial.println();
  Serial.print("connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void sendData() {
  sendAttribute("temp");
  sendAttribute("hum");
}

void sendAttribute(String att) {
  // Use WiFiClientSecure class to create TLS connection
  WiFiClientSecure client;
  Serial.print("connecting to ");
  Serial.println(host);
  if (!client.connect(host, httpsPort)) {
    Serial.println("connection failed");
    return;
  }

  String id = sensorId;
  String attribute = "temperature";
  String value = String(temp);
  if (att == "hum") {
    attribute = "humidity";
    value = String(hum);
  }
  
  String url = "/api/smartapps/installations/"+ String(appID) +"/devices/"+ id +"/" + attribute + "?access_token="+ accessToken + "&value=" + value;
  
  Serial.print("requesting URL: ");
  Serial.println(url);

  client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" +
               "User-Agent: BuildFailureDetectorESP8266\r\n" +
               "Connection: close\r\n\r\n");

  Serial.println("request sent");
}

void readDHTValues() {
    //Read data and store it to variables hum and temp
    hum = dht.readHumidity();
    temp= dht.readTemperature();
    //Print temp and humidity values to serial monitor
    Serial.print("Humidity: ");
    Serial.print(hum);
    Serial.print(" %, Temp: ");
    Serial.print(temp);
    Serial.println(" Celsius");
    sendData();
}

//handle timing of action()
TimedAction readDHT = TimedAction(60000,readDHTValues);

void loop() { 
  readDHT.check();
}
