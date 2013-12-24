package leorandiDatabase;

import java.util.ArrayList;

interface ISchema{
	public String getName();
	public void checkPassword(String password) throws WrongPasswordException;
	public int getTableCount();
	public ITable getTable(String name) throws NotFoundTableException;
	public boolean tableExists(String name);
	public void createTable(String name, String[] colunms);
}

public class LeOraSchema implements ISchema{
	private String name;
	private String password;
	private ArrayList<LeOraTable> tables = new ArrayList<LeOraTable>();
	public LeOraSchema(String name, String password){
		this.name = name;
		this.password = password;
	}
	
	public void gatherStatistics(boolean force){
		for(int i=0;i<tables.size();i++){
			tables.get(i).gatherStatistics(force);
		}
	}
	
	public String getName(){
		return this.name;
	}
	public String SetPassword(String password){
		return this.password;
	}
	public void checkPassword(String password) throws WrongPasswordException{
		if (this.password != password) throw new WrongPasswordException(); 
	}
	
	public LeOraTable getTable(String name) throws NotFoundTableException{
		int index = -1;
		for(int i=0; i<tables.size(); i++){
			 if(name == tables.get(i).getName()){
				 index = i; 
				 break;
			 }
		}
		if(index < 0) throw new NotFoundTableException();
		return tables.get(index);
	}
	
	public boolean tableExists(String name){
		int index = -1;
		for(int i=0; i<tables.size(); i++){
			 if(name == tables.get(i).getName()){
				 index = i; 
				 break;
			 }
		}
		if(index < 0) return false;
		return true;
	}
	
	public int getTableCount(){
		return tables.size();
	}
	
	public void createTable(String name, String[] colunms){
		if(!tableExists(name)){
			// creates new table:
			LeOraTable table = new LeOraTable(name, colunms);
			tables.add(table);	
		}
	}
}
