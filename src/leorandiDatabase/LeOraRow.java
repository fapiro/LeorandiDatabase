package leorandiDatabase;

interface IRow{
	public boolean exists();
	public int getColumnsCount();
}

public class LeOraRow implements IRow{
	private boolean rowExists = false;
	private int fieldsCount = 0;
	private LeOraField[] fields = null;
	
	public LeOraRow(int columnsCount){
		this.fieldsCount = columnsCount;
		fields = new LeOraField[columnsCount];
		for(int i=0; i<columnsCount; i++){
			fields[i] = new LeOraField();
		}
	}
	
	public boolean exists(){
		return rowExists;
	}
	
	public int getColumnsCount(){
		return fieldsCount;
	}
	
	public LeOraField getColumn(int index){
		return fields[index];
	}
	
	public int getMemorySize(){
		return fields.length * fields[0].getMemorySize();
	}
	
	public void update(String[] data){
		int len = data.length;
		for(int i=0; i<len; i++){
			fields[i].setData(data[i]);
		}
		rowExists = true;
	}
	
	public void delete(){
		rowExists = false;
	}
}
