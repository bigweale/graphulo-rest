package edu.mit.ll.graphulo.rest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.mit.ll.graphulo.Graphulo;
import edu.mit.ll.graphulo.rest.objects.Edges;
import edu.mit.ll.graphulo.rest.objects.Graph;
import edu.mit.ll.graphulo.rest.objects.GraphSummary;
import edu.mit.ll.graphulo.rest.objects.LinkOLD;
import edu.mit.ll.graphulo.rest.objects.Neighbors;
import edu.mit.ll.graphulo.rest.objects.Vertex;
import edu.mit.ll.graphulo.rest.objects.Vertices;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author ti26350
 *
 */
@Api(value="graph", description="Basic Graph Functions")
@RestController
public class GraphController {

    private final AtomicLong counter = new AtomicLong();

    /**
     * Checks for the existence of a graph. If it exists, returns true. Otherwise, false.
     * 
     * @param name Graph Name
     * @return boolean value for the existence of graph
     */
    @RequestMapping(path = "/graph/{name}", method = RequestMethod.GET)
    @ApiOperation(value = "Graph existance check")
    public ResponseEntity<Boolean> tableExists(@PathVariable String name) {
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

	    	Scanner scanner = conn.createScanner(name, new Authorizations());
    	} catch (TableNotFoundException e) {
    		return ResponseEntity.status(HttpStatus.OK).body(false);
    	} catch (Exception e) {
			//e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    	
    	return ResponseEntity.status(HttpStatus.OK).body(true);
    }
    
