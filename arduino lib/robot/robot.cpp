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
#include "robot.h"
Robot::Robot(HardwareSerial  &serial, long baud, int timer, int robot){
	Comm = &serial;
	Comm->begin(baud);
	queue.head_index = 0;
	queue.tail_index = 0;
	queue.length = 0;
	
	heartbeat = false;
	
	robot_name = robot;
	
	length = 0;
	state = LOOKING_FOR_HEADER;
	
	last_sent_P1hz = 0;
	last_sent_1hz = 0;
	last_sent_2hz = 0;
	last_sent_5hz = 0;
  last_sent_10hz = 0;
	last_sent_20hz = 0;
  last_sent_25hz = 0;
  last_sent_50hz = 0;
  last_sent_100hz = 0;
	
	timerP1hz = timer & TIMER_P1HZ_MASK;
	timer1hz = timer & TIMER_1HZ_MASK;
	timer2hz = timer & TIMER_2HZ_MASK;
	timer5hz = timer & TIMER_5HZ_MASK;
	timer10hz = timer & TIMER_10HZ_MASK;
	timer20hz = timer & TIMER_20HZ_MASK;
	timer25hz = timer & TIMER_25HZ_MASK;
	timer50hz = timer & TIMER_50HZ_MASK;
	timer100hz = timer & TIMER_100HZ_MASK;
	
}

void Robot::readSerial(){
	robot_event event;
	unsigned long lTemp;
	while(Comm->available()>0){
		buf[length++] = Comm->read();
		switch(state){
		case LOOKING_FOR_HEADER:
			if(buf[0] == MESSAGE_HEADER){
				state = READING_DATA;
			}
			else{
				Serial.println(buf[length-1]);
				length = 0; //reset buffer
			}	
			break;
		case READING_DATA:
			if(buf[length-1] == MESSAGE_HEADER || length >= BUFFER_SIZE){
				//restart because we found header
				buf[0] = MESSAGE_HEADER;
				length = 1;
			}
			if(buf[length-1] == MESSAGE_FOOTER){
				int pos = 1;
				int checksum = 0;
				while(buf[pos] != MESSAGE_CHECKSUM){
					checksum = checksum ^ buf[pos++];
				}
				char* cToken;
				cToken = strtok(buf, "U,*"); 
				
				event.command = (unsigned int)xtoi(cToken);
				cToken = strtok(NULL, "U,*");
				event.index = (unsigned char)xtoi(cToken);
				char* value = strtok(NULL, "U,*");
				cToken = strtok(NULL, "U,*");
				event.type = xtoi(cToken);
				switch(event.type){
				case BYTE:
					event.b = (unsigned char)xtoi(value);
					break;
				case INTEGER:
					event.i = (unsigned int)xtoi(value);
					break;
				case LONG:
					event.l = (unsigned long)xtoi(value);
					break;
				case FLOAT:
					 lTemp= xtoi(value); //can't dereferance a return value
					event.f = *(float *)&lTemp;
					break;
				case STRING:
				//break left out
				default:
					strcpy(event.s, value);
					break;
				}
				
				cToken = strtok(NULL, "U,*\n"	);
				int checksum2 = xtoi(cToken);
				if(checksum2 == checksum){
					if(event.command == ROBOT_EVENT_CMD_HEARTBEAT){
						heartbeat = 0;
					}
					else{
						enqueue(&event);
					}
				}
				length = 0;
				state = LOOKING_FOR_HEADER;
			}
			break;
		}
	}
}

void Robot::timerCheck(){
	robot_event ev;
	ev.command = ROBOT_EVENT_TIMER;
	ev.i = 0;
	ev.type = 0;
	
	if(timer100hz){
		if(millis() - last_sent_100hz >= 10){
			last_sent_100hz = millis();
			ev.index = TIMER_100HZ;
			enqueue(&ev);
		}
	}
	if(timer50hz){
		if(millis() - last_sent_50hz >= 20){
			last_sent_50hz = millis();
			ev.index = TIMER_50HZ;
			enqueue(&ev);
		}
	}
	if(timer25hz){
		if(millis() - last_sent_25hz >= 40){
			last_sent_25hz = millis();
			ev.index = TIMER_25HZ;
			enqueue(&ev);
		}
	}
	if(timer20hz){
		if(millis() - last_sent_20hz >= 50){
			last_sent_20hz = millis();
			ev.index = TIMER_20HZ;
			enqueue(&ev);
		}
	}
	//10 hz timer needed for heartbeat
	if(millis() - last_sent_10hz >= 100){
		last_sent_10hz = millis();
		if(timer10hz){
			ev.index = TIMER_10HZ;
			enqueue(&ev);
		}
		robot_event heartbeat;
		heartbeat.command = ROBOT_EVENT_CMD_HEARTBEAT;
		heartbeat.index = robot_name;
		heartbeat.i = 0;
		heartbeat.type = INTEGER;
		sendEvent(&heartbeat);
		checkHeartBeat();
	}
	if(timer5hz){
		if(millis() - last_sent_5hz >= 200){
			last_sent_5hz = millis();
			ev.index = TIMER_5HZ;
			enqueue(&ev);
		}
	}
	if(timer2hz){
		if(millis() - last_sent_2hz >= 500){
			last_sent_2hz = millis();
			ev.index = TIMER_2HZ;
			enqueue(&ev);
		}
	}
	if(timer1hz){
		if(millis() - last_sent_1hz >= 1000){
			last_sent_1hz = millis();
			ev.index = TIMER_1HZ;
			enqueue(&ev);
		}
	}
	if(timerP1hz){
		if(millis() - last_sent_P1hz >= 10000){
			last_sent_P1hz = millis();
			ev.index = TIMER_P1HZ;
			enqueue(&ev);
		}
	}
}

