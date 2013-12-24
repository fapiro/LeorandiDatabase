package leorandiDatabase;

public class UserRunnableExample1 extends LeOraUserRunnable{
	private void p(String s){
		LeOraUtils.p(s);
	}
	
	public void run() {
		p("UserRunnable1 started ...");
		p("UserRunnable1 Connected to databaseName="+session.getDatabase().getDatabaseName());
		p("UserRunnable1 Connected to schema="+session.getSchema().getName());
		
		String[] columns = {"ID", "FIRST_NAME", "SECOND_NAME", "DT_BIRTH", "DT_HIRED"};
		session.getSchema().createTable("EMPLOYEES", columns);
			
		ITable table = null;
		try {
			table = session.getSchema().getTable("EMPLOYEES");
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!table.getClass().getSimpleName().equals("LeOraTable")){
			p("UserRunnable1 UNKNOWN Implementation of ITable = <"+table.getClass().getSimpleName()+">");
		} else {	
			LeOraTable otable = (LeOraTable) table; 
			p("UserRunnable1 after creation: table="+otable.getName()+", isPartitioned="+otable.isPartitioned());
			try {
				otable.setPartitioned("DT_HIRED", "2012");
			} catch (WrongParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p("UserRunnable1 set partitioned: table="+otable.getName()+", isPartitioned="+otable.isPartitioned());
			p("UserRunnable1 table.getPartitionsCount()="+otable.getPartitionsCount());
			p("UserRunnable1 lets add new partition to the table...");
			try {
				otable.addPartition("2013");
			} catch (WrongParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int count = otable.getPartitionsCount();
			p("UserRunnable1 table.getPartitionsCount()="+count);
			for(int i=0; i<count; i++){
				try {
					p("UserRunnable1 partition "+i+" = "+otable.getPartition(i).getPartitionKey());
				} catch (WrongParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		p("UserRunnable1 Finished.");
	}
}
