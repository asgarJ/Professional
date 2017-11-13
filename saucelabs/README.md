### Health Monitoring
-----------------------------

The important point in this task is how the information about health status is summarised and presented to the user.
I have taken a time window approach that I currently take a period of 2 seconds (configurable), and summarise the health status info for that period. Not that I check the service status once within that period, but I do several (8) attempts to check it and count the number of times *"Magnificent!"* received. Classification is as follows:

Successful attempts | Label
--- | ---
 8 | Excellent
 6-7 | Good
 4-5 | Average
 2-3 | Bad
 1-0 | Too Bad
 For connection problems shown. | No signal

Regarding the implementation details, most of it implemented within `HealthChecker.java` class which has all dependent information injected via constructor. All necessary information for this service to run, i.e. host, port, logging details, time periods, number of attempts, are read from config files. 
