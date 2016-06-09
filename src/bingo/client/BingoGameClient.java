package bingo.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import bingo.client.ui.*;
import bingo.data.Data;
import bingo.data.GameRoom;
import bingo.data.User;

public class BingoGameClient extends JFrame implements Runnable {

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	// private LoginUI login;
	private Socket socket;
	public Data data;// 서버로부터 돌아오는 데이터, 이걸 GUI에 반영해야함, 클라객체에서
	private User now_user;
	private GameLobbyUI ui2;
	private GameRoomUI roomUI;
	private HashMap<String, GameRoom> roomList;
	private Data shareData;
	private String makeHost_id;

	public static void main(String[] args) {
		// new LoginUI();//GUI에서 로그인을 해야지 소켓이 생성되게 하려면 시작점이 LOGIN GUI
		new LoginUI();

	}

	/**
	 * 생성자의 역할 소켓생성
	 */
	public BingoGameClient() {

		try {
			socket = new Socket("localhost", 7777);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			Thread t = new Thread(this);
			t.start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "서버를 켜주세요");// GUI에서 하는게 더 나을 듯
			// e.printStackTrace();

		}
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}

	/**
	 * 리스너스레드역할 서버의 응답을 받는다.
	 */
	@Override
	public void run() {

		boolean exit = false;
		while (!exit) {
			try {
				data = (Data) ois.readObject();// 서버스레드로부터 응답 받음
				switch (data.getCommand()) {
				case Data.LOGIN: // LoginUI 입장,로그인 버튼 이벤트에서
					now_user = data.getUser();// 현접자
					// 전체리스트도 가져옴(로그인GUI에서 접속시마다 추가됨)
					ArrayList<User> RoomuserList = data.getUserList();// 여기서
					GameLobbyUI.getInstance();

					// 접속자 목록 추가
					GameLobbyUI.dlm.removeAllElements();// 누구 들어올 때마다 싹지운다
					for (User user : RoomuserList) {// 리스트 돌려서 재출력
						GameLobbyUI.dlm.addElement(user);
					}

					// 테이블 방목록 추가
					roomList = data.getRoomList();
					for (Entry<String, GameRoom> entry : roomList.entrySet()) {
						String hostIndex = entry.getKey();
						GameRoom room = entry.getValue();
						String row[] = { room.getRoomID(), room.getTitle(), room.getTheme(),
								Integer.toString(room.getMaxUserNum()) };
						GameLobbyUI.tm.addRow(row);
					}
					GameLobbyUI.lblNewLabel_2.setText("접속인원 :" + RoomuserList.size() + " 명");

					break;
				case Data.EXIT: // 로비에서 종료 시 접속자 갱신

					User exit_user = data.getUser();// 현재접속자정보(본인)
					GameLobbyUI.dlm.removeElement(exit_user);// 리스트에서 빼준다
					ArrayList<User> new_userList = data.getUserList();

					GameLobbyUI.dlm.removeAllElements();// 누구 들어올 때마다 싹지운다
					for (User user : new_userList) {// 리스트 돌려서 재출력
						GameLobbyUI.dlm.addElement(user);
					}

					break;
				case Data.MAKE_ROOM:// makeRoom에서 방 만들 때
					shareData = data;
					GameRoom gameRoom = data.getGameRoom();
					HashMap<String, GameRoom> roomList = data.getRoomList();

					// System.out.println("받아온 방장 아이디" + data.getUser());
					// System.out.println("받아온 방장의 권한" +
					// data.getUser().getPrivilege());
					// System.out.println("지금 나는 누구인가 : " +
					// GameLobbyUI.getInstance().getUser());

					// 방장만 실행
					User host = GameLobbyUI.getInstance().getUser();
					if (host.getId().equals(data.getUser().getId())) {// 방장만 실행
						System.out.println("방장만 실행");
						String id = host.getId();// 로그인 UI에서
						String state = host.getState();// 로비 GUI에서
						String[] rowData = { "-", id, state, "-" };
						GameRoomUI.tm.addRow(rowData);
						GameRoomUI.tableCellAlign();
						GameLobbyUI.getInstance().setVisible(false);

						GameRoomUI.getInstance().setGameTitle("[방제목 : " + gameRoom.getTitle() + "]");
						GameRoomUI.getInstance().setGameUser("[방장 : " + data.getUser().getId() + "]");
						// GameLobbyUI.tableCellAlign();//셀 가운데 갱신
					}

					// if (host.getId().equals(data.getUser().getId())) {
					// 최초 게임로비 테이블 추가
					GameLobbyUI.tm.setRowCount(0);
					for (Entry<String, GameRoom> entry : roomList.entrySet()) {
						String hostIndex = entry.getKey();
						GameRoom room = entry.getValue();
						String row[] = { room.getRoomID(), room.getTitle(), room.getTheme(),
								Integer.toString(room.getMaxUserNum()) };
						GameLobbyUI.tm.addRow(row);
					}
					// }

					makeHost_id = data.getUser().getId();
					// GUI
					break;
				case Data.JOIN:// 게임방참여
					/*
					 * 테이블GUI 인원갱신 GameRoom객체 업데이트 기존 <<방인원>>에게 갱신, 참여한 사람이 방
					 * 보이도록 room 싱글톤 호출
					 */
					GameRoom joinRoom = data.getGameRoom();// 나 혹은 누군가가 참여하려는 방
					User joiner = data.getUser();// 실제 방에 조인한 사람

					String now_user_id = GameLobbyUI.getInstance().getUser().getId();
					HashMap<String, User> userMap = data.getGameRoom().getUserList();
					// 창보이기 (근데 브로드캐스팅 때 조인한 사람만 보이도록 조건줄것)
					boolean check = joiner.getId().equals(now_user_id);// 조인신청
					// 조이너 게임룸 테이블
					if (check) {// 현접자가 조인했을 경우에만 실행
						GameRoomUI.getInstance().setData(shareData);// FIXME 문제가
																	// 생길 수도 있음
																	// , 이 경우
																	// 따로줘야 한다.
						GameRoomUI.getInstance();// 조인한 사람만 게임창보이기
						// GameRoomUI.getInstance().setVisible(true);
						// GameRoomUI.getInstance().setBm(this);
						// 테이블 갱신

						// Object[] rowData = {"-",now_user_id,"준비중","빙고개수"};
						// GameRoomUI.tm.addRow(rowData);
						// GameRoomUI.tableCellAlign();

						GameLobbyUI.getInstance().setVisible(false);
						// GameLobbyUI.getInstance().dispose();
						GameRoomUI.getInstance().setGameTitle("[방제목 : " + joinRoom.getTitle() + "]");
						GameRoomUI.getInstance().setGameUser("[아이디 : " + joiner.getId() + "]");

						// 조이너의 게임창 테이블 갱신
						GameRoomUI.tm.setRowCount(0);
						for (Entry<String, User> entry : userMap.entrySet()) {
							String key = entry.getKey(); // 유저 아이디
							User user = entry.getValue();
							String row[] = { "-", user.getId(), user.getState(), "-" };
							GameRoomUI.tm.addRow(row);
							System.out.println("테이블추가중");
						}
						GameLobbyUI.getInstance().setVisible(false);

					}
					// 방장 게임룸 테이블
					else if (makeHost_id.equals(data.getUser().getId())) {
						System.out.println(userMap);
						// 방장의 게임테이블
						GameRoomUI.tm.setRowCount(0);//
						for (Entry<String, User> entry : userMap.entrySet()) {
							String key = entry.getKey(); // 유저 아이디
							User user = entry.getValue();
							String row[] = { "-", user.getId(), user.getState(), "-" };
							GameRoomUI.tm.addRow(row);
							System.out.println("테이블추가중");
						}
						GameLobbyUI.getInstance().setVisible(false);
					}
					
					break;
				case Data.OUT:// 게임룸에서 나갈때 목록 지워야지않아?
					/*
					 * 딱히 해줄 것 있나? 테이블 GUI삭제는 언제해줘야할까? 싹다 삭제하고 다시 생성하는게 좋을거 같은데
					 * 또 다른 누군가 게임방 참여할때? 그러면 참여전까지는 갱신이 안되는데 근데 갱신할 필요가 없긴한듯
					 */
					data.getRoomList();// make 시 갱신하는 방목록
					data.getGameRoom();// 나가기 이벤트가 일어난 방

					break;
				case Data.CHAT_MESSAGE:// 게임방에서 채팅 시
					break;
				case Data.GAME_READY:// 게임방에서 게임 준비버튼 눌러서 서버에서 처리한 결과
					break;
				case Data.GAME_START:// 게임시작
					break;
				case Data.SEND_BINGO_DATA:// 게임진행결과
					break;
				case Data.SEND_WINNING_RESULT:// 게임승패
					break;

				default:
					break;
				}

			} catch (ClassNotFoundException | IOException e) {
				exit = true;
				System.out.println("서버가 종료되었습니다.");
			}
		}
	}

	public void sendData(Data data) {
		try {
			oos.writeObject(data);
			oos.reset();// 리셋은 보내고선 해야함
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	// public void actionPerformed(ActionEvent e) {
	// if (e.getSource() == btn_1) {
	// System.out.println("연결되었다!");
	// }
	//
	// }
}
