package bingo.client.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import bingo.client.BingoGameClient;
import bingo.data.Data;
import bingo.data.GameInfo;
import bingo.server.countThread;

import java.awt.Color;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.CardLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;

/**
 * 싱글턴
 * 
 * @author user
 *
 */
public class GameRoomUI extends JFrame implements ActionListener {

	public static GameRoomUI ui = new GameRoomUI();
	private JTextField[][] txt = new JTextField[5][5];
	private JButton[][] btn = new JButton[5][5];
	private CardLayout layout;
	private JPanel 빙고패널;
	private JButton 완료;
	private JButton 나가기;
	private JPanel 입력카드_패널;
	private JPanel 출력카드_패널;
	JFrame frame;
	private BingoGameClient bm;
	private Data data;
	public static DefaultTableModel tm;
	private JLabel gameTitle, gameUser;
	private JProgressBar progressBar;
	private JLabel label;
	private GameInfo info = new GameInfo();
	private String[][] bingoKeywords = new String[5][5];

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void setGameTitle(String Title) {
		this.gameTitle.setText(Title);
	}

	public void setGameUser(String gameUser) {
		this.gameUser.setText(gameUser);
	}

	public BingoGameClient getBm() {
		return bm;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void setBm(BingoGameClient bm) {
		this.bm = bm;
	}

	public static GameRoomUI getInstance() {
		return ui;
	}

	private GameRoomUI() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				txt[i][j] = new JTextField();
				출력카드_패널.add(txt[i][j]);

				txt[i][j].setLayout(layout);
				// FIXME 테스트코드
				txt[i][j].setText(Integer.toString((int) (Math.random() * 25) + 1));
			}
		}
		빙고패널.add(출력카드_패널, "name_1035154256703222");
		출력카드_패널.setLayout(new GridLayout(5, 5, 2, 2));

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {

				btn[i][j] = new JButton();
				입력카드_패널.add(btn[i][j]);
				btn[i][j].setCursor(getCursor());

				btn[i][j].setFocusPainted(false);

				btn[i][j].setFocusable(false);

				btn[i][j].setLayout(layout);

				btn[i][j].addActionListener(this);
			}
		}
		빙고패널.add(입력카드_패널, "name_1035168885640309");
		입력카드_패널.setLayout(new GridLayout(5, 5, 3, 3));

		///

		완료.setFocusable(false);
		완료.setBackground(Color.WHITE);
		완료.setForeground(Color.BLACK);
		나가기.setFocusable(false);
		나가기.setBackground(Color.WHITE);
		나가기.setForeground(Color.BLACK);

		progressBar.setMinimum(0); // 진행바 최소값 설정
		progressBar.setMaximum(10); // 진행바 최대값 설정
		// progressBar.setStringPainted(true); // 진행사항 퍼센티지로 보여주기 설정
		progressBar.setForeground(Color.DARK_GRAY); // 진행바 색설정

		progressBar.setForeground(Color.BLACK);
		progressBar.setBorderPainted(true); // 경계선 표시 설정
		// progressBar.setIndeterminate(true); // 진행이 안될때 모습 표시
		progressBar.setStringPainted(true);

		// FIXME 테스트코드
		countThread ct = new countThread();
		Thread t = new Thread(ct);
		t.start();
	}

	public void cleartxt() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				txt[i][j].setText("");

			}
		}
	}

	public void startBtn() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				String str = txt[i][j].getText();
				btn[i][j].setText(str);
				btn[i][j].setBackground(Color.BLACK);
				btn[i][j].setForeground(Color.WHITE);

			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == 완료) {

			startBtn();// 필드 텍스트 버튼 복사
			cleartxt();// 필드 텍스트 지우기
			// layout.previous(빙고패널);
			layout.next(빙고패널);

			// 버튼 정보 추출
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					if (e.getSource() == btn[i][j]) {
						btn[i][j].setBackground(Color.YELLOW);
						btn[i][j].setForeground(Color.BLACK);
						// 버튼의 텍스트를 게임인포로 만들어 데이터에 담아 서버로 보냄
						String keyword = btn[i][j].getText();// 누른 버튼의 값
						bingoKeywords[i][j] = keyword;// 배열생성 이제 버튼ㅂㅂ
					}
				}
			} // for
			info.setBingoKeywords(bingoKeywords);

			// 최초 방을 열은 방장의 경우 data 생성필요
			if (data == null) {
				Data data = new Data(Data.GAME_READY);
			} else {
				Data data = this.data;
				data.setCommand(Data.GAME_READY);
			} // 유저정보정도는 들어있겠지머...make랑 join에서 보내줌, 특히 조인에서는 sharedata
			data.setGameInfo(info);

			bm.sendData(data);

			완료.setEnabled(false);// 버튼 불활성화

		} else if (e.getSource() == 나가기) {// 방장나가면 방 없어지도록 하자, 데이터는 make에서 오는거고
			this.frame.setVisible(false);

			// Data data = new Data(Data.OUT);

			Data data = bm.data; // makeRoomUI 만들기 액션에서 만든 데이터를 이어 받음 \
			// 방정보 / 방장정보 들어있음
			data.setCommand(Data.OUT);// 커맨드 갱신해주고
			// 나갔다는 정보 받아서 리스너에서 로비테이블 갱신해줘야함, 단 인원이 없을 때만 지우도록
			// 그러려면 리스너에서 추가로 join 시 인원 추가 정보를 받아야함
			bm.sendData(data);

			GameLobbyUI.getInstance().frame.setVisible(true);

		}

		// 빙고버튼
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (e.getSource() == btn[i][j]) {
					btn[i][j].setBackground(Color.YELLOW);
					btn[i][j].setForeground(Color.BLACK);

					// 버튼의 텍스트를 게임인포로 만들어 데이터에 담아 서버로 보냄
					String keyword = btn[i][j].getText();// 누른 버튼의 값
					bingoKeywords[i][j] = keyword;

					info.setBingoKeywords(bingoKeywords);

					data.setCommand(Data.SEND_BINGO_DATA);
					data.setGameInfo(info);

					bm.sendData(data);
				}
			}
		} // for
	}

	public static void tableCellAlign() {
		DefaultTableCellRenderer Renderer = new DefaultTableCellRenderer();
		Renderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = GameLobbyUI.table.getColumnModel();
		// DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
		for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(Renderer);
		}
	}

	{// 초기화 블록
		frame = new JFrame();
		frame.setTitle("게임방");
		frame.setBounds(100, 100, 709, 575);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.TRAILING);
		gameTitle = new JLabel("방제목 : ---");
		gameTitle.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 15));
		gameUser = new JLabel("(접속자 ID : ---)");
		gameUser.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 15));
		panel.setLayout(flowLayout);
		panel.add(gameTitle);
		panel.add(gameUser);

		JPanel 좌측패널 = new JPanel();

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.LIGHT_GRAY);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(좌측패널, GroupLayout.PREFERRED_SIZE, 342,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
										.addComponent(panel, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
								.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE).addComponent(
										좌측패널, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE))
						.addContainerGap()));

		tm = new DefaultTableModel(null, new String[] { "차례", "ID", "상태", "빙고" });
		JTable table = new JTable();
		table.setEnabled(false);
		table.setModel(tm);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.setCellSelectionEnabled(false);

		완료 = new JButton("준비완료");
		완료.addActionListener(this);

		나가기 = new JButton("나가기");
		나가기.addActionListener(this);
		JLabel lblNewLabel = new JLabel("빙고");
		lblNewLabel.setFont(new Font("나눔고딕", Font.BOLD, 15));

		label = new JLabel("00초");
		label.setForeground(new Color(178, 34, 34));
		label.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 54));

		JLabel lblId = new JLabel("ID");
		lblId.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 48));

		JLabel label_2 = new JLabel("  차례");
		label_2.setFont(new Font("나눔고딕", Font.BOLD, 15));

		JLabel label_1 = new JLabel("0빙고");
		label_1.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 41));

		JLabel label_3 = new JLabel("남았습니다.");
		label_3.setFont(new Font("나눔고딕", Font.BOLD, 20));

		JScrollPane scrollPane_2 = new JScrollPane(table);

		progressBar = new JProgressBar();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup().addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup().addContainerGap().addComponent(완료,
								GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_2.createSequentialGroup().addGap(49)
								.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addComponent(lblId)
										.addComponent(label_2, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_2.createSequentialGroup()
										.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
												.addComponent(나가기, GroupLayout.PREFERRED_SIZE,
														132, GroupLayout.PREFERRED_SIZE)
												.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 132,
														Short.MAX_VALUE))
										.addContainerGap())
								.addGroup(gl_panel_2.createSequentialGroup().addComponent(lblNewLabel).addGap(57))))
				.addGroup(gl_panel_2.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE).addContainerGap())
				.addGroup(gl_panel_2.createSequentialGroup().addGap(28).addComponent(label)
						.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
						.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				.addGroup(gl_panel_2.createSequentialGroup().addContainerGap()
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE).addContainerGap()));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup().addContainerGap()
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
								.addComponent(나가기, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
								.addComponent(완료, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
						.addGap(43)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE).addComponent(label_2)
								.addComponent(lblNewLabel))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
								.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblId, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
						.addGap(47)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE).addComponent(label_3)
								.addComponent(label))
						.addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		panel_2.setLayout(gl_panel_2);
		SpringLayout sl_좌측패널 = new SpringLayout();
		좌측패널.setLayout(sl_좌측패널);

		JScrollPane scrollPane = new JScrollPane();
		sl_좌측패널.putConstraint(SpringLayout.EAST, scrollPane, 332, SpringLayout.WEST, 좌측패널);
		좌측패널.add(scrollPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		sl_좌측패널.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, scrollPane_1);
		sl_좌측패널.putConstraint(SpringLayout.NORTH, scrollPane_1, 420, SpringLayout.NORTH, 좌측패널);
		sl_좌측패널.putConstraint(SpringLayout.WEST, scrollPane_1, 0, SpringLayout.WEST, scrollPane);

		JTextArea textArea_1 = new JTextArea();
		textArea_1.setEnabled(false);
		textArea_1.setEditable(false);
		scrollPane.setViewportView(textArea_1);
		sl_좌측패널.putConstraint(SpringLayout.SOUTH, scrollPane_1, -10, SpringLayout.SOUTH, 좌측패널);
		sl_좌측패널.putConstraint(SpringLayout.EAST, scrollPane_1, 332, SpringLayout.WEST, 좌측패널);
		좌측패널.add(scrollPane_1);

		JTextArea textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);

		빙고패널 = new JPanel();
		sl_좌측패널.putConstraint(SpringLayout.SOUTH, 빙고패널, -139, SpringLayout.SOUTH, 좌측패널);
		sl_좌측패널.putConstraint(SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, 빙고패널);
		sl_좌측패널.putConstraint(SpringLayout.NORTH, 빙고패널, 0, SpringLayout.NORTH, 좌측패널);
		sl_좌측패널.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, 빙고패널);
		sl_좌측패널.putConstraint(SpringLayout.WEST, 빙고패널, 0, SpringLayout.WEST, 좌측패널);
		빙고패널.setBackground(Color.LIGHT_GRAY);
		sl_좌측패널.putConstraint(SpringLayout.EAST, 빙고패널, 332, SpringLayout.WEST, 좌측패널);
		좌측패널.add(빙고패널);
		layout = new CardLayout(5, 5);
		//
		출력카드_패널 = new JPanel();

		//
		입력카드_패널 = new JPanel();
		////

		빙고패널.setLayout(layout);
		// JButton btnNewButton = new JButton("New button");
		// 입력카드_패널.add(btnNewButton);
		frame.getContentPane().setLayout(groupLayout);

		frame.setVisible(true);

	}
}
