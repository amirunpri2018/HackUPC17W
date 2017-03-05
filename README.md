## HACKUPC WINTER 2017

# INSPIRATION
Traffic accidents are frequent every day and although it would be over-optimistic pretending to achieve zero victims, we can do our best to decrease human loses. We’ve decided to focus on motorbikes, but the concept is extensible to other types or vehicles. On the other hand, in special situations, emergency services need to know the amount of people in danger within a certain zone, and if they need urgent assistance.

# WHAT IT DOES
We’ve built the device that will look after your life, alerting emergency services in case of accident and transmitting your exact location to assure the medical assistance can know where to go, even if you cannot tell them.

First, there’s a jacket with a strip of LEDs attached to it, to shine in case you have an accident and make easier to find you. This jacket turns on when you fall accidentally from the bike, and notifies the Arduino 101 that you probably have had a drop.

In this moment, the Arduino system enters in Emergency Mode, and sends via Low Energy Bluetooth a signal to the Android smartphone to notify the possible accident. Then, if in within 10 the Android alarm has not been dismissed, an emergency protocol is started, calling a particular phone number that can be preset, or doing whatever the user has chosen to do in this case.

# HOW WE BUILT IT
Starting with a basic idea, we’ve been developing and improving the initial concept until reaching a functional prototype.

There are four of us in our team and everyone has focused on one essential part of the project. But when someone has been stuck, the other have helped to solve the problem.

ACCOMPLISHMENTS THAT WE'RE PROUD OF
In less than 36 hours, we've designed and implemented a functional device, that also is user-friendly and simply in appearance.

# WHAT WE LEARNED
We’ve learnt Low Energy Bluetooth protocols, as well as how to implement them to communicate Android devices with Arduino boards.

# WHAT'S NEXT FOR MotoCare
Extend the mechanism to other vehicles, improve of the sensors in case of accident, pattern analysis to prevent risks on the road…

# SPECIAL ACKNOWLEDGEMENTS
Xavi Algarra for the pieces of advice and ideas, the 4 Hardware guys from the A5202 for the tools, explanations and recommendations and the guy who lend us the Arduino 101.
