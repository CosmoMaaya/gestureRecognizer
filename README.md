Yusu Zhao \
20761282 y555zhao \
openjdk version "11.0.10" 2021-01-19 \
Windows10 (Lenovo Y7000P-1060)

## specification

------
- ###Addition

  - Users can directly draw a stroke on the screen. Once they've drawn one, they cannot make another stroke until
  they cleared the screen by clicking the "clear" button. Switching to another tab (recognition or library) will not
  erase the current stroke.
  - The stroke drawn must be longer than 300. This is manually set value, only to make sure that the gesture drawn makes
  sense. If the stroke is too short, a toast msg will pop up telling user to draw a longer stroke.
  - Only when a stroke is drawn will the two buttons be enabled. By clicking add button, a dialog window will pop up to let
  user input the name of the gesture. If nothing is entered in the input box, a default name "My great gesture" will be 
    used. We will not check if the name has been used before.


- ###Recognition
  
  - To recognize a gesture, user only needs to switch to "Recognition" tab. Users can draw a stroke in the area, and the 
  best three matched will be shown below after users finish drawing (i.e lifting the finger), with a number to indicates 
    the priority.
    
  - Different from addition, user do not need to manually clear the screen. Next stroke will automatically overwrite the
  current stroke. (Clicking the screen will not generate a new stroke, thus it will clear up everything)
    
  - Switching to a different tab will clear any stroke or result in recognition. 

- ###Library
  
  - Stored gestures are listed here.
  - Edit button will allow user to update a existing gesture, delete button will delete that one.
  - Editing a gesture is pretty similar to adding a gesture, except that if user not entering a gesture name, the name remains
    unchanged.
    
  - If user doesn't want to make any change, he could either use the back button to exit the activity, or click update and ok(so 
    essentially update again but with exactly the same value)

##ENJOY CUBIC CURVE DRAWING AND HAVE A GOOD DAY!