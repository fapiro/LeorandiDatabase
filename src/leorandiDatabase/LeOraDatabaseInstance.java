package leorandiDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/* These classes implement new own "in-memory database". There is no any conjunction with Oracle.
 * This is alternative implementation of the database and instance. All information about how that was done in Oracle has been
 *   received from public sites where everyone can get information of Oracle documentation.
 * This database internal organization (for example using Segments, Extents) is well known 
 *   and everyone can view it on public sites.
 * This Leorandi Database is just another implementation and doesn't establishes that something is made
 *   exactly this way in Oracle Database.
 * Oracle and Oracle Databases are registered trade marks of Oracle Corporation (www.oracle.com).
 * Author of LeOraDatabase and these LeOra...java files is Roman Kuzmin. 
 * Files are published on GIT (HUB). 
 * Downloading, viewing (in any form), using, coping these files you accept that Author is Roman Kuzmin.
 * Also you accept that you can use them only for not commercial purposes. 
 * If you would like to use them for commercial purposes you need get approval from the Author.
 * Also you accept that Author(Roman Kuzmin) has all rights on these (LeOra...java) files.
 * Author's contacts: fapiro@mail.ru, www.LeORAndi.com
 * These files are distributed under GPLv2 license.             
 * Class (delegation) hierarchy:
 *    LeOraDatabaseInstance:
 *      |-> field LeOraThreadsManager
 *          |-> ExecutorService
 *    	|-> List of LeOraSession
 *      |-> List of LeOraDatabase (implements IDatabase):
 *      	|-> List of: LeOraSchema (implements ISchema)
 *      	    |-> List of: LeOraTable (implements ITable)
 *      		    |-> List of: LeOraSegment (implements ISegment)
 *      			    |-> List of: LeOraExtent (implements IExtent)
 *      				    |-> array of: LeOraBlock (implements IBlock)
 *                              |-> array of: LeOraRow (implements IRow)
 *                                  |-> array of: LeOraField
 *                                      |-> array of: char 
 *  Other classes:
 *    * LeOraConstants: contains useful constants like:
 *    		VARCHAR_MAX_SIZE, ROWS_COUNT_IN_ONE_BLOCK, BLOCKS_COUNT_IN_ONE_EXTENT
 *    
 *    * LeOraDatabaseParameters: parameters for each database:
 *    		if useThreadCountEqualtoVirtualCpus == true { uses all virtual CPU on this machine for this database}
 *    		else limits number of CPUs to another parameter totalSystemThreadsCount (default value = 1)
 *    
 *    * LeOraException: own exceptions used:
 *    		Hierarchy:
 *    			Exception
 *    				|-> LeOraException   
 *    				|	|-> WrongParameterException
 *    				|		|-> WrongPasswordException
 *    				|-> NotFoundException
 *    				|	|-> NotFoundDatabaseException
 *    				|	|-> NotFoundUserException
 *    				|	|-> NotFoundTableException
 *    				|-> SessionCreationFailedException
 *   				|-> ColumnCountException
 *   				|-> PartitionKeyIsNullException
 *   
 *   * LeOraSession implements interface ISession:
 *   	The most major method is getThread(). Used for call to start().
 *   
 *   * LeOraStatistics: represents table statistics
 *      See also methods:
 *      	LeOraDatabase.gatherStatistics();
 *      	LeOraSchema.gatherStatistics();
 *      	LeOraTable.gatherStatistics();
 *      
 *   * LeOraThreadsManager: represents LeOraSystemThreadsFactory (system database daemons)
 *   	major methods are:
 *   		* newSystemThread()
 *   		* newUserThread()
 *   	Count of system threads is limited using LeOraDatabaseParameters: 
 *   		* useThreadCountEqualtoVirtualCpus 
 *                     or
 *   		* totalSystemThreadsCount
 *     (see info for LeOraDatabaseParameters)
 *     Note: System threads are always daemons except of user threads which are not.
 *     
 *	 * LeOraUserRunnable: helpful class for creation of user session's program code.
 *		All code must be placed in the method = run() of this class. 
 *      Pass this instance of this class to the
 *        session = instance.createSession(..., <here>) 
 *        where:
 *          * session declare as ISession session = null;
 *          * instance is the object of the class LeOraDatabaseInstance
 *       Don't forget to surround call with try catch (if needed)
 *       After that start Thread using:
 *       	session.getThread().start();
 *       Note: you can also use session.getThread().join() to start thread after another will complete
 *       
 *   * LeOraUtils: some useful utils:
 *   	* p() to prevent using inline System.out.println each time in the code. 
 *            Using small change in this method output can be easily redirected to another target.
 *      * printTableStatistics() example of how to print table statistics.
 *      
 *   *  Main.java: contains examples how-to use all LeOraDatabaseInstance and organize you development process.
 *   		These examples are complicated and placed in Main.java because of restrictions of JUnit 
 *   		regarding Threads.
 *   
 *   * TestLeOraDatabaseInstance.java in the "test" (source directory) contains some JUnit tests.
 *   
 *   Note: you can run Main.java usual way 
 *      or separately select file TestLeOraDatabaseInstance.java and Run As "JUnit Test".
 *   During execution control output in the Console (output via LeOraUtils.p()).
 *
 */

