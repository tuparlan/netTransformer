#!/usr/bin/env bash

projectOperation=$1;
projectName=$2;
projectType=$3;
baseDir=$4;
pathToNetTransformer=$5;

echo "project operation $projectOperation";

echo "projectName $projectName";

echo "projectType $projectType";

echo "baseDir $baseDir";

echo "pathToNetTransformer $pathToNetTransformer";


create(){
    java -jar ${pathToNetTransformer}/netTransformer.jar --create=y --name=${projectName} --type=${projectType} --baseDir=${baseDir}
}


discover(){

       java -jar ${pathToNetTransformer}/netTransformer.jar --discover=y --name=${projectName} --baseDir=${baseDir}

}

delete(){

       java -jar ${pathToNetTransformer}/netTransformer.jar --delete=y --name=${projectName} --baseDir=${baseDir}

}



if [[ "${projectOperation}" == "create" ]] ; then
    echo "Creating a new Project";
    create
fi

if [[ "${projectOperation}" == "discover" ]] ; then
    echo "Discovering the network";
    discover
fi

if [[ "${projectOperation}" == "delete" ]] ; then
    echo "Deleting the project with $projectName";
    delete
fi



