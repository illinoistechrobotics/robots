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

public abstract class Robot extends Thread{

	Queue recv_q;
	Communication comm;
	
	public Robot(Queue q, Communication c){
		recv_q = q;	
		comm = c;
	}
	
	private volatile Boolean run = true;
	
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
				case ROBOT_EVENT_CMD_FAILSAFE:
					on_command_code(ev);
					break;
				case ROBOT_EVENT_CMD_HEARTBEAT:
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
				case ROBOT_EVENT_DISPLAY:
					on_display(ev);
					break;
				case ROBOT_EVENT_TIMER:
					if(ev.getIndex() == 1)
						on_1hz_timer(ev);
					else if(ev.getIndex() == 2)
						on_10hz_timer(ev);
					else if (ev.getIndex() == 3)
						on_25hz_timer(ev);
					else if (ev.getIndex() == 4)
						on_50hz_timer(ev);
					else if (ev.getIndex() == 5)
						on_100hz_timer(ev);
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
		
	public abstract void on_command_code(Event ev);
	public abstract void on_heartbeat(Event ev);
	public abstract void on_status(Event ev);
	public abstract void on_axis_change(Event ev);
	public abstract void on_button_down(Event ev);
	public abstract void on_button_up(Event ev);
	public abstract void on_joy_hat(Event ev);
	public abstract void on_joy_status(Event ev);
	public abstract void on_keyboard(Event ev);
	public abstract void on_display(Event ev);
	public abstract void on_1hz_timer(Event ev);
	public abstract void on_10hz_timer(Event ev);
	public abstract void on_25hz_timer(Event ev);
	public abstract void on_50hz_timer(Event ev);
	public abstract void on_100hz_timer(Event ev);
	public abstract void on_motor(Event ev);
	public abstract void on_solenoid(Event ev);
	public abstract void on_pose(Event ev);
	public abstract void on_adc(Event ev);
	public abstract void on_variable(Event ev);
	public abstract void on_imu(Event ev);
	public abstract void on_encoder(Event ev);
	public abstract void on_eeprom(Event ev);
	public abstract void on_io(Event ev);
	public abstract void on_shutdown(Event ev);
	public abstract void on_unknown_command(Event ev);
}
