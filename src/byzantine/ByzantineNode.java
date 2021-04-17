import java.util.ArrayList;
import java.util.Random;
import peersim.config.*;
import peersim.core.*;
import peersim.cdsim.CDProtocol;
import java.util.List;
import java.util.concurrent.locks.*;

//Class that implement the ByzantineNode
public class ByzantineNode implements Linkable, Node {
  //the index of commander's node
  private final int COMMANDER = 0;
  //Value that mean attack
  private final int ATTACK = 1;
  //Value that mean retreat
  private final int RETREAT = 0;
  /**
  * The protocols on this node.
  */
  protected Protocol[] protocol = null;

  /**
  * The ID of the node.
  */
  private final int ID;
  //The final order extablish fit majority function
  private int final_order;
  //The number of nodes of the Network
  private int num_nodes;
  //extablish if this node is a traitor or not
  private boolean is_traitor;
  //The value of commander's order
  private int commander_value;
  //The list of messages receive with OralMessage algorithm
  private ArrayList<ArrayList<Message>> messages;
  //The list of other nodes in the Network
  private ArrayList<ByzantineNode> other_nodes;
  //the method to send the value if this node is a traitor
  private String traitor_method;
  //the index of the last set of messages modified by majority
  private int last_modified;
  //The root of tree's Messages
  private MyNode root;
  //a list of next messages to send to other_nodes
  private ArrayList<Message> messages_to_send;
  //this variable helps OralMessage protocol to understand if the previous iteration is terminated
  private boolean all_messages_sended = false;
  private boolean msg_status = false;
  final private Lock lock;
  final private Condition condition;
  final private Condition msg_condition;
  private int total_messages_receive = 0;
  public ByzantineNode(String prefix){
      this.lock = new ReentrantLock();
      this.condition = lock.newCondition();
      this.msg_condition = lock.newCondition();
      this.ID = 0;
  }

  //loyal ByzantineNode constructor
  public ByzantineNode(int commander_value, int num_nodes, int id){
    this.lock = new ReentrantLock();
    this.condition = lock.newCondition();
    this.msg_condition = lock.newCondition();
    this.messages_to_send = new ArrayList<Message>();
    this.num_nodes = num_nodes;
    this.other_nodes = new ArrayList<ByzantineNode>(num_nodes);
    this.commander_value = commander_value;
    this.ID = id;
    setLoyal(commander_value); //Set the node loyal
    //Setting the node in the Network with the protocols
    String[] names = Configuration.getNames(PAR_PROT);
    CommonState.setNode(this);
    protocol = new Protocol[names.length];
    for (int i=0; i < names.length; i++) {
	     CommonState.setPid(i);
  	   Protocol p = (Protocol) Configuration.getInstance(names[i]);
  	    protocol[i] = p;
    }
  }

  //Traitor ByzantineNode constructor
  public ByzantineNode(int commander_value, int num_nodes, int id, String traitor_method){
    this.lock = new ReentrantLock();
    this.condition = lock.newCondition();
    this.msg_condition = lock.newCondition();
    this.messages_to_send = new ArrayList<Message>();
    this.num_nodes = num_nodes;
    this.other_nodes = new ArrayList<ByzantineNode>(num_nodes);
    this.commander_value = commander_value;
    this.ID = id;
    setTraitor(commander_value, traitor_method); //Set the node loyal
    //Setting the node in the Network with the protocols
    String[] names = Configuration.getNames(PAR_PROT);
    CommonState.setNode(this);
    protocol = new Protocol[names.length];
    for (int i=0; i < names.length; i++) {
       CommonState.setPid(i);
       Protocol p = (Protocol) Configuration.getInstance(names[i]);
        protocol[i] = p;
    }
  }

    //Method to set this node traitor
    public void setTraitor(int value, String traitor_method){
      this.traitor_method = traitor_method;
      this.is_traitor = true;
      if(traitor_method.equalsIgnoreCase("opposite")){ //if traitor_method is to send the opposite value => change the commander's value
        if(value == ATTACK){
          this.commander_value = RETREAT;
        }else{
          this.commander_value = ATTACK;
        }
      }else{
        this.commander_value = value;
      }
      ArrayList<Integer> sender = new ArrayList<Integer>();
      sender.add(COMMANDER);
      //sender.add(this.ID);
      Message msg = new Message(value, sender);
      msg.setCycle(0);
      this.root = new MyNode(msg);
      if(this.num_nodes < 3){
        this.root.setFinalOrder(this.commander_value);
        msg.setFinalValue(this.commander_value);
      }
      if(value == RETREAT){
        this.root.attack();
      }else{
        this.root.retreat();
      }
    //  this.messages_to_send.add(msg);
      addMessage(msg, true);
    }

