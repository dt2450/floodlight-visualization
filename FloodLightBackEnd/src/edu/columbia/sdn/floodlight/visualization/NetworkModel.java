package edu.columbia.sdn.floodlight.visualization;

import java.util.List;

public class NetworkModel {
	/* Network model data structure. THis consists of switches. hosts and links */
	List<Switch> switches;
	List<Host> hosts;
	List<Link> links;
	int numSwitches;
	int numHosts;
}
