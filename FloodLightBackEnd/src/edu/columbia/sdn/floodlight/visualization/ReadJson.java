package edu.columbia.sdn.floodlight.visualization;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

public class ReadJson {
	
	public static String intToIp(int ip) {
		/* Converts integer based IP field to its equivalent string format */
	    StringBuilder sb = new StringBuilder(15);

	    for (int i = 0; i < 4; i++) {
	        sb.insert(0, Integer.toString(ip & 0xff));

	        if (i < 3) {
	            sb.insert(0, '.');
	        }

	        ip >>= 8;
	    }

	    return sb.toString();
	 }
	
	public static String longDpIdtoString(Long dpId) {
		/* Converts long based switch ID to its equivalent string format */
		StringBuilder sb = new StringBuilder(24);

	    for (int i = 0; i < 8; i++) {
	    	String one_byte = Long.toHexString(dpId & 0xff);
	        sb.insert(0, one_byte);
	        
	        if(one_byte.length() == 1)
	    		sb.insert(0, "0");

	        if (i < 7) {
	            sb.insert(0, ':');
	        }

	        dpId >>= 8;
	    }

	    return sb.toString();
	}
	
	public static String longMactoString(Long mac) {
		/* Converts long based MAC address to its equivalent string format */
		StringBuilder sb = new StringBuilder(18);

	    for (int i = 0; i < 6; i++) {
	    	String one_byte = Long.toHexString(mac & 0xff);
	        sb.insert(0, one_byte);
	        
	        if(one_byte.length() == 1)
	    		sb.insert(0, "0");
	        
	        if (i < 5) {
	            sb.insert(0, ':');
	        }

	        mac >>= 8;
	    }

	    return sb.toString();
	}
	static void readSwitchDetails(String jsonInputString, NetworkModel model) throws ParseException
	{
		//REST API: /wm/core/controller/switches/json
		
		model.switches = new ArrayList<Switch>();
		model.numSwitches = 0;
		/* parse the JSON string and store information in internal data structures */
		JSONArray rootArray = new JSONArray(jsonInputString);
		for(int index = 0; index < rootArray.length(); index++)
		{
			/* Process each switch information */
			JSONObject rootObject = rootArray.getJSONObject(index);
			Switch newSwitch = new Switch();
			
			int actions = rootObject.getInt("actions");
			String dpId = rootObject.getString("dpid");
			
			newSwitch.dpId = new String(dpId);
			
			JSONObject attributes = rootObject.getJSONObject("attributes");
			boolean floodingSupport = attributes.getBoolean("supportsOfppFlood");
			newSwitch.floodSupport = floodingSupport;
			
			int wildCards = attributes.getInt("FastWildcards");
			
			JSONObject descriptionData = attributes.getJSONObject("DescriptionData");
			int length = descriptionData.getInt("length");
			String manufacturer = descriptionData.getString("manufacturerDescription");
			newSwitch.manufacturer = new String(manufacturer);
			
			String hardwareType = descriptionData.getString("hardwareDescription");
			newSwitch.hwType = new String(hardwareType);
			
			String softwareVersion = descriptionData.getString("softwareDescription");
			newSwitch.swVersion = new String(softwareVersion);
			
			String serialNumber = descriptionData.getString("serialNumber");
			String dpDescription = descriptionData.getString("datapathDescription");
			
			boolean ofppTableSupport = attributes.getBoolean("supportsOfppTable");
			
			// "buffers":256,"connectedSince":1385006860983,"capabilities":135,"tables":2
			int numBuffers = rootObject.getInt("buffers");
			newSwitch.buffers = numBuffers;
			
			int cnSince = rootObject.getInt("connectedSince");
			int capab = rootObject.getInt("capabilities");
			int numTables = rootObject.getInt("tables");
			newSwitch.tables = numTables;
			
			String inetAddress = rootObject.getString("inetAddress");
			newSwitch.inetAddress = new String(inetAddress);
			
			/*System.out.printf("actions = %d, dpId = %s flooding: %s wildcards: %d length = %d\n", 
					actions, dpId, floodingSupport?"true":"false", wildCards, length);
			System.out.printf("manufacturer: %s hwType: %s swVersion: %s serialNo: %s dpDescription: %s\n",
					manufacturer, hardwareType, softwareVersion, serialNumber, dpDescription);
			System.out.printf("ofppTableSupport: %s\n", ofppTableSupport?"true":"false");
			System.out.printf("buffers = %d connection = %d capabilities = %d tables = %d\n",
					numBuffers, cnSince, capab, numTables);
			System.out.printf("inetAddress: %s\n", inetAddress);
			*/
			
			JSONArray portArray = rootObject.getJSONArray("ports");
			newSwitch.portList = new ArrayList<SwitchPort>();
			
			for(int pIndex = 0; pIndex < portArray.length(); pIndex++) {
				JSONObject portObject = portArray.getJSONObject(pIndex);
				SwitchPort newPort = new SwitchPort();
				/* Process the link information of each switch */
				
				/* EXAMPLE: "name":"dp1","state":1,"hardwareAddress":"00:23:20:bd:c7:4a",
				 * "portNumber":65534,"config":1,"currentFeatures":0,
				 * "advertisedFeatures":0,"supportedFeatures":0,"peerFeatures":0 */
				String name = portObject.getString("name");
				newPort.name = new String(name);
				
				String hwAddress = portObject.getString("hardwareAddress");
				newPort.hwAddress = new String(hwAddress);
				
				int state =  portObject.getInt("state");
				newPort.state = state;
				
				int portNumber = portObject.getInt("portNumber");
				newPort.port = portNumber;
				
				//System.out.printf("name: %s hwAddress: %s state: %d port: %d\n", 
				//		name, hwAddress, state, portNumber);
				
				newSwitch.portList.add(newPort);
			}
			/* add the switch to the network model */
			model.switches.add(newSwitch);
			/* maintain a count of the total number of switches */
			model.numSwitches++;
		}
	}
	
