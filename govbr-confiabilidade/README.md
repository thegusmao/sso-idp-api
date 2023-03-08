GOVBR Integration Services 
====================================

GOVBR Integration​ : is the component that will act as an access layer to the Services provided by Gov.br authentitication

## Deployment

This project can be deployed using two methods:

* Standalone Spring-Boot container
* On an Openshift environment using Fuse Integration Services 2.0

## Standalone Spring Boot Container

The standalone method takes advantage of the [Camel Spring Boot Plugin](http://camel.apache.org/spring-boot.html) to build and run the microservice.

Execute the following command from the root project directory:

```
mvn spring-boot:run 
```

Once the spring boot service has started, the routes will listenning the RHAMQ broker

## Openshift Deployment

First, create a new OpenShift project called *govbr-confiabilidade*

```

```

Create Openshift pipeline using xml file 

* The webhook git repo will start pipeline and:
* Compiles and packages the Java artifact
* Creates the OpenShift API objects
* Starts a Source to Image (S2I) binary build using the previously packaged artifact
* Deploys the application


