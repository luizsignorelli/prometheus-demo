#!/bin/bash

curl --request PUT --data 'error.rate=3' http://localhost:8500/v1/kv/config/application/data
