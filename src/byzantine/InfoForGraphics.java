import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;

public class InfoForGraphics implements Serializable{
  private int value;
  private int num_nodes;
  private ArrayList<Integer> traitors;
  private int final_value;
  public InfoForGraphics(int num_nodes, int num_traitors, ArrayList<Integer> traitors, int value){
    this.num_nodes = num_nodes;
    this.traitors = traitors;
    this.value = value;
    this.final_value = -1;
  }
  public InfoForGraphics(){};
  public int getValue(){
    return this.value;
  }
  public int getNum_nodes(){
    return this.num_nodes;
  }

  public ArrayList<Integer> getTraitors(){
    return this.traitors;
  }

  public int getFinal_value(){
    return this.final_value;
  }
  public void setValue(int value){
    this.value = value;
  }
  public void setNum_nodes(int num_nodes){
    this.num_nodes = num_nodes;
  }
  public void setFinal_value(int final_value){
    this.final_value= final_value;
  }
  public void setTraitors(ArrayList<Integer> traitors){
    this.traitors = traitors;
  }
}
