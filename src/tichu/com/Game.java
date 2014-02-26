package tichu.com;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import tichu.com.enums.CardSuit;
import tichu.com.enums.CardsetPattern;

public class Game {

	// 카드생성기
	public CardFactory cardFactory;
	// 플레이어 수. 0부터 시작하기 때문에 3이 최대다.
	public int playerCount = 0;
	// 진행중인 플레이어
	public Map<Integer, Player> players;
	// 진행중인 플레이어 (코딩을 쉽게 하기 위해 플레이어만 모아놓은 집합을 정의.
	public Set<Player> playerSet;
	// 게임판 위에 있는 카드
	public ConcurrentLinkedDeque<CardSet> onDeskCards;
	// 게임판 위 진행중인 카드셋
	public CardsetPattern playingPattern;
	// 턴
	public int turn;
	// 마지막으로 카드를 낸 사람.
	public Player lastPutPlayer;
	// 순위 정리
	public Map<Integer, Player> playerRanking;
	// 점수판
	public Map<Player, Integer> scoreBoard;

	// 준비상태
	public boolean isReady = false;

	public Game() {
		cardFactory = new CardFactory();
		players = new HashMap<Integer, Player>();
		playerSet = new HashSet<Player>();
		onDeskCards = new ConcurrentLinkedDeque<>();
		playerRanking = new HashMap<>();
		scoreBoard = new HashMap<>();
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
			player.playSeq = playSeq;
			// 플레이어를 등록하고
			players.put(playSeq, player);
			// 플레이어가 4명이 가득 차면 준비상태를 준비완료true로 바꾼다. 0,1,2,3이기 때문에 playSeq가 3일때를
			// 체크한다.
			if (playSeq == 3) {
				isReady = true;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 모든 플레이어의 카드를 출력한다.
	 * 
	 * @param playerSet
	 */
	public void printEveryPlayersCards(Set<Player> playerSet) {
		for (Player p : playerSet) {
			System.out.format("\n %s : ", p.playerName);
			for (Card c : p.onHandDeck) {
				System.out.format("\t%s", c.toString());
			}
			System.out.format("\n \t : ");
			for (CardSet cs : p.takenCards) {
				System.out.format("\t%s", cs.toString());
			}
		}
	}

	/**
	 * 1번카드를 갖고있는 플레이어를 검색한다.
	 * 
	 * @param playerSet
	 * @return
	 */
	public Player getHadMahJongPlayer(Set<Player> playerSet) {
		for (Player p : playerSet) {
			for (Card c : p.onHandDeck) {
				if (c.suit.equals(CardSuit.MAH_JONG)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * 현재 게임상황을 출력한다.
	 */
	public void printStatus() {
		// printEveryPlayersCards(playerSet);

		// System.out.format(" on Desk Card ");
		for (CardSet cs : onDeskCards) {
			System.out.format("\t %s ", cs.toString());
		}
	}

	/**
	 * 카드판 초기화
	 */
	public void initOnDeskCards() {
		// 게임판 초기화
		this.onDeskCards = new ConcurrentLinkedDeque<>();

		// 새로운 패를 시작한다.
		this.playingPattern = null;
	}

}
