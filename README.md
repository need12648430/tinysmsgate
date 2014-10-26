<div style="text-align:center">
  ![Ironically large logo.](http://i.imgur.com/1mVX0Qg.png?1)
</div>

# Tiny SMS Gate
Tiny SMS Gate is a tiny (GPL 2 License) SMS gateway for Android. It allows you to send and receive SMS messages through your phone, over regular ol' HTTP. Nothing more.

As such, I have to ask that you please, please don't use this outside of a trusted LAN. If not for your sake, for mine.

Full disclosure: This project is running [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd) in the background. It's pretty great, I'd highly recommend it.

# Screenshots
![Main Screen](http://i.imgur.com/yF1bI8d.png) ![Preferences](http://i.imgur.com/VthlOnU.png) ![Running](http://i.imgur.com/VlYRxeP.png)

# Okay, so what do I do with it?
There are some code samples [here](https://gist.github.com/need12648430/205c8288693ead748fed). They're written in PHP.

Here's the skinny though:
1. Open Tiny SMS Gate
2. Set up your Preferences
3. Start the internal server

Then, from another server, e.g. Apache or something written in node.js
1. Set up a page to receive SMS data
2. Write some functions to send SMS data to Tiny SMS Gate
3. Use them

# Why would I want to do that?
I just wanted to text from my PC without having to go through some third party. Others have used similar apps (none of which worked for me, either broken or sketchy) to set up small SMS services.

Sky's the limit, I guess.
