#!/usr/bin/env bash


projectName=$1;
baseDir=$2;
version=$3;

checkUndirectedGraphml(){

    cd $baseDir/$projectName/$version/graphml-undirected/

    if [[ $? -eq 0 ]] ; then
       echo "Entering $version folder !!!";
    else
       echo "Error $baseDir/$projectName/$version/graphml-undirected/ does not exist!!!";
       exit 1;
    fi
    if test -f "network.graphml"; then echo "network.graphml exists";
    else
       echo "network.graphml does not exist!!!";
       exit 1;
    fi

    checkGraphml
}

checkDirectedGraphml(){
     cd $baseDir/$projectName/$version/graphml-directed/

    if [[ $? -eq 0 ]] ; then
       echo "Entering $version folder !!!";
    else
       echo "Error $baseDir/$projectName/$version/graphml-undirected/ does not exist!!!";
       exit 1;
    fi

     checkGraphml
}

checkGraphml(){
    pwd;
    keyNum=`cat network.graphml | xpath 'count(/graphml//key)' 2>/dev/null`;

    if [ -z "$keyNum" ]; then
       echo "Number of keys: $keyNum is empty!";
        exit 1;
    fi

    graphNum=`cat network.graphml | xpath 'count(/graphml//graph)' 2>/dev/null`;
    if [ -z "$graphNum" ]; then
       echo "There is no graphs in graphml!";
        exit 1;
    fi

    nodeNum=`cat network.graphml | xpath 'count(/graphml//node)' 2>/dev/null`;
    if [ -z "$nodeNum" ]; then
       echo "There is no nodes in graphml!";
        exit 1;
    fi
    edgeNum=`cat network.graphml | xpath 'count(/graphml//edge)' 2>/dev/null`;
    if [ -z "$nodeNum" ]; then
       echo "There is no edges in graphml!";
        exit 1;
    fi
    echo "Number of graphs: $graphNum";
    echo "Number of nodes: $nodeNum";
    echo "Number of edges: $edgeNum";

}

checkUndirectedGraphml

