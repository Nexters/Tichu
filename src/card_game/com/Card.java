package card_game.com;

import card_game.com.enums.CardRank;
import card_game.com.enums.CardSuit;
import card_game.com.enums.CardType;

public class Card implements Comparable<Card> {
	public CardType type;
	public CardSuit suit;
	public CardRank rank;
	public String name;

	public Card(CardType type, CardSuit suit, CardRank rank, String name) {
		super();
		this.type = type;
		this.suit = suit;
		this.rank = rank;
		this.name = name;
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
