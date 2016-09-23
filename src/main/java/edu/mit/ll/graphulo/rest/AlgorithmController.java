package edu.mit.ll.graphulo.rest;

import java.util.Map;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.mit.ll.graphulo.Graphulo;
import edu.mit.ll.graphulo.rest.objects.Graph;
import io.swagger.annotations.Api;

/**
 * 
 * @author ti26350
 *
 */
@Api(value="algorithm", description="Basic Algorithm Class")
@RestController
public class AlgorithmController {
	
    @RequestMapping(path = "/algorithm/bfs/{name}", method = RequestMethod.GET)
    public Graph greeting(@PathVariable String name, @RequestParam(value="node", defaultValue="1") String node) throws TableNotFoundException  {
    	
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	
    	try { 
    		//Read Accumulo Configuration Information
//    		String instance = "txg-classdb04";
//	    	String zkServers = "txg-classdb04.cloud.llgrid.ll.mit.edu";
//	    	String principal = "AccumuloUser";
//	    	authToken = new PasswordToken("K-Yt5H9LkweiZ_EbQCxz5p4jm");
	    	
    		String instance = "graphuloLocal";
	    	String zkServers = "localhost";
	    	String principal = "root";
	    	authToken = new PasswordToken("graphuloLocal");
	
	    	ZooKeeperInstance inst = new ZooKeeperInstance(instance, zkServers);
	    	conn = inst.getConnector(principal, authToken);
	    	
//	    	ConnectionProperties cp = new ConnectionProperties("txg-classdb04.cloud.llgrid.ll.mit.edu","AccumuloUser",
//	    			"onPKURqGF@zFRyEZDLsV460EH", "txg-classdb04",null);
//	    	AccumuloConnection ac = new AccumuloConnection(cp);
//	    	PasswordToken at = new PasswordToken("onPKURqGF@zFRyEZDLsV460EH");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        Graphulo g = new Graphulo(conn, authToken);
//        Map<org.apache.accumulo.core.data.Key,org.apache.accumulo.core.data.Value> clientResultMap = new TreeMap<org.apache.accumulo.core.data.Key,org.apache.accumulo.core.data.Value>();
//    	String s = g.AdjBFS(name, node, 1, null, null, clientResultMap, -1, name+"Deg",
//		null, false, 0, 1000, null);

        String s = g.AdjBFS(name, node, 1, name+node+"_BFS_R", name+node+"_BFS_Rt", null, null, false, 0, Integer.MAX_VALUE);

    	Scanner scanner = conn.createScanner(name+node+"_BFS_R", new Authorizations());
    	String mapString = "";    	
    	for (Map.Entry<Key,Value> entry : scanner) {
    		mapString += "<"+entry.getKey() + "," + entry.getValue()+">";
    	}

/*    	if(clientResultMap != null) {
	    	Iterator<Map.Entry<org.apache.accumulo.core.data.Key,org.apache.accumulo.core.data.Value>> it = clientResultMap.entrySet().iterator();
	    	mapString = clientResultMap.size() + ":";
	    	while(it.hasNext()){
	    		Map.Entry<org.apache.accumulo.core.data.Key,org.apache.accumulo.core.data.Value> map = it.next();
	    		mapString += "<"+map.getKey() + "," + map.getValue()+">";
	    	}
    	}
    	else {
    		mapString = "NULL";
    	}*/
    	
    	return null;// new Graph(counter.incrementAndGet(),mapString,s);
    }
}
