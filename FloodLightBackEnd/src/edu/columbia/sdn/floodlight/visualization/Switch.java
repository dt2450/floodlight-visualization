package edu.columbia.sdn.floodlight.visualization;

import java.util.List;

public class Switch {
	/* Switch parameters */
	String dpId;
	boolean floodSupport;
	String manufacturer;
	String hwType;
	String swVersion;
	int buffers;
	int tables;
	String inetAddress;
	List<SwitchPort> portList;
	List<FirewallRule> ruleList;
}
