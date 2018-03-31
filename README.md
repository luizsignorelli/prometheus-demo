# Prometheus Demo App
This is a sample application to demonstrate the use of Prometheus + Grafana to visualize metrics of a simple 
spring-boot application. It also uses Consul as the service discovery strategy in Prometheus.

## Dependencies
* Docker and Docker Compose
* Java and Maven is necessary to build the app image

## Running

```
$ ./build-docker-image.sh
$ docker-compose up
```

You can scale the application:
```
$ docker-compose up --scale app=2
```

A load balancer is included on `docker-compose.yml` that will pick up app instances automatically, so that you can just 
hit `localhost:80`.

To access the services present on `docker-compose.yml`:
* Consul: `http://localhost:8500`
* Prometheus: `http://localhost:9090`
* Grafana: `http://localhost:3000`