package card_game.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import card_game.com.enums.CardsetPattern;
import card_game.com.enums.Tichu;

public class Player {

	// 플레이어 아이디. 나중에 사용.
	public String playerId;

	// 플레이어 이름
	public String playerName;

	// 플레이어가 호출한 티츄 종류. 없음, 티츄, 라지티츄.
	public Tichu tichu = Tichu.NONE;

	// 플레이어가 손에 들고있는 카드 집합
	public List<Card> onHandDeck;

	// 플레이어가 가져간 카드들 집합.
	public List<CardSet> takenCards;

	// 플레이어가 게임판에 내는 카드들의 집합. 자기 턴을 종료할때 낸 카드들이 유효한지 검사한다.
	public List<Card> tempForCardSet;

	// 맨처음 순서 체크를 위해 진행순서를 저장한다.
	public Integer playSeq;

	// 낼 수 있는 카드셋 조합. 필요할까?
	public List<CardSet> cardSetList;

	// 낼 수 있는 카드셋을 맵으로 정리함
	public Map<CardsetPattern, List<CardSet>> cardSetMapByPattern;

	/**
	 * 플레이어 초기화.
	 * 
	 * @param playerName
	 */
	public Player(String playerName) {
		this.playerName = playerName;
		this.onHandDeck = new LinkedList<>();
		this.tempForCardSet = new ArrayList<>();
		this.takenCards = new ArrayList<>();
	}

	/**
	 * 카드를 분배한다. 처음엔 8장, 그다음 6장.
	 * 
	 * @param cardFactory
	 * @param numOfCards
	 */
	public void initCards(CardFactory cardFactory, int numOfCards) {
		System.out.println();
		onHandDeck.addAll(cardFactory.getCard(numOfCards));
		Collections.sort(this.onHandDeck);
	}

	/**
	 * 카드를 낸다.
	 * 
	 * @param card
	 */
	public void putCard(Game game) {
		Rule.putCard(game, this);
	}

	/**
	 * 자기 턴을 종료한다.
	 * 
	 * @see 제출한 카드들이 유효한지 판별한다.
	 * @return 제출한 카드의 카드셋
	 */
	public CardSet finishTurn() {
		// 카드셋 유효성 판단.

		this.tempForCardSet = new ArrayList<Card>();
		// 유효한 카드셋이면 반환
		// 아니면 null 반환
		return null;
	}

	/**
	 * 맨 처음 내는 카드인지 여부. 티츄를 부르기 위해 사용한다.
	 * 
	 * @return
	 */
	public boolean isFirstCard() {
		if (this.onHandDeck.size() == 14) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 카드셋을 생성한다.
	 */
	public void generateCardSet() {
		cardSetList = Rule.generateCardSet(onHandDeck);
		cardSetMapByPattern = Rule.getCardSetByPattern(cardSetList);
	}

	/**
	 * 카드셋을 낸다.
	 * 
	 * @param game
	 * @param cardSet
	 */
	protected void handlePutCardSet(Game game, CardSet cardSet) {
		// 카드를 내고
		game.onDeskCards.add(cardSet);
		// 자기 카드 덱에서 카드를 지운다
		for (Card c : cardSet.cards) {
			this.onHandDeck.remove(c);
		}
		// 현재 낸 카드 패턴을 등록한다
		game.playingPattern = cardSet.pattern;
		// 마지막에 카드를 낸 사람을 등록한다.
		game.lastPutPlayer = this;
		// 카드를 냈으면 카드덱을 다시 조합한다!!!
		this.generateCardSet();
	}

}
