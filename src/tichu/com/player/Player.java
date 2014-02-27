package tichu.com.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;

import tichu.com.card.Card;
import tichu.com.card.CardSet;
import tichu.com.card.CardSetGenerator;
import tichu.com.enums.CardRank;
import tichu.com.enums.CardSuit;
import tichu.com.enums.CardsetPattern;
import tichu.com.enums.Tichu;
import tichu.com.etc.Counter;
import tichu.com.ex.CardException;
import tichu.com.ex.CardSetException;
import tichu.com.game.Round;

public class Player {

	// 플레이어 이름
	private String playerName;

	// 플레이어가 호출한 티츄 종류. 없음, 티츄, 라지티츄.
	private Tichu tichu = Tichu.NONE;

	// 플레이어가 손에 들고있는 카드 집합
	protected List<Card> onHandDeck;

	// 플레이어가 가져간 카드들 집합.
	private List<CardSet> takenCards;

	// 플레이어가 게임판에 내는 카드들의 집합. 자기 턴을 종료할때 낸 카드들이 유효한지 검사한다.
	protected List<Card> tempCardList;

	// 맨처음 순서 체크를 위해 진행순서를 저장한다.
	private Integer playSeq;

	// 낼 수 있는 카드셋을 맵으로 정리함
	protected Map<CardsetPattern, List<CardSet>> cardSetMapByPattern;

	// 등수
	private int rank;

	// 점수
	private int score;

	/**
	 * 플레이어 초기화.
	 * 
	 * @param playerName
	 */
	public Player(String playerName) {
		// TODO 플레이어 순서 확인요망
		this.playerName = playerName;
		this.onHandDeck = new LinkedList<>();
		this.tempCardList = new ArrayList<>();
		this.takenCards = new ArrayList<>();
	}

	/**
	 * 플레이 순서를 정한다.
	 * 
	 * @param playSeq
	 */
	public void setPlaySeq(Integer playSeq) {
		this.playSeq = playSeq;
	}

	/**
	 * 카드를 받아온다.
	 * 
	 * @param cards
	 */
	public void getCards(List<Card> cards) {
		this.onHandDeck.addAll(cards);
		Collections.sort(this.onHandDeck);
	}

	/**
	 * 티츄여부를 선택한다.
	 * 
	 * @param tichu
	 *            티츄종류
	 * @param counter
	 *            모든 플레이어가 선택을 마쳤는지 확인하기 위한 카운터
	 */
	public void callLargeTichu(Counter counter) {

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);

		//현재 있는 패를 보여주고
		System.out.println(onHandDeck);
		System.out.println("라지 티츄".concat("? (Y/N) : "));
		String input = scan.next();

		if ("Y".equalsIgnoreCase(input)) {
			this.tichu = Tichu.LARGE_TICHU;
		} else {
			this.tichu = Tichu.NONE;
		}

