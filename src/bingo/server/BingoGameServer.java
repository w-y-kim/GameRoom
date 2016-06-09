package bingo.server;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;

import bingo.data.User;

public class BingoGameServer implements Runnable {

	private JFrame frame;
	JTextArea textArea;
	private JList list;
	 DefaultListModel dlm ;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		 EventQueue.invokeLater(new Runnable() {
	         public void run() {
	            try {
	               BingoGameServer window = new BingoGameServer();
	               window.frame.setVisible(true);
	              
	               
	            } catch (Exception e) {
	               e.printStackTrace();
	            }
	         }
	      });


	}
	
	/* 스레드 - 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		try {
			ServerSocket ss = new ServerSocket(7777);
		
			while(true) {
			Socket client = ss.accept();
			
			this.printEventLog(client.getPort()+"번 client 접속");
			
			
			
			
			BingoGameServerThread bgt = new BingoGameServerThread(this,client);
			Thread t = new Thread(bgt);
			t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
				
	}

	/**
	 * Create the application.
	 */
	public BingoGameServer() {
		initialize();
		this.printEventLog("서버시동");
		
		Thread tt = new Thread(this);
		tt.start();
		
		
	}
	
	public void updateUserList(ArrayList<User> userList){
		Object a_userList [] = userList.toArray();
		list.setListData(a_userList);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setBounds(100, 100, 402, 314);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JLabel lblNewLabel = new JLabel("서버이벤트로그");

		JLabel lblNewLabel_1 = new JLabel("접속자목록");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JScrollPane scrollPane2 = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
						.addComponent(scrollPane))
					.addContainerGap())
		);
		
		dlm = new DefaultListModel();
		list = new JList (dlm);
		scrollPane2.setViewportView(list);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		frame.getContentPane().setLayout(groupLayout);
	}

//	public void printEventLog(String message) {
//		String logMessage = String.format("[%tY - %<tm, - %t<d %<tH:%tM:%<tS] %s%n", new Date(), message);
//		// < 는 옆의 파라미터 쓰겠다는 의미
//		textArea.append(logMessage);
//
//	}
	
	 public void printEventLog(String message){
	      String logMessage = String.format(" [%tY-%<tm-%<td %<tH:%<tM:%<tS] %s%n", new Date(), message);
	      textArea.append(logMessage);
	   }

	public void updateConnectionList(ArrayList<User> userList) {
	
		for (User user : userList) {
			user.getId();
		}
	}
}
