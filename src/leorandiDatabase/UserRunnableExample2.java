package leorandiDatabase;

import java.util.concurrent.TimeUnit;

public class UserRunnableExample2 extends LeOraUserRunnable{
	private void p(String s){
		LeOraUtils.p(s);
	}
	
	public void run() {
		p("  UserRunnable2 started ...");
		p("  UserRunnable2 user thread - waiting for statistics...");
		while(true){
			try {
				if(session.getDatabase().getSchema("HR").tableExists("EMPLOYEES")){
					LeOraTable otable = (LeOraTable) session.getDatabase().getSchema("HR").getTable("EMPLOYEES");
				
					if(otable.isStatisticsGathered()){
						p("  UserRunnable2 user thread - waiting for statistics...stastics is ready");
						LeOraUtils.printTableStatistics(otable);
						break;
					}
					
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (NotFoundUserException | NotFoundTableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
		p("  UserRunnable2 Finished.");
	}
}
