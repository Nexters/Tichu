package tichu.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tichu.com.enums.CardRank;
import tichu.com.enums.CardSuit;
import tichu.com.enums.CardType;

/**
 * 카드를 생성한다.
 * 
 * @author lifenjoy51
 * 
 */
public class CardFactory {
	// 카드를 담는 덱
	private List<Card> deck;

	public CardFactory() {
		deck = new ArrayList<Card>();

		// 특수카드를 생성한다.
		makeExtraCards();

		// 일반 카드를 생성한다.
		makeNormalCards();

		// 카드를 섞는다.
		Collections.shuffle(deck);

		// TODO 카드 확인용 출력
		System.out.println(deck);
	}

	/**
	 * 특수카드를 생성한다.
	 */
	private void makeExtraCards() {

		Card MahJong = new Card(CardType.EXTRA, CardSuit.MAH_JONG, CardRank.ONE);
		Card Dog = new Card(CardType.EXTRA, CardSuit.DOG, CardRank.DOG);
		Card Phoenix = new Card(CardType.EXTRA, CardSuit.PHOENIX,
				CardRank.PHOENIX);
		Card Dragon = new Card(CardType.EXTRA, CardSuit.DRAGON, CardRank.DRAGON);

		// 덱에 추가
		deck.add(MahJong);
		deck.add(Dog);
		deck.add(Phoenix);
		deck.add(Dragon);

	}

	/**
	 * 일반카드를 생성한다.
	 */
	private void makeNormalCards() {
		for (CardSuit suit : CardSuit.values()) {
			// 특수카드를 제외한다.
			if (!suit.equals(CardSuit.DOG) && !suit.equals(CardSuit.MAH_JONG)
					&& !suit.equals(CardSuit.DRAGON)
					&& !suit.equals(CardSuit.PHOENIX)) {

				for (CardRank rank : CardRank.values()) {
					// 특수카드를 제외한다.
					if (!rank.equals(CardRank.ONE)
							&& !rank.equals(CardRank.DOG)
							&& !rank.equals(CardRank.PHOENIX)
							&& !rank.equals(CardRank.DRAGON)) {

						// 카드 생성하고
						Card card = new Card(CardType.NORMAL, suit, rank);

						// 덱에 넣는다.
						deck.add(card);
					}
				}
			}
		}

	}

	/**
	 * 카드를 가져온다.
	 * 
	 * @param size
	 *            가져올 카드 개수
	 * @return 카드 목록
	 */
	public List<Card> getCard(int size) {
		// sublist 는 객체의 특정 부분을 받아오기 때문에, 객체에서 자기 자신을 삭제하는 로직을 수행하면
		// ConcurrentModificationException이 발생한다.
		// deck.removeAll(cardList); 여기서 cardList가 deck을 sublist한것이라면, cardlist는
		// deck의 일부를 포인팅 하고 있기 때문에 ConcurrentModificationException이 발생하는 것이다.
		// 그렇기 때문에 sublist를 해서 새로운 list를 생성하는 것이다.
		if (deck.size() >= size) {
			List<Card> cardList = new ArrayList<Card>(deck.subList(0, size));
			deck.removeAll(cardList);
			return cardList;
		} else {
			// 접근 가능한 리스트 범위를 넘었을 때 어떤 로직을 수행할 것인가.
			return null;
		}
	}

}
