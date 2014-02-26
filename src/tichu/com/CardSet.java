package tichu.com;

import java.util.List;

import tichu.com.enums.CardsetPattern;

public class CardSet implements Comparable<CardSet> {
	// 카드셋 패턴
	public CardsetPattern pattern;
	// 카드셋에 포함된 카드
	public List<Card> cards;

	/**
	 * 패턴과 카드들로 카드셋을 생성한다.
	 * 
	 * @param pattern
	 *            패턴
	 * @param cards
	 *            카드들.
	 */
	public CardSet(CardsetPattern pattern, List<Card> cards) {
		super();
		this.pattern = pattern;
		this.cards = cards;
	}

	/**
	 * 카드셋에 포함되어있는 카드들을 출력한다.
	 */
	@Override
	public String toString() {
		String cardSetString = "";
		// cardSetString = this.pattern.name().concat("#");
		for (Card card : cards) {
			cardSetString = cardSetString.concat("_").concat(card.toString());
		}
		return cardSetString;
	}

	/**
	 * @see 기준개체가 매개변수보다 작으면 음수
	 * @see 기준개체가 매개변수보다 크면 양수
	 * @see 기준개체가 매개변수랑 같으면 0
	 */
	@Override
	public int compareTo(CardSet compared) {

		if (this.pattern.getValue() < compared.pattern.getValue()) {
			return -1;
		} else if (this.pattern.getValue() > compared.pattern.getValue()) {
			return 1;
		} else {
			// 폭탄이 아닌 경우이다.
			// 카드셋의 맨 처음 카드를 꺼내서 크기를 비교한다
			// 봉황일때 처리를 해준다.
			Card thisCard = this.cards.get(0);
			Card comparedCard = compared.cards.get(0);
			int thisCardRank = thisCard.rank.getValue();
			int comparedCardRank = comparedCard.rank.getValue();

			if (thisCardRank < comparedCardRank) {
				return -1;
			} else if (thisCardRank > comparedCardRank) {
				return +1;
			} else {
				return 0;
			}
		}
	}

}
