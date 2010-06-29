 Name				: java-chatroom
 Author				: Hakki Caner KIRMIZI, #b96902133
 Date				: 20100629
 Description		: IRC-like chatroom project
 Environment		: Ubuntu 10.04 (lucid lynx), Kernel Linux 2.6.32-22-generic
 Java IDE			: NetBeans IDE 6.8 (Build 100326-unknown-revn)
 Javac				: javac 1.6.0_20
 Integration Tests	: Same with development environment
 Version Control	: svn, version 1.6.6 (r40053)
 Project Hosting	: https://code.google.com/p/java-chatroom/
 Licence			: GNU General Public License v3
 
 
 Build/Compile
 -------------
 $ make
 
 Run server
 ----------
 $ sudo java Server // (under bin/)
 
 Run client
 ----------
 $ java Client // (under bin/)
 
 
 
 Functions Implemented
 =====================
 
 1) Username validity check
    - No space allowed in username
    - Already-taken username cannot be taken again by another client
    
 2) Private messaging in a channel
    - so called $ /tell [username] command
   
 3) Ignore/Unignore user message
    - so called $ /ignore [username]  &  $ /unignore [username] commands
   
 4) Listing currently online users
    - Listing all users in the room (so called $ /listall command)
    - List all users in a specific channel (so called $ /list [channelname] command
    
 5) Logout
 	- so called $ /quit command
 	- Abnormal disconnect is also tolerated
 	
 6) Multi-channel Support (so called $ /join [channelname] command) 
 	- Users can create channel if it does not exist
 	- Users can join channel if it does exist
 	- If a channel does not have user, it will be destroyed
 	
 7) Server side debug messages
 	- A user join the chatroom system	: [user] login from [IP] @ [time]
	- A user leave the chatroom system	: [user] logout  @ [time]
	- A channel is created				: channel [channel] is created by [user] @ [time]
	- A channel is destroyed			: channel [channel] is destroyed by [user] @ [time]