    //Methos to set this node Loyal
    public void setLoyal(int value){
      this.is_traitor = false;
      this.commander_value = value;
      ArrayList<Integer> sender = new ArrayList<Integer>();
      sender.add(COMMANDER);
    //  sender.add(this.ID);
      Message msg = new Message(this.commander_value, sender);
      msg.setCycle(0);
      this.root = new MyNode(msg);
      if(this.num_nodes < 3){
        this.root.setFinalOrder(this.commander_value);
        msg.setFinalValue(this.commander_value);
      }
      if(this.commander_value == ATTACK){
        this.root.attack();
      }else{
        this.root.retreat();
      }
    //  this.messages_to_send.add(msg);
      addMessage(msg, true);
    }


  //Method to add a Message by other nodes
  synchronized public void addMessage(Message msg, boolean to_resend){
    List<MyNode<Message>> initial_children = this.root.getChildren();
    if(initial_children.size() < this.num_nodes - 1){
      this.total_messages_receive++;
      MyNode<Message> new_node = new MyNode<Message>(msg, this.root);
    }else{
      for(MyNode<Message> child: initial_children){
        recAddMessage(msg, child);
      }
    }
    if(to_resend){
      this.messages_to_send.add(msg);
    }
  }
  synchronized public void recAddMessage(Message msg, MyNode<Message> node){
    ArrayList<Integer> node_senders = node.getData().getSenders();
    ArrayList<Integer> msg_senders = msg.getSenders();
    int i = 0;
    if( node_senders.size() == msg_senders.size()-1){
      for(int z = 0; z < node_senders.size(); z++){
        if(node_senders.get(z) != msg_senders.get(z)){
          return;
        }else{
          i++;
        }
      }
      if(i == node_senders.size()){
        MyNode<Message> new_node = new MyNode<Message>(msg, node);
        this.total_messages_receive++;
      }
      return;
    }else{
      List<MyNode<Message>> children = node.getChildren();
      for(MyNode<Message> child: children){
        recAddMessage(msg, child);
      }
    }
    return;
  }


  //Method to extablish if this node is traitor or not
  public boolean isTraitor(){
    return this.is_traitor;
  }

  //Method to return
  //the value that commander sent to this node if this node is loyal
  //or an other value if this node is a traitor
  public int getValue(){
    if(!is_traitor || this.traitor_method.equalsIgnoreCase("opposite")){
      return this.commander_value;
    }else {//if i'm a traitor and traitor_method is random => send a random value
      Random rand = new Random();
      if(rand.nextBoolean()){
        return ATTACK;
      }else{
        return RETREAT;
      }
    }
  }

  //Method to return the root of messages
  public MyNode<Message> getRoot(){
    return this.root;
  }
  //return the number of neighbors
  public int degree(){
    return this.other_nodes.size();
  }

  //return the ByzantineNode i of my neighbors
  public ByzantineNode getNeighbor(int i){
    return this.other_nodes.get(i);
  }

  //add a ByzantineNode to my neighbors
  public boolean addNeighbor(Node neighbour){
    return this.other_nodes.add((ByzantineNode)neighbour);
  }

  //Method to get the ID of this node
  public long getID(){
    return this.ID;
  }

  //Method to get the neighbors of this node
  public ArrayList<ByzantineNode> getNeighbors(){
    return this.other_nodes;
  }

  //Method to get the list of messages that this node has received by other nodes
  synchronized public ArrayList<Message> getMessages(){
    ArrayList<Message> res = new ArrayList<Message>();
    int cycle = (int)CommonState.getTime();
    int index = 0;
    boolean find =false;
    if(cycle == 0){
      res.add(this.messages_to_send.remove(0));
    }else{
      while(!find && !this.messages_to_send.isEmpty()){
        if(this.messages_to_send.get(0).getCycle() == cycle){
          res.add(this.messages_to_send.remove(0));
        }else{
          find = true;
        }
      }
    }
    return res;
  }

  //Method to set the final value of the order obtained by the protocol Majority
  public void setFinalOrder(int value){
    this.final_order = value;
  }

  //Method to get the final order of this node
  public int getOrder(){
    if(!isTraitor()){
      Message msg = (Message)this.root.getData();
      return msg.getFinalValue();
    }else{
      return this.commander_value;
    }
  }

  //====METHODs NEVER USED====


  //Method to extablisg if a Node is my neighbor or not
  public boolean contains(Node neighbour){
    return this.other_nodes.contains((ByzantineNode)neighbour);
  }

  //Method to get the protocol i
  public Protocol getProtocol(int i){
    return protocol[i];
  }
  //Method to get the protocol's number
  public int protocolSize(){
    return protocol.length;
  }

  public int getMessagesReceive(){
    return this.total_messages_receive;
  }
  //NEVER USED
  public Object clone(){
    ByzantineNode bn = null;
    try{
      bn = (ByzantineNode) super.clone();
    }catch(CloneNotSupportedException e){
      System.out.println(e);
    }
    return bn;
  }


  public void pack(){}
  public void onKill(){}
  public void setIndex(int index){}
  public int getIndex(){return -1;}
  public String toString(){return "FAIL";}
  public int hashCode() {return -1;}
  public int getFailState(){return -1;}
  public void setFailState(int failState){}
  public boolean isUp(){return true;}
}
