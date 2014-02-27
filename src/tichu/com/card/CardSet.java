package tichu.com.card;

import java.util.List;

import tichu.com.enums.CardsetPattern;
import tichu.com.ex.CardSetException;

public class CardSet implements Comparable<CardSet> {
	// 카드셋 패턴
	private CardsetPattern pattern;
	// 카드셋에 포함된 카드
	private List<Card> cards;

	/**
	 * 패턴과 카드들로 카드셋을 생성한다.
	 * 
	 * @param pattern
	 *            패턴
	 * @param cards
	 *            카드들.
	 */
	public CardSet(CardsetPattern pattern, List<Card> cards) {
		this.pattern = pattern;
		this.cards = cards;
	}

	/**
	 * 입력받은 카드 목록을 가지고 카드셋을 생성한다.
	 * 
	 * @param cards
	 *            입력 카드 목록
	 * @throws CardSetException
	 *             입력한 카드목록으로 카드셋을 생성할 수 없으면 예외처리.
	 */
	public CardSet(List<Card> cards) throws CardSetException {
		makeCardSetByCards(cards);
	}

	/**
	 * 카드셋 생성
	 * 
	 * @param cards
	 * @throws CardSetException
	 */
	private void makeCardSetByCards(List<Card> cardList)
			throws CardSetException {
		// 입력한 카드들로 조합 가능한 카드셋을 만든다.
		List<CardSet> cardSetList = CardSetGenerator.generateCardSet(cardList);

		for (CardSet cs : cardSetList) {
			// 입력한 카드 모두를 사용해서 만든 카드셋을 판별한다.
			if (cs.cards.size() == cardList.size()) {
				this.pattern = cs.pattern;
				this.cards = cs.cards;
				return;
			}
		}

		// 입력한 카드 모두를 사용한 카드셋이 없으면 예외처리
		throw new CardSetException("적합한 카드셋을 생성할 수 없습니다.");
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
			// TODO 봉황일때 처리를 해준다.
			Card thisCard = this.cards.get(0);
			Card comparedCard = compared.cards.get(0);
			int thisCardRank = thisCard.getRank().getValue();
			int comparedCardRank = comparedCard.getRank().getValue();

			if (thisCardRank < comparedCardRank) {
				return -1;
			} else if (thisCardRank > comparedCardRank) {
				return +1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 패턴을 받아온다.
	 * 
	 * @return
	 */
	public CardsetPattern getPattern() {
		return pattern;
	}

	/**
	 * 카드셋에 속한 카드들을 받아온다.
	 * @return
	 */
	public List<Card> getCards() {
		return cards;
	}
	
	

}
