RSM-Server
==========

Really Simple Multiplayer Server

**Whats RSM?**

RSM stands for Really Simple Multiplayer [Library], a library for built Ludum Dare games. RSM's a Java based library that when finished will allow anyone to quickly and easily add multiplayer support to a Java game. RSM's not meant to be the be all and end all of multiplayer libraries and in fact will support only barebones feautres. However, RSM will be super light weight, easy to use and deployable in minutes, not hours (crucial for Ludum Dare).

**Documentation**

You can find some basic documentation for the ~~current~~ (now outdated) build here: https://dl.dropboxusercontent.com/u/53944475/RSMClient.html

**Server Setup**

- Grab the source for RSM-Server
- Compile the source into a jar (server.jar)
- Make a server.props file (sort of working), example here:
- Run genkeys.sh and copy the clienttruststore file to the location server.jar resides
- Start up server.jar

**Project Status**

RSM is far from completed and has only the base system setup. It should be ready for the upcoming Ludum Dare in December for any brave souls who'd like to try it.

- [x] SSL
- [ ] More efficient client search algorithim
- [x] Better server responce handeling
- [x] Basic server properties file (server.props)
- [ ] Better server properties (not just java strings)
- [ ] Server properties error handeling
- [x] Client side game creation
- [x] Clients can safely disconnect
- [x] Keystore generation script