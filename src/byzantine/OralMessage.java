import java.util.ArrayList;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.*;
import peersim.cdsim.CDProtocol;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
//Protocol that implements the OralMessage algorithm of Lamport
public class OralMessage implements Control{
  //the number of nodes in the network
  private static final String PAR_NUM_NODES = "nodes";
  //the number of traitors. Default to 0.
  private static final String PAR_BAD_NODES = "badnodes";
  //number of total nodes in the network
  private final int num_nodes;
  //number of traitors nodes in the network
  private final int num_bad_nodes;
  //the threadpool that executes the tasks to send messages
  private ThreadPoolExecutor executor;

  private final Condition condition;

  private final Lock lock;

  private final Condition lieutenant_condition;

  private Integer counter;
  private int current_state = 0;
  public OralMessage(String prefix){
    this.num_nodes = Configuration.getInt(prefix + "." + PAR_NUM_NODES);
    this.num_bad_nodes = Configuration.getInt(prefix + "." + PAR_BAD_NODES);
    this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.num_nodes);
    this.lock = new ReentrantLock();
    this.condition = lock.newCondition();
    this.lieutenant_condition = lock.newCondition();
    this.counter = new Integer(0);
  }

  public Object clone(){
    OralMessage om = null;
    try{
      om = (OralMessage) super.clone();
    }catch(CloneNotSupportedException e){
      System.out.println(e);
    }
    return om;
  }

  public boolean execute(){
    if(CommonState.getTime() < this.num_bad_nodes){ // ==> The algorithm OralMessage should be performed!
        for(int i = 1; i < Network.size(); i++){//For each lieutenant i!=0 (0 is the commander)
          ByzantineNode byz_node = (ByzantineNode)Network.get(i);; //get the ByzantineNode by the node
          if(byz_node.getID() != 0){//if this isn't the commander node
            MessagesHandler task = new MessagesHandler(byz_node, (int)CommonState.getTime(), this.num_bad_nodes, this, this.lock, this.lieutenant_condition);
            this.executor.execute(task);
          }
        }
        lock.lock();
        while(getCount() < this.num_nodes-1){
          try{
            condition.await();
            }catch(InterruptedException e){
              System.out.println("ERROR: thread interrupted");
            }
          }
          this.counter = 0;
          this.current_state++;
          lieutenant_condition.signalAll();
          lock.unlock();
      }else if(CommonState.getTime() == this.num_bad_nodes + 1){
        this.executor.shutdownNow();
      }
      return false;
    }

    public int getCurrentState(){
      return this.current_state;
    }
    public void addCount(){
      this.counter++;
    }
    public int getCount(){
      return this.counter;
    }
    public Condition getCondition(){
      return this.condition;
    }
}
