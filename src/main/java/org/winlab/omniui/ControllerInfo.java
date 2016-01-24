package org.winlab.omniui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by zylin on 2016/1/24.
 */
public class ControllerInfo {
    public String getInfo() {
        String OS = getOS();
        String freeMem[] = getFreeMem();
        String loadavg = getLoad();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("type","ONOS");
        objectNode.put("os",OS);
        objectNode.put("mem_total",freeMem[0]);
        objectNode.put("mem_used",freeMem[1]);
        objectNode.put("mem_free",freeMem[2]);
        objectNode.put("cpu",loadavg);
        return objectNode.toString();
    }
    private String getOS(){
        try {
            Process p = Runtime.getRuntime().exec("cat /etc/issue");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return stdInput.readLine().replaceAll("\\\\\\w+", "");
        }
        catch (Exception e) {
            return "OS info failed";
        }
    }
    private String[] getFreeMem() {
        try {
            Process p = Runtime.getRuntime().exec("free -h");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            stdInput.readLine();
            String freeLine = stdInput.readLine();
            String freeArray[] = freeLine.split("\\s+");
            String freeMem[] = new String[3];
            freeMem[0] = freeArray[1];
            freeMem[1] = freeArray[2];
            freeMem[2] = freeArray[3];
            return freeMem;
        }
        catch (Exception e){
            String error[] = new String[3];
            Arrays.fill(error,"free mem info fail");
            return error;
        }
    }
    private String getLoad(){
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/loadavg");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return stdInput.readLine().split(" ")[0];
        }
        catch (Exception e) {
            return "loadavg info failed";
        }
    }
}
