package bingo.client.ui;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import bingo.client.BingoGameClient;
import bingo.data.Data;
import bingo.data.GameRoom;
import bingo.data.User;

import javax.swing.JComboBox;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class MakeRoomUI extends JFrame implements ActionListener {
	private JTextField textField;
	private JTextField textField_1;
	JFrame frame;
	private JButton 만들기;
	private JButton 취소;
	private JComboBox comboBox;
	BingoGameClient bm;
	// public static void main(String[] args) {
	// new MakeRoomUI();
	// }
	private Data data;
	

	public MakeRoomUI(BingoGameClient bm) {
		this.bm = bm;
		this.data = bm.data;// 동일 데이터는 아님, 복사했을 뿐, 거기에 방정보도 추가저장하는 것임
		만들기.setFocusable(false);
		만들기.setBackground(Color.WHITE);
		만들기.setForeground(Color.BLACK);
		취소.setFocusable(false);
		취소.setBackground(Color.WHITE);
		취소.setForeground(Color.BLACK);

		취소.addActionListener(this);
		만들기.addActionListener(this);
		frame.setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == 취소) {// 취소

			// 일단 감추고 로비에서 재실행시 보이게 하자
			frame.dispose();
			clearALL();

		} else if (e.getSource() == 만들기) {// 만들기
			// GUI
			if (textField.getText().equals("") == false && textField_1.getText().equals("") == false) {
				frame.setVisible(true);//2번 만들 때 보이도록 
				//데이터생성 
				GameLobbyUI.getInstance().frame.setVisible(false);// 창감추기
				String id = data.getUser().getId() + Calendar.getInstance().getTimeInMillis();
				String name = textField.getText();
				String theme = textField_1.getText();
				String str = ((String) comboBox.getSelectedItem()).substring(0, 1);
				int max_User_number = Integer.parseInt(str);
				GameRoom room = new GameRoom(id, name, theme, max_User_number);

				//포장
//				data.setCommand(Data.MAKE_ROOM);//커맨드
				data = new Data(Data.MAKE_ROOM);
				User user = GameLobbyUI.getInstance().getUser();
				user.setRoom(room);
				
				user.getRoom().setHostID(user.getId());
				user.getRoom().setRoomID(id);//게임 레디에서 쓸 데이터 
				user.getRoom().setGamehost(user);
				
				data.setGameRoom(room);// 방정보 저장(서버에서 사용)
				data.setUser(user);
				
				// 게임방객체생성, 데이터에 저장해줘야함
				data.getGameRoom().setNowUserNum(1);//인원 변경 

				
				
				bm.sendData(data);
				
				bm.data = data; //여기서 생성한 데이터를 bm에 넣어줌
				
				GameRoomUI.getInstance().setBm(this.bm);// 데이터도 건내줌 
				GameRoomUI.getInstance().frame.setVisible(true);// 창감추기
//				this.frame.dispose();// clearALL();프레임버릴거니까 안써도 될 듯?
				frame.setVisible(false);//dispose 하면 2번째 실행 시 창정보가 없어짐
			} else
				JOptionPane.showMessageDialog(this, "정보를 입력해주세요");
		}
	}

	/**
	 * 사용자 입력값 지워주는 메소드
	 */
	public void clearALL() {
		textField.setText("");
		textField_1.setText("");
		comboBox.setSelectedIndex(0);
	}

	{// 초기화블록
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("방만들기");
		frame.setBounds(100, 100, 220, 200);
		JLabel lblNewLabel = new JLabel("방제목");

		JLabel lblNewLabel_1 = new JLabel("빙고주제");

		JLabel lblNewLabel_2 = new JLabel("참가인원");

		textField = new JTextField();
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);

		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "2명", "3명", "4명", "5명" }));

		만들기 = new JButton("만들기");

		취소 = new JButton("취소");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout
				.setHorizontalGroup(
						groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addContainerGap()
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel)
														.addPreferredGap(ComponentPlacement.RELATED, 11,
																Short.MAX_VALUE)
														.addComponent(textField, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGroup(groupLayout
														.createSequentialGroup().addComponent(lblNewLabel_1)
														.addPreferredGap(ComponentPlacement.RELATED, 11,
																Short.MAX_VALUE)
														.addComponent(textField_1, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addGroup(groupLayout
														.createSequentialGroup().addComponent(lblNewLabel_2)
														.addPreferredGap(ComponentPlacement.UNRELATED)
														.addComponent(comboBox, 0, 115, Short.MAX_VALUE))
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(만들기, GroupLayout.PREFERRED_SIZE, 75,
																GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, 34,
																Short.MAX_VALUE)
														.addComponent(취소, GroupLayout.PREFERRED_SIZE, 75,
																GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup().addGap(19)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel).addComponent(
						textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(18)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_1).addComponent(
						textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(18)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_2).addComponent(
						comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(만들기).addComponent(취소))
				.addContainerGap()));
		frame.getContentPane().setLayout(groupLayout);
	}

}
