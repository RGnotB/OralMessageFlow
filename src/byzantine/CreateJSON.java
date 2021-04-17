import java.io.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
import java.util.*;

//class to create task to create the file JSON
public class CreateJSON implements Runnable{
  private ArrayList<Message> messages;
  private Lock lock;
  private Condition condition;
  private Integer counter;
  private ByzantineNode byz;
  private JsonControl j_control;
  public CreateJSON(ByzantineNode byz, ArrayList<Message> messages, Lock lock, Condition condition, JsonControl j_control){
    this.byz = byz;
    this.messages = messages;
    this.lock = lock;
    this.condition = condition;
    this.j_control = j_control;
  }
  public void run(){
    recursiveAdding(this.byz.getRoot());
    this.lock.lock();
    this.j_control.addCount();
    this.condition.signal();
    this.lock.unlock();
  }
  public void recursiveAdding(MyNode<Message> node){
    List<MyNode<Message>> children = node.getChildren();
    if(children.size() > 0){
      for(int i = 0; i < children.size(); i++){
        this.messages.add(children.get(i).getData());
        recursiveAdding(children.get(i));
      }
    }
  }
}
