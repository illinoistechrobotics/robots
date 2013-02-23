package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Communication extends Thread{

	public Communication()
	{

	}

	protected Queue recv = null;

	protected enum ReadState{
		LOOKING_FOR_HEADER,
		READING_DATA,
		CALCULATE_CHECKSUM;
	}

	protected final String HEADER = "U";
	protected final String FOOTER = "\n";
	protected final int BUFFER_SIZE = 1024;
	protected StringBuffer sBuf = new StringBuffer(BUFFER_SIZE);
	protected byte[] buf = new byte[1];
	protected int length = 0;
	protected ReadState state = ReadState.LOOKING_FOR_HEADER;
	protected InputStream input = null;
	protected OutputStream output = null;

	protected void parseData(){
		try{
			while((length = input.read(buf,0,1))>0){
				String temp = new String(buf,0,length);
				sBuf.append(temp);
				switch(state){
				case LOOKING_FOR_HEADER:
					if(sBuf.indexOf(HEADER)>=0){
						sBuf = new StringBuffer(sBuf.substring(sBuf.indexOf(HEADER))); //remove data in front of header
						state = ReadState.READING_DATA;
					}
					else{
						sBuf.delete(0, sBuf.length()); //remove everything since no valid data
					}
					break;
				case READING_DATA:
					if(sBuf.indexOf(FOOTER)>=0){
						state = ReadState.CALCULATE_CHECKSUM;
					}
					else if(sBuf.indexOf(HEADER,1)>-1)
					{
						//new header "start over"
						sBuf = new StringBuffer(sBuf.substring(sBuf.indexOf(HEADER,1)));
					}
					break;
				case CALCULATE_CHECKSUM:

					Event ev = new Event();
					String fullMessage = sBuf.substring(0, sBuf.indexOf(FOOTER));
					String checksumMessage = fullMessage.substring(1, fullMessage.indexOf("*"));
					sBuf = new StringBuffer(sBuf.substring(sBuf.indexOf(FOOTER)));
					int checksum = 0;
					for(int i = 0; i < checksumMessage.length(); i++) //checksum not include the start byte
					{
						checksum = checksum^(((int)checksumMessage.charAt(i))&0xFF);
					}

					String[] token = fullMessage.split("\\*");
					int checksumRecived = Integer.parseInt(token[1],16);
					
					token = token[0].substring(1, token[0].length()).split(",");
					
					//String[] testToken = s.split("\\*");

					if(checksumRecived == checksum )
					{
						ev.setCommand(EventEnum.getEvent(Integer.parseInt(token[0],16))); //command
						ev.setIndex((short)Integer.parseInt(token[1],16)); //index
						//ev.setSValue(token[2]);	//set string always since unknown length
						Event.ValueType type = Event.ValueType.getType(Integer.parseInt(token[3]));
						switch(type){
						case BYTE:
							ev.setBValue((short)Integer.parseInt(token[2],16));
							break;
						case INTEGER:
							ev.setValue(Integer.parseInt(token[2],16));
							break;
						case LONG:
							ev.setLValue(Long.parseLong(token[2],16));
							break;
						case FLOAT:
							ev.setFValue(Float.intBitsToFloat((int)Long.parseLong(token[2],16))); //since float and long same amount of bits need to set both
							break;
						case STRING:
							ev.setSValue(token[2]);
							break;
						default:
							ev.setSValue(token[2]);
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
		}catch (IOException ioe) {

		}

	}
	
	public synchronized void sendEvent(Event ev){
		
		//System.out.println(ev.toStringSend());
		//*
		try{
			output.write(ev.toStringSend().getBytes());   //write needs a byte array instead of a string
		}
		catch(Exception e){
			System.err.println("Cannot write to robot.");
		}
		//*/
	}
	

}
