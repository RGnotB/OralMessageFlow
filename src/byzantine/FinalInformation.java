import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;

//class to store all necessary informations for Graphic Interface
public class FinalInformation implements Serializable{
  private int num_nodes;
  private ArrayList<Integer> traitors;
  private ArrayList<ArrayList<Message>> messages;
  private int commander_value;
  private int final_value;
  public FinalInformation(int num_nodes, int commander_value, int final_value, ArrayList<Integer> traitors, ArrayList<ArrayList<Message>> messages){
    this.num_nodes = num_nodes;
    this.commander_value = commander_value;
    this.final_value = final_value;
    this.traitors = traitors;
    this.messages = messages;
  }

  public int getNum_nodes(){
    return this.num_nodes;
  }

  public int getCommander_value(){
    return this.commander_value;
  }

  public int getFinal_value(){
    return this.final_value;
  }
  public ArrayList<Integer> getTraitors(){
    return this.traitors;
  }

  public ArrayList<ArrayList<Message>> getMessages(){
    return this.messages;
  }
  public void setNum_nodes(int num_nodes){
    this.num_nodes = num_nodes;
  }

  public void setCommander_value(int commander_value){
    this.commander_value = commander_value;
  }

  public void setFinal_value(int final_value){
    this.final_value = final_value;
  }
  public void setTraitors(ArrayList<Integer> traitors){
    this.traitors = traitors;
  }
  public void setMessages(ArrayList<ArrayList<Message>> messages){
    this.messages = messages;
  }
}
