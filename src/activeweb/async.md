<div class="page-header">
   <h1>Async</h1>
</div>

JavaLite Async is a lightweight system for processing asynchronous jobs.
When developing a website, you often need to run some process while not slowing down 
user page refresh.


Async uses [Apache Artemis](https://activemq.apache.org/artemis/) under the hood, but makes it 
very easy to do so. While Apache Artemis is a [JMS](https://en.wikipedia.org/wiki/Java_Message_Service) 
broker, the Async adds an abstraction layer based on a [Command Pattern](https://en.wikipedia.org/wiki/Command_pattern), 
which makes it trivial to add asynchronous processing: 



## Embedded broker instance

Setting up Apache Artemis requires substantial knowledge of JMS and this specific implementation. 
However, JavaLite Async makes it easy by configuring Apache Artemis with reasonable defaults 


~~~~ {.java  .numberLines}
Async async = new Async("/opt/project1", false, new QueueConfig("email", new CommandListener(), 50));
async.start();
~~~~

where `/opt/project1` is a place to store persistent messages, `email` is a name of a queue, and 50 is number of 
 listeners (threads) to create for processing. 



## Writing a simple command

## Processing a command

## Creating multiple queues

## Peeking into queues

## Reading synchronously

## Text vs Binary messages

## Command with DB access

## Integration with ActiveWeb

## Access to Artemis Config