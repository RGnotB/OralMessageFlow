import peersim.config.*;
import peersim.core.*;
import java.util.Random;
import java.util.*;
import peersim.cdsim.CDProtocol;
import java.io.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
//Control that creates the JSON file for the Graphic Interface
public class JsonControl implements Control {
    //the number of traitor nodes in the network
    private static final String PAR_BAD_NODES = "badnodes";
    //number of traitor nodes in the network
    private final int num_bad_nodes;
    //the value shared by the commander
    private int commander_value;
    //the final order of lieutenants
    private int final_value;
    //number of nodes
    private int num_nodes;
    //the list of ID of traitors
    private ArrayList<Integer> traitors;
    //The object mapper to write and read information
    private ObjectMapper mapper;
    //file of information
    private File file_info;
    //counter to understand how many threads had finish write his information
    private int counter;
    public JsonControl(String prefix){
      this.num_bad_nodes = Configuration.getInt(prefix + "." + PAR_BAD_NODES);
      this.counter = 0;
    }

    public boolean execute(){
      if(CommonState.getTime() == this.num_bad_nodes + 2){//==> the JSON file must be created
        System.out.println("Please wait a while your JSON file is about to be created");
        //get the information
        InfoForGraphics infos;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.file_info = new File("InfoGraphics.json");
        try{
          infos = this.mapper.readValue(this.file_info, InfoForGraphics.class);
          this.num_nodes = infos.getNum_nodes();
          this.commander_value = infos.getValue();
          this.final_value= infos.getFinal_value();
          this.traitors = infos.getTraitors();
        }catch(IOException e){
          System.out.println("OH NO, SOMETHING GOES WRONG");
        }
        //create the final json file for graphic interface
        final File final_file = new File("./final_file.json");
        Map<Integer, ArrayList<Message>> lieutenants_messages = new HashMap<>();

        //create threads to create the file JSON
        CreateJSON task;
        Thread thread;
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        Integer counter = new Integer(0);
        ArrayList<Message> tmp;
        for(int i = 1; i < Network.size(); i++){//For each lieutenant i!=0 (0 is the commander)
          ByzantineNode byzantine = (ByzantineNode) Network.get(i);//get the lieutenant i of the Network
          tmp = new ArrayList<Message>();
          lieutenants_messages.put((int)byzantine.getID(), tmp);
          task = new CreateJSON(byzantine, tmp, lock, condition, this);
          thread = new Thread(task);
          thread.start();
        }
        try{
            //wait until all threads had finish
          lock.lock();
          while(getCount() < Network.size()-1){
            condition.await();
          }
          lock.unlock();
        }catch(InterruptedException e){
          System.out.println("OH NO, SOMETHING GOES WRONG");
        }
        try{
            //create FINALINFORMATION and write it on the file hson
          ArrayList<ArrayList<Message>> messages = new ArrayList<>();
          for(int i = 1; i < this.num_nodes; i++){
            messages.add(lieutenants_messages.get(i));
          }
          FinalInformation f_info = new FinalInformation(this.num_nodes, this.commander_value, this.final_value, this.traitors, messages);
          this.mapper.writeValue(final_file, f_info);
          //delete the partial JSON file
          File file_to_delete = new File("./InfoGraphics.json");
          file_to_delete.delete();
          System.out.println("File JSON is Ready!");
        }catch(IOException e){
          System.out.println("OH NO, SOMETHING GOES WRONG");
        }
      }
      return false;
    }
    public void addCount(){
      this.counter++;
    }
    public int getCount(){
      return this.counter;
    }
}
