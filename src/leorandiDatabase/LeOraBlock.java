package leorandiDatabase;

class NoFreeSpaceException extends Exception{};

interface IBlock{
	public int getRowsCount();
	public int getFreeRowsCount();
	public IRow getRow(int index);
	public void insertRow(String[] data) throws NoFreeSpaceException;
	public int getRowIndexForBulkInsert();
}

public class LeOraBlock implements IBlock{
	private LeOraRow[] rows = new LeOraRow[LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK];
	private int freeRowsCount = LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK;
	private int rowIndexForBulkInsert = -1;
	
	public int getRowIndexForBulkInsert(){
		int len = rows.length, currentPosition = rowIndexForBulkInsert;
		boolean found = false; // new variable to speed up execution
		do{
			if(rowIndexForBulkInsert == -1){
				rowIndexForBulkInsert = 0;
			} 
			if(rowIndexForBulkInsert == len){
				rowIndexForBulkInsert = 0;
			}
			if(!rows[rowIndexForBulkInsert].exists()){
				found = true;
				break;
			}
		}while(++rowIndexForBulkInsert != currentPosition);
		if(!found){
			rowIndexForBulkInsert = -1;
		}
		return rowIndexForBulkInsert;
	}
	
	public LeOraBlock(int columnsCount){
		for(int i=0; i<rows.length; i++){
			rows[i] = new LeOraRow(columnsCount);
		}
		rowIndexForBulkInsert = -1;
	}
	
	public int getRowsCount(){
		return LeOraConstants.ROWS_COUNT_IN_ONE_BLOCK;
	}
	
	public int getFreeRowsCount(){ 
		// stelio: made via call to get value of simple variable to gain more fast execution
		//LeOraUtils.p("  block getFreeRowsCount="+freeRowsCount);
		return freeRowsCount;
	}
	
	public LeOraRow getRow(int index){
		return rows[index];
	}
	
	public int getMemorySize(){
		return rows.length * rows[0].getMemorySize();
	}
	
	public void insertRow(String[] data) throws NoFreeSpaceException{
		if(0 == getFreeRowsCount()){ 
			throw new NoFreeSpaceException();
		}
		int position = getRowIndexForBulkInsert();
		if(-1 == position) throw new NoFreeSpaceException();
		rows[position].update(data);
		freeRowsCount--;
	}
}
