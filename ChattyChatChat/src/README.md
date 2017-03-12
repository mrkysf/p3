#Server Files
* ChattyChatChatServer.java
* ChatServer.java
* ClientListener.java

#Client Files
* ChattyChatChatClient.java
* ChatClient.java
* ServerListener.java
* ChatCommands.java

#Shared Files (Server and Client)
* ICommand.java
* ServerMessageCommand.java
* ClientMessageCommand.java
* ServerNameChangeCommand.java
* MissingArgumentException.java
* ServiceDataProvider.java
* StringHelper.java

#Coding Patterns
* The name of every command that runs on the server is prefixed with 'Server', i.e. ServerMessageCommand
* The name of every command that runs on the client is prefixed with 'Client'
* All commands transmitted between Client and Server must implement ICommand interface
* ServiceDataProvider should be used to encapsulate any data that needs to be passed
  to the listener threads and/or commands.
* The server implementation should be only concerned with routing messages to the right clients.
* The client implementation should interpret user input, send the right command to the server, and 
  handle server commands.

#Instructions to build project
1. Download ChattyChatChat project and open it in eclipse
2. Select **Project -> Build** to build the project

#Instructions to run the server
1. Open CMD and change directory to ChattyChatChat\bin
2. Run the following command to start the server:
   **java ChattyChatChatServer 9001**

#Instructions to run the client
1. Open CMD and change directory to ChattyChatChat\bin
2. Run the following command to start the client:
   **java ChattyChatChatClient localhost 9001**
```html
```