public class LeOraDatabaseInstance {
	private final int release = 1;
	private final int patch = 1;
	private LeOraThreadsManager threadsManager;
	
	// managed sessions to different databases
	private ArrayList<LeOraSession> sessions = new ArrayList<LeOraSession>();
	
	//managed databases
	private ArrayList<LeOraDatabase> databases = new ArrayList<LeOraDatabase>();
	
	class LeOraPerformanceOptimizerSystemDaemon implements Runnable{
		public void run() {
			int printCount = 0;
			final int printMaxCount = 2;
			while(true){
				if(printCount < printMaxCount)LeOraUtils.p("Garthering statistics for all databases (starting daemon)...");
				for(int i=0; i<databases.size();i++){
					// gathers statistics for next database in cycle
					databases.get(i).gatherStatistics(false);
				}
				if(printCount < printMaxCount)LeOraUtils.p("Garthering statistics ... Finished.");
				printCount++;
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					LeOraUtils.p("daemon interrupted");
				}
			}	
		}
	}
	
	public int getRelease(){return release;}
	public int getPatch(){return patch;}
	public String getVersion(){return release+"."+patch;}
	
	public LeOraDatabase addDatabase(String databaseName, LeOraDatabaseParameters params){
		LeOraDatabase database = new LeOraDatabase(databaseName, params);
		databases.add(database);
		return database;
	}
	
	public LeOraDatabase getDatabase(String databaseName) throws NotFoundDatabaseException{
		int res = -1;
		for(int i=0; i<databases.size();i++){
			if(databaseName == databases.get(i).getDatabaseName()){
				res = i;
				break;
			}
		}
		if(res < 0){
			throw new NotFoundDatabaseException();
		}
		return databases.get(res);
	}
	
	// instance must be connected at least to one database. 
	//   That is why we need specify databaseName here. 
	LeOraDatabaseInstance(String databaseName, LeOraDatabaseParameters params){
		addDatabase(databaseName, params);
		if(params.useThreadCountEqualtoVirtualCpus){
			threadsManager = new LeOraThreadsManager(Runtime.getRuntime().availableProcessors());
		}else{
			threadsManager = new LeOraThreadsManager(params.totalSystemThreadsCount);
		}
		threadsManager.newSystemThread(new LeOraDatabaseInstance.LeOraPerformanceOptimizerSystemDaemon());
	}
	
	public LeOraSession createSession(String databaseName, String ConnectedUserName, String ConnectedAs, String password, LeOraUserRunnable userRunnable)
			throws NotFoundDatabaseException, NotFoundUserException, WrongPasswordException
	{
		LeOraDatabase database = getDatabase(databaseName);
		LeOraSession session = new LeOraSession();
		session.setConnected(database, ConnectedUserName, ConnectedAs, password);
		if (userRunnable != null) {
			userRunnable.session = session;
			session.setThread(threadsManager.newUserThread(userRunnable)); 
		}
		return session;
	}
}
