"""Custom topology example

author: Brandon Heller (brandonh@stanford.edu)

Two directly connected switches plus a host for each switch:

   host --- switch --- switch --- host

Adding the 'topos' dict with a key/value pair to generate our newly defined
topology enables one to pass in '--topo=mytopo' from the command line.
"""

from mininet.topo import Topo, Node

class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self, enable_all = True ):
        "Create custom topo."

        # Add default members to class.
        super( MyTopo, self ).__init__()

        # Set Node IDs for hosts and switches
        Host1 = 1
        Host2 = 2
        Host3 = 3
        Host4 = 4
        Host5 = 5
        Host6 = 6
        Host7 = 7
        Host8 = 8
        Switch1 = 9
        Switch2 = 10
        Switch3 = 11
        Switch4 = 12
        Switch5 = 13
        Switch6 = 14

        # Add nodes
        self.add_node( Switch1, Node( is_switch=True ) )
        self.add_node( Switch2, Node( is_switch=True ) )
        self.add_node( Switch3, Node( is_switch=True ) )
        self.add_node( Switch4, Node( is_switch=True ) )
        self.add_node( Switch5, Node( is_switch=True ) )
        self.add_node( Switch6, Node( is_switch=True ) )
        self.add_node( Host1, Node( is_switch=False ) )
        self.add_node( Host2, Node( is_switch=False ) )
        self.add_node( Host3, Node( is_switch=False ) )
        self.add_node( Host4, Node( is_switch=False ) )
        self.add_node( Host5, Node( is_switch=False ) )
        self.add_node( Host6, Node( is_switch=False ) )
        self.add_node( Host7, Node( is_switch=False ) )
        self.add_node( Host8, Node( is_switch=False ) )

        # Add edges
        self.add_edge( Host1, Switch1 )
        self.add_edge( Host2, Switch1 )
        self.add_edge( Host3, Switch6 )
        self.add_edge( Host4, Switch6 )
        self.add_edge( Host5, Switch3 )
        self.add_edge( Host6, Switch3 )
        self.add_edge( Host7, Switch5 )
        self.add_edge( Host8, Switch5 )
        self.add_edge( Switch1, Switch2 )
        self.add_edge( Switch2, Switch6 )
        self.add_edge( Switch2, Switch3 )
        self.add_edge( Switch3, Switch4 )
        self.add_edge( Switch4, Switch5 )
        self.add_edge( Switch4, Switch1 )

        # Consider all switches and hosts 'on'
        self.enable_all()


topos = { 'mytopo': ( lambda: MyTopo() ) }
