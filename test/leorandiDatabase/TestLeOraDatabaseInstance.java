package leorandiDatabase;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import leorandiDatabase.LeOraDatabase;
import leorandiDatabase.LeOraDatabaseInstance;
import leorandiDatabase.LeOraDatabaseParameters;
import leorandiDatabase.LeOraUserRunnable;
import leorandiDatabase.NotFoundDatabaseException;
import leorandiDatabase.NotFoundException;
import leorandiDatabase.NotFoundUserException;
import leorandiDatabase.SessionCreationFailedException;
import leorandiDatabase.WrongPasswordException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author stelio
 *  
 */
public class TestLeOraDatabaseInstance {
	final static String databaseName = "LeORAndi_MEM_DB";
	final static String schemaName = "HR";
	private static LeOraDatabaseInstance instance;
	
	private void p(String s){System.out.println(s);};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LeOraDatabaseParameters params = new LeOraDatabaseParameters();
		params.useThreadCountEqualtoVirtualCpus = true;
		instance = new LeOraDatabaseInstance(databaseName, params);
		LeOraDatabase database = instance.getDatabase(databaseName);
		database.createUser("HR", "pass");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = NotFoundDatabaseException.class)
	public void test1() throws NotFoundUserException, NotFoundDatabaseException, WrongPasswordException {
		instance.createSession("SOMEONE_DB", schemaName, "", "TEST_PASSWORD", new LeOraUserRunnable());
	}

	@Test(expected = WrongPasswordException.class)
	public void test2() throws WrongPasswordException, NotFoundDatabaseException, NotFoundUserException{
		instance.createSession(databaseName, schemaName, "", "<WhichPassword?>", new LeOraUserRunnable());
	}
	
	@Test
	public void test3() throws NotFoundDatabaseException, NotFoundUserException, WrongPasswordException{
		// Roman Kuzmin: Don't use thread Runnable here because of bugs in JUnit
		class Nested{
			public void run(LeOraSession session) {
				LeOraDatabase db = (LeOraDatabase) session.getDatabase();
				LeOraSchema schema = null;
				try {
					schema =  db.getSchema(schemaName);
				} catch (NotFoundUserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String[] columns = {"ID", "FIRST_NAME", "SECOND_NAME", "DT_BIRTH", "DT_HIRED"};
				final String tableName = "EMPLOYEES";
				LeOraTable emp = schema.createTable(tableName, columns);
				String[] data    = {"0", "ROMAN", "KUZMIN", "", ""};
				p("before: emp.getRowsCount() = "+emp.getRowsCount());
				p("before: emp.getFreeRowsCount() = "+emp.getFreeRowsCount());
				try {
					p("inserting one row...");
					emp.insertRow(data);
				} catch (ColumnCountException | PartitionKeyIsNullException
						| WrongParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				p("Done");
				p("after: emp.getRowsCount() = "+emp.getRowsCount());
				p("emp.getFreeRowsCount() = "+emp.getFreeRowsCount());
				p("emp.getMainSegment().getExtentsCount() = "+emp.getMainSegment().getExtentsCount());
				p("emp.getMainSegment().getFreeExtentsCount() = "+emp.getMainSegment().getFreeExtentsCount());
				p("emp.getSegmentsCount() = "+emp.getSegmentsCount());
				p("emp.getFreeSegmentsCount() = "+emp.getFreeSegmentsCount());
			}
		};
		
		LeOraSession session = instance.createSession(databaseName, schemaName, "", "pass", null);
		Nested nested = new Nested();
		nested.run(session);
	}
}
