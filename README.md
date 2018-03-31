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

To check the application ports:
```
$ docker-compose ps

           Name                          Command               State                                              Ports
-------------------------------------------------------------------------------------------------------------------------------------------------------------------
prometheusdemo_app_1          /bin/sh -c exec java -Xms$ ...   Up      0.0.0.0:32774->8080/tcp
prometheusdemo_app_2          /bin/sh -c exec java -Xms$ ...   Up      0.0.0.0:32775->8080/tcp
prometheusdemo_consul_1       docker-entrypoint.sh agent ...   Up      8300/tcp, 8301/tcp, 8301/udp, 8302/tcp, 8302/udp, 0.0.0.0:8500->8500/tcp, 8600/tcp, 8600/udp
prometheusdemo_grafana_1      /run.sh                          Up      0.0.0.0:3000->3000/tcp
prometheusdemo_prometheus_1   /bin/prometheus --config.f ...   Up      0.0.0.0:9090->9090/tcp
```

Here we can see that the application is accessible through ports 32774 and 32775.

* Consul: `http://localhost:8500`
* Prometheus: `http://localhost:9090`
* Grafana: `http://localhost:3000`