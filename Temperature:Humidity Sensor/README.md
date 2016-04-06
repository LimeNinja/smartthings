
#Creating cheap Smartthings Temperature sensors using ESP8266 Wifi Module

Home Automation ain’t cheap. And with the Smartthings sensors running at up to £40 ($50 - check) each, kitting out an entire house can be an expensive affair.
We’ve been trying to come up with some nice ways to add sensors and components to our smart home, without spending too much and also without compromising too much either.

We’re going to be building a temperature and humidity sensor for under £10. We’ll also show you how to integrate the sensor with your Smartthings hub and create a new device using the device handler code.

The sensor will be wifi enabled and will periodically post it’s data to Smartthings (or any other REST based client) using the ESP8266 chip.

##ESP8266
The ESP8266 is an amazingly cheap, simple and easy to use wifi module. It’s available from as little as £1-2 for the module itself. I’m going to be using the WeMos D1 Mini, which includes the wifi chip, and also a USB port for easy programming and power supply. The downside? - power consumption. While it doesn’t use an extraordinary amount of power, it is considerably more than zigbee modules that can run off a coin cell. I did consider running the sensor of 2x AA batteries but this would give me around a years worth of readings if the sensor was transmitting every 15 minutes. 
I decided that I wanted a more up-to-date readings (every 60 seconds) and so have decided to power the sensor using a micro usb cable.

I’d be interested to hear if anyone else decides to go with the battery option…


##Components
The basic components required are:
1 x WeMos D1 Mini				£2.66		http://tinyurl.com/gnl4dsk
1 x DHT22 Temperature Sensor		£1.80		http://tinyurl.com/zopxohh

And that’s it… simple eh?

You’ll also need a micro usb cable for power, a breadboard, wire etc but that’s all fairly standard.
I’ve included below the enclosure i’ve used but this is probably more down to personal taste.

1 x Enclosure					£0.71		http://tinyurl.com/h7tpf3t

##Wiring
Wiring the module is fairly simple. 

![alt tag](http://i0.wp.com/beerandchips.azurewebsites.net/wp-content/uploads/2015/12/dht22-pinout.jpg)

Plug your EP8266 development board into a breadboard
Similarly, plug your DHT22 temperature sensor into the breadboard
Connect pin 1 on the DHT22 to the 3.3v output of the ESP8266
Connect pin 2 on the DHT2 to D4 on the ESP8266
Connect pin 4 on the DHT22 to GND on the ESP8266

##Code
There are a couple of prerequisites needed in order to use the sensor in smartthings.

First you'll need to use the Smartthings API to create a new device handler. Copy the code from temperature-humidity-sensor.groovy.

Second create a new device, give it a name and a device type of 'Temperature/Humidity Sensor'. This should be near the bottom of the dropdown list.
The device network ID can be anyhthing so make something up...

On the next screen you'll need to copy the device ID from the url.
For example, my url is: https://graph-eu01-euwest1.api.smartthings.com/device/show/*02e3437b-4ae4-436a-8cab-a1674ce96f90*
You want that bit in bold. Save that ID for later

Finally you'll need to install the 'LimeNinja (Connect)' Smartapp. Do this by creating a new smartapp, copy the code from *here*
You'll also need to enable Oauth in the app's settings.

Then in the smartthings app, install 'LimeNinja (Connect)'
The Connect app has many functions including connecting to the LimeNinja bridge (read about that *here*)
To keep things simple, just ignore the top section and move on to selecting sensors. Select your new sensor from the list.

At the bottom of the smartapp, click config. This will open a screen with a load of JSON you don't need to worry about. For the time being we just need the "app_id" and the "access_token".
The easiest way to get these is to email them to yourself. Again, save these ID's for the next step!

###Arduino
To program the chip, use the temperature-humidity-sensor.ini
There are a couple of configuration changes you will need to make:

1. First, make sure the correct 'host' is defined. I'm in the UK, so our API host address is "graph-eu01-euwest1.api.smartthings.com"
2. Edit the sensorID, appID and accessToken from the data you gathered earlier. This identifies the device and smartapp in Smartthings that the sensor will communicate with.
3. Change the ssid and password to match your home wifi network so the module can connect.

You're good to go. Upload the code and your sensor will post it's readings directly to Smartthings!