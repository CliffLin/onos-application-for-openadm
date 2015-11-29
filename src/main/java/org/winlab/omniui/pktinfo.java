/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Sample topology viewer application.
 */
package org.winlab.omniui;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.IpAddress;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onlab.rest.BaseResource;

public class pktinfo extends BaseResource {

	public class ReactivePacketProcessor implements PacketProcessor {
	String srcmac;
        @Override
        public void process(PacketContext context) {

		InboundPacket pkt = context.inPacket();
            	Ethernet ethPkt = pkt.parsed();

            	if (ethPkt == null) {
                	return;
            	}
 
	    	String EthType = Short.toString(ethPkt.getEtherType());
            	if(EthType.equals("2048"))
            		EthType = "IPv4";
            	else if(EthType.equals("2054"))
            		EthType = "ARP";
		
		srcmac="empty";
	    //log.info("Src_MAC: " + ethPkt.getSourceMAC() + "  Dst_MAC: " + ethPkt.getDestinationMAC());
	    //log.info("EthType: " + EthType + " Vlan: " + Short.toString(ethPkt.getVlanID()));
	  }
	}

	public String testinfo() {
	ReactivePacketProcessor pktprocess = new ReactivePacketProcessor();
	return pktprocess.srcmac;
	}
	
	public String totalinfo(){
        	ObjectMapper mapper = new ObjectMapper();
        	ArrayNode root = mapper.createArrayNode();
        	Iterable<Device> devices = get(DeviceService.class).getDevices();

	        for (Device d : devices) {
		ObjectNode device = mapper.createObjectNode();
            	Iterable<PortStatistics> portStatistics = get(DeviceService.class).getPortStatistics(d.id());
            	ArrayNode ports = mapper.createArrayNode();
            	for (PortStatistics p:portStatistics) {
                	ObjectNode port = mapper.createObjectNode();
                	port.put("PortNumber", p.port());
                	port.put("transmitPackets", p.packetsSent());
                	port.put("recvPackets", p.packetsReceived());
                	port.put("transmitBytes", p.bytesSent());
                	port.put("recvBytes", p.bytesReceived());
                	port.put("transmitDrop", p.packetsTxDropped());
                	port.put("recvDrop", p.packetsRxDropped());
                	ports.add(port);
            	}
            	device.set("ports", ports);
	      	root.add(device);
        	}
		return root.toString();
	}
}
