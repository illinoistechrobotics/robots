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


public class Timer extends Thread{
	private Queue queue = null;
	private volatile Boolean run = true;
	public Boolean timerP1hz = false;
	public Boolean timer1hz = false;
	public Boolean timer2hz = false;
	public Boolean timer5hz = false;
	public Boolean timer10hz = false;
	public Boolean timer20hz = false;
	public Boolean timer25hz = false;
	public Boolean timer50hz = false;
	public Boolean timer100hz = false;
	
	public enum TimerEnum{
		TIMER_P1HZ 		((short)0x00),
		TIMER_1HZ		((short)0x01),
		TIMER_2HZ		((short)0x02),
		TIMER_5HZ		((short)0x03),
		TIMER_10HZ		((short)0x04),
		TIMER_20HZ		((short)0x05),
		TIMER_25HZ		((short)0x06),
		TIMER_50HZ		((short)0x07),
		TIMER_100HZ		((short)0x08),
		TiMER_HEARTBEAT ((short)0xFF);
		
		public short value;
		
		private TimerEnum(short v){
	    	this.value = v;
	    }
	}
	
	public Timer(Queue q){
		this.queue = q;
	}
	
	public void stopThread(){
		if(run != false){
			run = false;
			this.interrupt();
		}
	}
	
	public void run(){
		run = true;
		long  last_sent_P1hz = 0;
		long  last_sent_1hz = 0;
		long  last_sent_2hz = 0;
		long  last_sent_5hz = 0;
	    long  last_sent_10hz = 0;
		long  last_sent_20hz = 0;
	    long  last_sent_25hz = 0;
	    long  last_sent_50hz = 0;
	    long  last_sent_100hz = 0;
	    long  last_sent_heartbeat = 0;
		
		Date time = new Date();
		
		while(run){
			try{
				if(timerP1hz == true){
					if((time.getTime()-last_sent_P1hz) > 10000){
						last_sent_P1hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_P1HZ.value,0));
					}
				}
				if(timer1hz == true){
					if((time.getTime()-last_sent_1hz) > 1000){
						last_sent_1hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_1HZ.value,0));
					}
				}
				if(timer2hz == true){
					if((time.getTime()-last_sent_2hz) > 500){
						last_sent_2hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_2HZ.value,0));
					}
				}
				if(timer5hz == true){
					if((time.getTime()-last_sent_5hz) > 200){
						last_sent_5hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_5HZ.value,0));
					}
				}
				if(timer10hz == true){
					if((time.getTime()-last_sent_10hz) > 100){
						last_sent_10hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_10HZ.value,0));
					}
				}
				if(timer20hz == true){
					if((time.getTime()-last_sent_20hz) > 50){
						last_sent_20hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_20HZ.value,0));
					}
				}
				if(timer25hz == true){
					if((time.getTime()-last_sent_25hz) > 40){
						last_sent_25hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_25HZ.value,0));
					}
				}
				if(timer50hz == true){
					if((time.getTime()-last_sent_50hz) > 20){
						last_sent_50hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_50HZ.value,0));
					}
				}
				if(timer100hz == true){
					if((time.getTime()-last_sent_100hz) > 10){
						last_sent_100hz = time.getTime();
						queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TIMER_100HZ.value,0));
					}
				}
				if((time.getTime() - last_sent_heartbeat) > 100){
					queue.put(new Event(EventEnum.ROBOT_EVENT_TIMER,TimerEnum.TiMER_HEARTBEAT.value,0));
				}
			}
			catch(Exception e){	
			}
		}
	}	
}
