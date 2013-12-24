package leorandiDatabase;

interface ISession{
	public IDatabase getDatabase();
	public String getConnectedUserName();
	public ISchema getSchema();
	public String getConnectedAs();
	public boolean getConnected();
	public void setThread(Thread thread);
	public Thread getThread();
}

public class LeOraSession implements ISession{
	private LeOraDatabase database;
	private String connectedUserName;
	private LeOraSchema schema;
	private String connectedAs;
	private boolean connected;
	private Thread thread;
	
	public LeOraDatabase getDatabase(){
		return this.database;
	}
	public String getConnectedUserName(){
		return this.connectedUserName;
	}
	public LeOraSchema getSchema(){
		return this.schema;
	}
	public String getConnectedAs(){
		return this.connectedAs;
	}
	public boolean getConnected(){
		return this.connected;
	}
	
	public void setConnected(LeOraDatabase database, String connectedUserName, String connectedAs, String password) throws NotFoundUserException, WrongPasswordException{
		this.schema = database.getSchema(connectedUserName);
		this.schema.checkPassword(password);
		this.database = (LeOraDatabase) database;
		this.connected = true;
		this.connectedUserName = connectedUserName;
		this.connectedAs = connectedAs;
	}
	
	public void setThread(Thread thread){
		this.thread = thread;
	}
	
	public Thread getThread(){
		return this.thread;
	}
}
