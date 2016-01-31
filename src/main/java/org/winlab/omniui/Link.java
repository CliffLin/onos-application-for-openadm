package org.winlab.omniui;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zylin on 2016/1/27.
 */
public class Link {
    private String controller = Omniui.controller_name;
    private List<link> links = new ArrayList<link>();
    private class link {
        String dpid;
        String port;
        public link(String dpid, String port){
            this.dpid = dpid;
            this.port = port;
        }
    }
    public void newLink(String dpid, String port) {
        this.links.add(new link(dpid, port));
    }
}
