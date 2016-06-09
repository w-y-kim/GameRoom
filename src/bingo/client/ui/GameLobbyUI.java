package bingo.client.ui;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.sun.media.sound.DLSModulator;

import bingo.client.BingoGameClient;
import bingo.data.Data;
import bingo.data.GameRoom;
import bingo.data.User;

import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class GameLobbyUI extends JFrame implements ActionListener {
	public JFrame frame;
	private JPanel panel;
	private JLabel lblNewLabel_1;
	private JList list;
	public static JLabel lblNewLabel_2;
	public JLabel idlbl;
	private JPanel panel_1;
	private GroupLayout groupLayout;
	private JButton btnNewButton_1;
	private JButton btnNewButton;
	private JLabel lblNewLabel;
	public static JTable table;
	private MakeRoomUI makeRoom;
	private static GameLobbyUI ui = new GameLobbyUI();
	// Object[][] obj;
	public static DefaultTableModel tm;

	public BingoGameClient bm;
	private User user;//로그인 UI 에서 접속 시 입력받음

	public void setBm(BingoGameClient bm) {
		this.bm = bm;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static DefaultListModel dlm;// 클라에서 클래스 변수로 접근해서 사용

	static HashMap<String, Data> hm = new HashMap<String, Data>();// 로비접속자들의 데이터를 임시저장
	// private Data data;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private GameLobbyUI() {

		btnNewButton_1.setFocusable(false);
		btnNewButton_1.setBackground(Color.WHITE);
		btnNewButton_1.setForeground(Color.BLACK);
		btnNewButton.setFocusable(false);
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setForeground(Color.BLACK);

		btnNewButton.addActionListener(this);
		btnNewButton_1.addActionListener(this);

		table.removeColumn(table.getColumnModel().getColumn(0));// 아이디컬럼 안보이게 하기

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {// 조인
				super.mouseClicked(e);
				int a = JOptionPane.showConfirmDialog(null, "해당 게임방에 입장하시겠습니까?", "Click Yes or No:",
						JOptionPane.YES_NO_OPTION);
				if (a == 0) {
					// bm.data.getRoomList();
					
					Data data = new Data(Data.JOIN); // 명령마다 data를 새로 생성하는 것이 좋을
														// 듯, 불필요 정보도 담기면 느릴듯

					int rowNum = table.getSelectedRow();
					String roomId = (String) tm.getValueAt(rowNum, 0);
					String title = (String) tm.getValueAt(rowNum, 1);
					String theme = (String) tm.getValueAt(rowNum, 2);
					int maxUserNum = Integer.parseInt((String) tm.getValueAt(rowNum, 3));
					System.out.println("룸 ID : "+roomId);

					GameRoom selRoom = new GameRoom(roomId,"","",0);//어차피 서버에서 찾음, 여기서는 클릭한 아이디만 넘김
//					selRoom.addUser(user);//참여자정보저장 , 게임룸의 2종류 변수에 저장해주는 역할
					
					data.setGameRoom(selRoom);
					selRoom.addUser(user);//현재 참여자 정보 더해줌 
					
//					data.setUser(user);//사용자 정보 보냄
//					data.setGameRoom(joinRoom);
					// GameRoomUI.getInstance();//기존 게임방입장, 서버에서 검색해서 리스너에서
					// 해야할듯?

					bm.sendData(data);// 클릭한 방번호가 조인으로 전달됨
					
				}
			}

		});
		System.out.println("로비GUI 입장");
	}

	public static void tableCellAlign(){
		DefaultTableCellRenderer Renderer = new DefaultTableCellRenderer();
		Renderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = GameLobbyUI.table.getColumnModel();
		// DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
		for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(Renderer);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnNewButton) {// 종료
			// Data data = bm.data;//이렇게하면 로비가 하나밖에 없으니 마지막들어온애로갱신
			// data.setCommand(Data.EXIT);
			Data data = new Data(Data.EXIT);
			data.setUser(user);//데이터에 현재 로비의 사용자 정보 보냄 

			// data.getUserList().remove(data.getUser());// 스레드 지우는게 정상
			bm.sendData(data);

			System.exit(JFrame.EXIT_ON_CLOSE);
		} else if (e.getSource() == btnNewButton_1) {// 방만들기

			// synchronized(this){//싱글톤이래서 필요할지도?
			new MakeRoomUI(bm);
			// }

		}
	}

	// public static GameLobbyUI getInstance(BingoGameClient bm2) {
	// bm = bm2;
	// return ui;
	//
	// }

	public static GameLobbyUI getInstance() {
		return ui;

	}

	
	
	{// 초기화블록_생성자보다 먼저 실행
		frame = new JFrame();
		frame.setTitle("게임로비");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 461, 314);

		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);

		lblNewLabel_1 = new JLabel("    접속자목록");

		dlm = new DefaultListModel();
		list = new JList(dlm);

		lblNewLabel_2 = new JLabel("접속인원 : 00명");

		panel_1 = new JPanel();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(UIManager.getBorder("ComboBox.editorBorder"));

		idlbl = new JLabel("ID : 000");
		groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout
				.setHorizontalGroup(
						groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup()
								.addContainerGap().addGroup(groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addComponent(panel, GroupLayout.DEFAULT_SIZE, 421,
												Short.MAX_VALUE)
										.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
												.createParallelGroup(Alignment.TRAILING, false)
												.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
														311, Short.MAX_VALUE))
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addGroup(groupLayout.createSequentialGroup().addGap(8)
																.addGroup(groupLayout
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(list, GroupLayout.DEFAULT_SIZE,
																				102, Short.MAX_VALUE)
																		.addComponent(lblNewLabel_1,
																				GroupLayout.DEFAULT_SIZE, 102,
																				Short.MAX_VALUE)))
														.addGroup(groupLayout.createSequentialGroup()
																.addPreferredGap(ComponentPlacement.UNRELATED)
																.addGroup(groupLayout
																		.createParallelGroup(Alignment.LEADING)
																		.addComponent(idlbl).addComponent(lblNewLabel_2,
																				GroupLayout.PREFERRED_SIZE, 90,
																				GroupLayout.PREFERRED_SIZE))))))
								.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel_1)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(list,
												GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE))
						.addGap(10)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 36,
										GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel_2)
										.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(idlbl)))
						.addContainerGap(21, Short.MAX_VALUE)));

		table = new JTable();
		// obj = new Object[][] {};
		// 데이터는 클라이언트 리스너 makeROOM 커맨드에서 넣어준다.
		tm = new DefaultTableModel(null, new String[] { "아이디", "방제목", "주제", "인원" });// makeRoomUI가
		
		
		
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(tm);
		table.getColumnModel().getColumn(0).setPreferredWidth(132);
		scrollPane.setViewportView(table);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnNewButton_1 = new JButton("방만들기");
		panel_1.add(btnNewButton_1);

		btnNewButton = new JButton("종료하기");
		panel_1.add(btnNewButton);

		lblNewLabel = new JLabel("빙고게임");
		lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 30));
		lblNewLabel.setForeground(Color.WHITE);
		panel.add(lblNewLabel);
		frame.getContentPane().setLayout(groupLayout);
		frame.setVisible(true);
	}
}
