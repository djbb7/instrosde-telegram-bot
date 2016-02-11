# HealthProfile Telegram Bot

This telegram bot is part of the second assignment of Service Design and Engineering, taught at the University of Trento, First Semester 2015-2016. It is a proof of concept that allows to communicate to a remote web service via Telegram, to save personal health measurements (e.g. height, weight, blood pressure) and to retrieve the measurement history.

Interesting concepts applied in this project are viewing the Bot as a state machine, and the incoming messages from telegram allow one to transition from one state to another. In a given state, only certain messages are expected and allowed. This allowed also to solve the issue caused by telegram sending repeated messages.

Another interesting aspect is that when the bot receives a request, it immediately replies 200 OK. In this way, the user gets the 2 delivered checkmarks that indicate that the message has been delivered. In the background, the bot spawns a worker which handles the request and POSTs the message to the user when it is done communicating with other web services.

Feel free to use this project as a base of how to interact with telegram in Java. It is ready to be deployed to heroku, only your access token must be set. This is done by setting the bot_access_token environment variable in Heroku.

![Screenshot](https://raw.githubusercontent.com/djbb7/instrosde-telegram-bot/master/screenshot.png)

## About the Project

The scope of the project was building and deploying a REST web service to maintain a series of personal health measurements (e.g. height, weight, blood pressure). The web service had to work with both XML and JSON content requests and responses, and these had to comply with a structure given to us in the problem statement.

Although we had to build a client as well as a server. Our client had to communicate with another student's server. And viceversa.

Please refer to my implementations, as well as the student I worked with:

My client and server: https://github.com/djbb7/introsde-2015-assignment-2

His server: https://github.com/federico-fiorini/introsde-2015-assignment-2
