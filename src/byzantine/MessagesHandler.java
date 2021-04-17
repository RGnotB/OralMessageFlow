import java.io.*;
import java.util.*;

import java.util.concurrent.locks.*;

//class to create tasks to send messages between lieutenants
public class MessagesHandler implements Runnable{
  //the lieutenant sender
  private ByzantineNode byz_node;
  //the cycle of the message
  private int cycle;
  //the number of traitors
  private int num_bad_nodes;

  private Lock om_lock;

  private Condition om_condition;

  private OralMessage om_protocol;
  public MessagesHandler(ByzantineNode byz_node, int cycle, int num_bad_nodes, OralMessage om_protocol, Lock om_lock, Condition om_condition){
    this.byz_node = byz_node;
    this.cycle = cycle;
    this.num_bad_nodes = num_bad_nodes;
    this.om_lock = om_lock;
    this.om_protocol = om_protocol;
    this.om_condition = om_condition;
  }

  public void run(){
    ArrayList<Integer> senders;
    Message new_message;
    boolean just_received;
    ArrayList<Message> messages = byz_node.getMessages();
    //for each message of the byz_lieutenant
    for(Message msg: messages){
      senders = msg.getSenders();//get the senders of messages
      if(byz_node.isTraitor()){//check if this is traitor
        new_message = new Message(byz_node.getValue(), senders);
      }else{
        new_message = new Message(msg.getValue(), senders);
      }

      if(!senders.contains((int)byz_node.getID())){
        new_message.addSender((int)byz_node.getID());//Add this general to the senders
        new_message.setCycle(cycle + 1);
        byz_node.addMessage(new_message, false);//send the value
      }

      //sending the message at other lieutenants
      for(ByzantineNode lieutenant: byz_node.getNeighbors()){//for every other node
        senders = msg.getSenders();
        if(byz_node.isTraitor()){
          new_message = new Message(byz_node.getValue(), senders);
        }else{
          new_message = new Message(msg.getValue(), senders);
        }
        if(!senders.contains((int)byz_node.getID())){
          new_message.addSender((int)byz_node.getID());//Add this general to the senders
          new_message.setCycle(cycle + 1);
          lieutenant.addMessage(new_message, true);//send the value at the lieutenant
          }
        }
      }

      //say to OM control that this lieutenant had finish to send messages
      this.om_lock.lock();
      this.om_protocol.addCount();
      this.om_protocol.getCondition().signal();
      this.om_lock.unlock();
  }
}
