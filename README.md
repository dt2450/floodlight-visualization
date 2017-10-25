# floodlight-visualization

# Authors:

1. Devashi Tandon
2. Anant Sharma

# Acknowledgements
We would like to thank Dr. Erran Li for providing us this opportunity to work on this project. We would also like to thank Young Hoon Jung for providing a brief direction on how to go about it.

This project provides an application over floodlight which gives the visualization of the network.

# Introduction
Visualization is an important tool that can help administrators understand the topology of the network and to see how the network is behaving. With software defined networking a visualization application can achieve a lot more since the controller has a significant visibility of the network.
Motivation
The motivation behind this project is driven by the fact that graphical visualization is an important tool for administrators. When we were working on the homework assignments, we had to imagine the topology structure on our own based on the input parameters given to mininet. A graphical visualization tool makes this simpler and it is easier to understand the network based on a graphical view of the network topology.
After working on the SDN competition at Juniper/Comcast, we got motivated to implement a visualization module over Floodlight, and with the support of some of the REST APIs in Floodlight this seemed doable within the given timeframe.

# Project Description
This project provides an application over floodlight which gives the visualization of the network. The visualization in HTML is capable of refreshing the topology by itself so the changes get reflected almost immediately after the network topology is modified, and the topology view reconfigures itself to balance out the nodes giving a beautiful display, whatever be the topology of the network.
We chose floodlight for the following reasons:
We had developed a familiarity with this controller through the assignments given in the class.
It supports REST APIs so it was easier to fetch information from the controller for the visualization tool
It has an inbuilt Firewall module which we could use to display the rules on the switches.
We thought that with these features of floodlight it was easier to give a Proof of Concept for the virtualization application using floodlight controller.
The visualization tool provides an easy way to see the firewall rules configured on the switches. These rules get reflected on the visualization almost immediately after they are applied on the switches.
Further, the link based information like which switch port is connected to which switch port or host is visible on clicking the links in the topology graph.
When the switch is clicked, the switch details like id/manufacturer/software version etc. get displayed in the tooltip. Similarly when the host is clicked, the host based details get displayed. These details are based on the information exposed by the REST APIs of the floodlight controller.