    /**
     * Calculates and returns summary information on the graph.
     * 
     * @param name Graph Name
     * @return
     */
    @RequestMapping(path = "/graph/{name}/summary", method = RequestMethod.GET)
    @ApiOperation(value = "Elementary graph information")
    public ResponseEntity<GraphSummary> getGraphSummary(@PathVariable String name)  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	Graphulo g = null;
       	GraphSummary gs = null;
       	
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
	    	g = new Graphulo(conn, authToken);
	    	gs = new GraphSummary(counter.incrementAndGet(), g.countRows(name), -1);
		} catch (Exception e) {
			//e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    	
    	return ResponseEntity.status(HttpStatus.OK).body(gs);
    }

	/**
	 * Calculates the number of vertices in the graph.
	 * 
	 * @param name Graph Name
	 * @return
	 */
    @RequestMapping(path = "/graph/{name}/vertices/count", method = RequestMethod.GET)
    @ApiOperation(value = "Count of vertices in the graph")
    public ResponseEntity<GraphSummary> getGraphVertexCount(@PathVariable String name)  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	Graphulo g = null;
       	GraphSummary gs = null;
       	
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
	    	g = new Graphulo(conn, authToken);
	    	gs = new GraphSummary(counter.incrementAndGet(), g.countRows(name), -1);
    	} catch (Exception e) {
			// e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}
    	
    	return ResponseEntity.status(HttpStatus.OK).body(gs); 
    }
    
    /**
     * Returns a list of the vertices in a graph.
     * 
     * **NOTE: Uses direct Accumulo functions. Could be modified to be a Graphulo call.
     * **NOTE: ORDER and TYPE not implemented
     * 
     * @param name Graph Name
     * @param order Ordering for the vertices. Defaults to "none".
     * @param type Type of edge to use for the ordering. Defaults to "all". This parameter is only used if order is not "none".
     * @return
     */
    @RequestMapping(path = "/graph/{name}/vertices/list", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of the vertices in a graph.", notes = "OrderBy and Type are not yet implemented.")
    public ResponseEntity<Vertices> getGraphVertexList(@PathVariable String name, 
     									   @RequestParam(value="ordering", defaultValue="none") String order,
    									   @RequestParam(value="type", defaultValue="all") String type)  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	
       	Scanner scanner = null;
       	ArrayList<Vertex> llVertices = null;
       	ArrayList<String> vertexNames = null;
       	Vertices vertices = null;
       	
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

	    	scanner = conn.createScanner(name, new Authorizations());
	    	llVertices = new ArrayList<Vertex>();
	    	vertexNames = new ArrayList<String>();
	    	
	    	//Maps Node Names to Integer Values
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		Key k = entry.getKey();
	    		Text f = k.getRow();
	    		
	    		if( !vertexNames.contains(f) ) {
	    			vertexNames.add(f.toString());
	    			Vertex v = new Vertex(f.toString());
	    			llVertices.add(v);
	    		}
	    	}//end:for

	    	vertices = new Vertices(counter.incrementAndGet(), llVertices);
	    	
		} catch (Exception e) {
			// e.printStackTrace();
			return ResponseEntity.status(500).body(null);
		}

    	return ResponseEntity.status(HttpStatus.OK).body(vertices); 
    }
    
    /**
     * Returns a list of the vertices in a graph.
     * 
     * @param name Graph Name
     * @param order Ordering for the vertices. Defaults to "none".
     * @param type Type of edge to use for the ordering. Defaults to "all". This parameter is only used if order is not "none".
     * @return
     */
    @RequestMapping(path = "/graph/{name}/{vertex}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of the vertices in a graph.", notes = "NOT IMPLEMENTED")
    public ResponseEntity<Graph> getGraphVertex(@PathVariable String name, @PathVariable String vertex)  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
    	Scanner scanner = null;
    	LinkedList<Vertex> llNodes = null;
    	List<LinkOLD> llLinks = null;
       	
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
		   	scanner = conn.createScanner(name, new Authorizations());
	    	scanner.setRange(new Range(vertex,vertex));

	    	llNodes = new LinkedList<Vertex>();
	    	llLinks = new LinkedList<LinkOLD>();
	    	
	    	//Maps Node Names to Integer Values
	    	TreeMap<Text,Integer> tm = new TreeMap<Text,Integer>();
	    	int nodeNum = 0;
	    	
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		Key k = entry.getKey();
	    		
	    		Text f = k.getRow();
	    		Text t = k.getColumnQualifier();
	    		
	    		if( !tm.containsKey(f) ) {
	    			tm.put(f, nodeNum);
	    			nodeNum++;
	    			Vertex n = new Vertex(f.toString(),"1");
	        		llNodes.add(n);
	    		}
	    		
	    		if( !tm.containsKey(t) ) {
	    			tm.put(t, nodeNum);
	    			nodeNum++;
	    			Vertex n = new Vertex(t.toString(),"1");
	        		llNodes.add(n);
	    		} 
	    		    		
	    		LinkOLD<Integer> e = new LinkOLD<Integer>(tm.get(f),tm.get(t),1);
	    		llLinks.add(e);
	    	}   
	    	
    	} catch (TableNotFoundException e) {
    		throw new ResourceNotFoundException("Graph " + name + " does not exist.");
    	} catch (Exception e) {
			// e.printStackTrace();
    		return ResponseEntity.status(500).body(null);
		}
    	
    	Graph graph = new Graph(counter.incrementAndGet(),llNodes,llLinks);
    	return ResponseEntity.status(HttpStatus.OK).body(graph); 
    }

    /**
     * Returns a list of the vertices in a graph.
     * 
     * @param name Graph Name
     * @param order Ordering for the vertices. Defaults to "none".
     * @param type Type of edge to use for the ordering. Defaults to "all". This parameter is only used if order is not "none".
     * @return
     */
    @RequestMapping(path = "/graph/{name}/{vertex}/edgecount", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of the vertices in a graph.", notes = "NOT IMPLEMENTED")
    public Graph getEdgeSummary(@PathVariable String name, @PathVariable String vertex)  {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Scanner scanner = null;
    	try {
	    	scanner = conn.createScanner(name, new Authorizations());
	    	
    	} catch (TableNotFoundException e) {
    		throw new ResourceNotFoundException("Graph " + name + " does not exist.");
    	}
    	
    	scanner.setRange(new Range(vertex,vertex));

    	LinkedList<Vertex> llNodes = new LinkedList<Vertex>();
    	List<LinkOLD> llLinks = new LinkedList<LinkOLD>();
    	
    	//Maps Node Names to Integer Values
    	TreeMap<Text,Integer> tm = new TreeMap<Text,Integer>();
    	int nodeNum = 0;
    	
    	for (Map.Entry<Key,Value> entry : scanner) {
    		
    		Key k = entry.getKey();
    		
    		Text f = k.getRow();
    		Text t = k.getColumnQualifier();
    		
    		if( !tm.containsKey(f) ) {
    			tm.put(f, nodeNum);
    			nodeNum++;
    			Vertex n = new Vertex(f.toString(),"1");
        		llNodes.add(n);
    		}
    		
    		if( !tm.containsKey(t) ) {
    			tm.put(t, nodeNum);
    			nodeNum++;
    			Vertex n = new Vertex(t.toString(),"1");
        		llNodes.add(n);
    		} 
    		    		
    		LinkOLD<Integer> e = new LinkOLD<Integer>(tm.get(f),tm.get(t),1);
    		llLinks.add(e);
    	}    	
    	return new Graph(counter.incrementAndGet(),llNodes,llLinks);
    }
    
    /**
     * Returns the list of vertices linked to the given vertex. 
     * 
     * Currently uses Accumulo functions, but could be modified by adding Graphulo functionality.
     * 
     * @param name Graph Name
     * @param vertex Vertex Name
     * @return
     * @throws TableNotFoundException
     */
    @RequestMapping(path = "/graph/{name}/{vertex}/neighborLists", method = RequestMethod.GET)
    public ResponseEntity<Neighbors> getNeighborLists(@PathVariable String name, @PathVariable String vertex) throws TableNotFoundException  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	Scanner scanner = null;
       	ArrayList<Vertex> inVertices = null;
       	ArrayList<Vertex> outVertices = null;
       	Neighbors n = null;

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
	    	//Scan the original table (->)
	    	scanner = conn.createScanner(name, new Authorizations());
	    	scanner.setRange(new Range(vertex,vertex));
	    	outVertices = new ArrayList<Vertex>();
	    	
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		Key k = entry.getKey();
	    		Text f = k.getRow();
	    		Text t = k.getColumnQualifier();
	    		
	    		Vertex v = new Vertex(t.toString(), "2");
	    		outVertices.add(v);
	    	}    	
	    	scanner.close();
	    	
	    	scanner = conn.createScanner(name+"T", new Authorizations());
	    	scanner.setRange(new Range(vertex,vertex));
	    	inVertices = new ArrayList<Vertex>();
	    	
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		Key k = entry.getKey();
	    		Text f = k.getRow();
	    		Text t = k.getColumnQualifier();
	    		
	    		Vertex v = new Vertex(t.toString(),"0");
	    		inVertices.add(v);
	    	}
	    	
	    	scanner.close();
	    	
	    	Vertex currVertex = new Vertex(vertex, "1");
	    	
	    	n = new Neighbors(counter.incrementAndGet(), currVertex, inVertices, outVertices);
    	} catch (TableNotFoundException e) {
    		throw new ResourceNotFoundException("Graph " + name + " does not exist.");
    	} catch (Exception e) {
			// e.printStackTrace();
    		return ResponseEntity.status(500).body(null);
    	}
    		
    	return ResponseEntity.status(HttpStatus.OK).body(n);
    }

    /**
     * Returns the list of vertices linked to the given vertex. 
     * 
     * Currently uses Accumulo functions, but could be modified by adding Graphulo functionality.
     * 
     * @param name Graph Name
     * @param vertex Vertex Name
     * @return
     * @throws TableNotFoundException
     */
    @RequestMapping(path = "/graph/{name}/{vertex}/neighborGraph", method = RequestMethod.GET)
    public ResponseEntity<Graph> getNeighborGraph(@PathVariable String name, @PathVariable String vertex) throws TableNotFoundException  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	Scanner scanner = null;
       	ArrayList<Vertex> allVertices = null;
       	ArrayList<LinkOLD> edges = null;
       	Graph g = null;

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

	    	//Maps Node Names to Integer Values
	    	allVertices = new ArrayList<Vertex>();
	    	edges = new ArrayList<LinkOLD>();
	    	
	    	TreeMap<Text,Integer> tm = new TreeMap<Text,Integer>();
	    	TreeMap<Text,Integer> tm2 = new TreeMap<Text,Integer>();
	    	int nodeNum = 0;
	    	
	    	//Initialize basic vertex
	    	Vertex currVertex = new Vertex(vertex, "1");
	    	allVertices.add(currVertex);
	    	tm.put(new Text(vertex), nodeNum);
	    	nodeNum++;
	    	
	    	ZooKeeperInstance inst = new ZooKeeperInstance(instance, zkServers);
	    	conn = inst.getConnector(principal, authToken);
	    	//Scan the original table (->)
	    	scanner = conn.createScanner(name, new Authorizations());
	    	scanner.setRange(new Range(vertex,vertex));
	    	
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		// Row value is always 0
	    		Key k = entry.getKey();
	    		Text t = k.getColumnQualifier();
	    		
	    		if( !tm.containsKey(t) ) {
	    			tm.put(t, nodeNum);
	    			nodeNum++;
	    			Vertex n = new Vertex(t.toString(),"2");
	    			allVertices.add(n);
	    		} 
	    		    		
	    		LinkOLD<Integer> e = new LinkOLD<Integer>(0,tm.get(t),1);
	    		edges.add(e);
	    	}    	
	    	scanner.close();
	    	
	    	scanner = conn.createScanner(name+"T", new Authorizations());
	    	scanner.setRange(new Range(vertex,vertex));
	    	
	    	for (Map.Entry<Key,Value> entry : scanner) {
	    		
	    		// Row value is always 0
	    		Key k = entry.getKey();
	    		Text f = k.getColumnQualifier();
	    		
	    		if( !tm2.containsKey(f) ) {
	    			tm2.put(f, nodeNum);
	    			nodeNum++;
	    			Vertex n = new Vertex(f.toString(),"0");
	    			allVertices.add(n);
	    		} 
	    		    		
	    		LinkOLD<Integer> e = new LinkOLD<Integer>(tm2.get(f),0,1);
	    		edges.add(e);
	    	}
	    	
	    	scanner.close();
	    	
	    	g = new Graph(counter.incrementAndGet(), allVertices, edges);
    	} catch (TableNotFoundException e) {
    		throw new ResourceNotFoundException("Graph " + name + " does not exist.");
    	} catch (Exception e) {
			// e.printStackTrace();
    		return ResponseEntity.status(500).body(null);
    	}
    		
    	return ResponseEntity.status(HttpStatus.OK).body(g);
    }

    /**
     * 
     * @param name Graph Name
     * @return
     */
    @RequestMapping(path = "/graph/{name}/edges/count", method = RequestMethod.GET)
    @ApiOperation(value = "Counts the number of edges in the graph")
    public GraphSummary getEdgeCount(@PathVariable String name)  {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Graphulo g = new Graphulo(conn, authToken);
    	return new GraphSummary(counter.incrementAndGet(), -1, g.countEntries(name));
    }
    
    /**
     * 
     * @param name
     * @param node
     * @return
     * @throws TableNotFoundException
     */
    @RequestMapping(path = "/graph/{name}/edges/list", method = RequestMethod.GET)
    @ApiOperation(value = "Gets full list of graph edges")
    public Edges getEdgeList(@PathVariable String name)  {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Scanner scanner = null;
		try {
			scanner = conn.createScanner(name, new Authorizations());
		} catch (TableNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	LinkedList<Vertex> llNodes = new LinkedList<Vertex>();
    	LinkedList<LinkOLD> llLinks = new LinkedList<LinkOLD>();
    	
    	//Maps Node Names to Integer Values
    	TreeMap<Text,Integer> tm = new TreeMap<Text,Integer>();
    	int nodeNum = 0;
    	
    	for (Map.Entry<Key,Value> entry : scanner) {
    		
    		Key k = entry.getKey();
    		
    		Text f = k.getRow();
    		Text t = k.getColumnQualifier();
    		
    		if( !tm.containsKey(f) ) {
    			tm.put(f, nodeNum);
    			nodeNum++;
    			Vertex n = new Vertex(f.toString(),"1");
        		llNodes.add(n);
    		}
    		
    		if( !tm.containsKey(t) ) {
    			tm.put(t, nodeNum);
    			nodeNum++;
    			Vertex n = new Vertex(t.toString(),"1");
        		llNodes.add(n);
    		} 
    		    		
    		LinkOLD<String> e = new LinkOLD<String>(f.toString(),t.toString(),1);
    		llLinks.add(e);
    	}
    	
    	return new Edges(counter.incrementAndGet(),llLinks);
    }
    
/*    @RequestMapping(path = "/{name}/bfsviz", method = RequestMethod.GET)
    public Graph viz(@PathVariable String name, @RequestParam(value="node", defaultValue="1") String node) throws TableNotFoundException  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	
    	try { 
    		//Read Accumulo Configuration Information
    		String instance = "txg-classdb04";
	    	String zkServers = "txg-classdb04.cloud.llgrid.ll.mit.edu";
	
	    	String principal = "AccumuloUser";
	    	authToken = new PasswordToken("K-Yt5H9LkweiZ_EbQCxz5p4jm");
	
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
    	
    	LinkedList<Node> llNodes = new LinkedList<Node>();
    	LinkedList<Link> llLinks = new LinkedList<Link>();
    	
    	Scanner scanner = conn.createScanner(name+node+"_BFS_R", new Authorizations());

    	Node n = new Node(node,"1");
    	llNodes.add(n);
    	
    	int i = 1;
    	for (Map.Entry<Key,Value> entry : scanner) {
    		Key k = entry.getKey();
    		Text f = k.getRow();
    		Text t = k.getColumnQualifier();
    		
    		n = new Node(t.toString(),"2");
    		llNodes.add(n);
    		
    		Link e = new Link(0,i,1);
    		llLinks.add(e);
    		i++;
    	}
    	
    	return new Graph(counter.incrementAndGet(),llNodes,llLinks);
    }*/

    /*
    @RequestMapping(path = "/{name}/allviz", method = RequestMethod.GET)
    public Graph viz(@PathVariable String name) throws TableNotFoundException  {
       	Connector conn = null;
       	AuthenticationToken authToken = null;
       	
    	try { 
    		//Read Accumulo Configuration Information
//	 		String instance = "txg-classdb04";
//	    	String zkServers = "txg-classdb04.cloud.llgrid.ll.mit.edu";
	
//	    	String principal = "AccumuloUser";
//	    	authToken = new PasswordToken("K-Yt5H9LkweiZ_EbQCxz5p4jm");
	
    		//Read Accumulo Configuration Information
    		String instance = "graphuloLocal";
	    	String zkServers = "localhost";
	    	String principal = "root";
	    	authToken = new PasswordToken("graphuloLocal");
	    	
	    	ZooKeeperInstance inst = new ZooKeeperInstance(instance, zkServers);
	    	conn = inst.getConnector(principal, authToken);	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
       	Scanner scanner = conn.createScanner(name, new Authorizations());

    	LinkedList<Node> llNodes = new LinkedList<Node>();
    	LinkedList<LinkOLD> llLinks = new LinkedList<LinkOLD>();
    	
    	//Maps Node Names to Integer Values
    	TreeMap<Text,Integer> tm = new TreeMap<Text,Integer>();
    	int nodeNum = 0;
    	
    	for (Map.Entry<Key,Value> entry : scanner) {
    		
    		Key k = entry.getKey();
    		
    		Text f = k.getRow();
    		Text t = k.getColumnQualifier();
    		
    		if( !tm.containsKey(f) ) {
    			tm.put(f, nodeNum);
    			nodeNum++;
    			Node n = new Node(f.toString(),"1");
        		llNodes.add(n);
    		}
    		
    		if( !tm.containsKey(t) ) {
    			tm.put(t, nodeNum);
    			nodeNum++;
    			Node n = new Node(t.toString(),"1");
        		llNodes.add(n);
    		} 
    		    		
    		LinkOLD e = new LinkOLD(tm.get(f),tm.get(t),1);
    		llLinks.add(e);
    	}
    	
    	return new Graph(counter.incrementAndGet(),llNodes,llLinks);
    }
    */
    

}
