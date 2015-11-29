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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onlab.rest.BaseResource;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
/**
 * Topology viewer resource.
 */
@javax.ws.rs.Path("")
public class Omniui extends BaseResource {

    @javax.ws.rs.Path("/switch/json")
    @GET
    @Produces("application/json")
    public Response switches() {
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
        return Response.ok(root.toString()).build();
    }
    
	@javax.ws.rs.Path("/switch/pktinfo")
	@GET
	@Produces("application/json")
	public Response switchinfo() {
	pktinfo pkt = new pktinfo();
	String respon = pkt.testinfo();
	return 	Response.ok(respon).build();
	}
}
