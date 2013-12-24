package leorandiDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class LeOraSystemThreadsFactory implements ThreadFactory{
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true); // system threads are always daemons
		return thread;
	}
}

public class LeOraThreadsManager {
	private int maxSystemThreadsCount = 1;
	private ExecutorService systemExecutorService;
	
	public LeOraThreadsManager(int maxSystemThreadsCount){
		this.maxSystemThreadsCount = maxSystemThreadsCount;
		if(maxSystemThreadsCount == 1) {
			systemExecutorService = Executors.newSingleThreadExecutor(new LeOraSystemThreadsFactory());
		}else{
			systemExecutorService = Executors.newFixedThreadPool(maxSystemThreadsCount, new LeOraSystemThreadsFactory());
		}	
	}
	
	public void newSystemThread(Runnable runnable){
		systemExecutorService.execute(runnable);
	}
	
	public Thread newUserThread(Runnable runnable){
		return new Thread(runnable);
	}
	
	public int getMaxSystemThreadsCount(){
		return this.maxSystemThreadsCount;
	}
}