	static void readLinkDetails(String jsonInputString, NetworkModel model) throws ParseException
	{
		//REST API: /wm/topology/links/json
		model.links = new ArrayList<Link>();
		/* The switches need to be processed before the links */
		if(model.switches == null) {
			System.out.println("readLinkDetails: model.switches is NULL\n");
			return;
		}
		/* The hosts need to be processed before the links */
		if(model.hosts == null) {
			System.out.println("readLinkDetails: model.hosts is NULL\n");
			return;
		}
		
		for(int hostIndex = 0; hostIndex < model.hosts.size(); hostIndex++) {
			/* Process each host */
			Host currHost = model.hosts.get(hostIndex);
			for(int lnIndex = 0; lnIndex < currHost.hostLinks.size(); lnIndex++) {
				/* Process each link of each host */
				String srcSwitch = currHost.hostLinks.get(lnIndex).switchId;
				int port = currHost.hostLinks.get(lnIndex).port;
				/* create a new GENERAL link for the host to the switch */
				Link hostLink = new Link();
				for(int swIndex = 0; swIndex < model.switches.size(); swIndex++) {
					/* Process all the switches and find the switch 
					 * matching the switch id of the host link 
					 */
					String switchId = model.switches.get(swIndex).dpId;
					if(switchId.equals(srcSwitch)) {
						/* convert the information into the format read by visualization module */
						hostLink.target_index = swIndex + model.numHosts;
						hostLink.target_port = port;
						hostLink.targetSwitchId = new String(srcSwitch);
						hostLink.source_index = hostIndex;
						//this is host so no switch id/port
						hostLink.src_port = -1;
						hostLink.srcSwitchId = new String("");
						hostLink.type = new String("host-switch");
						break;
					}
				}
				model.links.add(hostLink);
			}
		}
		
		/* process the link REST API */
		JSONArray rootArray = new JSONArray(jsonInputString);
		for(int index = 0; index < rootArray.length(); index++)
		{
			/* Process each link */
			JSONObject rootObject = rootArray.getJSONObject(index);
			/* Create a new link and store the information fetched from REST API */
			Link newLink = new Link();
			String srcSwitch = rootObject.getString("src-switch");
			newLink.srcSwitchId = new String(srcSwitch);
			int srcPort = rootObject.getInt("src-port");
			newLink.src_port = srcPort;
			String destSwitch = rootObject.getString("dst-switch");
			newLink.targetSwitchId = new String(destSwitch);
			int destPort = rootObject.getInt("dst-port");
			newLink.target_port = destPort;
			
			for(int swIndex = 0; swIndex < model.switches.size(); swIndex++) {
				String switchId = model.switches.get(swIndex).dpId;
				if(switchId.equals(srcSwitch)) {
					/* Match the switch with the link src switch id and store the information
					 * in the format read by visualization module 
					 */
					newLink.source_index = swIndex + model.numHosts;
				}
				if(switchId.equals(destSwitch)) {
					/* Match the swtich with the link dest switch id and store the information
					 * in the format read by visualization module
					 */
					newLink.target_index = swIndex + model.numHosts;
				}
			}
			
			String type = rootObject.getString("type");
			newLink.type = new String(type);
			
			/*System.out.printf("src = %s port = %d dest = %s port = %d type = %s\n",
					srcSwitch, srcPort, destSwitch, destPort, type);
			*/
			/* Add the new link to the network model */
			model.links.add(newLink);
		}
	}
	
