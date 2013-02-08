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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ethernet extends Communication{
	
	private Queue recv = null;
	private GUI dis = null;
	private Socket sock = null;
	private InputStream is = null;
    private OutputStream os = null;
    private volatile boolean run = false;
    private boolean isConnected = false;
    private String ipAddress;
    private int portNumber;
    
	public Ethernet(Queue r, GUI d){
		this.recv = r;
		this.dis = d;
	}
	
	public void stopThread() {
        
        if (run != false) {
            run = false;
            this.interrupt();
        }
        try {
            is.close();
            os.close();
            sock.close();
            isConnected = false;
        } 
        catch (Exception e) {
        }
    }
	
	private final int BUF_SIZE = 1024;
	private final String HEADER = "U";
	private final String FOOTER = "\n";
	private String sBuf = new String();
	private byte[] buf = new byte[1];
	private int length = 0;
	private ReadState state = ReadState.LOOKING_FOR_HEADER;
	
	private enum ReadState{
		LOOKING_FOR_HEADER,
		READING_DATA,
		CALCULATE_CHECKSUM;
	}
	
	public void run() {
        run = true;
        while (run){
        	try {
            	if (!sock.isConnected() || sock.isClosed()) {
            		System.out.println("Connection Error");
            		run = false;
            		return;
                }
            	while((length = is.read(buf,0,1))>0){
            		sBuf.concat(new String(buf, 0, length));
            		switch(state){
            		case LOOKING_FOR_HEADER:
            			if(sBuf.contains(HEADER)){
            				sBuf = sBuf.substring(sBuf.indexOf(HEADER)); //remove data in front of header
            				state = ReadState.READING_DATA;
            			}
            			else{
            				sBuf = ""; //remove everything since no valid data
            			}
            			break;
            		case READING_DATA:
            			if(sBuf.contains(FOOTER)){
            				state = ReadState.CALCULATE_CHECKSUM;
            			}
            			else if(sBuf.indexOf(HEADER,1)>-1)
            			{
            				//new header "start over"
            				sBuf = sBuf.substring(sBuf.indexOf(HEADER,1));
            			}
            			break;
            		case CALCULATE_CHECKSUM:

            			Event ev = new Event();
						String fullMessage = sBuf.substring(0, sBuf.indexOf(FOOTER));
						String checksumMessage = fullMessage.substring(1, fullMessage.indexOf("*"));
						sBuf = sBuf.substring(sBuf.indexOf(FOOTER));
						int checksum = 0;
						for(int i = 2; i < checksumMessage.length(); i++) //checksum not include the start byte
						{
							checksum = checksum^(((int)checksumMessage.charAt(i))&0xFF);
						}

						String[] token = fullMessage.split(",*");

						if(Integer.parseInt(token[5],16) == checksum )
						{
							ev.setCommand(EventEnum.getEvent(Integer.parseInt(token[1],16))); //command
							ev.setIndex((short)Integer.parseInt(token[2],16)); //index
							//ev.setSValue(token[3]);	//set string always since unknown length
							Event.ValueType type = Event.ValueType.getType(Integer.parseInt(token[4]));
							switch(type){
							case BYTE:
								ev.setBValue((short)Integer.parseInt(token[3],16));
								break;
							case INTEGER:
								ev.setValue(Integer.parseInt(token[3],16));
								break;
							case LONG:
								ev.setLValue(Long.parseLong(token[3],16));
								break;
							case FLOAT:
								ev.setFValue(Float.intBitsToFloat((int)Long.parseLong(token[3],16))); //since float and long same amount of bits need to set both
								break;
							case STRING:
								ev.setSValue(token[3]);
								break;
							default:
								ev.setSValue(token[3]);
								break;
							}

							try{
								recv.put(ev);
							}
							catch(Exception e){
							}
            			}


            			state = ReadState.LOOKING_FOR_HEADER;
            			break;
            		}

            	}
            	try {
            		Thread.sleep(0,100);
            	} 
            	catch (InterruptedException ie) {
            	}
        	} catch (IOException ioe) {
        	}
        }
    }
	
	public String getIPAddress(){
		return ipAddress;
	}
	
	public int getPortNumber(){
		return portNumber;
	}
	
	public boolean isConnected(){
		return isConnected;
	}
        
	public boolean connect(String ip, int port) {
		boolean connect = true;
		ipAddress = ip;
		portNumber = port;
		InetAddress inet;

		try {
			inet = InetAddress.getByName(ip);
			sock = new Socket(inet, port);
			is = sock.getInputStream();
			os = sock.getOutputStream();
			isConnected = true;
		} catch (UnknownHostException uhe) {
			//System.out.println("Unknown Host");
			connect = false;
		} catch (IOException ioe) {
			//System.out.println("Can not connect");
			connect = false;
		}
		return connect;
	}
	
	public synchronized void sendEvent(Event ev) {
        try {
            os.write(ev.toStringSend().getBytes());   //write needs a byte array instead of a string
        } 
        catch (Exception e) {
        }

    }
	
}