void Robot::update(){
	readSerial();
	timerCheck();
}

bool Robot::getEvent(robot_event *ev){
	return dequeue(ev);
}

void Robot::sendEvent(robot_event *ev){
	char buf[64];
	int i = 0;
	int checksum = 0;
	buf[i++] = 'U';
	i += itox(ev->command, &buf[i]);
	buf[i++] = ',';
	i += itox(ev->index, &buf[i]);
	buf[i++] = ',';
	switch(ev->type){
		case BYTE:
			i += itox(ev->b, &buf[i]);
			break;
		case INTEGER:
			i += itox(ev->i, &buf[i]);
			break;
		case LONG:
			i += itox(ev->l, &buf[i]);
			break;
		case FLOAT:
			i += itox(*(unsigned long *)&(ev->f), &buf[i]);
			break;
		case STRING:
		//break left out
		default:
			strcpy(&buf[i], ev->s);
			i += strlen(ev->s);
			break;
	}
	buf[i++] = ',';
	i += itox(ev->type, &buf[i]);
	
	for(int j=1; j<i; j++){
		checksum = checksum^(((int)buf[j])&0xFF);
	}
	buf[i++] = '*';
	i += itox(checksum, &buf[i]);
	buf[i++] = '\n';
	buf[i++] = '\0';
	
	Comm->print(buf);
}

void Robot::enqueue(robot_event *ev){
	int tail_index, head_index, i;
	head_index = queue.head_index;
	tail_index = queue.tail_index;
	i = head_index;
	memcpy(&queue.array[tail_index], ev, sizeof(robot_event));
	incTail();
}

bool Robot::dequeue(robot_event *ev){
	int head_index;
	
	head_index = queue.head_index;
	if(queue.length > 0){
		memcpy(ev, &queue.array[head_index], sizeof(robot_event));
		incHead();
		return true;
	}
	else{
		return false;
	}
}

void Robot::incTail(){
	queue.tail_index ++;
	while(queue.tail_index >= QUEUE_SIZE){
		queue.tail_index -= QUEUE_SIZE;
	}
	
	queue.length ++;
	while(queue.length > QUEUE_SIZE){
		incHead();
	}
}

void Robot::incHead(){
	if(queue.length > 0){
		queue.head_index ++;
	
		while(queue.head_index >= QUEUE_SIZE){
			queue.head_index -= QUEUE_SIZE;
		}
		queue.length --;
	}
}

void Robot::checkHeartBeat(){
	heartbeat++;
	if(heartbeat > 3){
		robot_event failsafe;
		failsafe.command = ROBOT_EVENT_CMD_FAILSAFE;
		failsafe.index = 0;
		failsafe.i = 0;
		failsafe.type = INTEGER;
		enqueue(&failsafe);
	}
}

unsigned long Robot::xtoi(const char* xs){
	
	int len = strlen(xs);
	unsigned long result = 0;

	for(int i = 0; i<len; i++){
		int temp = 0;
		if(xs[i] >= '0' && xs[i] <= '9'){
			temp = xs[i] - '0';
		}
		else if(xs[i] >= 'A' && xs[i] <= 'F'){
			temp = xs[i] - 'A' + 10;
		}
		else if(xs[i] >= 'a' && xs[i] <= 'f'){
			temp = xs[i] - 'a' + 10;
		}
		
		result = result << 4;
		result = result | (unsigned long)temp;	
	}
	return result;
}

int Robot::itox(unsigned long value, char *buf){
	int i = 0;
	char c[16];
	if(value == 0){
		buf[0] = '0';
		return 1;
	}
	
	for(i = 0; value > 0; i++){
		int temp = value & (long)0x0F;
		value = value >> 4;
		if(temp >= 0 && temp <= 9){
			temp = temp + '0';
		}
		else if(temp >= 10 && temp <= 15){
			temp = temp + 'A' - 10;
		}
		else{
			temp = 0;
		}
		
		c[i] = (char)temp;
	}

	for(int j=0; j<i; j++){
		buf[j] = c[i-j-1];
	}
	
	return i;
}


