#!/usr/bin/env bash


projectName=$1;
baseDir=$2;
version=$3;

checkUndirectedGraphml(){

    cd $baseDir/$projectName/$version/graphml-undirected/
    checkGraphml
}

checkUndirectedGraph(){
     cd $baseDir/$projectName/$version/graphml-directed/
     checkGraphml
}

checkGraphml(){
    keyNum=`cat network.graphml | xpath 'count(/graphml//key)' 2>/dev/null`;

    graphNum=`cat network.graphml | xpath 'count(/graphml//graph)' 2>/dev/null`;

    nodeNum=`cat network.graphml | xpath 'count(/graphml//node)' 2>/dev/null`;
    edgeNum=`cat network.graphml | xpath 'count(/graphml//edge)' 2>/dev/null`;

    echo "Number of keys: $keyNum";
    echo "Number of graphs: $graphNum";
    echo "Number of nodes: $nodeNum";
    echo "Number of edges: $edgeNum";

}

checkUndirectedGraph

