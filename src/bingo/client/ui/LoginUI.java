package bingo.client.ui;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import bingo.client.BingoGameClient;
import bingo.data.Data;
import bingo.data.User;

public class LoginUI extends JFrame implements ActionListener {
	JTextField textField;
	JFrame frame;
	public JButton 로그인버튼;
	JLabel lblNewLabel;
	public JButton 입장버튼;
	public JButton button;
	GroupLayout groupLayout;
	GameLobbyUI lobby;
	public BingoGameClient bm;
	private Data data;//로그인에서 보내는 데이터 

	public LoginUI() {
		로그인버튼.setFocusable(false);
		로그인버튼.setBackground(Color.WHITE);
		로그인버튼.setForeground(Color.BLACK);
		입장버튼.setFocusable(false);
		입장버튼.setBackground(Color.WHITE);
		입장버튼.setForeground(Color.BLACK);
		button.setFocusable(false);
		button.setBackground(Color.WHITE);
		button.setForeground(Color.BLACK);

		button.addActionListener(this);
		입장버튼.addActionListener(this);
		로그인버튼.addActionListener(this);
		
		System.out.println("로그인GUI 로딩");
	}

	{// 초기화블록
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setType(Type.UTILITY);
		frame.setTitle("로그인창");
		로그인버튼 = new JButton("빙고게임 로그인");
		로그인버튼.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		textField = new JTextField();
		textField.setColumns(10);

		lblNewLabel = new JLabel("아이디");

		입장버튼 = new JButton("입장");

		button = new JButton("닫기");
		groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(로그인버튼, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(lblNewLabel).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE).addContainerGap())
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(입장버튼, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
						.addComponent(button, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(로그인버튼, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel))
						.addPreferredGap(ComponentPlacement.UNRELATED).addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(입장버튼).addComponent(button))
						.addContainerGap(34, Short.MAX_VALUE)));
		frame.setBounds(100, 100, 200, 200);
		frame.getContentPane().setLayout(groupLayout);
		frame.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {// 닫기
			System.exit(JFrame.EXIT_ON_CLOSE);
		} else if (e.getSource() == 입장버튼 && textField.getText().equals("") == false
				|| e.getSource() == 로그인버튼 && textField.getText().equals("") == false) {// 입장

			bm = new BingoGameClient();
//			System.out.println(bm.hashCode() + "로그인에서 bm값인데 이게 로비와 일치하나?");
			String id = textField.getText();
			User user = new User(id, User.NORMAL_PRIVILEGE);
			data = new Data(Data.LOGIN);
			data.setUser(user);// 자신의 정보 저장해 보냄
			System.out.println("로그인UI : " + data.getUser());
			bm.sendData(data);
			// bm.data = data; // FIXME bm.data는 서버를 거쳐서 돌아온 데이터이다.
			// 싱글턴
			// TODO 클라이언트 객체를 보냄

			// synchronized(this){//TODO 이거 해줘야하지 않을까?
			
			synchronized(this){
			GameLobbyUI.getInstance().idlbl.setText("ID : " + id);
			GameLobbyUI.getInstance().setBm(this.bm);//이렇게만 하면 user 정보가 제대로 가지 않고 계속 갱신되는 문제
			
			GameLobbyUI.getInstance().setUser(user);
			 }
			// this.frame.setVisible(false);//일단 감추자

			this.frame.dispose();

		} else
			JOptionPane.showMessageDialog(this, "존말할때 아이디 입력해라");
	}

}
