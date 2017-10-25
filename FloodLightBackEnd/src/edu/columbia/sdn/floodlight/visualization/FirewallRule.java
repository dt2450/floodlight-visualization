package edu.columbia.sdn.floodlight.visualization;

public class FirewallRule {
	/* Parameters in a firewall rule */
	long ruleid;
	String dpId;
	String switch_in_port;
	String src_mac;
	String dest_mac;
	String dl_type;
	String src_ip;
	String dest_ip;
	String nw_protocol;
	String src_port;
	String dest_port;
	int priority;
	String action;
}
