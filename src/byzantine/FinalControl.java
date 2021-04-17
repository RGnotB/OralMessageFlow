import peersim.config.*;
import peersim.core.*;
import java.util.Random;
import peersim.cdsim.CDProtocol;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
//Control that show the final state of the system
public class FinalControl implements Control {
  //Value that mean attack
  private final int ATTACK = 1;
  //Value that mean retreat
  private final int RETREAT = 0;
  //the number of nodes in the network
  private static final String PAR_BAD_NODES = "badnodes";
  //number of total nodes in the network
  private int num_nodes;

  //the commander_value
  private int commander_value;

  //number of total nodes in the network
  private final int num_bad_nodes;
  //number of lieutenant that say attack
  private int attack;

  //number of lieutenant that say retreat
  private int retreat;

  private int final_value;
  private ArrayList<Integer> traitors;
  private ObjectMapper mapper;
  private File file_info;
  private InfoForGraphics infos;
  public FinalControl(String prefix){
    this.num_bad_nodes = Configuration.getInt(prefix + "." + PAR_BAD_NODES);
    this.attack = 0;
    this.retreat = 0;
    this.infos = null;
  }

  public boolean execute(){
    if(CommonState.getTime() == this.num_bad_nodes + 1){//==> the FinalControl  should be performed!!
      this.mapper = new ObjectMapper();
      this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
      this.file_info = new File("InfoGraphics.json");
      try{
        infos = this.mapper.readValue(this.file_info, InfoForGraphics.class);
        this.num_nodes = infos.getNum_nodes();
        this.commander_value = infos.getValue();
        this.traitors = infos.getTraitors();
      }catch(IOException e){
        System.out.println("OH NO, SOMETHING GOES WRONG");
      }

      String res;
      System.out.println(" __________________________________________________________");
      System.out.println("|                   Execution Terminated                   |");
      System.out.println("|----------------------------------------------------------|");
      System.out.println("| Here are the FINAL values obtained from each Lieutenant  |");

      for(int i = 1; i < Network.size(); i++){//For each lieutenant i!=0 (0 is the commander)
        ByzantineNode byzantine = (ByzantineNode) Network.get(i);//get the lieutenant i of the Network
        if(byzantine.isTraitor()){//if the byzantine is traitor
          calculateFinalOrder(byzantine.getValue());
          System.out.println("|----------------------------------------------------------|");
          System.out.println("|       The (TRAITOR) Lieutenant " + i + " says: " + byzantine.getValue() + "                 |"); //get and print the order of lieutenant i
        }else{//if is loyal
          calculateFinalOrder(byzantine.getOrder());
          System.out.println("|----------------------------------------------------------|");
          System.out.println("|       The (LOYAL) Lieutenant " + i + " says: " + byzantine.getOrder() + "                   |"); //get and print the order of lieutenant i
        }
      }
      System.out.println("|__________________________________________________________|");
      if(this.attack > this.retreat){
        res = "ATTACK!";
        final_value = 1;
      }else if (this.attack < this.retreat){
        res = "RETREAT!";
        final_value = 0;
      }else{
        res = "I DON'T KNOW!";
        final_value = -1;
      }
      System.out.println("------------------------------------------------------------------> THE MAJORITY SAYS: " + res);
      try{
        infos.setFinal_value(this.final_value);
        this.mapper.writeValue(this.file_info, infos);
      }catch(IOException e){
        System.out.println("OH NO");
      }
    }
    return false;
  }

  public void calculateFinalOrder(int order){
    if(order == ATTACK){
      this.attack++;
    }else{
      this.retreat++;
    }
  }

}
