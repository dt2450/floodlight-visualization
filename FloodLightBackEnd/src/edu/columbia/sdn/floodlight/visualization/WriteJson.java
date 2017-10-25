package edu.columbia.sdn.floodlight.visualization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class WriteJson {

	static void createLocalData(NetworkModel model, String fileName) throws IOException{
		/* This function converts the network model data structure to a JSON format
		 * understood by the visualization module and then dumps it to a file
		 */
		String result = null;
		JSONObject rootObj = new JSONObject();
		JSONArray nodesArray = new JSONArray();
		JSONArray linksArray = new JSONArray();
		
		for(int hostIndex = 0; hostIndex < model.numHosts; hostIndex++) {
			/* Process all the hosts one by one */
			JSONObject hostObj = new JSONObject();
			Host currHost = model.hosts.get(hostIndex);
			String hostString = new String("");
			if(currHost.ipAddr.isEmpty() == false) {
				hostString = hostString.concat("IP:" + currHost.ipAddr + "<br>");
			}
			hostString = hostString.concat("MAC:" + currHost.macAddr + "<br>");
			hostObj.put("group", 2);
			hostObj.put("ADDR", hostString);
			nodesArray.put(hostObj);
		}
		
		for(int swIndex = 0; swIndex < model.numSwitches; swIndex++) {
			/* Process all the switches one by one */
			JSONObject switchObj = new JSONObject();
			Switch currSwitch = model.switches.get(swIndex);
			String switchString = new String("");
			switchString = switchString.concat("Switch ID:" + currSwitch.dpId + "<br>");
						
			switchString = switchString.concat("Manufacturer: " + currSwitch.manufacturer + "<br>");
			switchString = switchString.concat("Hardware Type: " + currSwitch.hwType + "<br>");
			switchString = switchString.concat("Software Version: " + currSwitch.swVersion + "<br>");
			switchString = switchString.concat("Number of buffers: " + currSwitch.buffers + "<br>");
			switchString = switchString.concat("Number of Tables: " + currSwitch.tables + "<br>");
			switchString = switchString.concat("inet Address: " + currSwitch.inetAddress + "<br>");
			
			if(currSwitch.ruleList.size() >= 1) {
				switchString = switchString.concat("<br><br>Rules: <br><br>");
				
				for(int rIndex = 0; rIndex < currSwitch.ruleList.size(); rIndex++) {
					FirewallRule currRule = currSwitch.ruleList.get(rIndex);	
					switchString = switchString.concat("Rule " + Integer.toString(rIndex + 1) + ":<br><br>");
					
					switchString = switchString.concat("Switch ID: " + currRule.dpId + "<br>");
					switchString = switchString.concat("rule ID: " + currRule.ruleid + "<br>");
					switchString = switchString.concat("Switch Port: " + currRule.switch_in_port + "<br>");
					switchString = switchString.concat("Source MAC: " + currRule.src_mac + "<br>");
					switchString = switchString.concat("Destination MAC: " + currRule.dest_mac + "<br>");
					switchString = switchString.concat("DL-Type: " + currRule.dl_type + "<br>");
					switchString = switchString.concat("Source IP: " + currRule.src_ip + "<br>");
					switchString = switchString.concat("Destination IP: " + currRule.dest_ip + "<br>");
					switchString = switchString.concat("Transport Protocol: " + currRule.nw_protocol + "<br>");
					switchString = switchString.concat("Source Port: " + currRule.src_port + "<br>");
					switchString = switchString.concat("Destination Port: " + currRule.dest_port + "<br>");
					switchString = switchString.concat("Priority: " + currRule.priority + "<br>");
					switchString = switchString.concat("Action: " + currRule.action + "<br><br>");
				}
			}
			
			switchObj.put("group", 1);
			switchObj.put("ADDR", switchString);
			nodesArray.put(switchObj);
		}
		
		for(int linkIndex = 0; linkIndex < model.links.size(); linkIndex++) {
			/* Process all the links one by one */
			JSONObject linkObj = new JSONObject();
			Link currLink = model.links.get(linkIndex);
			String targetInfo = new String("");
			targetInfo = targetInfo.concat("targetSwitchID: " + currLink.targetSwitchId + " ");
			targetInfo = targetInfo.concat("targetPort: " + currLink.target_port);
			linkObj.put("targetInfo", targetInfo);
			
			if(currLink.type.equals("host-switch")) {
				linkObj.put("value", 10);
			} else {
				String srcInfo = new String("");
				srcInfo = srcInfo.concat("srcSwitchID: " + currLink.srcSwitchId + " ");
				srcInfo = srcInfo.concat("srcPort: " + currLink.src_port);
				linkObj.put("srcInfo", srcInfo);
				linkObj.put("value", 5);
			}
			linkObj.put("target", currLink.target_index);
			linkObj.put("source", currLink.source_index);

			linksArray.put(linkObj);
		}
		
		rootObj.put("nodes",nodesArray);
		rootObj.put("links", linksArray);
		result = rootObj.toString();
		
		/* Write results to a file read by visualization module */
		BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(fileName));
        
        if (writer != null) {
        	writer.write(result);
        	writer.close();
        }
	     
	    //System.out.println(result);
        System.out.println("Wrote JSON results to file: " + fileName);
	}
}
