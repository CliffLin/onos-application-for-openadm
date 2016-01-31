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
package org.winlab.omniui;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ObjectArrays;
import org.onlab.packet.*;
import org.onosproject.core.DefaultGroupId;
import org.onosproject.core.GroupId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.IndexedLambda;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.EthTypeCriterion;
import org.onosproject.net.flow.criteria.LambdaCriterion;
import org.onosproject.net.flow.criteria.TcpPortCriterion;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.flow.instructions.Instructions.OutputInstruction;
import org.onosproject.rest.AbstractWebResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.zip.Inflater;

/**
 * Topology viewer resource.
 */
@javax.ws.rs.Path("")
public class Omniui extends AbstractWebResource {
	public static String controller_name = "";
	final FlowRuleService service = get(FlowRuleService.class);
	@javax.ws.rs.Path("/controller/name")
	@POST
	@Produces("application/json")
	public Response controller_name(@PathParam("name") String name){
		controller_name = name;
		return Response.ok("OK").build();
	}
	@javax.ws.rs.Path("add/json")
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response flowmod(InputStream inputStream) {
		try {
			ObjectNode jsonTree = (ObjectNode) mapper().readTree(inputStream);
			jsonTree.put("deviceId", jsonTree.get("dpid").toString());
			jsonTree.remove("dpid");
			jsonTree.remove("cookie");
			jsonTree.remove("cookie_mask");
			jsonTree.remove("table_id");
			jsonTree.remove("buffer_id");

			ObjectNode objectNode = (ObjectNode) mapper().createObjectNode();
			ArrayNode arrayNode = (ArrayNode) mapper().createArrayNode();
			ObjectNode action = (ObjectNode) mapper().createObjectNode();
			action.put("type",jsonTree.get("action").toString().split("=")[0]);
			action.put("value",jsonTree.get("action").toString().split("=")[1].split(",")[0]);
			arrayNode.add(action);
			objectNode.put("instructions",arrayNode);
			jsonTree.put("treatment", objectNode);
			jsonTree.remove("action");

			ObjectNode criteria = (ObjectNode) mapper().createObjectNode();
			FlowModEntry flowModEntry = new FlowModEntry(jsonTree.get("dpid").toString());
			flowModEntry.selector = DefaultTrafficSelector.builder()
					.matchEthDst(MacAddress.valueOf(jsonTree.get("dl_dst").textValue()))
					.matchEthSrc(MacAddress.valueOf(jsonTree.get("dl_src").textValue()))
					.matchEthType(Short.valueOf(jsonTree.get("eth_type").textValue()))
					.matchIcmpCode(Byte.valueOf(jsonTree.get("icmpv4_code").textValue()))
					.matchIcmpType(Byte.valueOf(jsonTree.get("icmpv4_type").textValue()))
					.matchIcmpv6Code(Byte.valueOf(jsonTree.get("icmpv6_code").textValue()))
					.matchIcmpv6Type(Byte.valueOf(jsonTree.get("icmpv6_type").textValue()))
					.matchInPhyPort(PortNumber.portNumber(jsonTree.get("in_phy_port").textValue()))
					.matchInPort(PortNumber.portNumber(jsonTree.get("in_port").textValue()))
					.matchIPDscp(Byte.valueOf(jsonTree.get("ip_dscp").textValue()))
					.matchIPEcn(Byte.valueOf(jsonTree.get("ip_ecn").textValue()))
					.matchIPProtocol(Byte.valueOf(jsonTree.get("ip_proto").textValue()))
					.matchIPDst(IpPrefix.valueOf(jsonTree.get("ipv4_dst").textValue()))
					.matchIPSrc(IpPrefix.valueOf(jsonTree.get("ipv4_src").textValue()))
					.matchIPv6Dst(IpPrefix.valueOf(jsonTree.get("ipv6_dst").textValue()))
					.matchIPv6Src(IpPrefix.valueOf(jsonTree.get("ipv6_src").textValue()))
					.matchIPv6ExthdrFlags(Short.valueOf(jsonTree.get("ipv6_exthdr").textValue()))
					.matchIPv6FlowLabel(Integer.parseInt(jsonTree.get("ipv6_flabel").textValue()))
					.matchIPv6NDSourceLinkLayerAddress(MacAddress.valueOf(jsonTree.get("ipv6_nd_sll").textValue()))
					.matchIPv6NDTargetAddress(Ip6Address.valueOf(jsonTree.get("ipv6_nd_target").textValue()))
					.matchIPv6NDTargetLinkLayerAddress(MacAddress.valueOf(jsonTree.get("ipv6_nd_tll").textValue()))
					.matchMetadata(Long.valueOf(jsonTree.get("metadata").textValue()))
					.matchMplsBos(Boolean.valueOf(jsonTree.get("mpls_bos").textValue()))
					.matchMplsLabel(MplsLabel.mplsLabel(jsonTree.get("mpls_label").intValue()))
					.matchSctpDst(TpPort.tpPort(jsonTree.get("sctp_dst").intValue()))
					.matchSctpSrc(TpPort.tpPort(jsonTree.get("sctp_src").intValue()))
					.matchTcpDst(TpPort.tpPort(jsonTree.get("tcp_dst").intValue()))
					.matchTcpSrc(TpPort.tpPort(jsonTree.get("tcp_src").intValue()))
					.matchTunnelId(jsonTree.get("tunnel_id").asLong())
					.matchUdpDst(TpPort.tpPort(jsonTree.get("udp_dst").intValue()))
					.matchUdpSrc(TpPort.tpPort(jsonTree.get("udp_src").intValue()))
					.matchVlanId(VlanId.vlanId(jsonTree.get("vlan_vid").shortValue()))
					.matchVlanPcp(Byte.valueOf(jsonTree.get("vlanP").textValue()))
					.build();
			String actions[] = jsonTree.get("action").textValue().split(",");
			TrafficTreatment.Builder builder =	DefaultTrafficTreatment.builder();
			for (String act:actions ) {
				String acts[] = act.split("=");
				switch(acts[0]) {
					case "OUTPUT":
						builder.setOutput(PortNumber.portNumber(acts[1]));
						break;
					case "COPY_TTL_OUT":
						builder.copyTtlOut();
						break;
					case "COPY_TTL_IN":
						builder.copyTtlIn();
						break;
					case "DEC_MPLS_TTL":
						builder.decMplsTtl();
						break;
					case "POP_MPLS":
						builder.popMpls(new EthType(Short.valueOf(acts[1])));
						break;
					case "PUSH_VLAN":
						builder.pushVlan();
						break;
					case "POP_VLAN":
						builder.popVlan();
						break;
					case "PUSH_MPLS":
						builder.pushMpls();
						break;
					case "GROUP":
						builder.group(new DefaultGroupId(Integer.valueOf(acts[1])));
						break;
					case "DEC_NW_TTL":
						builder.decNwTtl();
						break;
				}
			}
			flowModEntry.treatment = builder.build();
			service.applyFlowRules(flowModEntry);
			return Response.ok("{\"status\":\"success\"}").build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.ok("{\"status\":\"error\",\"error\":\"" + e.toString() + "\"}").build();
		}
	}