		// 작업완료 체크
		counter.check();
	}

	/**
	 * 티츄여부를 선택한다.
	 * 
	 * @param tichu
	 *            티츄종류
	 */
	public void callTichu() {

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);

		System.out.println("티츄".concat("? (Y/N) : "));
		String input = scan.next();

		if ("Y".equalsIgnoreCase(input)) {
			this.tichu = Tichu.TICHU;
		} else {
			this.tichu = Tichu.NONE;
		}
	}

	/**
	 * 카드를 교환한다.
	 * 
	 * @param players
	 *            전체 플레이어 목록
	 * @param exchangeCardListMap
	 *            카드 교환 목록
	 */
	public void exchangeCard(List<Player> players,
			Map<Player, List<Card>> exchangeCardListMap, Counter counter) {

		// 각 플레이어에게 카드를 준다.(카드교환 리스트에 담는다.)
		// TODO 현재 로직은 임시!
		// TODO 사용자에게 입력받게 해야한다.
		for (Player p : players) {

			// 자기자신이 아닐 때
			if (!p.equals(this)) {

				// 교환리스트가 비어있으면 초기화하고
				if (!exchangeCardListMap.containsKey(p)) {
					exchangeCardListMap.put(p, new ArrayList<Card>());
				}

				// 교환리스트에 카드를 추가한다.
				exchangeCardListMap.get(p).add(this.onHandDeck.remove(0));
			}
		}

		// 작업완료 체크
		counter.check();
	}

	/**
	 * 선 카드를 갖고있는지 확인한다.
	 * 
	 * @return
	 */
	public boolean hasMahJong() {
		for (Card c : this.onHandDeck) {
			if (c.getSuit().equals(CardSuit.MAH_JONG)) {
				return true;
			}
		}

		return false;
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
	 * 플레이어 순서를 받아온다.
	 * 
	 * @return
	 */
	public Integer getPlaySeq() {
		return playSeq;
	}

	/**
	 * 카드셋을 생성한다.
	 */
	public void generateCardSet() {
		// TODO 카드셋 생성로직은 추후 추가?!?!
		List<CardSet> cardSetList = CardSetGenerator
				.generateCardSet(onHandDeck);
		cardSetMapByPattern = CardSetGenerator.getCardSetByPattern(cardSetList);
	}

	/**
	 * 카드셋을 가져온다.
	 * @param players 
	 */
	public void collectCardSets(ConcurrentLinkedDeque<CardSet> onDeskCards, List<Player> players) {
		//TODO 예외처리.
		boolean hasDragon = false;
		
		//용으로 밟았으면!
		CardSet cs = onDeskCards.peekLast();
		
		//마지막에 낸 패가 있으면
		if(cs != null){
			//용이 있는지 검사한다.
			for(Card c : cs.getCards()){
				if(c.getSuit().equals(CardSuit.DRAGON)){
					hasDragon = true;
				}
			}
		}
		
		if(hasDragon){
			//용이 있으면 상대편에게 준다.
			for(Player p : players){
				if(p.getTeam() != this.getTeam()){
					p.takenCards.addAll(onDeskCards);
					break;
				}
			}
		}else{
			// 카드를 가져오고
			this.takenCards.addAll(onDeskCards);
		}
		// 초기화한다.
		onDeskCards.clear();
	}

	/**
	 * 카드를 선택한다.
	 * 
	 * @param round
	 */
	public void selectCards(Round round) {

		// 낼 카드가 없으면 스킵!
		if (onHandDeck.size() == 0) {
			return;
		}

		round.printStatus(); // 현재 게임판을 출력한다.

		// 플레이어가 완료명령을 내릴때 까지 선택을 계속한다.
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String input;

		// 자신의 카드를 보여준다
		System.out.format("\n%s 카드 : \t ", getPlayerName());
		System.out.println(this.onHandDeck);

		if (isFirstCard()) { // 처음 카드를 내는건지 판단한다.
			callTichu(); // 티츄여부를 확인한다.
		}

		do {
			// 현재 선택한 카드를 보여준다
			System.out.println("선택한 카드 : \t " + this.tempCardList);

			// 명령어를 보여준다
			System.out
					.print("카드를 선택해주세요. (명령종료=X, 용=Y, 봉황=P, 개=G, 1=1, ♥=H, ◇=D, ♣=C, ♤=S ) \n => ");

			// TODO 플레이어 입력 명령을 받는다.
			input = scan.next();

			// 종료명령이면 강제 종료
			if ("X".equalsIgnoreCase(input)) {
				return;
			}

			try {
				// 입력받은 문자열로 카드를 만든다.
				Card card = new Card(input);

				// 카드 유효성 검사
				if (!isValidCard(card)) {
					continue;
				}

				// TODO 임시 카드배열에 담는다.
				tempCardList.add(card);

				// 다음 입력을 받는다.
				continue;

			} catch (CardException ce) {
				System.out.println(ce.getMessage());
			}

		} while (true); // 종료명령어가 아니면 계속 실행.

	}

	/**
	 * 입력한 카드가 유효한지 체크한다.
	 * 
	 * @return
	 */
	private boolean isValidCard(Card c) {

		if (!this.onHandDeck.contains(c)) { // 사용자 덱에 있는지 검사
			System.out.println("카드가 없어요!");
			return false;
		} else if (!this.onHandDeck.contains(c)) { // 이미 선택한 카드인지 검사
			System.out.println("이미 낸 카드에요.");
			return false;
		}
		return true;
	}

	/**
	 * 카드셋의 유효성을 체크한다.
	 * 
	 * @param onDeskCards
	 * @return
	 */
	public boolean validateAndPlayCardSet(Round round) {
		ConcurrentLinkedDeque<CardSet> onDeskCards = round.getOnDeskCards();
		CardSet playingCardSet = onDeskCards.peekLast();

		try {
			// 사용자가 선택한 카드로 카드셋을 생성하고
			CardSet cardSet = new CardSet(this.tempCardList);

			// 생성에 성공하면 선택 카드 리스트는 초기화
			this.tempCardList = new ArrayList<>();

			// TODO 낼 수 있는지 판단한다.
			if (!validateCardSet(cardSet, playingCardSet)) {
				System.out.println("낼 수 없는 카드조합입니다.");
				return false;
			}
			
			//카드를 내는 로직
			if(cardSet.getCards().get(0).getSuit().equals(CardSuit.DOG)){
				//개일땐 턴을 점프한다.
				round.jumpTurn();
				// 자기 카드 덱에서 개카드를 지운다
				for (Card c : cardSet.getCards()) {
					this.onHandDeck.remove(c);
				}
			}else{
				// 개가 아니면 일반적으로 카드를 낸다.
				playCardSet(cardSet, round);
			}
			
			return true;
		} catch (CardSetException e) {
			// 아무것도 내지 않은것이면 그냥 패스한것이므로 true를 반환한다.
			if (tempCardList.size() == 0) {
				return true;

			} else { // 아니면 에러
				System.out.println(e.getMessage());
				// 카드셋 초기화
				this.tempCardList = new ArrayList<>();
				return false;
			}
		}
	}

	/**
	 * 현재 게임판에 카드셋을 낼 수 있는지 판단한다.
	 * 
	 * @return
	 */
	private boolean validateCardSet(CardSet cardSet, CardSet playingCardSet) {

		// 게임진행 판 위에 카드가 없을경우. 자신이 선인 경우.
		if (playingCardSet == null) {
			return true;

		} else { // 진행중인 패턴에 맞추는 경우
			CardsetPattern pattern = playingCardSet.getPattern();

			// 패턴이 같거나, 자신의 카드가 폭탄일때.
			if ((cardSet.getPattern().equals(pattern) && cardSet
					.compareTo(playingCardSet) > 0)
					|| cardSet.getPattern().equals(CardsetPattern.FOUR_CARDS)
					|| cardSet.getPattern().equals(
							CardsetPattern.STRAIGHT_FLUSH)) {

				// TODO 폭탄일 때 비교하는 로직도 들어가야 한다.
				// TODO 유효성 검사는 다른 클래스에서 하는게 좋지 않을까?!

				return true;
			}
			return false;
		}
	}

	/**
	 * 카드셋을 낸다.
	 * 
	 * @param round
	 */
	public void playCardSet(CardSet cardSet, Round round) {

		// 카드를 내고
		round.addCardSetOnDesk(cardSet);

		// 자기 카드 덱에서 카드를 지운다
		for (Card c : cardSet.getCards()) {
			if(c.getSuit().equals(CardSuit.PHOENIX)){
				this.onHandDeck.remove(new Card(CardSuit.PHOENIX, CardRank.PHOENIX));
			}
			this.onHandDeck.remove(c);
		}

		// 마지막에 카드를 낸 사람을 등록한다.
		round.setLastPutPlayer(this);

		// 카드를 냈으면 카드덱을 다시 조합한다!!!
		this.generateCardSet();

	}

	/**
	 * 티츄정보를 가져온다.
	 * 
	 * @return
	 */
	public Tichu getTichu() {
		return tichu;
	}

	/**
	 * 라운드 종료여부를 판별한다.
	 * 
	 * @return
	 */
	public boolean isEnd() {
		if (onHandDeck.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 플레이어 이름을 받아온다.
	 * 
	 * @return
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * 카드점수 계산
	 * 
	 * @return
	 */
	public void calculateCardScore() {
		int total = 0;
		for (CardSet cs : takenCards) {
			for (Card c : cs.getCards()) {
				if (c.getSuit().equals(CardSuit.DRAGON)) {
					total += 25;
				} else if (c.getSuit().equals(CardSuit.PHOENIX)) {
					total -= 25;
				} else if (c.getRank().equals(CardRank.FIVE)) {
					total += 5;
				} else if (c.getRank().equals(CardRank.TEN)) {
					total += 10;
				} else if (c.getRank().equals(CardRank.KING)) {
					total += 10;
				}
			}
		}

		// TODO 테스트용
		System.out.println(playerName + "\t" + takenCards);

		this.score = total;
	}

	/**
	 * 등수를 받아온다.
	 * 
	 * @return
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * 등수를 저장한다.
	 * 
	 * @param rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * 팀
	 * 
	 * @return
	 */
	public int getTeam() {
		if (playSeq % 2 == 0) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * 점수를 받아온다.
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}

	/**
	 * 점수를 조정한다
	 * 
	 * @param score
	 */
	public void addScore(int score) {
		this.score += score;
	}

}
