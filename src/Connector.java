/* Connector class
 * "Connects" pathNodes together if movement is possible, each node has its own unique connectors
 * If the node being checked for connection is within the 40x40 grid and is movable, then
 * the connection can be made. When going through the nodes to find best path, only the connecting 
 * nodes can be considered
 */
public class Connector {

	PathNode node; // Child Node (Parent node holds connector)

	// Connector is called in the pathNode class and each node holds an arraylist of
	// connectors
	Connector(PathNode connect) {
		node = connect;
	}

}