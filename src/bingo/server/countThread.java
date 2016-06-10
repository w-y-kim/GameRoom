package bingo.server;

import bingo.client.ui.GameRoomUI;

public class countThread implements Runnable {

	@Override
	public void run() {
		// for (int i = 1; i <= 10; i++) {
		// System.out.println(i);
		// }
		int i = 0;
		while (i != 11) {
			try {

				GameRoomUI.getInstance().getProgressBar().setValue(i);
				GameRoomUI.getInstance().getLabel().setText(10 - i + "초");
				// System.out.println("랜덤: "+Integer.toString((int)
				// (Math.random()*25)+1));
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public countThread() {

		Thread t = new Thread(this);
		// t.start();
	}

}
