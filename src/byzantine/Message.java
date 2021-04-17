import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;
//Class that rapresents messages send and received by lieutenants
public class Message implements Serializable{
  private int value_received;
  private int final_value;
  private ArrayList<Integer> senders_sequence;
  private int cycle_send;

  public Message(int value_received, ArrayList<Integer> sender_sequence){
    this.value_received = value_received;
    this.senders_sequence = new ArrayList<>(sender_sequence);
    this.final_value = -1;
  }


  public void addSender(int sender){
    this.senders_sequence.add(sender);
  }

  public void setFinalValue(int final_value){
    this.final_value = final_value;
  }

  public ArrayList<Integer> getSenders(){
    return this.senders_sequence;
  }
  public void setValue(int value){
    this.value_received = value;
  }
  public int getValue(){
    return this.value_received;
  }

  public int getFinalValue(){
    return this.final_value;
  }

  public void setCycle(int cycle_send){
    this.cycle_send = cycle_send;
  }
  public int getCycle(){
    return this.cycle_send;
  }
}
