# Java-RMI-ISS

Java app simulating a distributed system consisting of 5 servers with a database and one proxy. 
Its purpose is to demonstrate load balancing accross different servers depending on the geographical location of the users and the server load, as well as using different caches.

## How to run
1. Extract the content of the zip file into a folder
2. Open two terminals in this folder
3. On terminal 1 (Server), run the following command:

```java -jar Java-RMI-ISS.jar [server mode]```

On terminal 2 (Client), run the following command:

```java -cp Java-RMI-ISS.jar Client [client mode]```


### Modes (server - client)
- Naive mode : 0 0
- Server cache : 1 1
- Server and client : 1 2 
