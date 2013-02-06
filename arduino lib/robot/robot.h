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
#ifndef ROBOT_H
#define ROBOT_H

#include "Arduino.h"
#undef BYTE //to fix compiler errors 

enum {
    ROBOT_EVENT_CMD           	= 0x00, // Commands  
    ROBOT_EVENT_CMD_START				= 0x01,
    ROBOT_EVENT_CMD_STOP				= 0x02,
    ROBOT_EVENT_CMD_REBOOT			= 0x03,
    ROBOT_EVENT_CMD_SHUTDOWN		= 0x04,
    ROBOT_EVENT_CMD_FAILSAFE		= 0x05,
    ROBOT_EVENT_CMD_HEARTBEAT		= 0x06,
    
    ROBOT_EVENT_STATUS          = 0x10, // Status information
    
    ROBOT_EVENT_JOY_AXIS        = 0x20, // Joystick movements
    ROBOT_EVENT_JOY_BUTTON      = 0x21, // Button presses
    ROBOT_EVENT_JOY_HAT					= 0x22, // D-pad pressed
    ROBOT_EVENT_JOY_STATUS			= 0x23, // Joystick status
    
    ROBOT_EVENT_KEYBOARD				= 0x30, // Keyboard Events
    
    ROBOT_EVENT_DISPLAY					= 0x40, // Display info events
    ROBOT_EVENT_TIMER           = 0x50, // Timer events
    ROBOT_EVENT_MOTOR           = 0x60, // Motor events
    ROBOT_EVENT_SOLENOID				= 0x70, // Solenoid events for pneumatics and relays
    ROBOT_EVENT_POSE						= 0x80, // Pose events for states
    ROBOT_EVENT_ADC             = 0x90, // ADC events
    ROBOT_EVENT_VARIABLE        = 0xA0, // Variable events
    ROBOT_EVENT_IMU		          = 0xB0, // IMU events
		ROBOT_EVENT_ENCODER					= 0xC0, // Encoder events
    ROBOT_EVENT_EEPROM					= 0xD0, // Send data to be stored in eeprom
    ROBOT_EVENT_IO							= 0xE0, // Send generic IO events
    UNKNOWN_EVENT								= 0xFF // Unknown Event
	};
		
	enum{
		TIMER_P1HZ 		= 0x00,
		TIMER_1HZ			= 0x01,
		TIMER_2HZ			= 0x02,
		TIMER_5HZ			= 0x03,
		TIMER_10HZ		= 0x04,
		TIMER_20HZ		= 0x05,
		TIMER_25HZ		= 0x06,
		TIMER_50HZ		= 0x07,
		TIMER_100HZ		= 0x08
	};
	
	enum{
		TIMER_P1HZ_MASK 	= 0x01,
		TIMER_1HZ_MASK 		= 0x02,
		TIMER_2HZ_MASK 		= 0x04,
		TIMER_5HZ_MASK 		= 0x08,
		TIMER_10HZ_MASK 	= 0x10,
		TIMER_20HZ_MASK 	= 0x20,
		TIMER_25HZ_MASK 	= 0x40,
		TIMER_50HZ_MASK 	= 0x80,
		TIMER_100HZ_MASK 	= 0x100
	};
	
	enum{
		BYTE							= 0x01,
		INTEGER						= 0X00,
		LONG							= 0X02,
		FLOAT							= 0X03,
		STRING						= 0X04,
		UNKOWN_TYPE				= 0x05
	};


	typedef struct {
		unsigned char command;
		unsigned char index; 
		union{
			unsigned char b;
			unsigned int i; 
			unsigned long l;
			float f;
			char s[33];
		};
		int type;
	} robot_event;

class Robot{

public:
	
	enum{
		ROSLUND = 0x00,
		FENRIR  = 0x01,
		GOLIATH = 0x02,
		REAPER  = 0x03,
		PENGUIN = 0x04,
		MONGOL  = 0x05,
		GHOST   = 0x06,
		UNKNOWN_ROBOT = 0xFF
	};
	
	Robot(HardwareSerial &serial, long baud, int timer, int robot);
	//Robot(usb_serial_calss &serial, long baud);
	void update();
	bool getEvent(robot_event *ev);
	void sendEvent(robot_event *ev);

	private:
	const static int QUEUE_SIZE = 10;
	const static int BUFFER_SIZE = 64;
	typedef struct {
		robot_event array[QUEUE_SIZE];
		int head_index;
		int tail_index;
		int length;
	} robot_queue;
	
	enum{
		LOOKING_FOR_HEADER,
		READING_DATA,
		CALCULATE_CHECKSUM
	};
	
	const static char MESSAGE_HEADER = 'U';
	const static char MESSAGE_FOOTER = '\n';
	const static char MESSAGE_CHECKSUM = '*';
	
	HardwareSerial* Comm;
	robot_queue queue;
	int robot_name;
	
	void readSerial();
	void timerCheck();
	void checkHeartBeat();
	void enqueue(robot_event *ev);
	bool dequeue(robot_event *ev);
	void incTail();
	void incHead();
	unsigned long xtoi(const char *xs);
	int itox(unsigned long value, char *buf);
	
	char heartbeat;
	
	char buf[BUFFER_SIZE];
	int length;
	int state;
	
	unsigned long  last_sent_P1hz;
	unsigned long  last_sent_1hz;
	unsigned long  last_sent_2hz;
	unsigned long  last_sent_5hz;
  unsigned long  last_sent_10hz;
	unsigned long  last_sent_20hz;
  unsigned long  last_sent_25hz;
  unsigned long  last_sent_50hz;
  unsigned long  last_sent_100hz;
	
	char timerP1hz;
	char timer1hz;
	char timer2hz;
	char timer5hz;
	char timer10hz;
	char timer20hz;
	char timer25hz;
	char timer50hz;
	char timer100hz;
	
};
#endif