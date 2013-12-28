package leorandiDatabase;

import java.util.ArrayList;

interface ITable{
	public int getMemorySize(int unitOfMeasure);
	public LeOraSegment getSegment(String partitionKey) throws WrongParameterException;
	public LeOraSegment getSegment(int index);
	public int getRowsCount();
	public int getFreeRowsCount();
	public int getSegmentsCount();
	public int getFreeSegmentsCount();	
	public LeOraSegment getMainSegment();
	public void insertRow(String[] data) throws ColumnCountException, PartitionKeyIsNullException, WrongParameterException;
	public String getName();
}

public class LeOraTable implements ITable{
	private String name;
	private String[] columns;
	private ArrayList<LeOraSegment> segments = new ArrayList<LeOraSegment>();
	private boolean partitioned = false;
	private String partitionByColumn = null;
	private int partitionByColumnIndex = -1;
	private boolean statisticsGathered = false; // statistics has not been gathered yet for this table
	private LeOraStatistics statistics;
	private int freeSegmentsCount = 0;
	
	public boolean isPartitioned(){
		return partitioned;
	}
	
	public boolean isStatisticsGathered(){
		return this.statisticsGathered;
	}
	
	public void setPartitioned(String partitionByColumn, String valueForMainSegment) throws WrongParameterException{
		if(partitioned){
			throw new WrongParameterException();
		}
		partitionByColumnIndex = -1;
		for(int i=0;i<columns.length;i++){
			if(columns[i] == partitionByColumn){
				partitionByColumnIndex = i;
				break;
			}	
		}
		if(-1 == partitionByColumnIndex){
			throw new WrongParameterException();
		}
		this.partitionByColumn = partitionByColumn;
		this.getSegment(0).setPartitionKey(valueForMainSegment);
		partitioned = true;
	}
	
	public void addPartition(String partitionKeyValue) throws WrongParameterException{
		if(!partitioned){
			throw new WrongParameterException();
		}
		if(null == getPartition(partitionKeyValue)){
			LeOraSegment segment = new LeOraSegment(columns.length);
			segment.setPartitionKey(partitionKeyValue);
			this.segments.add(segment);
			freeSegmentsCount++;
			segment = null;
		}
	}
	
	public LeOraStatistics gatherStatistics(boolean force){
		if(force || (!statisticsGathered)){
			statistics = null;
			statisticsGathered = false;
			LeOraStatistics st = new LeOraStatistics();
			for(int segmentIndex=0; segmentIndex<segments.size(); segmentIndex++){
				st.addFrom(getSegment(segmentIndex).gatherStatistics());
			}
			statistics = st;
			statisticsGathered = true;
		}	
		return statistics;
	}
	
	public LeOraStatistics getStatistics(){
		return statistics;
	}
	
	public LeOraTable(String name, String[] columns){
		this.name = name;
		this.columns = columns;
		this.segments.add(new LeOraSegment(columns.length));
		freeSegmentsCount++;
	}
	
	public int getMemorySize(int unitOfMeasure){
		int res = 0;
		for(int i=0; i<segments.size(); i++){
			res += segments.get(i).getMemorySize();
		}
		return (res / unitOfMeasure);
	}
	
	public int getRowsCount(){
		int res = 0;
		for(int i=0; i<segments.size(); i++){
			res += segments.get(i).getRowsCount();
		}
		return res;
	}
	
	public int getFreeRowsCount(){
		int res = 0;
		for(int i=0; i<segments.size(); i++){
			res += segments.get(i).getFreeRowsCount();
		}
		//LeOraUtils.p("  table getFreeRowsCount res="+res);
		return res;
	}
	
	public LeOraSegment getPartition(String partitionKey) throws WrongParameterException{
		return getSegment(partitionKey);
	}
	
	public LeOraSegment getPartition(int indexInList) throws WrongParameterException{
		return getSegment(indexInList);
	}
	
	public LeOraSegment getSegment(String partitionKey) throws WrongParameterException{
		if(!isPartitioned()){
			throw new WrongParameterException();
		}
		LeOraSegment segment = null;
		for(int i=0; i<segments.size(); i++){
			if(segments.get(i).getPartitionKey().equals(partitionKey)){
				segment = segments.get(i);
				break;
			}
		}
		return segment;
	}
	
	public LeOraSegment getSegment(int index){
		return segments.get(index);
	}
	
	public int getSegmentsCount(){
		return segments.size();
	}
	
	public int getFreeSegmentsCount(){
		return segments.size();
	}
	
	public int getPartitionsCount(){
		return getSegmentsCount();
	}
	
	public LeOraSegment getMainSegment(){
		return segments.get(0);
	}
	
	public String[] getColumns(){
		return columns;
	}
	
	public void insertRow(String[] data) throws ColumnCountException, PartitionKeyIsNullException, WrongParameterException{
		int len = data.length;
		if(len > this.columns.length){
			throw new ColumnCountException();
		}
		LeOraSegment segment;
		if(isPartitioned()){
			if(partitionByColumnIndex >= len){
				throw new PartitionKeyIsNullException();
			}
			segment = getPartition(data[partitionByColumnIndex]);
		} else {
			segment = getMainSegment();
		}
		try {
			segment.insertRow(data);
		} catch (NoFreeSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (0 == segment.getFreeExtentsCount()) freeSegmentsCount--; 
		segment = null;
	}
	
	public String getName(){
		return this.name;
	}
}
