FROM tomcat:8.0-jre8-alpine
MAINTAINER iTransformers Labs Ltd

RUN mkdir /usr/local/netTransformer;
RUN export PATH=$PATH:/usr/local/netTransformer
ADD netTransformerBundle-0.8.jar  /usr/local/netTransformer/netTransformer.jar
RUN chmod 775 /usr/local/netTransformer
#RUN java -jar /usr/local/netTransformer/netTransformer.jar -n=y -p=/usr/local/netTransformer/prj1
#ADD context.xml        /usr/local/tomcat/conf
ADD wsitransformer-0.8.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]
