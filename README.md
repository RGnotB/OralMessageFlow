# OralMessageFlow
To run a simulation of OralMessages you have first of all to compile the code by the command:

make byz

now you can simulate the algoritm! 
You have to send the command:

make oralmsg NUM_NODES=n TRAITORS=t C_TRAITOR={1||0} VALUE={1||0}

where:
-> n is the number of total nodes of the network
-> t is the number of total traitors in the network
-> C_TRAITOR={1||0} means that the value can be only 1 or 0, and it indicates if the commander is traitor==> 1 the commander is traitor, 0 the commander is loyal
-> VALUE={1||0}  means that the value can be only 1 or 0, and it indicates what value the commander want to send to eache lieutants (if is loyal)

Now the execution start and you have to wait until the algoritm is terminated.
At the end the result will print on the terminal and you will know:
-> what is the order for each lieutenant
-> what is the order reached by the network

Now the JSON file is created and the OralMessage site can be open, 
you can drag and drop the json file on the site and you can see the animation of the execution of Lamport's algorithm.
