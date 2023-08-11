# EcomApp

# Ecom request limiter application 

### Spring boot project on java with gradle 

The main purpose of the application is to set the limit for the request 
amount from the remote hosts. 

Request, from some ip, knocks the controller, which check the amount of the requests from this ip. 
If the amount is exceeded, predefined configuration, then we return 502 ("bad_request) otherwise we return some imaginary response. 

We have two configurations (resources/application.properties): 
 - the amount fo the request during some time (for example, minute)
 - the time itself (currently we use minute). 

We use in memory DB, which based on ConcurrentHashMap<T>. 

We have test class for the controller. 

Also, we have dockerized spring boot application. 
