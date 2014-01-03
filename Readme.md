PROJECT GOAL:
-------------
The goal of this project is to offer a foundation for the issuer service (but not the functional service itself). The issuer service expose a REST API. For the scope of this project it consists of two simple interfaces for demonstration purposes only:
- A GET interface to allow clients to retrieve coins in JSON format
- A POST interface to allow clients to redeem/send coins in JSON format

Optional: Coins are stored in a SQL database (via squeryl library)

Further Requirements:
---------------------
- Coins have the following structure: case class Coin(type: String, value: Int)
- The deliverable should contain a documentation which describes how the system is deployed to allow future updates of individual software components. For instance in the future a newer Finagle server version may be used and the documentation should describe step by step how to integrate it with the other libraries and the actual source code.
- Source code should be documented as well.
- Test cases should be performed and documented. Optional: ScalaTest may be used for testing.

To be used technologies:
------------------------
- Scala programming language
- Twitter's Finagle server
- SBT build tool
- Optional: Eclipse IDE is recommended
- Optional: Coins are stored in a SQL database via squeryl library

The following links might be useful to understand Finagle's REST capabilities:
https://github.com/robi42/heroku-finagle-rogue/blob/master/src/main/scala/server.scala
https://github.com/twitter/finagle/blob/master/finagle-http/src/main/scala/com/twitter/finagle/http/path/Path.scala

BACKGROUND:
-----------
This information is optional background information about the project but doesn't describe the scope or functionality of the current project goal!

Open Coin introduces versatile digital cash which can be used similar to ordinary cash but allows a higher flexibility. The cash is issued by a central service and you can think of it as digital coins which hold a certain denomination. Each coin is basically a long string like 6A09E317F4BCD... which can be stored locally and transferred between customers in a peer-to-peer manner. Actually the central issuer doesn't perform the transaction itself but is only involved to ensure the validity of the coins and to prevent fraud by double-spending a coin. This approach is based on David Chaum's blind signature algorithm in which cryptography protects the coins and ensures the anonymity of the customers. This algorithm was invented 1983 and since then has been analyzed exhaustively by cryptographers and proven to be secure. Since our central issuer service is not directly involved in the transfers, coins can be exchanged peer-to-peer via social networks, e-mail, chat, web portals, and even via portable USB drives.

"In cryptography a blind signature as introduced by David Chaum is a form of digital signature in which the content of a message is disguised (blinded) before it is signed. The resulting blind signature can be publicly verified against the original, unblinded message in the manner of a regular digital signature. [...] An often used analogy to the cryptographic blind signature is the physical act of enclosing a message in a special 'write through' capable envelope, which is then sealed and
signed by a signing agent. Thus, the signer does not view the message content, but a third party can later verify the signature and know that the signature is valid within the limitations of the underlying signature scheme." (Wikipedia) 
1. The sender Alice retrieves coins from the issuer in a way that the issuer can verify its validity but doesn't know which particular coin (serial number) Alice has.
2. Alice sends the coin to the receiver Bob.
3. To verify the validity of the received coin, Bob sends the coin to the issuer.
4. The issuer verifies the coin and because of the blind signature algorithm, is able to say whether a particular coin is valid or not but doesn't know whom the coin was issued to in the first place. If the coin is valid the issuer mints a new coin for Bob in exchange for the received one and sends it back. The issuer stores the received coin in the Double Spending Database to prevent its later reuse. In general this mechanism prevents fraud by double spending money.
5. Bob receives the new coin from the issuer and marks the transfer as complete and verified. It is important to understand that technically steps 3 and 4 are a coin
exchange but on the user interface level these steps are simply the coin verification and are only raised to Bob's attention in the error case. Even though they may sound complicated, in the usual case these steps are hidden from the user Bob at all.


Installation

Requirements:
	- Java JDK6 or JDK7.
	- Scala 2.9.1 or lastest.
	- MySQL server (for server side)

- Install Java JDK
	$ sudo apt-get install openjdk-6-jdk openjdk-6-jre

- Install scala 2.9.1 (use for Ubuntu 12.04 or you can see at http://www.scala-lang.org/downloads)
	$ sudo apt-get install scala

- Install MySQL server
	You can check out http://www.howtoforge.com/installing-apache2-with-php5-and-mysql-support-on-ubuntu-9.10-lamp



DEMO Setup Client side

Use CURL as a client.
To use run client side demo you have to see server side before.
To used CURL as client side to communicate between client and server, first of all, we need install it on your computer. You can use the follow syntax:
sudo apt-get install curl
After install completed, we can start use curl to send request to server. In this demo, we used 192.168.1.149 as IP address, and 1000 is a port.
In terminal, we type a follow command:
curl http://192.168.1.149:1000/”Hello_server”
The word “Hello_server” is a simple string that I used to send to server as a request. We can use any string with any format such as JSON, XML,.... to change “Hello_server” word. Here, I received response from server, the word “Hello_client” is a response.

The following example, is in JSON format in request string to send to server.
In this, JSON string as {“id”:1,”name”:”tuan”}

Use scala to create client.
First, you can download source code of project here. (we will post into github later.)
This demo run in command-line. To run it, you have to use cd command in terminal to go to Client directory and run follow command:
bash sbt
Wait a moment and use run command to run it.
And wait for run. When completed, you have to input some values. Special IP address you have to see from server screen to input IP address.

After server response the request, client screen similar the follow picture:


String response is a JSON string, such as:
“say”:”Hello”,”number”:10000,”account”:995500
Client will parse and show in screen as in this demo. 
