/*
 *  Orvibo Socket
 *
 *  Copyright 2016 LimeNinja
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 metadata {
	definition (name: "Orvibo Socket", namespace: "com.limeninja", author: "LimeNinja") {
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        
        attribute "hubactionMode", "string"
        
        parent: "LimeNinja:LimeNinja (Connect)"
	}

	// simulator metadata
	simulator {}

	// UI tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 3, height: 2, canChangeIcon: true) {
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821"
			state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "switch"
		details (["switch", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
    
    if (description == "updated") {
    	return
    }

	def msg = parseLanMessage(description)
    
    def json = msg.json
    log.debug "JSON: $json"
    log.debug json.data.value
    
    if (json.data.value == false) {
    	log.debug "Setting switch OFF"
    	sendEvent(name: "switch", value: "off")
    }
    
    if (json.data.value == true) {
    	log.debug "Setting switch ON"
    	sendEvent(name: "switch", value: "on")
    }
}


private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {

	def ip = getDataValue("ip")
	def port = getDataValue("port")
	if (!ip || !port) {
		def parts = device.deviceNetworkId.split(":")
		if (parts.length == 2) {
			ip = parts[0]
			port = parts[1]
		} else {
			log.warn "Can't figure out ip and port for device: ${device.id}"
		}
	}
	return convertHexToIP(ip) + ":" + 9000
}


def on() {
	log.debug "Executing 'on'"
    sendEvent(name: "switch", value: "on")
    
    def turnOn = new physicalgraph.device.HubAction(
        method: "PUT",
        path: "/api/v1/devices/${getDataValue("bridge_id")}/",
        headers: [
            HOST: getHostAddress()
        ],
        body: [value: "true"]
    )
    
    return turnOn
}

def off() {
	log.debug "Executing 'off'"
    sendEvent(name: "switch", value: "off")
    
    def turnOff = new physicalgraph.device.HubAction(
        method: "PUT",
        path: "/api/v1/devices/${getDataValue("bridge_id")}/",
        headers: [
            HOST: getHostAddress()
        ],
        body: [value: "false"]
    )
    
    return turnOff
}

def refresh() {
	log.debug "Refreshing"
    def ip = getDataValue("ip")
    log.debug ip
    parent.refresh(ip)
    parent.refreshDevices()
}

def poll() {
	log.debug "Executing 'poll'"
	def test = new physicalgraph.device.HubAction([
		method: "GET",
		path: "/api/v1/devices/56b8c31d9616f6b7235a938d",
		headers: [
			HOST: getHostAddress()
		]], newDNI)
        
    log.debug "SENDING GET"
    
    return test
}



private subscribeAction(path, callbackPath="") {
    log.trace "subscribe($path, $callbackPath)"
    def address = getCallBackAddress()
    def ip = getHostAddress()
	def callback = "<http://${address}/notify$callbackPath>"
    
	log.debug "Address: $address"
    log.debug "Callback: $callback"
    
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: path,
        headers: [
            HOST: ip,
            CALLBACK: callback,
//            NT: "upnp:event",
            TIMEOUT: "Second-28800"
        ]
    )

    log.trace "SUBSCRIBE $path"

    return result
}
