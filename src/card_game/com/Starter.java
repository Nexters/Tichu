package card_game.com;

import java.util.Map.Entry;

public class Starter {

	/**
	 * 게임실행!!
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();

		// 순위 정보
		int rankCnt = 1;

		// 플레이어 생성
		Player p1 = new PlayerAI("player_01");
		Player p2 = new PlayerAI("player_02");
		Player p3 = new PlayerAI("player_03");
		Player p4 = new PlayerAI("player_04");

		// 플레이어 등록
		game.addPlayer(p1);
		game.addPlayer(p2);
		game.addPlayer(p3);
		game.addPlayer(p4);

		// 플레이어 목록
		for (Entry<Integer, Player> e : game.players.entrySet()) {
			Player p = e.getValue();
			game.playerSet.add(p);
		}

		// 카드를 돌린다. 8장
		for (Player p : game.playerSet) {
			p.initCards(game.cardFactory, 8);
		}

		// 라지 티츄 선택기회

		// 나머지 카드 돌리기 6장
		for (Player p : game.playerSet) {
			p.initCards(game.cardFactory, 6);
		}

		// 카드 확인
		// game.printEveryPlayersCards(game.playerSet);

		// 1을 갖고있는 사람이 선을 잡는다.
		Player firstPlayer = game.getHadMahJongPlayer(game.playerSet);
		game.turn = firstPlayer.playSeq;
		System.out.format("\nfirst player : %s ", firstPlayer.playerName);

		// 카드셋 생성
		for (Player p : game.playerSet) {
			p.generateCardSet();
		}

		// 한명씩 돌아가면서 게임 진행
		// TODO 게임종료조건은 임시!
		while (rankCnt < 4) {
			Integer curruntTurn = game.turn % 4;
			// 증가
			game.turn++;
			Player p = game.players.get(curruntTurn);

			// 마지막으로 낸사람이 자기 자신이면
			if (p.equals(game.lastPutPlayer)) {
				// 덱에 있는 카드를 가져가고
				p.takenCards.addAll(game.onDeskCards);

				game.initOnDeskCards();

			}

			// 카드를 내기 전에 종료 여부를 확인한다.
			if (p.onHandDeck.size() == 0) {
				// 순위를 입력한다.
				if (game.playerRanking.containsValue(p)) {
					continue;
				} else {
					game.playerRanking.put(rankCnt++, p);
				}

				// 모두 다 끝났으면?
				// 이후 로직은 스킵
				continue;
			}

			if (p instanceof PlayerAI) {
				System.out.format("\n %s turn ", p.playerName);
			} else {
				System.out.println("\n\n my turn!");
				System.out
						.println("##########################################################################################################");
				System.out.format("\n on desk \t ");
				game.printStatus();
				System.out
						.println("\n\n##########################################################################################################");
			}

			// 카드를 낸다.
			p.putCard(game);

			// 상황 출력
			game.printStatus();
		}

		// 꼴찌를 넣는다.
		for (Player p : game.playerSet) {
			if (!game.playerRanking.containsValue(p)) {
				game.playerRanking.put(rankCnt++, p);
			}
		}

		//
		System.out.println(game.turn);
		System.out.println("game.playerRanking");
		for (Entry<Integer, Player> e : game.playerRanking.entrySet()) {
			System.out.println(e.getKey());
			System.out.println(e.getValue().playerName);
		}

	}

}
