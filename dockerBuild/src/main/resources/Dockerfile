FROM tomcat:8.0-jre8-alpine
MAINTAINER iTransformers Labs Ltd

ENV projectPath  /Projects/TheNetworkProject

RUN mkdir -p ${projectPath}
RUN export PATH=$PATH:/usr/local/netTransformer
ADD netTransformerBundle.jar  /usr/local/netTransformer/netTransformer.jar
RUN java -jar /usr/local/netTransformer/netTransformer.jar -n=y -o=y -p=${projectPath}
ADD context.xml        /usr/local/tomcat/conf
ADD setenv.sh          /usr/local/tomcat/bin
ADD appenv.sh          /usr/local/tomcat/bin
ADD wsitransformer.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]
