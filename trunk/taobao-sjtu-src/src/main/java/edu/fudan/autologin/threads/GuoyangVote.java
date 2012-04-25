package edu.fudan.autologin.threads;

import java.util.concurrent.CountDownLatch;

import org.apache.http.impl.client.DefaultHttpClient;

import edu.fudan.autologin.main.impl.Guoyang;

public class GuoyangVote implements Runnable {
	private CountDownLatch signal;
	
	public GuoyangVote(CountDownLatch signal){
		this.signal = signal;
	}
	public void run() {
		// TODO Auto-generated method stub
		Guoyang test = new Guoyang();
		test.execute();
		
		signal.countDown();
	}

}
