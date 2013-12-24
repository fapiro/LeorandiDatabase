package leorandiDatabase;

import java.util.Arrays;

public class LeOraField {
	private int busyCount = 0;
	private char[] data = new char[LeOraConstants.VARCHAR_MAX_SIZE];
	
	public boolean isNull(){
		return (0 == busyCount);
	}
	public boolean isNotNull(){
		return (0 != busyCount);
	}
	public int getBusyCount(){
		return busyCount;
	}
	public int getFreeCount(){
		return (LeOraConstants.VARCHAR_MAX_SIZE-busyCount);
	}
	public String getDataAsString(){
		return (String.copyValueOf(data));
	}
	public void setData(String source){
		this.data = source.toCharArray();
		busyCount = source.length();
	}
	public void setData(char[] source, int len){
		System.arraycopy(source, 0, this.data, 0, len);
		busyCount = len;
	}
	public boolean compare(char[] with){
		return Arrays.equals(data, with);
	}
	public int getMemorySize(){
		return data.length;
	}
}
