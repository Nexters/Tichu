package tichu.com.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;

import tichu.com.card.Card;
import tichu.com.card.CardFactory;
import tichu.com.card.CardSet;
import tichu.com.enums.Tichu;
import tichu.com.etc.Counter;
import tichu.com.etc.Util;
import tichu.com.player.Player;

public class Round {

	// 라운드에 사용되는 카드
	private CardFactory cardFactory = new CardFactory();

	// 플레이어 목록
	private List<Player> players;

	// 턴
	private int turn;

	// 마지막으로 카드를 낸 사람.
	private Player lastPutPlayer;

	// 게임판 위에 있는 카드
	private ConcurrentLinkedDeque<CardSet> onDeskCards;

	// 카운터
	private Counter counter = new Counter();

	// 등수 카운터
	private int rankCounter = 0;

	public Round(List<Player> players) {
		this.players = players;
		this.onDeskCards = new ConcurrentLinkedDeque<>();
	}

	/**
	 * 라운드 실행!!
	 */
	public void run() {

		dealCards(8); // 플레이어에게 8장의 카드를 나누어 준다.

		callLargeTichu(counter); // 플레이어가 라지티츄를 선택하게 한다.

		dealCards(6); // 플레이어에게 나머지 6장의 카드를 나누어준다.

		exchangeCard(counter); // 모든 플레이어가 카드를 교환한다. 카드교환 리스트에 담는다.

		generateCardSet(); // 카드셋을 생성 한다.

		selectStarter();// 시작 플레이어를 선택한다.

		do { // 반복로직 시작

			Player p = selectCurrentTurnPlayer(); // 이번 턴에 플레이할 사용자를 선택한다.

			if (p.equals(lastPutPlayer)) { // 마지막으로 낸 사람을 체크한다.
				p.collectCardSets(onDeskCards, players); // 현재 턴인 플레이어면 게임판의 카드를 가져온다.
			}

			do { // 사용자가 제대로 카드를 낼 때까지 반복.

				p.selectCards(this); // 플레이어가 낼 카드를 선택한다.

			} while (!p.validateAndPlayCardSet(this)); // 카드셋이 적합한지 검사하고, 유효하면
														// 카드를 낸다.

			checkPlayerEnds(p);

		} while (!roundIsEnd()); // 게임종료 체크?

		calculateScore(); // 라운드 종료시 점수계산.
	}

	/**
	 * 플레이어에게 카드를 나누어 준다.
	 */
	private void dealCards(int size) {
		// 한명씩
		for (Player p : players) {
			// 카드 8장을 받아와서
			List<Card> cards = cardFactory.getCard(size);

			// 플레이어가 카드를 가져간다.
			p.getCards(cards);
		}
	}

