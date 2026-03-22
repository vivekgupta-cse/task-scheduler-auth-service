#!/bin/bash -x
URL='http://localhost:8081'

curl ${URL}/actuator/health
curl -i ${URL}/actuator/health/liveness
curl -i ${URL}/actuator/health/readiness
curl -i ${URL}/actuator
curl -i ${URL}/actuator/prometheus