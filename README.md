This was the first assessment in a program I had done. It is a very simple proof-of-concept for a filesharing client and filesharing server.

The server backend was created in Java, and the front-end was created in Javascript. The front-end uses Node.JS and Vorpal.

To run the server
-
For WHATEVER reason, this is not running properly in Intellij - works perfect in eclipse. Will eventually find out the cause
* Import maven project
* in Main.java - update database information, possibly the port depending on your system

To run the client
-
*You need node.
* If you had to change the port in Main.java, change it in cli.js
* npm install
* npm run build && node ./dist/main.js

Client is self explanatory. You need to register, login, upload file, or download.

There are some limitations, however, after a certain size it just breaks. This is just a proof of concept anyways.