	/**
	 * 모든 플레이어들이 라지티츄 여부를 선택한다.
	 * 
	 * @param counter
	 */
	private void callLargeTichu(Counter counter) {

		for (Player p : players) {
			p.callLargeTichu(counter);
		}

		while (!counter.isReady()) {
			// TODO 잠시휴식. 이거 필요하나?!
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 모든 플레이어가 카드를 교환을 위해 카드교환 리스트에 카드를 추가한다.
	 */
	private void exchangeCard(Counter counter) {
		// 카드교환 리스트를 맵을 생성한다.
		Map<Player, List<Card>> exchangeCardListMap = new HashMap<>();

		for (Player p : players) {
			// 각 플레이어마다 카드교환 리스트에 카드를 추가한다.
			p.exchangeCard(players, exchangeCardListMap, counter);
		}

		while (!counter.isReady()) {
			// TODO 잠시휴식. 이거 필요하나?!
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		distributeCard(exchangeCardListMap); // 카드교환 리스트에 있는 카드를 나눈다.

	}

	/**
	 * 카드교환 리스트에 담겨져있는 카드를 플레이어마다 나누어준다.
	 * 
	 * @param exchangeCardListMap
	 */
	private void distributeCard(Map<Player, List<Card>> exchangeCardListMap) {

		for (Entry<Player, List<Card>> e : exchangeCardListMap.entrySet()) {
			Player p = e.getKey();
			p.getCards(e.getValue());
		}
	}

	/**
	 * 처음 시작할 플레이어를 선정한다. 1카드를 갖고있는 사람이 시작하는 사람이다.
	 */
	private void selectStarter() {

		for (Player p : players) {
			// 플레이어가 선카드를 들고있으면
			if (p.hasMahJong()) {
				this.turn = p.getPlaySeq();
				return;
			}
		}
	}

	/**
	 * 카드셋을 생성한다.
	 */
	private void generateCardSet() {
		// 카드셋 생성
		for (Player p : players) {
			p.generateCardSet();
		}
	}

	/**
	 * 현재 턴인 사용자를 받아온다.
	 * 
	 * @return
	 */
	private Player selectCurrentTurnPlayer() {
		Integer curruntTurn = this.turn++ % 4;
		return this.players.get(curruntTurn);
	}

	/**
	 * 라운드 종료 여부를 판단한다.
	 * 
	 * @return
	 */
	private boolean roundIsEnd() {
		// TODO 해야함.
		if (rankCounter == 4) {
			return true;
		}
		return false;
	}

	/**
	 * 점수를 계산한다.
	 */
	private void calculateScore() {
		// TODO 점수계산이 뭐이리 복잡하지??
		System.out.println("\n랭킹");

		// 팀별 점수
		int team1Score = 0;
		int team2Score = 0;

		// 등수별 플레이어
		Player rank1 = getPlayerByRank(1);
		Player rank2 = getPlayerByRank(2);
		Player rank3 = getPlayerByRank(3);
		Player rank4 = getPlayerByRank(4);

		// 자기카드 점수 계산.
		rank1.calculateCardScore();
		rank2.calculateCardScore();
		rank3.calculateCardScore();
		rank4.calculateCardScore();

		System.out.println("\n플레이어 \t 점수 \t 등수");
		for (Player p : players) {
			System.out.println(p.getPlayerName() + "\t" + p.getScore() + "\t" + p.getRank());
		}

		// 4등 점수를 1등에게 준다.
		rank1.addScore(rank4.getScore());
		rank4.addScore(-rank4.getScore());

		// 1등이 티츄가 있으면 티츄점수 +
		if (rank1.getTichu().equals(Tichu.TICHU)) {
			rank1.addScore(100);
		} else if (rank1.getTichu().equals(Tichu.LARGE_TICHU)) {
			rank1.addScore(200);
		}

		// 나머지는 티츄가 있으면 -
		for (Player p : players) {
			if (!p.equals(rank1)) {
				if (p.getTichu().equals(Tichu.TICHU)) {
					p.addScore(-100);
				} else if (p.getTichu().equals(Tichu.LARGE_TICHU)) {
					p.addScore(-200);
				}
			}
		}

		// 팀별로 합친다
		for (Player p : players) {
			if (p.getTeam() == 1) {
				team1Score += p.getScore();
			} else {
				team2Score += p.getScore();
			}
		}

		// 1등과 2등이 같은 팀면 그 팀이 200점
		if (rank1.getTeam() == rank2.getTeam()) {
			if (rank1.getTeam() == 1) {
				team1Score = 200;
				team2Score = 0;
			} else {
				team1Score = 0;
				team2Score = 200;
			}
		}

		System.out.println();
		System.out.println("팀1 \t "+team1Score);
		System.out.println("팀2 \t "+team2Score);

	}

	/**
	 * 등수에 해당하는 플레이어를 받아온다.
	 * 
	 * @param rank
	 * @return
	 */
	private Player getPlayerByRank(int rank) {
		for (Player p : players) {
			if (p.getRank() == rank) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 게임판에 카드셋을 놓는다.
	 * 
	 * @param cardSet
	 */
	public void addCardSetOnDesk(CardSet cardSet) {
		this.onDeskCards.add(cardSet);
	}

	/**
	 * 마지막으로 플레이한 사람을 저장한다.
	 * 
	 * @param lastPutPlayer
	 */
	public void setLastPutPlayer(Player lastPutPlayer) {
		this.lastPutPlayer = lastPutPlayer;
	}

	/**
	 * 게임판에 있는 카드셋을 받아온다.
	 * 
	 * @return
	 */
	public ConcurrentLinkedDeque<CardSet> getOnDeskCards() {
		return onDeskCards;
	}

	/**
	 * 플레이어가 종료했는지 체크한다.
	 * 
	 * @param p
	 */
	private void checkPlayerEnds(Player p) {

		// 플레이어가 라운드를 끝냈으면.
		if (p.isEnd()) {
			// 순위를 입력한다. 1부터 4까지.
			if (p.getRank() == 0) {
				p.setRank(++rankCounter);
			}
		}

	}

	/**
	 * 현재 게임상황을 출력한다.
	 */
	public void printStatus() {

		// 화면 초기화
		Util.clearConsole();

		System.out
				.println("\n\n########################################################");
		for (CardSet cs : onDeskCards) {
			System.out.format("\t %s ", cs.toString());
		}
		System.out
				.println("\n########################################################");
	}
	
	/**
	 * 개를 냈을때 턴을 점프한다.
	 */
	public void jumpTurn(){
		if(rankCounter == 0){
			this.turn += 1;
		}
	}

}