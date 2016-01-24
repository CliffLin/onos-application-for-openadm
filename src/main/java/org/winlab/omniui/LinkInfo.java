package org.winlab.omniui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onlab.rest.BaseResource;
import org.onosproject.net.Link;
import org.onosproject.net.link.LinkService;

/**
 * Created by zylin on 2016/1/24.
 */
public class LinkInfo extends BaseResource {
    public String getLinks() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        Iterable<Link> linkIter = get(LinkService.class).getLinks();
        for (Link l: linkIter) {
            ObjectNode link = mapper.createObjectNode();
            link.put("src-switch", l.src().deviceId().toString());
            link.put("src-port", l.src().port().toString());
            link.put("dst-switch", l.dst().deviceId().toString());
            link.put("dst-port", l.dst().port().toString());
            root.add(link);
        }
        return root.toString();
    }
}
