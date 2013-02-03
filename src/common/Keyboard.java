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

import net.java.games.input.*;
import net.java.games.input.Component.Identifier;

public class Keyboard extends Thread{

	private Controller keyboard = null;
	private Queue queue = null;
	private GUI dis = null;
	private volatile Boolean run = true;
	
	public Keyboard(Queue q, GUI d){
		queue = q;
		dis = d;
		
		Controller[] cs;
		if((System.getProperty("os.name").contains("nux"))||(System.getProperty("os.name").contains("Mac OS X"))){
			ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment(); 
			cs = ce.getControllers(); 
		}
		else{
			DirectInputEnvironmentPlugin diep = new DirectInputEnvironmentPlugin();
			cs = diep.getControllers();
		}
		Controller[] st = new Controller[cs.length];
		int j=0;
		for(int i=0; i<cs.length; i++){
			//this will only output the controllers
			if(cs[i].getType() == Controller.Type.KEYBOARD){
				keyboard = cs[i];
				/*
				EventQueue event_q = cs[i].getEventQueue();
				net.java.games.input.Event joy_event = new net.java.games.input.Event();
				while(true){
					cs[i].poll();
					while(event_q.getNextEvent(joy_event)){
						System.out.println(joy_event.toString());
					}
				}
				//*/
				break;
			}
			//System.out.println(cs[i].getType().toString());
		}
	}
	
	public void stopThread(){
		if(run != false){
			run = false;
			this.interrupt();
		}
	}
	
	public void run(){
		Event ev = new Event();
		run = true;
		//clear any joystick events in the queue
		EventQueue event_q = keyboard.getEventQueue();
		net.java.games.input.Event key_event = new net.java.games.input.Event();
		while(event_q.getNextEvent(key_event)){
			;
		}
		
		while(run){
			try{
				if(keyboard.poll()==false){
					Event key_ev = new Event(EventEnum.ROBOT_EVENT_JOY_STATUS,(short)0,0);
					queue.put(key_ev);
					return;
				}
			}
			catch(Exception e){
				System.err.println("Can not open keyboard");
				return;
			}
			//EventQueue event_q = joy.getEventQueue();
			//net.java.games.input.Event joy_event = new net.java.games.input.Event();
			while(event_q.getNextEvent(key_event)){
				Component comp = key_event.getComponent();						
				String command = comp.getName();
				//switch(command)
				//{
				//case "hello":
					
				//}
				
				try{
					queue.put(ev);
				}
				catch(Exception e){
					System.out.println("Error");
				}
			}
			
			try{
				Thread.sleep(10);
			}
			catch(Exception e){	
			}
			
		}
	}
	
	
}

