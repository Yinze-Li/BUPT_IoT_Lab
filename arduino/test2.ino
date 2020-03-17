#include <gprs.h>
#include <SoftwareSerial.h>
#include <dht11.h>
#include <UTFT.h>

GPRS gprs;
dht11 DHT11;

#define DHT11PIN A2


UTFT myGLCD(QD_TFT180A,7,8,5,4,6); 
extern uint8_t SmallFont[];
extern uint8_t BigFont[];
extern uint8_t SevenSegNumFont[];
int timedelay;  
int temline;
int humline;
int warn;

bool gprsInit()
{
  gprs.preInit();
  int i = 0;
  while(0 != gprs.init()) {
    delay(500);
    Serial.println("init error");
    i++;
    if(i>=10){
      return false;
    }
  }  
  while(0 != gprs.connectTCP("183.230.40.40",1811)){
    Serial.println("connect error");
    i++;
    delay(2000);
    if(i >= 3)
      break;
  }
  gprs.closeTCP();
  Serial.println("gprs connect ok");
  i = 0;
  while(!gprs.join()) {  
    Serial.println("gprs join network error");
    delay(2000);
    i++;
    if(i>=5){
      return false;
    }    
  }
  Serial.print("IP Address is ");
  Serial.println(gprs.getIPAddress());
  Serial.println("Init success, start to connect mbed.org...");
  return true;
}

void setup() 
{
  Serial.begin(9600);
  while(!Serial);
  gprs.closeTCP();
  Serial.println("Setup Complete!");
  Serial.println("DHT11 TEST PROGRAM ");
  Serial.print("LIBRARY VERSION: ");
  Serial.println(DHT11LIB_VERSION);
  Serial.println();
  myGLCD.InitLCD();
  pinMode(LED_BUILTIN, OUTPUT);
  while(!Serial);
  Serial.println("startInit");
  gprsInit();
  String identify = gprs.getIdentify();
  Serial.print("identify:");
  Serial.println(identify);
  timedelay = 5000;
  temline = 100;
  humline = 100;
  warn = 0;
}

void loop() 
{
   warn = 0;

   if(Serial.available()){
  	char first = Serial.read();
  	if(first == 'd'){
  		char second = Serial.read();
  		char third = Serial.read();
 	 	timedelay = (second - '0') * 10 + (third - '0');
  		timedelay *= 1000;
  	} else if (first == 't'){
  		char second = Serial.read();
  		char third = Serial.read();
  		temline = (second - '0') * 10 + (third - '0');
  	} else if (first == 'h'){
  		char second = Serial.read();
  		char third = Serial.read();
  		humline = (second - '0') * 10 + (third - '0');
  	}
  }
  
  
  int chk = DHT11.read(DHT11PIN);

  Serial.print("Read sensor: ");
  switch (chk)
  {
    case DHTLIB_OK: 
                Serial.println("OK"); 
                break;
    case DHTLIB_ERROR_CHECKSUM: 
                Serial.println("Checksum error"); 
                break;
    case DHTLIB_ERROR_TIMEOUT: 
                Serial.println("Time out error"); 
                break;
    default: 
                Serial.println("Unknown error"); 
                break;
  }

  char h[2];
  float hum = (float)DHT11.humidity;
  
  h[0] = (int)hum/10 + 48;
  h[1] = (int)hum%10 + 48;
  Serial.print("h");
  Serial.print(h[0]);
  Serial.println(h[1]);
  gprs.connectTCP("183.230.40.40",1811);
  gprs.sendTCPData("*153149#01#hum*");
  gprs.sendTCPData(h);
  gprs.closeTCP();

  float temp = (float)DHT11.temperature;
  char t[2];
  t[0] = (int)temp/10 + 48;
  t[1] = (int)temp%10 + 48;
  Serial.print("t");
  Serial.print(t[0]);
  Serial.println(t[1]);
  gprs.connectTCP("183.230.40.40",1811);
  gprs.sendTCPData("*153149#01#temp*");
  gprs.sendTCPData(t);
  gprs.closeTCP();

  if(hum > humline){
    warn = warn + 1;
  } 
  if(temp > temline){  
    warn = warn + 1;
  } 

  myGLCD.clrScr();//clear
  myGLCD.setFont(SmallFont);
  myGLCD.setColor(255, 255, 255);
  myGLCD.setBackColor(0, 0, 0);
  myGLCD.print("Temp: " + (String)temp, CENTER, 20);
  myGLCD.print("Humid: " + (String)hum, CENTER, 40);
  myGLCD.print("TimeCyc: " + (String)timedelay, CENTER, 60);
  if(warn > 0){
  	myGLCD.print("Warning!!!!", CENTER, 80);
	digitalWrite(LED_BUILTIN, HIGH);
  }
  else if(warn==0){
  	digitalWrite(LED_BUILTIN, LOW);
  }
  delay(timedelay);
}
