package NEAT;

import java.util.ArrayList;

// Connection History for innovation
public class ConnectionHistory {
    public int global_inno = 0;
    public ArrayList<Connection> allConnections = new ArrayList<Connection>(); // All Connections in existence (or atleast using this history)
    public int n_inputs, n_outputs; // number of inputs and outputs

    // Constructor
    public ConnectionHistory(int i, int o){
        n_inputs = i;
        n_outputs = o;
    }

    // Check if connection exists in history
    public Connection exists(Node n1, Node n2){
        if(allConnections.size() == 0) return null;

        for(Connection c : allConnections){
            if(c.in_node.number == n1.number && c.out_node.number == n2.number){
                return c;
            }
        }
        return null;
    }

    // Print all connections
    public void printAll(){
        System.out.println("All Connections");
        System.out.println("------------------------------------------------");
        for(int i = 0; i < allConnections.size(); i++){
            allConnections.get(i).printGene();
        }
        System.out.println("------------------------------------------------");
    }
}