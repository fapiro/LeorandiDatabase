package leorandiDatabase;

import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;

public class Main {
	final static String databaseName = "LeORAndi_MEM_DB";
	private static LeOraDatabaseInstance instance;
	
	public void p(String s){
		System.out.println(s);
	}
	
	public void run(){
		LeOraDatabaseParameters params = new LeOraDatabaseParameters();
		params.useThreadCountEqualtoVirtualCpus = true;
		instance = new LeOraDatabaseInstance(databaseName, params);
		LeOraDatabase database = null;
		try {
			database = instance.getDatabase(databaseName);
		} catch (NotFoundDatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		database.createUser("HR", "pass");
		
		p("Main.run: instance.getVersion() "+instance.getVersion());

		
		ISession session1 = null;
		UserRunnableExample1 userRunnable1 = new UserRunnableExample1();
		UserRunnableExample2 userRunnable2 = new UserRunnableExample2();
		try {
			session1 = instance.createSession(databaseName, "HR", "", "pass", userRunnable1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ISession session2 = null;
		try {
			session2 = instance.createSession(databaseName, "HR", "", "pass", userRunnable2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		session1.getThread().start();
		session2.getThread().start();
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.run();
	}
}
