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
	private Socket sock = null;
	private volatile boolean run = false;
	private boolean isConnected = false;
	private String ipAddress;
	private int portNumber;

	public Ethernet(Queue r){
		this.recv = r;
	}

	public void stopThread() {

		if (run != false) {
			run = false;
			this.interrupt();
		}
		try {
			input.close();
			output.close();
			sock.close();
			isConnected = false;
		} 
		catch (Exception e) {
		}
	}

	/*
	private final String HEADER = "U";
	private final String FOOTER = "\n";
	private String sBuf = new String();
	private byte[] buf = new byte[1];
	private int length = 0;
	private ReadState state = ReadState.LOOKING_FOR_HEADER;
	 */
	/*
	private enum ReadState{
		LOOKING_FOR_HEADER,
		READING_DATA,
		CALCULATE_CHECKSUM;
	}
	 */
	public void run() {
		run = true;
		while (run){
			if (!sock.isConnected() || sock.isClosed()) {
				System.out.println("Connection Error");
				run = false;
				return;
			}
			parseData();
			try {
				Thread.sleep(0,100);
			} 
			catch (InterruptedException ie) {
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
			input = sock.getInputStream();
			output = sock.getOutputStream();
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

}
