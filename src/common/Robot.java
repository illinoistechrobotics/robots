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

import common.Timer.TimerEnum;

public abstract class Robot extends Thread{

	protected Queue recv_q;
	protected Communication comm;
	protected Timer timer;
	
	public Robot(Queue q, Communication c){
		recv_q = q;	
		comm = c;
		timer = new Timer(recv_q);
		timer.timer10hz = true;
		timer.start();
	}
	
	private volatile Boolean run = true;
	int heartbeatcount = 0;
	
	public void stopThread(){
		if(run != false){
			run = false;
			this.interrupt();
		}
	}
	
	public void run()
	{	
		while(run){
			try{
				Event ev = recv_q.take();
				switch (ev.getCommand()){
				case ROBOT_EVENT_CMD:
					//break left out
				case ROBOT_EVENT_CMD_STOP:
					//break left out
				case ROBOT_EVENT_CMD_START:
					//break left out
				case ROBOT_EVENT_CMD_REBOOT:
					//break left out
					on_command_code(ev);
					break;
				case ROBOT_EVENT_CMD_FAILSAFE:
					on_failsafe(ev);
					break;
				case ROBOT_EVENT_CMD_HEARTBEAT:
					reset_heartbeat();
					on_heartbeat(ev);
					break;
				case ROBOT_EVENT_STATUS:
					on_status(ev);
					break;
				case ROBOT_EVENT_JOY_AXIS:
					on_axis_change(ev);
					break;
				case ROBOT_EVENT_JOY_BUTTON:
					if(ev.getValue() == 1)
						on_button_down(ev);
					else if (ev.getValue() == 0)
						on_button_up(ev);
					break;
				case ROBOT_EVENT_JOY_HAT:
					on_joy_hat(ev);
					break;
				case ROBOT_EVENT_JOY_STATUS:
					on_joy_status(ev);
					break;
				case ROBOT_EVENT_KEYBOARD:
					on_keyboard(ev);
					break;
				case ROBOT_EVENT_KEYBOARD_STATUS:
					on_key_status(ev);
					break;
				case ROBOT_EVENT_DISPLAY:
					on_display(ev);
					break;
				case ROBOT_EVENT_TIMER:
					if(ev.getIndex() == TimerEnum.TIMER_1HZ.value){
						on_1hz_timer(ev);
					}
					else if(ev.getIndex() == TimerEnum.TIMER_10HZ.value){
						send_heartbeat();
						check_heartbeat();
						on_10hz_timer(ev);	
					}
					else if (ev.getIndex() == TimerEnum.TIMER_25HZ.value){
						on_25hz_timer(ev);
					}
					else if (ev.getIndex() == TimerEnum.TIMER_50HZ.value){
						on_50hz_timer(ev);
					}
					else if (ev.getIndex() == TimerEnum.TIMER_100HZ.value){
						on_100hz_timer(ev);
					}
					break;	
				case ROBOT_EVENT_MOTOR:
					on_motor(ev);
					break;
				case ROBOT_EVENT_SOLENOID:
					on_solenoid(ev);
					break;
				case ROBOT_EVENT_POSE:
					on_pose(ev);
					break;
				case ROBOT_EVENT_ADC:
					on_adc(ev);
					break;
				case ROBOT_EVENT_VARIABLE:
					on_variable(ev);
					break;
				case ROBOT_EVENT_IMU:
					on_imu(ev);
					break;
				case ROBOT_EVENT_ENCODER:
					on_encoder(ev);
					break;
				case ROBOT_EVENT_EEPROM:
					on_eeprom(ev);
					break;
				case ROBOT_EVENT_IO:
					on_io(ev);
					break;
				case ROBOT_EVENT_CMD_SHUTDOWN:
					run = false;
					on_shutdown(ev);
					break;
				default:
					on_unknown_command(ev);
					break;
				}
			}
			catch(Exception e){}
		}
		
	}
	
	public void send_heartbeat(){
		comm.sendEvent(new Event(EventEnum.ROBOT_EVENT_CMD_HEARTBEAT,(short)6,(int)0xFF));
	}
	
	public void check_heartbeat(){
		heartbeatcount++;
		if(heartbeatcount>10){ //one second
			on_failsafe(new Event(EventEnum.ROBOT_EVENT_CMD_FAILSAFE,(short)0,(int)0));
		}
	}
	
	public void reset_heartbeat(){
		heartbeatcount=0;
	}
	
	protected abstract void on_failsafe(Event ev);
	protected abstract void on_command_code(Event ev);
	protected abstract void on_heartbeat(Event ev);
	protected abstract void on_status(Event ev);
	protected abstract void on_axis_change(Event ev);
	protected abstract void on_button_down(Event ev);
	protected abstract void on_button_up(Event ev);
	protected abstract void on_joy_hat(Event ev);
	protected abstract void on_joy_status(Event ev);
	protected abstract void on_keyboard(Event ev);
	protected abstract void on_key_status(Event ev);
	protected abstract void on_display(Event ev);
	protected abstract void on_1hz_timer(Event ev);
	protected abstract void on_10hz_timer(Event ev);
	protected abstract void on_25hz_timer(Event ev);
	protected abstract void on_50hz_timer(Event ev);
	protected abstract void on_100hz_timer(Event ev);
	protected abstract void on_motor(Event ev);
	protected abstract void on_solenoid(Event ev);
	protected abstract void on_pose(Event ev);
	protected abstract void on_adc(Event ev);
	protected abstract void on_variable(Event ev);
	protected abstract void on_imu(Event ev);
	protected abstract void on_encoder(Event ev);
	protected abstract void on_eeprom(Event ev);
	protected abstract void on_io(Event ev);
	protected abstract void on_shutdown(Event ev);
	protected abstract void on_unknown_command(Event ev);
}
