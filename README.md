# TristanChatApp

Add ip address to below code:
InetAddress localhostS = InetAddress.getByName("192.xxx.xxx.xxx");

First run the Server and then run the LoginFrame. Enter Username and Password and then press either the "Create Account" or "Login" buttons.

The combobox at the top of the Gui list all the accounts stored on the database. Upon logging in all previous messages stored on the database will be automatically printed out. The first time a client logs in it might produce duplicate messages, but just select a name on the combobox at the top to get rid of them.

If you select someone you had not spoken with before, there will be lingering messages from your conversation with a different user. Just send a message to them to clear up the display.

This program saves any sent messages to both online and offline users to the Server's Mongo database and upon logging in all previous messages are automatically printed out for that user to view, per chat.

# Project Development
This project was created using Apache Netbeans 12.3, Windows 10 and MongoDB Enterprise Edition.

# Prerequisites
1. Java JDK on the device that will be acting as the server as well as the client device that will be used
2. IP Address of the device that will be acting as the server

# Running the client program (LoginFrame.java) for the first time on any client device:
1. Go to the LoginFrame.java file in the clientPackage subpackage
2. Open the LoginFrame.java file in any compatible IDE
3. Edit the ipAddress variable to contain the server device ip address
