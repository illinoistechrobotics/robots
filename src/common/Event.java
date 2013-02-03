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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Event {
	
	public enum ValueType
	{
		BYTE		(1),
		INTEGER		(0),
		LONG		(2),
		FLOAT		(3),
		STRING		(4),
		UNKNOWN_TYPE(5);
		
		private int value;
		private static final Map<Integer,ValueType> lookup = new HashMap<Integer,ValueType>();
	    static {
	    	for(ValueType s : EnumSet.allOf(ValueType.class))
	         lookup.put(s.getValue(), s);
	    }
	    public static ValueType getType(int value){
	    	ValueType temp = lookup.get(value);
	    	if(temp == null){
	    		return UNKNOWN_TYPE;
	    	}
	    	return temp;
	    }
		private ValueType(int v){
	    	this.value = v;
	    }
		public int getValue(){
	    	return this.value;
	    }
	}
	
	public EventEnum command = null;
	public short index = 0;
	public short bValue = 0; //one byte
	public int value = 0;	 //two bytes
	public long lValue = 0;	 //four bytes
	public float fValue = 0; //float four bytes
	public String sValue = null; //string 1-32 bytes
	public ValueType type = ValueType.INTEGER;
	
	
	/**
	 * One new Robot_Event object for each event
	 * since java doesn't support unsigned types the values are larger than they should be
	 * actual values that are used on the arduino and will be sent are
	 * (may add node 1 byte sender 1 byte receiver)
	 * command: 1 byte
	 * index:   1 byte
	 * value:	variable 1 byte to 32 bytes depending on type 
	 */
	public Event(){
	}
	
	public Event(EventEnum c, short i, short v){
		this.command = c;
		this.index = i;
		this.bValue = v;
		this.type = ValueType.BYTE;
	}
	public Event(EventEnum c, short i, int v){
		this.command = c;
		this.index = i;
		this.value = v;
		this.type = ValueType.INTEGER;
	}
	public Event(EventEnum c, short i, long v){
		this.command = c;
		this.index = i;
		this.lValue = v;
		this.type = ValueType.LONG;
	}
	public Event(EventEnum c, short i, float v){
		this.command = c;
		this.index = i;
		this.fValue = v;
		this.type = ValueType.FLOAT;
	}
	public Event(EventEnum c, short i, String v){
		this.command = c;
		this.index = i;
		this.sValue = v;
		this.type = ValueType.STRING;
	}
	public EventEnum getCommand(){
		return command;
	}
	public short getIndex(){
		return index;
	}
	public short getBValue(){
		return bValue;
	}
	public int getValue(){
		return value;
	}
	public long getLValue(){
		return lValue;
	}
	public float getFValue(){
		return fValue;
	}
	public String getSValue(){
		return sValue;
	}
	public void setCommand(EventEnum c){
		this.command = c;
	}
	public void setIndex(short i){
		this.index = i;
	}
	public void setBValue(short v){
		this.bValue = v;
		this.type = ValueType.BYTE;
	}
	public void setValue(int v){
		this.value = v;
		this.type = ValueType.INTEGER;
	}
	public void setLValue(long v){
		this.lValue = v;
		this.type = ValueType.LONG;
	}
	public void setFValue(float v){
		this.fValue = v;
		this.type = ValueType.FLOAT;
	}
	public void setSValue(String v){
		this.sValue = v;
		this.type = ValueType.STRING;
	}
	
	/**
	 * Is the packet compiled that is sent over the serial port
	 * U,(command),(index),(value),(checksum)\n
	 * checksum is the XOR of all of the bytes between the header and footer
	 * All values are sent as Hex values comma delimited with a newline terminated
	 * Start byte is U binary(01010101)
	 */
	public String toStringSend(){
		StringBuffer st = new StringBuffer("U,");
		int checksum = 0;
		st.append(Integer.toString(command.getValue()&0xFF,16));
		st.append(",");
		st.append(Integer.toString(index&0xFF, 16));
		st.append(",");
		switch(this.type)
		{
		case BYTE:
			st.append(Integer.toString(bValue&0xFF,16));
			break;
		case INTEGER:
			st.append(Integer.toString(value&0xFFFF,16));
			break;
		case LONG:
			st.append(Long.toString(lValue&0xFFFFFFFF,16));
			break;
		case FLOAT:
			st.append(Integer.toHexString(Float.floatToRawIntBits((float)fValue))); 
			break;
		case STRING:
			st.append(sValue);
			break;
		default:
			st.append(Integer.toString(value&0xFFFF,16));
			break;
		}
		st.append(",");
		st.append(Integer.toString(type.value&0xFF,16));
		
		for(int i = 2; i < st.length(); i++) //checksum do not include the start "U,"
		{
			checksum = checksum^(((int)st.charAt(i))&0xFF);
		}
		st.append("*");
		st.append(Integer.toString(checksum&0xFF,16));
		st.append("\n");
		return st.toString();
	}
	
	public String toString(){
		return command + "," + index + "," + value;
	}
	
	public Event copy(){
		switch(this.type)
		{
		case BYTE:
			return new Event(this.command,this.index,this.bValue);
		case INTEGER:
			return new Event(this.command,this.index,this.value);
		case LONG:
			return new Event(this.command,this.index,this.lValue);
		case FLOAT:
			return new Event(this.command,this.index,this.fValue);
		case STRING:
			return new Event(this.command,this.index,this.sValue);
		default:
			return new Event(this.command,this.index,this.value);
		}		
	}
}
