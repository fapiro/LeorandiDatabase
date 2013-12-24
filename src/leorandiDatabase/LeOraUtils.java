package leorandiDatabase;

public abstract class LeOraUtils {
	public static void p(String s){
		System.out.println(s);
	}
	
	public static void printTableStatistics(LeOraTable table){
		if(!table.isStatisticsGathered()){
			p("statistics has not been gathered yet for this table="+table.getName());
		}else{
			LeOraStatistics statistics = table.getStatistics();
			p(
					"memory bytes="+statistics.memoryBytes+
					", KBytes="+(statistics.memoryBytes/LeOraConstants.KILO_BYTE)+
					", MBytes="+(statistics.memoryBytes/LeOraConstants.MEGA_BYTE));
			
			p(
					"free   bytes="+statistics.freeBytes+
					", KBytes="+(statistics.freeBytes/LeOraConstants.KILO_BYTE)+
					", MBytes="+(statistics.freeBytes/LeOraConstants.MEGA_BYTE));
			
			p("freePercent="+statistics.freePercent());
		}	
	}
}
