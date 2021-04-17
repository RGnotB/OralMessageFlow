import peersim.config.*;
import peersim.core.*;
import java.util.Random;
import peersim.cdsim.CDProtocol;
import java.io.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;
//Control initializer to setup the Network and the ByzantineNodes
public class Initializer implements Control {
  private final int ATTACK = 1;//value that means attack
  private final int RETREAT = 0;//value that means retreat

  //the number of nodes in the network
  private static final String PAR_NUM_NODES = "nodes";
  //the number of traitors. Default to 0.
  private static final String PAR_BAD_NODES = "badnodes";

  //the commander_value that good nodes want to exchange
  private static final String PAR_VALUE = "commander_value";

  //extablish if commander is a traitor or not
  private static final String PAR_COMMANDER_TRAITOR = "commander_is_traitor";

  //The traitor method to send a value
  private static final String PAR_TRAITOR_METHOD = "traitor_method";

  //The traitor commander method to send a value
  private static final String PAR_TRAITOR_COMMANDER_METHOD = "traitor_commander_method";

  //number of total nodes in the network
  private final int num_nodes;

  //number of traitors nodes in the network
  private final int num_bad_nodes;

  //the commander_value
  private int commander_value;

  //boolean that extablish if commander is a traitor or not
  private final boolean commander_is_traitor;

  //extablish the traitor method to send a send a value must be "RANDOM" or "OPPOSITE"
  private final String traitor_method;

  //extablish the traitor method to send a send a value must be "RANDOM" or "OPPOSITE"
  private final String traitor_commander_method;

  //extablish if the commander is a traitor and has the random method is active
  private final boolean random_method;

  private int nodes_setted = 0; //number of nodes setted traitors

  private ArrayList<Integer> traitors;
  private final ObjectMapper mapper;
  private final File file_info;
  /*
  Standard constructor that reads the configuration parameters.
  Invoked by the simulation engine.
  */
  public Initializer(String prefix){
    this.num_nodes = Configuration.getInt(prefix + "." + PAR_NUM_NODES);
    this.num_bad_nodes = Configuration.getInt(prefix + "." + PAR_BAD_NODES);
    this.commander_value = Configuration.getInt(prefix + "." + PAR_VALUE);
    int tmp = Configuration.getInt(prefix + "." + PAR_COMMANDER_TRAITOR);
    this.traitor_method = Configuration.getString(prefix + "." + PAR_TRAITOR_METHOD);
    this.traitor_commander_method = Configuration.getString(prefix + "." + PAR_TRAITOR_COMMANDER_METHOD);
    this.traitors = new ArrayList<Integer>();
    this.file_info = new File("./InfoGraphics.json");
    this.mapper = new ObjectMapper();
    this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    if(tmp == 1){//if commander must be traitor
      this.traitors.add(0);
      this.commander_is_traitor = true;
      this.nodes_setted = 1;
    }else{
      this.commander_is_traitor = false;
    }
    //Check if the commander is a traitor and what is the traitor_method to set the value
    if(this.commander_is_traitor && this.traitor_commander_method.equalsIgnoreCase("opposite")){
      if(this.commander_value == ATTACK){
        this.commander_value = RETREAT;
      }else{
        this.commander_value = ATTACK;
      }
    }
    if(this.commander_is_traitor && this.traitor_commander_method.equalsIgnoreCase("random")){
      this.random_method = true;
    }else{
      this.random_method = false;
    }
  }

  public boolean execute(){
    Random rand = new Random(); //Random for decide if a node is a traitor or not
    boolean must_be_traitor = false; //if the remaining nodes are <= of (the num_bad_nodes - nodes_setted) ==> remaining nodes must be traitors
    for (int i = 0; i < this.num_nodes; i++){
      if((this.num_nodes - i) <= (num_bad_nodes - nodes_setted)){//check if the remaining nodes must be traitors or not
        must_be_traitor = true;
      }
      ByzantineNode new_byz_node;
      if(this.random_method){//if the commander is a traitor and has random method => change the value random
        if(rand.nextBoolean()){
          this.commander_value = ATTACK;
        }else{
          this.commander_value = RETREAT;
        }
      }
      if((nodes_setted < num_bad_nodes && rand.nextBoolean() && i!=0 )|| must_be_traitor){ //if this node is traitor
         new_byz_node = new ByzantineNode(this.commander_value, this.num_nodes, i, this.traitor_method); //create a new traitor ByzantineNode
         this.traitors.add(i);
         nodes_setted++;
      }else{//the node must be loyal
         new_byz_node = new ByzantineNode(this.commander_value, this.num_nodes, i); //create a new loyal ByzantineNode
      }
      Network.add(new_byz_node); //add the ByzantineNode to the Network
    }
    for(int i = 1; i < Network.size(); i++){// For each i node of the Network
      ByzantineNode tmp = (ByzantineNode)Network.get(i);
      for(int j=1; j < Network.size(); j++){ // For each j node of the Network
      	if(j!=i){//if i and j aren't the same node
      	  tmp.addNeighbor((ByzantineNode)Network.get(j)); //add j to the neighbours of i
      	}
      }
    }
    try{
      InfoForGraphics info = new InfoForGraphics(this.num_nodes, this.num_bad_nodes, this.traitors, this.commander_value);
      this.mapper.writeValue(this.file_info, info);
    }catch(IOException e){
      System.out.println("Oh no error with Graphic's file");
    }
    return false;
  }
}