	private class FlowModEntry implements FlowEntry {
		String deviceId;
		TrafficTreatment treatment;
		TrafficSelector selector;
		public FlowModEntry(String dpid) {
			deviceId = dpid;
		}

		@Override
		public FlowEntryState state() {
			return null;
		}

		@Override
		public long life() {
			return 0;
		}

		@Override
		public long packets() {
			return 0;
		}

		@Override
		public long bytes() {
			return 0;
		}

		@Override
		public long lastSeen() {
			return 0;
		}

		@Override
		public int errType() {
			return 0;
		}

		@Override
		public int errCode() {
			return 0;
		}

		@Override
		public FlowId id() {
			return null;
		}

		@Override
		public short appId() {
			return 0;
		}

		@Override
		public GroupId groupId() {
			return null;
		}

		@Override
		public int priority() {
			return 0;
		}

		@Override
		public DeviceId deviceId() {
			return null;
		}

		@Override
		public TrafficSelector selector() {
			return null;
		}

		@Override
		public TrafficTreatment treatment() {
			return null;
		}

		@Override
		public int timeout() {
			return 0;
		}

		@Override
		public boolean isPermanent() {
			return false;
		}

		@Override
		public int tableId() {
			return 0;
		}

		@Override
		public boolean exactMatch(FlowRule flowRule) {
			return false;
		}

		@Override
		public FlowRuleExtPayLoad payLoad() {
			return null;
		}
	}
}
