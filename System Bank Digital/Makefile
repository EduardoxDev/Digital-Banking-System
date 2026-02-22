.PHONY: build run test docker-up docker-down clean benchmark

# Variables
DOCKER_COMPOSE = docker-compose

build:
	mvn clean package -DskipTests

run:
	mvn clean spring-boot:run

test:
	mvn clean test

docker-up:
	$(DOCKER_COMPOSE) up -d

docker-down:
	$(DOCKER_COMPOSE) down

clean:
	mvn clean
	$(DOCKER_COMPOSE) down -v

benchmark:
	@echo "Running benchmarks..."
	# Placeholder for benchmark command, e.g., wrk or a custom script
	curl -s http://localhost:8080/actuator/health | grep UP
