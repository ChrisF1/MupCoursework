MupCoursework
=============

Submission for the Mobile and Ubiquitous Computing Coursework.

I've included both my project and the google play services project which you have to reference as a library, not sure if they're project specific or one will fit all.

Application runs on one screen with 5 overlays on top of the map. From top left (1st row):

Left (1st Row): Spinner to change terrain type

Middle (1st Row): Spinner to change map to position of a previous host city (whatever one you click).

Right (1st Row): GPS button, if GPS is enabled (or sent in the emulator) then it will place a marker wherever the GPS location is.

Left (2nd Row): Edit Text box to enter your location (Country, City, Postcode...)

Middle (2nd Row): Find button, clicked once you have entered a location and will drop a pin on wherever you have entered.

Right (2nd Row): Distance button, clicked once you have entered a location and a pin has dropped on the map. Will draw a red line from wherever your pin has dropped to Glasgow and will calcuate and display distance in km.





Current Issues: 

GPS can sometimes not work (Might be an emulator issue) even when location controls are sent to the device. 

Multiple location and distances can keep drawing multiple red lines to Glasgow even though a new location should delete the old red line. This happens sometimes and not all the time.



Fully commented code, comments where methods aren't mine and where the code was obtained/adapted from.