	static void readDeviceDetails(String jsonInputString, NetworkModel model) throws ParseException
	{
		//REST API: /wm/device/
		model.hosts = new ArrayList<Host>();
		model.numHosts = 0;
		
		/* Process the device REST API */
		JSONArray rootArray = new JSONArray(jsonInputString);
		for(int index = 0; index < rootArray.length(); index++)
		{
			JSONObject rootObject = rootArray.getJSONObject(index);
			JSONArray tempArr = null;
			boolean addHost = false;
			Host newHost = new Host();
			
			String entityClass = rootObject.getString("entityClass");
			
			String macAddr = new String("");
			tempArr = rootObject.getJSONArray("mac");
			if(tempArr.isNull(0) == false) {
				macAddr = tempArr.getString(0);
			}
			newHost.macAddr = new String(macAddr);
			
			String ipAddr = new String("");
			tempArr = rootObject.getJSONArray("ipv4");
			if(tempArr.isNull(0) == false) {
				ipAddr = tempArr.getString(0);
			}
			newHost.ipAddr = new String(ipAddr);
			
			tempArr = rootObject.getJSONArray("vlan");
			String vLan = new String("");
			if(tempArr.isNull(0) == false)
				vLan = tempArr.getString(0);
			
			JSONArray apArray = rootObject.getJSONArray("attachmentPoint");
			
			if(apArray.isNull(0) == false) {
				/* This is a host and will be added. 
				 * Switches are ignored in this API
				 */
				addHost = true;
				//System.out.printf("entityClass = %s macAddr = %s ipAddr = %s vlan = %s\n",
				//	entityClass, macAddr, ipAddr, vLan);
			}
			
			newHost.hostLinks = new ArrayList<HostLink>();
			
			for(int apIndex = 0; apIndex < apArray.length(); apIndex++) {
				/* process a list of host links. Usually there is only one */
				JSONObject apObject = apArray.getJSONObject(apIndex);
				HostLink newLink = new HostLink();
				int port = apObject.getInt("port");
				newLink.port = port;
				
				String switchId = apObject.getString("switchDPID");
				newLink.switchId = new String(switchId);
				
				/*String errorStatus = new String("");
				if(apObject.isNull("errrorStatus") == false)
					errorStatus = apObject.getString("errorStatus");
				
				System.out.printf("port = %d switchID = %s errorStatus = %s\n",
					port, switchId, errorStatus);
				*/
				/* add the host link to the host */
				newHost.hostLinks.add(newLink);
			}
			if(addHost == true) {
				/* add the host to the network model */
				model.hosts.add(newHost);
				/* maintain a count of the hosts */
				model.numHosts++;
			}
		}
	}
	
