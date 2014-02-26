package card_game.com;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import card_game.com.enums.CardSuit;
import card_game.com.enums.CardsetPattern;

public class PlayerAI extends Player {

	public PlayerAI(String playerName) {
		super(playerName);
	}
	


	/**
	 * 카드를 낸다. 자동으로 낸다.
	 * 
	 * @param game
	 */
	public void putCard(Game game) {
		//잠시 휴식
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		CardsetPattern pattern = game.playingPattern;
		CardSet playingCardSet = game.onDeskCards.peekLast();

		// TODO 이건 나중에 메소드로 빼면 될듯?
		// 자기가 선인 경우
		if (pattern == null) {
			// 패턴이 없을 경우. 즉 처음 시작하는 경우.
			// 1을 낸다.
			// 1을 포함한 스트레이트를 낸다.
			// 각종 카드를 낸다.

			// TODO 우선은 1을 포함한 카드를 내도록 한다.
			CardSet hasMahJongStraightCardSet = getHasMahJongStraightCardSet();
			// 1이 들어있는 스트레이트가 있으면 낸다.
			if (hasMahJongStraightCardSet != null) {
				handlePutCardSet(game, hasMahJongStraightCardSet);
				return;
			}
			// 1을 갖고 있으면 싱글로 낸다.
			if (hasMahJong(onHandDeck)) {
				pattern = CardsetPattern.SINGLE_CARD;
			}

			// 낼 수 있는 카드 조합중에 아무거나 낸다.
			List<CardsetPattern> cardSetKeys = new ArrayList<CardsetPattern>(
					cardSetMapByPattern.keySet());
			double r = Math.random() * cardSetKeys.size();
			pattern = cardSetKeys.get((int) r);
		}

		// 낼 수 있는 카드 목록
		List<CardSet> availableCardSetList = cardSetMapByPattern.get(pattern);

		// 낼 카드가 없으면?
		if (availableCardSetList == null) {
			// 패스!
		} else {
			Collections.sort(availableCardSetList);
			for (CardSet cs : availableCardSetList) {
				// 게임진행 판 위에 카드가 없을경우
				if (playingCardSet == null) {
					handlePutCardSet(game, cs);
					return;
				} else if (cs.compareTo(playingCardSet) > 0) {
					// 판 위에 있는 카드보다 숫자가 높을 경우
					// 이 카드를 낸다!!
					handlePutCardSet(game, cs);
					return;
				}
			}
		}

		// System.out.println("\n availableCardSetList : ");
		// System.out.println(availableCardSetList);

	}

	/**
	 * 선 카드를 갖고있는지 검사한다.
	 * @return
	 */
	public CardSet getHasMahJongStraightCardSet() {
		if (cardSetMapByPattern.get(CardsetPattern.STRAIGHTS) != null) {
			List<CardSet> straightCardSet = cardSetMapByPattern
					.get(CardsetPattern.STRAIGHTS);
			for (CardSet cs : straightCardSet) {
				for (Card c : cs.cards) {
					if (c.suit.equals(CardSuit.MAH_JONG)) {
						return cs;
					}
				}
			}
		}
		return null;
	}
	


	/**
	 * 선을 가지고 있는가.
	 * 
	 * @return
	 */
	public boolean hasMahJong(List<Card> cardList) {
		for (Card c : cardList) {
			if (c.suit.equals(CardSuit.MAH_JONG)) {
				return true;
			}
		}
		return false;
	}

}
