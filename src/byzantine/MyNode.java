import java.util.ArrayList;
import java.util.List;

public class MyNode<Message> {
  //Value that mean attack
  private final int ATTACK = 1;
  //Value that mean retreat
  private final int RETREAT = 0;
    private List<MyNode<Message>> children;
    private MyNode<Message> parent;
    private Message data;
    private int attack_counter;
    private int retreat_counter;
    private int final_order;
    private int total_nodes;
    public MyNode(Message data) {
        this.children = new ArrayList<MyNode<Message>>();
        this.data = data;
        this.final_order = -2;
        this.parent = null;
        this.total_nodes++;
        this.attack_counter = 0;
        this.retreat_counter = 0;
    }

    public MyNode(Message data, MyNode<Message> parent) {
        this.data = data;
        this.setParent(parent);
        this.final_order = -1;
        this.children = new ArrayList<MyNode<Message>>();
        this.total_nodes++;
        this.attack_counter = 0;
        this.retreat_counter = 0;
    }

    public List<MyNode<Message>> getChildren() {
        return children;
    }

    public void setParent(MyNode<Message> parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public MyNode<Message> getParent(){
      return this.parent;
    }
    public void addChild(Message data) {
        MyNode<Message> child = new MyNode<Message>(data);
        this.children.add(child);
    }

    public void addChild(MyNode<Message> child) {
        this.children.add(child);
    }

    public Message getData() {
        return this.data;
    }

    public void setData(Message data) {
        this.data = data;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public void attack(){
      this.attack_counter++;
      checkFinish();
    }

    public void retreat(){
      this.retreat_counter++;
      checkFinish();
    }

    //Check and set the final order if can
    public void checkFinish(){
      if((this.retreat_counter + this.attack_counter) == (this.children.size()) && this.children.size() > 0){
        if(this.attack_counter > this.retreat_counter){
          this.final_order = ATTACK;
        }else{
          this.final_order = RETREAT;
        }
      }else if(isRoot() && (this.retreat_counter + this.attack_counter) == (this.children.size() + 1)){
        if(this.attack_counter > this.retreat_counter){
          this.final_order = ATTACK;
        }else{
          this.final_order = RETREAT;
        }
      }
    }

    //check if each child of the node has said attack or retreat
    public boolean canEstablish(){
      checkFinish();
      if(isRoot()){
        return ((this.retreat_counter + this.attack_counter) == (this.children.size()+1));
      }else{
        return ((this.retreat_counter + this.attack_counter) == (this.children.size())) ;
      }
    }
    public int getFinalOrder(){
      return this.final_order;
    }
    public void setFinalOrder(int final_order){
      this.final_order = final_order;
    }
    public int getTotalNodes(){
      return this.total_nodes;
    }
}
