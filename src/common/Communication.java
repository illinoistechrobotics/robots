package common;

public abstract class Communication extends Thread{

	public Communication()
	{
		
	}
	
	public enum ReadState{
		LOOKING_FOR_HEADER,
		READING_DATA,
		CALCULATE_CHECKSUM;
	}
	
	public abstract void sendEvent(Event ev);
	
}
