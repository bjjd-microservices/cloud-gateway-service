FROM openjdk:11
COPY target/cloud-gateway-service-*.jar cloud-gateway-service-0.0.1-RELEASE.jar
EXPOSE 8181
CMD ["java","-jar","/cloud-gateway-service-0.0.1-RELEASE.jar"]