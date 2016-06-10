package bingo.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import bingo.client.ui.GameLobbyUI;
import bingo.client.ui.GameRoomUI;
import bingo.data.Data;
import bingo.data.GameInfo;
import bingo.data.GameRoom;
import bingo.data.User;

public class BingoGameServerThread implements Runnable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean exit;
	private BingoGameServer server;
	private Socket client;
	static ArrayList<User> connedUser = new ArrayList<>(); // 서버에 접속된
															// 클라이언트, 각
															// 클라이언트의
									//						// ObjectOutputStream이
															// 저정되어 있음
	static HashMap<String, GameRoom> gameRoomList = new HashMap<>();
	private Data data;
	private User user;
	private int index;
	private User host_user;
	private User host_user_join;

	/**
	 * 스레드가 조작해주는 서버GUI는 하나지만 클라이언트마다 다른 서버 스레드 이다 !!!!!!!!!!!!!!!!!!!!!!!!!!
	 * 1:1 관계이다!!!!!!!!!!!!! 공용자원으로 쓰려면 static 해줘야 한다
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * 
	 * 클라이언트는 GUI도 여러개이고 스레드도 여러개이다. 한마디로 클라수만큼 각각 가진다.
	 * 
	 * @param parent
	 * @param client
	 */
	public BingoGameServerThread(BingoGameServer parent, Socket client) {
		this.server = parent;
		this.client = client;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!exit) {
			try {
				data = (Data) ois.readObject();
				switch (data.getCommand()) {
				case Data.LOGIN: /* LoginUI 입장,로그인 버튼 이벤트에서 */
					user = data.getUser();// 현재 클라이언트의 접속자
					user.setOos(oos);// 브로드캐스팅에서 꺼내서 쓰기 위해 oss를 저장

					connedUser.add(user);// connedUser가 static 아니면 저장 안되는 이유는???
					index = connedUser.indexOf(user);// 신의한수 from 종찬

					data.setUserList(connedUser);// 데이터에도 저장, 한명분의 데이터임
					data.setRoomList(gameRoomList);// 로그인 시 로비의 게임목록 불어온다

					// 서버접속자목록 갱신
					server.dlm.removeAllElements();// 한번싹다지운다
					for (User sever_user : connedUser) {
						server.dlm.addElement(sever_user);// 다시추가
					}
					this.broadCasting();

					break;
				case Data.EXIT: /* 로비GUI에서 종료시 */

					User exit_user = data.getUser();// 나간 사용자, 로비를 거치면서 오염됨
					// User exit_user = connedUser.get(index);
					System.out.println("나간사람" + exit_user);
					System.out.println("============");

					System.out.println("삭제전" + connedUser);
					// FIXME 객체의 비교는 매우 어려운것 같다. 결국 못하고 3시간만에 id로 비교했다.
					for (int i = 0; i < connedUser.size(); i++) {
						if (exit_user.getId().equals(connedUser.get(i).getId())) {
							connedUser.remove(i);// 현접자나가면지움
						}

					}

					System.out.println("삭제후" + connedUser);

					System.out.println("============");

					data.setUserList(connedUser);// 갱신된거 data에 저장
					/*
					 * System.out.println("모델삭제전" + server.dlm);
					 * 
					 * server.dlm.removeAllElements();
					 * System.out.println("모델삭제후" + server.dlm);
					 * System.out.println("============");
					 * 
					 * // 서버접속자목록 갱신 for (User user2 : connedUser) {
					 * server.dlm.addElement(user2);// 다시갱신
					 * System.out.println(user2); } System.out.println("모델갱신" +
					 * server.dlm); // oos.writeObject(data);
					 */
					server.updateUserList(connedUser);
					this.broadCasting();
					break;

				case Data.MAKE_ROOM:/* makeRoomUI에서 보낸 데이터 처리 */
					GameRoom room = data.getGameRoom();// 만들자고 한 room, 유저객체에도

					room.setNowUserNum(1);
					host_user = connedUser.get(index);
					// User host = GameLobbyUI.getInstance().getUser();//이렇게쓰면
					// 창이 열려부러ㅠㅠ
					host_user.setRoom(room);// 만든 방 정보 추가(방마다 번호 등 다름)
					
					host_user.setPrivilege(User.HOST_PRIVILEGE);// 방장권한부여
					host_user.setState(User.READY);
					
					room.addUser(host_user);
					
					gameRoomList.put(room.getRoomID(), room);// 방정보를
																// 서버해시맵에
					System.out.println("만들기 맵 :" + gameRoomList); // 이게 저장이
																	// 안된다고?

					// 포장
					data.setCommand(Data.MAKE_ROOM);
					data.setRoomList(gameRoomList);// 데이터에도 저장해서 방송(리스너에서 로비테이블
													// 생성 위해)
					data.setUser(host_user);

					this.broadCasting();
					break;

				case Data.JOIN:/* 로비GUI에서 행을 클릭하면 */
					// User user = data.getUser().getRoom();
					// 룸로비에서 클릭한 룸ID / 추가된 인원이 들어있음(방장은 안들어있음)
					GameRoom selRoom = data.getGameRoom();// 로비GUI에서 보내준 데이터(id만 들어있음)
					String roomID = selRoom.getRoomID();

					HashMap<String, User> userMap = selRoom.getUserList(); // user
					System.out.println("들어갔니? :"+userMap);//참여자 아이디가 나와야함
					User user = userMap.get(connedUser.get(index).getId());// 참여자뽑음 , 못뽑고 지랄 

					System.out.println("============="+userMap);
					System.out.println("(ST)조인신청한 방 아이디  : " + roomID);
					
					GameRoom foundRoom = gameRoomList.get(roomID);//make시 저장한 방목록에서 해당 아이디방 찾음
					foundRoom.addUser(user);//만든 방에 저장(조인 시 테이블 만드는 데이터로 쓰려고..)
					
					User add_user = foundRoom.getUserList().get(user.getId());//제대로 들어갔나 확인
					System.out.println("들어온애 저장변수 옮겨서 재확인? :"+add_user);

					//방장아이디 찾기 
					//TODO 
					for (Entry<String, User> entry : foundRoom.getUserList().entrySet()) {
						String key = entry.getKey(); //유저 아이디 
						if (entry.getValue().getPrivilege() == User.HOST_PRIVILEGE) {
							host_user_join = entry.getValue();
							
						}
					}
					
					User ho_user2 = foundRoom.getUserList().get(host_user_join.getId());//제대로 들어갔나 확인
					
					System.out.println("참가자 :"+add_user);
					System.out.println("방주인1:"+host_user_join);
					System.out.println("방주인2:"+ho_user2);
					
					foundRoom.setHostID(host_user_join.getId());
					
					int num = 1;// 방장포함,최소1
					foundRoom.setNowUserNum(num++);// 방금입장

					user.setPrivilege(User.NORMAL_PRIVILEGE);// 참여자권한 부여
					user.setRoom(foundRoom);

					data.setUser(user);// 참여자저장
					data.setGameRoom(foundRoom);// 참여한 방을 데이터로 저장해서 전달
					// data.setRoomList(gameRoomList);//이거 꼭 해줘야하려나

					this.broadCasting();
					break;
				case Data.OUT:// 나가기버튼 눌렀을 때 실행

					// User killer = data.getUser(); //방장
					GameRoom outRoom = data.getGameRoom();
					// outRoom.setNowUserNum(nowUserNum);
					// HashMap<String, User> list = deleteRoom.getUserList();
					//
					// for (Entry<String, User> entry : list.entrySet()){
					// String key = entry.getKey();
					// User user = entry.getValue();
					// if (list == null) {
					// gameRoomList.remove(deleteRoom.getRoomID());
					// }
					// }
					data.setGameRoom(outRoom);

					this.broadCasting();
					break;
				case Data.CHAT_MESSAGE:
					this.broadCasting();
					break;
				case Data.GAME_READY:
					//방장이 아닌 경우 조인 시 리스너에서 담은 데이터
					//게임인포_ bingoKeywords 담음(버튼텍스트 2차원 배열)

					GameInfo info = data.getGameInfo();
					//현재 게임
					User now_user = info.getUser();
					now_user.setState(User.READY);//준비//FIXME null 
					data.setUser(now_user);//현재사용자
					
					this.broadCasting();
					break;
				case Data.GAME_START:
					break;
				case Data.SEND_WINNING_RESULT:
					break;
				case Data.SEND_BINGO_DATA: {
//					sendDataRoommate(data.getGameRoom().getRoomID());
				}
					break;
				default : 
					break;
				}// switch

			} catch (IOException e) {
				server.printEventLog(client.getPort() + "번 client 종료");
				exit = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} // while
	}// run

	/**
	 * 같은 방에 있는 유저에게 Data객체 전송
	 */
	public void sendDataRoommate(String roomID) {

	}

	/**
	 * 모든 유저에게 Data객체 전송
	 */
	public void broadCasting() {
		// 현재 접속된 모든 User객체에게 보낼 oos를 저장
		try {
			for (User user : connedUser) {
				System.out.println(user.getOos());
				user.getOos().writeObject(data);// 각 유저에게 oos 쏜다. 로그인 시에 oos를
				user.getOos().reset();
				// 저장해둠
				System.out.println(client.getPort() + "의 스레드 방송중");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
