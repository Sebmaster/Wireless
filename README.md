Wireless
===
This plugin for bukkit allows you to create transmitters and receivers of redstone power.

Usage:
---
Place a sign with the following lines somewhere:

* transmitter
* \<channelname\>

You can replace "\<channelname\>" with anything you like. As soon as you're finished editing the sign, the first line changes to "[Transmitter]".
This means, the sign got recognized and the plugin is working correctly.

Afterwars place another sign somewhere else with the following lines:

* receiver
* \<channelname\>

"\<channelname\>" should be the same value, you put on the transmitter sign (case-sensitive). As with the transmitter sign, the first line also changes to "[Receiver]".

After this is done, you can connect the transmitter to some powered redstone. As soon as this is done, the receiver sign should change to a redtorch.