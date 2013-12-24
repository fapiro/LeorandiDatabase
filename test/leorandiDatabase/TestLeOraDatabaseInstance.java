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

public class TestLeOraDatabaseInstance {
	final static String databaseName = "LeORAndi_MEM_DB";
	private static LeOraDatabaseInstance instance;
	
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
	public void test1() {
		try {
			instance.createSession("SOMEONE_DB", "HR", "", "TEST_PASSWORD", new LeOraUserRunnable());
		} catch (SessionCreationFailedException e) {
		} catch (NotFoundDatabaseException e) {
		} catch (NotFoundUserException e) {
		} catch (WrongPasswordException e) {
		}
	}

	@Test(expected = SessionCreationFailedException.class)
	public void test2() {
		try {
			instance.createSession(databaseName, "HR", "", "<WhichPassword?>", new LeOraUserRunnable());
		} catch (SessionCreationFailedException e) {
		} catch (NotFoundException e) {
		} catch (WrongPasswordException e) {
		}
	}
	
	public void p(String s){
		System.out.println(s);
	}
}
