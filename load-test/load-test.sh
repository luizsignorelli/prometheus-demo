# order-request.json contains the json you want to post
# -p means to POST it
# -H adds an Auth header (could be Basic or Token)
# -T sets the Content-Type
# -c is concurrent clients
# -t is the time ab will keep requesting
# -n is the number of requests to run in the test

ab -p order-request.json -T application/json -c 1 -n 100000000 http://127.0.0.1/orders
