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

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
/**
 * Topology viewer resource.
 */
@javax.ws.rs.Path("")
public class Omniui{

	@javax.ws.rs.Path("/switch/json")
	@GET
	@Produces("application/json")
    	public Response switches() {
		pktinfo pkt = new pktinfo();
		String switchinfo = pkt.totalinfo();
        	return Response.ok(switchinfo).build();
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
