package edu.columbia.sdn.floodlight.visualization;

public class Link {
	/* Common (Host/Switch) Link Parameters
	 * These are as per the information exposed by Links API 
	 */
	int source_index;
	int src_port;
	String srcSwitchId;
	int target_index;
	int target_port;
	String targetSwitchId;
	String type;
}
