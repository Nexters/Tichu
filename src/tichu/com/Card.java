package tichu.com;

import tichu.com.enums.CardRank;
import tichu.com.enums.CardSuit;
import tichu.com.enums.CardType;
import tichu.com.ex.CardException;

public class Card implements Comparable<Card> {
	public CardType type;
	public CardSuit suit;
	public CardRank rank;

	/**
	 * 종류, 문양, 숫자를 설정한다.
	 * 
	 * @param type
	 *            일반, 특수카드 종류
	 * @param suit
	 *            문양
	 * @param rank
	 *            숫자
	 */
	public Card(CardType type, CardSuit suit, CardRank rank) {
		this.type = type;
		this.suit = suit;
		this.rank = rank;
	}

	/**
	 * 입력문자열로 카드를 생성한다.
	 * 
	 * @param name
	 *            입력 문자열
	 * @throws CardException
	 *             입력 문자열에 해당하는 카드가 없을경우 예외를 던진다.
	 */
	public Card(String name) throws CardException {
		// 이름을 받아와 검사하고, 종류, 문양, 숫자를 설정한다.
		makeCardByName(name);
	}

	/**
	 * 카드생성
	 * 
	 * @param name
	 * @throws CardException
	 */
	private void makeCardByName(String name) throws CardException {
		// 빈값 체크
		if (Util.isEmpty(name)) {
			return;
		}

		// 빈값이 아니면 무조건 1보다 큰 문자열이다.
		// 맨 앞에 한자리를 잘라서 문양을 검사한다.
		String suitString = name.substring(0, 1);

		// 문양은 특수문자로 되어있기 때문에, enum을 비교하지 않고 문자열을 비교한다.
		if ("Y".equalsIgnoreCase(suitString)) { // 용
			suit = CardSuit.DRAGON;
			rank = CardRank.DRAGON;
		} else if ("P".equalsIgnoreCase(suitString)) { // 봉황
			suit = CardSuit.PHOENIX;
			rank = CardRank.PHOENIX;
		} else if ("G".equalsIgnoreCase(suitString)) { // 개
			suit = CardSuit.DOG;
			rank = CardRank.DOG;
		} else if ("1".equalsIgnoreCase(suitString)) { // 선
			suit = CardSuit.MAH_JONG;
			rank = CardRank.ONE;
		} else if ("H".equalsIgnoreCase(suitString)) { // 하트
			suit = CardSuit.JADE;
		} else if ("D".equalsIgnoreCase(suitString)) { // 다이아
			suit = CardSuit.SWORD;
		} else if ("C".equalsIgnoreCase(suitString)) { // 클로버
			suit = CardSuit.PAGODA;
		} else if ("S".equalsIgnoreCase(suitString)) { // 스페이드
			suit = CardSuit.STAR;
		}

		// 두번째 자리부터 마지막까지 자른다.
		String rankString = name.substring(1, name.length());
		if ("J".equalsIgnoreCase(rankString)) {
			rank = CardRank.JACK;
		} else if ("Q".equalsIgnoreCase(rankString)) {
			rank = CardRank.QUEEN;
		} else if ("K".equalsIgnoreCase(rankString)) {
			rank = CardRank.KING;
		} else if ("A".equalsIgnoreCase(rankString)) {
			rank = CardRank.ACE;
		} else if ("1".equalsIgnoreCase(rankString)) {
			rank = CardRank.ONE;
		} else if ("2".equalsIgnoreCase(rankString)) {
			rank = CardRank.TWO;
		} else if ("3".equalsIgnoreCase(rankString)) {
			rank = CardRank.THREE;
		} else if ("4".equalsIgnoreCase(rankString)) {
			rank = CardRank.FOUR;
		} else if ("5".equalsIgnoreCase(rankString)) {
			rank = CardRank.FIVE;
		} else if ("6".equalsIgnoreCase(rankString)) {
			rank = CardRank.SIX;
		} else if ("7".equalsIgnoreCase(rankString)) {
			rank = CardRank.SEVEN;
		} else if ("8".equalsIgnoreCase(rankString)) {
			rank = CardRank.EIGHT;
		} else if ("9".equalsIgnoreCase(rankString)) {
			rank = CardRank.NINE;
		} else if ("10".equalsIgnoreCase(rankString)) {
			rank = CardRank.TEN;
		}

		// 에러처리
		if (suit == null || rank == null) {
			throw new CardException("잘못된 카드입니다.");
		}
	}

	/**
	 * @see 기준개체가 매개변수보다 작으면 음수
	 * @see 기준개체가 매개변수보다 크면 양수
	 * @see 기준개체가 매개변수랑 같으면 0
	 */
	@Override
	public int compareTo(Card o) {
		// TODO 특수카드에 대한 비교 정보를 여기에 넣어야 한다
		return this.rank.compareTo(o.rank);
	}

	/**
	 * 문양과 숫자로 출력한다.
	 */
	@Override
	public String toString() {
		// 콘솔에서 확인을 위해..
		String cardName = suit.getName().concat(rank.getName());
		return cardName;
	}

	/**
	 * 같은 카드인지 검사한다.
	 */
	@Override
	public boolean equals(Object obj) {
		// 카드 인스턴스 일때
		if (obj instanceof Card) {
			Card c = (Card) obj;
			// 문양과 숫자가 같으면 같은카드로 판별
			if (this.suit.equals(c.suit) && this.rank.equals(c.rank)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
