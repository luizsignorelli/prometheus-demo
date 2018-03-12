# order-request.json contains the json you want to post
# -p means to POST it
# -H adds an Auth header (could be Basic or Token)
# -T sets the Content-Type
# -c is concurrent clients
# -t is the time ab will keep requesting
# -n is the number of requests to run in the test

ab -p order-request.json -T application/json -c 1 -t $1 -n 100000000 http://localhost:8080/orders
