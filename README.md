netTransformer
==============
Welcome!

netTransformer is a software prototype able to:
*	Discover networks through various pluggable discoverers (SNMP, JSON, MRT)
*	Capture their state in a graph data model
*	Provide ability to engineers to review and reason about L2, L3, OSPF, ISIS and BGP network topology
*	Automate device configuration process through simplified template interface
*	Track the network evolution process and create network DIFFs between any two network states

If that sounds interesting and you want to find out more please review the [UserGuide](http://www.itransformers.net/UserGuide-Pirin.pdf) or visit our [youtube channel] (https://www.youtube.com/channel/UCVrXTSM9Hj6d3OFbIdF4Z2w). 

[![Build Status](http://build.itransformers.net:8080/buildStatus/icon?job=netTransformer-jobs/netTransformerPlumberPipe)](http://build.itransformers.net:8080/job/netTransformer-jobs/job/netTransformerPlumberPipe/) 
==============


Q&A and Issue tracker
==============

[Community forum](http://forum.itransformers.net/fluxbb/index.php) 
```
http://forum.itransformers.net/fluxbb

```

[Issue tracker](https://github.com/iTransformers/netTransformer/issues)

```
https://github.com/iTransformers/netTransformer/issues

```

Getting Started for Developers
==============

### Install java sdk 1.8 or newer. 
Preferably you should use [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). 
However with OpenJDK you should also be fine. If not please report an issue to our issue tracker. 

### Get Maven
netTransformer is using maven as a build and dependency management tool. So you should install it. Instructions  [here](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)


### Ensure that you have git 
Install [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git). Then clone netTransformer source like this. 
```
git clone https://github.com/iTransformers/netTransformer.git
```

### Build and package from the command line
Build and package netTransformer 
```
cd netTransformer
mvn package
```

### Navigate to your build
If the process finish successfully navigate to 
```
cd netTransformer/distribution/target/netTransformer-bin/netTransformer/bin
```
Then run it
```
**!On Windows**\
cd bin

netTransformer.bat

**! On Linux/Unix**\
cd bin\
./netTransfomrer.sh

