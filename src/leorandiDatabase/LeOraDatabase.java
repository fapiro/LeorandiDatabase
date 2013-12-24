package leorandiDatabase;

import java.util.ArrayList;

interface IDatabase{
	public String getDatabaseName();
	public ISchema createUser(String schemaName, String password);
	public ISchema getSchema(String schemaName) throws NotFoundUserException;
}

public class LeOraDatabase implements IDatabase{
	private String databaseName; // equals to Oracle SID
	private LeOraDatabaseParameters params;
	private ArrayList<LeOraSchema> schemas = new ArrayList<LeOraSchema>();
	
	public LeOraDatabase(String databaseName, LeOraDatabaseParameters params){
		this.databaseName = databaseName;
		this.params = params;
	}
	
	public void gatherStatistics(boolean force){
		for(int i=0;i<schemas.size();i++){
			schemas.get(i).gatherStatistics(force);
		}
	}
	
	public String getDatabaseName(){
		return this.databaseName;
	}
	
	public LeOraDatabaseParameters getParams(){
		return this.params;
	}
	
	public LeOraSchema createUser(String schemaName, String password){
		LeOraSchema schema;
		try {
			schema = getSchema(schemaName);
		} catch (NotFoundUserException e) {
			schema = new LeOraSchema(schemaName, password);
			schemas.add(schema);
		}
		return schema;
	}
	
	public LeOraSchema getSchema(String schemaName) throws NotFoundUserException {
		LeOraSchema cur = null;
		int res = -1;
		for(int i=0; i<schemas.size();i++){
			cur = schemas.get(i); // speed up via caching get
			if(schemaName == cur.getName()){
				res = i;
				break;
			}
		}
		cur = null;
		if(res < 0){
			throw new NotFoundUserException();
		}
		return schemas.get(res);
	}
}
