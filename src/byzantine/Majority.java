import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.*;
import peersim.cdsim.CDProtocol;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.locks.*;
//Protocol to implement the majority function of OralMessage of Lamport
public class Majority implements CDProtocol{
  private int attack_value_counter = 0; //Number of value for attack
  private int retreat_value_counter = 0;//Number of value for retreat
  private final int ATTACK = 1;//value that means attack
  private final int RETREAT = 0;//value that means retreat
  //number of traitors nodes in the network
  private final int num_bad_nodes;
  //the number of traitors. Default to 0.
  private static final String PAR_BAD_NODES = "badnodes";

  private int num_done;
  //while majority must be done
  private final int num_cycle;
  public Majority(String prefix){
    this.num_bad_nodes = Configuration.getInt(prefix + "." + PAR_BAD_NODES);
    this.num_cycle = (this.num_bad_nodes * 2);
    this.num_done = 0;
  }

  public Object clone(){
    Majority ma = null;
    try{
      ma = (Majority) super.clone();
    }catch(CloneNotSupportedException e){
      System.out.println(e);
    }
    return ma;
  }

  public void nextCycle(Node node, int protocol_id){
     if(CommonState.getTime() == this.num_bad_nodes){//=> function majority  should be performed!
          ByzantineNode byz_node = (ByzantineNode) node; //get the lieutenant
          if(byz_node.getID() != 0){//if this isn't the commander node
            MyNode<Message> root = byz_node.getRoot();
            if(root.getChildren().size() == 0){ //If the lieutenant has received only the commander's message

              byz_node.setFinalOrder(byz_node.getValue());//take the order of the commander
            }else{
              recSetLeaf(root);//Set the order of the leaf of the tree;
              recursiveCalculate(root); //calculate the order by messages received
              byz_node.setFinalOrder(root.getFinalOrder());//take the final order from the root
            }
            if(byz_node.isTraitor()){
              byz_node.getRoot().getData().setFinalValue(byz_node.getValue());
            }
          }
        }
  }
  //Recursive Method to set the final value of leaf's tree
  public void recSetLeaf(MyNode<Message> n){
    List<MyNode<Message>> children = n.getChildren();
    if(children.size() == 0){
      n.getData().setFinalValue(n.getData().getValue());
      n.setFinalOrder(n.getData().getValue());
    }else{
        for(MyNode<Message> child: children){//for eache child of the node
          recSetLeaf(child);
        }
    }
  }
  //Recursive Method to calculate the final order by the tree of messages
   public void recursiveCalculate(MyNode<Message> n){
    List<MyNode<Message>> children = n.getChildren();
    for(MyNode<Message> child: children){//for eache child of the node
      if(child.getFinalOrder() == -1){//if the child can't establish the order
        recursiveCalculate(child); //calculate
        if(child.getFinalOrder() == ATTACK){//now he can say to his father what is the order
          n.attack();
        }else if (child.getFinalOrder() == RETREAT){
          n.retreat();
        }
        this.num_done++;
      }else{
        if(child.getFinalOrder() == ATTACK){//now he can say to his father what is the order
          n.attack();
        }else if (child.getFinalOrder() == RETREAT){
          n.retreat();
        }
        this.num_done++;
      }

      if(n.canEstablish()){//if the node has receive all the children's orders he can establish his order
        if(n.getFinalOrder() == ATTACK){
          n.getData().setFinalValue(ATTACK);
        }else if(n.getFinalOrder() == RETREAT){
          n.getData().setFinalValue(RETREAT);
        }
      }
    }
  }
}
