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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ObjectArrays;
import com.sun.jersey.api.uri.UriComponent;
import org.apache.commons.lang.ObjectUtils;
import org.onlab.packet.MacAddress;
import org.onlab.util.Counter;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onlab.rest.BaseResource;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.*;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.FlowObjectiveStore;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

public class SwitchInfo extends BaseResource {
	
	public String totalinfo(){
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode root = mapper.createArrayNode();
		Iterable<Device> devices = get(DeviceService.class).getDevices();

		for (Device d : devices) {
			ObjectNode device = mapper.createObjectNode();
			Iterable<PortStatistics> portStatistics = get(DeviceService.class).getPortStatistics(d.id());
			Iterable<FlowEntry> flowEntriess = get(FlowRuleService.class).getFlowEntries(d.id());
			device.put("dpid",d.id().toString());
			ArrayNode ports = mapper.createArrayNode();
			ArrayNode flows = mapper.createArrayNode();
			for (PortStatistics p:portStatistics) {
				ObjectNode port = mapper.createObjectNode();
				port.put("port", p.port());
				port.put("txpacket", p.packetsSent());
				port.put("rxpacket", p.packetsReceived());
				port.put("txbyte", p.bytesSent());
				port.put("rxbyte", p.bytesReceived());
               	ports.add(port);
           	}
			for (FlowEntry f:flowEntriess)
			{
				ObjectNode flow = mapper.createObjectNode();
				TrafficSelector tSelector = f.selector();
				flow.put("ingressPort" , tSelector.getCriterion(Criterion.Type.IN_PORT) == null ? "": ((PortCriterion) tSelector.getCriterion(Criterion.Type.IN_PORT)).port().toString()) ;
				flow.put("dstMac" , tSelector.getCriterion(Criterion.Type.ETH_DST) == null ? "" : ((EthCriterion) tSelector.getCriterion(Criterion.Type.ETH_DST)).mac().toString());
				flow.put("srcMac" , tSelector.getCriterion(Criterion.Type.ETH_SRC) == null ? "" : ((EthCriterion) tSelector.getCriterion(Criterion.Type.ETH_SRC)).mac().toString());
				flow.put("dstIP" , tSelector.getCriterion(Criterion.Type.IPV4_DST) == null ? "" : ((IPCriterion) tSelector.getCriterion(Criterion.Type.IPV4_DST)).ip().toString());
				flow.put("dstIPMask" , "x");
				flow.put("srcIP" , tSelector.getCriterion(Criterion.Type.IPV4_SRC) == null ? "" : ((IPCriterion) tSelector.getCriterion(Criterion.Type.IPV4_SRC)).ip().toString());
				flow.put("srcIPMask", "x");
				flow.put("netProtocol", tSelector.getCriterion(Criterion.Type.IP_PROTO) == null ? "" : ((IPCriterion) tSelector.getCriterion(Criterion.Type.IP_PROTO)).type().toString());
				flow.put("dstPort", tSelector.getCriterion(Criterion.Type.TCP_DST) == null ? "" : ((TcpPortCriterion) tSelector.getCriterion(Criterion.Type.TCP_DST)).tcpPort().toString());
				flow.put("srcPort", tSelector.getCriterion(Criterion.Type.TCP_SRC) == null ? "" : ((TcpPortCriterion) tSelector.getCriterion(Criterion.Type.TCP_SRC)).tcpPort().toString());
				flow.put("vlan",tSelector.getCriterion(Criterion.Type.VLAN_VID) == null ? "" : ((VlanIdCriterion) tSelector.getCriterion(Criterion.Type.VLAN_VID)).vlanId().toString());
				flow.put("vlanP", tSelector.getCriterion(Criterion.Type.VLAN_PCP) == null ? "" : String.valueOf(((VlanPcpCriterion) tSelector.getCriterion(Criterion.Type.VLAN_PCP)).priority()));
				flow.put("wildcards", "");
				flow.put("tosBits", tSelector.getCriterion(Criterion.Type.IP_ECN) == null ? "" : String.valueOf(((IPEcnCriterion) tSelector.getCriterion(Criterion.Type.IP_ECN)).ipEcn()));
				flow.put("counterByte" , f.bytes());
				flow.put("counterPacket " , f.packets());
				flow.put("idleTimeout" , "x");
				flow.put("hardTimeout" , "x");
				flow.put("priority" , f.priority() );
				flow.put("duration" , f.life());
				flow.put("dlType" , "x");
				ArrayNode flowActions = mapper.createArrayNode();
				List<Instruction> action = f.treatment().allInstructions();
				for (Instruction instruction : action) {
					ObjectNode flowAction = mapper.createObjectNode();
					flowAction.put("type",instruction.type().toString());
					flowAction.put("value",instruction.toString().replace(instruction.type().toString(),""));
					flowActions.add(flowAction);
				}
				flow.put("actions",flowActions);
				flows.add(flow);
			}
           	device.set("ports", ports);
			device.set("flows",flows);
	    	root.add(device);
        }

		return root.toString();
	}
}
