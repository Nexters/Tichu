package tichu.com;

import java.util.ArrayList;
import java.util.List;

public class Game {
	// 목표점수
	public int score;

	// 플레이어 목록
	public List<Player> players;

	// 플레이어 수. 0부터 시작하기 때문에 3이 최대다.
	public int playerCount = 0;

	// 준비상태
	public boolean isReady = false;

	public Game(int score) {
		this.score = score;
		this.players= new ArrayList<>();  
	}

	/**
	 * 게임 실행
	 */
	public void run() {
		Round round;

		do {
			// 새로운 라운드 생성
			round = new Round(players);

			// 라운드 시작
			start(round);

			// TODO 종료조건은 임시다.
			// 점수를 만족할 때 까지 계속한다.
		} while (gameIsEnd(round));
	}

	/**
	 * 라운드 시작
	 * 
	 * @param round
	 */
	private void start(Round round) {
		round.run();
	}

	/**
	 * 게임종료체크
	 * 
	 * @param round
	 * @return
	 */
	private boolean gameIsEnd(Round round) {
		return false;
	}

	/**
	 * 플레이어 등록
	 * 
	 * @param player
	 *            등록할 플레이어
	 * @return 성공여부
	 */
	public boolean addPlayer(Player player) {
		// 0부터 시작하기 때문에 playerCount는 3이 최대다. (최대 게임 가능 인원수 4명)
		if (playerCount < 4) {
			int playSeq = playerCount++;
			// 순서를 플레이어에게 입력하고
			player.setPlaySeq(playSeq);
			// 플레이어를 등록하고
			players.add(player);
			// 플레이어가 4명이 가득 차면 준비상태를 준비완료true로 바꾼다.
			// 0,1,2,3이기 때문에 playSeq가 3일때를 체크한다.
			if (playSeq == 3) {
				isReady = true;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 게임실행!
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		//게임방 생성
		Game game = new Game(100);

		// 플레이어 생성
		Player p1 = new AI("player_01");
		Player p2 = new AI("player_02");
		Player p3 = new AI("player_03");
		Player p4 = new AI("player_04");
		
		// 플레이어 등록
		game.addPlayer(p1);
		game.addPlayer(p2);
		game.addPlayer(p3);
		game.addPlayer(p4);

		//게임 시작
		game.run();
		
	}
}
