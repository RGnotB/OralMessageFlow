# OralMessageFlow

OralMesageFlow is a simulator with a graphic interface that can execute the Oral Message algorithm (Lamport's algorithm for the Byzantine Generals Problem presented in [this article](http://doi.acm.org/10.1145/357172.357176)). 

This simulator can first of all executes the Oral Message algorithm writed in Java then, when it finish the execution, it will creates a JSON file that can be passed at the site OralMessage.html (writed in Javascript, HTML and CSS), the web page will show you the iterations of the execution.

This project was born with the idea to show how the Lamport's algorithm works. Because this type of algorithms, aren't simple to understand and follow their executions is very difficoult.

This is a Thesis's project for a Computer Science Degree at [University of Pisa](https://di.unipi.it/). 

This project is write with the help of [Peersim](http://peersim.sourceforge.net/) and [Cytoscape.js](https://js.cytoscape.org/).

## The Lamport's algorithm

Algorithm OM(0). 

- (1) The commander sends his value to every lieutenant. 
- (2) Each lieutenant uses the value he receives from the commander, or uses the value RETREAT, that is 0, if he receives no value. 

Algorithm OM(m), m > 0. 

- (1) The commander sends his value to every lieutenant. 
- (2) For each i, let Vi be the value Lieutenant i receives from the commander, or else be RETREAT if he receives no value. Lieutenant i acts as the commander in Algorithm OM(m - 1) to send the value Vi to each of the n-2 other lieutenants. 
- (3) For each i, and each j!=i, let Vj be the value Lieutenant i received from Lieutenant j in step (2) (using Algorithm OM(m - 1)), or else RETREAT if he received no such value. Lieutenant i uses the value majority (Vl,..., Vn-1). 

## Compiling

To run a simulation of OralMessages you have first of all to compile the code, you can do it in this folder by the command:

```bash
make byz
```

## Usage
 
You have to send, in this folder, the command:

```bash
make oralmsg NUM_NODES=n TRAITORS=t C_TRAITOR=1/0 VALUE=1/0
```

where:

- n is the number of total nodes of the network.
- t is the number of total traitors in the network.
- C_TRAITOR=1/0 means that the value can be only 1 or 0, and it indicates if the commander is traitor==> 1 the commander is traitor, 0 the commander is loyal.
- VALUE=1/0  means that the value can be only 1 or 0, and it indicates what value the commander want to send to eache lieutants (if is loyal).


AN EXAMPLE: 

```bash
make oralmsg NUM_NODES=7 TRAITORS=2 C_TRAITOR=0 VALUE=0
```

This example creates a network of 7 nodes with 2 traitors, the commander is loyal and the value that he sends to the lieutenants is 0.

#### NOTE:
You don't have to exaggerate with the number of nodes or the execution will take a lot of time. For example with 16 nodes and 5 traitors the exacution need approximatly 20/30 minute on a good machine.


Now the execution start and you have to wait until the algoritm is terminated.

At the end the result will be print on the terminal and you will know:
- what is the order for each lieutenant.
- what is the order reached by the network.


Now the JSON file is created and the OralMessage site can be opened!

You have to open OralMessage.html in the folder: ./byzantine_js

You can drag and drop the json file on the site and you can see the network rapresented by a complete graph.

Using the two button on top left of this page you can choose to:
- see the iterations of messages exchanged.
- skip the animations to see the final result.

If you decide to see the iterations, you can encrease or decrease the speed of animations by the two buttons on this page.

If you want stop the animations to watch what messages the lieutenants have received, just click on the graph. You will able to restart the animation with the button RESTART. At the end of each iteration you can see the next by click on the NEXT ITERATION button. 

When the animations terminate you will see the final result.

You can see how each lieutenants had reached the order by clicking on his node. A page will be opened, and you will see the information tree of the node.

The nodes of this tree represent all the messages received by the lieutenant.

The information of a node are respectively:

- The value of the message.
- The path of the message. That is the set of the lieutenants who has received the message and in the next iteretion sent it to this lieutenant.
- The final order of the message. This is equal to the value if this node is a leaf or is equal to " ? " if this node isn't a leaf of the tree.

By clicking on a node of this tree you will see the final order of the node reached by calculating Majority(children of the node).

If you want see what is the final order reached by the lieutenant just click on the root of this information tree.

If you want to come back on the page with the graph of the network just click on the BACK button.

# Tips

This algorithm to work need a lot of exchages of messages. 

The number of messages exchaged is: n!/((n-f)!), where f is the number of traitors.

So the simulation in Java needs a lot of time. 

If you want only to see the animations of the algorithm you can try with the JSON files on the folder ./jsons.

There are a lot of examples, you can just drag and drop a file on the web page OralMessage.html that will shows you the simulation.