	static void readFirewallRules(String jsonInputString, NetworkModel model) throws ParseException
	{
		//REST API: /wm/firewall/rules/json
		
		JSONArray rootArray = new JSONArray(jsonInputString);
		
		/* Switches should be processed before the firewall rules */
		if(model.switches == null) {
			System.out.println("readFirewallRules: model.switches is NULL\n");
			return;
		}
		
		for(int swIndex = 0; swIndex < model.numSwitches; swIndex++) {
			Switch currSwitch = model.switches.get(swIndex);
			/* Initialize the rulelist for each switch */
			currSwitch.ruleList = new ArrayList<FirewallRule>();
		}
		
		for(int index = 0; index < rootArray.length(); index++)
		{
			/* Process each rule */
			JSONObject rootObject = rootArray.getJSONObject(index);
			FirewallRule newRule = new FirewallRule();
			boolean allSwitches = false;
			
			newRule.ruleid = rootObject.getLong("ruleid");
			newRule.dpId = longDpIdtoString(rootObject.getLong("dpid"));
			newRule.switch_in_port = Integer.toString(rootObject.getInt("in_port"));
			newRule.src_mac = longMactoString(rootObject.getLong("dl_src"));
			newRule.dest_mac = longMactoString(rootObject.getLong("dl_dst"));
			newRule.dl_type = Integer.toString(rootObject.getInt("dl_type"));
			newRule.src_ip = intToIp(rootObject.getInt("nw_src_prefix"));
			newRule.src_ip = newRule.src_ip.concat("/" + Integer.toString(rootObject.getInt("nw_src_maskbits")));
			newRule.dest_ip =  intToIp(rootObject.getInt("nw_dst_prefix"));
			newRule.dest_ip =  newRule.dest_ip.concat("/" + Integer.toString(rootObject.getInt("nw_dst_maskbits")));
			newRule.nw_protocol = Integer.toString(rootObject.getInt("nw_proto"));
			newRule.src_port = Integer.toString(rootObject.getInt("tp_src"));
			newRule.dest_port = Integer.toString(rootObject.getInt("tp_dst"));
			newRule.priority = rootObject.getInt("priority");
			newRule.action = rootObject.getString("action");
			
			/* Process wildcards */
			if(rootObject.getBoolean("wildcard_dpid") == true) {
				newRule.dpId = new String("all");
				allSwitches = true;
			}
			if(rootObject.getBoolean("wildcard_in_port") == true) {
				newRule.switch_in_port = new String("any");
			}
			if(rootObject.getBoolean("wildcard_dl_src") == true) {
				newRule.src_mac  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_dl_dst") == true) {
				newRule.dest_mac  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_dl_type") == true) {
				newRule.dl_type  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_nw_src") == true) {
				newRule.src_ip  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_nw_dst") == true) {
				newRule.dest_ip  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_nw_proto") == true) {
				newRule.nw_protocol  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_tp_src") == true) {
				newRule.src_port  = new String("any");
			}
			if(rootObject.getBoolean("wildcard_tp_dst") == true) {
				newRule.dest_port  = new String("any");
			}
			
			/*
			System.out.printf("Switch ID: %s\n", newRule.dpId);
			
			System.out.printf("ruleId = %d, swPort = %s src-mac = %s dest-mac = %s dl-type = %s\n",
					newRule.ruleid, newRule.switch_in_port, newRule.src_mac, newRule.dest_mac, newRule.dl_type);
			
			System.out.printf("src-ip = %s dest-ip = %s nw-proto = %s src-port = %s dest-port = %s\n",
					newRule.src_ip, newRule.dest_ip, newRule.nw_protocol, newRule.src_port, newRule.dest_port);
			
			System.out.printf("priority = %d action = %s\n\n", newRule.priority, newRule.action);
			*/
			for(int swIndex = 0; swIndex < model.numSwitches; swIndex++) {
				Switch currSwitch = model.switches.get(swIndex);
				String switchId = currSwitch.dpId;
				if(allSwitches == true) {
					/* add rule to this switch */
					currSwitch.ruleList.add(newRule);
				} else if(switchId.equals(newRule.dpId)) {
					/* Add this rule to ONLY this switch since the 
					 * rule is specific to this switch */
					currSwitch.ruleList.add(newRule);
					break;
				}
			}
		}
	}
}