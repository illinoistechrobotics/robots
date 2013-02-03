/*
Copyright 2013 (c) Illinois Tech Robotics <robotics.iit@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package common;

import java.util.*;

public enum EventEnum {
    ROBOT_EVENT_CMD                 (0x00), // Commands  
    ROBOT_EVENT_CMD_START			(0x01),
    ROBOT_EVENT_CMD_STOP			(0x02),
    ROBOT_EVENT_CMD_REBOOT			(0x03),
    ROBOT_EVENT_CMD_SHUTDOWN		(0x04),
    ROBOT_EVENT_CMD_FAILSAFE		(0x05),
    ROBOT_EVENT_CMD_HEARTBEAT		(0x06),
    
    ROBOT_EVENT_STATUS              (0x10), // Status information
    
    ROBOT_EVENT_JOY_AXIS            (0x20), // Joystick movements
    ROBOT_EVENT_JOY_BUTTON          (0x21), // Button presses
    ROBOT_EVENT_JOY_HAT				(0x22), // D-pad pressed
    ROBOT_EVENT_JOY_STATUS			(0x23), // Joystick status
    
    ROBOT_EVENT_KEYBOARD			(0x30), // Keyboard Events
    
    ROBOT_EVENT_DISPLAY				(0x40), // Display info events
    ROBOT_EVENT_TIMER               (0x50), // Timer events
    ROBOT_EVENT_MOTOR               (0x60), // Motor events
    ROBOT_EVENT_SOLENOID			(0x70), // Solenoid events for pneumatics and relays
    ROBOT_EVENT_POSE				(0x80), // Pose events for states
    ROBOT_EVENT_ADC                 (0x90), // ADC events
    ROBOT_EVENT_VARIABLE            (0xA0), // Variable events
    ROBOT_EVENT_IMU		            (0xB0), // IMU events
	ROBOT_EVENT_ENCODER				(0xC0), // Encoder events
    ROBOT_EVENT_EEPROM				(0xD0), // Send data to be stored in eeprom
    ROBOT_EVENT_IO					(0xE0), // Send generic IO events
    UNKNOWN_EVENT					(0xFF); // Unknown Event
    
    // Feel free to add more commands but set different values. Try to do it with the available commands first 
    // Don't remove events please 
    
    private int value;
    
    /**
     * This is for reverse lookup 
     * If you have a value it will return the Enum
     */
    private static final Map<Integer,EventEnum> lookup = new HashMap<Integer,EventEnum>();
    static {
    	for(EventEnum s : EnumSet.allOf(EventEnum.class))
         lookup.put(s.getValue(), s);
    }
    public static EventEnum getEvent(int value){
    	EventEnum temp = lookup.get(value);
    	if(temp == null){
    		return UNKNOWN_EVENT;
    	}
    	return temp;
    }
        
    private EventEnum(int v){
    	this.value = v;
    }
    
    public int getValue(){
    	return this.value;
    }
    
}
