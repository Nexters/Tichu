package tichu.com.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tichu.com.card.Card;
import tichu.com.card.CardSet;
import tichu.com.enums.CardRank;
import tichu.com.enums.CardSuit;
import tichu.com.enums.CardsetPattern;
import tichu.com.etc.Counter;
import tichu.com.game.Round;

public class AI extends Player {

	public AI(String playerName) {
		super(playerName);
	}

	@Override
	public void callLargeTichu(Counter counter) {
		counter.check();
	}

	@Override
	public void selectCards(Round round) {

		// 낼 카드가 없으면 스킵!
		if (onHandDeck.size() == 0) {
			return;
		}

		// 잠시 휴식
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		round.printStatus(); // 현재 게임판을 출력한다.

		System.out.format("\n%s 카드 : \t ", getPlayerName());
		System.out.println(this.onHandDeck);

		CardSet playingCardSet = round.getOnDeskCards().peekLast();
		CardsetPattern pattern;

		// 1을 갖고 있으면 싱글로 낸다.
		if (hasMahJong()) {
			// TODO 우선은 1을 포함한 카드를 내도록 한다.
			CardSet hasMahJongStraightCardSet = getHasMahJongStraightCardSet();

			// 1이 들어있는 스트레이트가 있으면 낸다.
			if (hasMahJongStraightCardSet != null) {
				tempCardList.addAll(hasMahJongStraightCardSet.getCards());
			} else {
				tempCardList.add(new Card(CardSuit.MAH_JONG, CardRank.ONE));
			}
		} else {
			// 패턴이 없을 경우. 즉 처음 시작하는 경우
			if (playingCardSet == null) {

				// 낼 수 있는 카드 조합중에 아무거나 낸다.
				List<CardsetPattern> cardSetKeys = new ArrayList<CardsetPattern>(
						cardSetMapByPattern.keySet());
				double r = Math.random() * cardSetKeys.size();
				pattern = cardSetKeys.get((int) r);

				// 낼 수 있는 카드 목록
				List<CardSet> availableCardSetList = cardSetMapByPattern
						.get(pattern);

				for (CardSet cs : availableCardSetList) {
					// 판 위에 있는 카드보다 숫자가 높을 경우
					tempCardList.addAll(cs.getCards());
					break;
				}

			} else {
				//이미 올라온 패턴에 맞출 경우
				pattern = playingCardSet.getPattern();

				// 낼 수 있는 카드 목록
				List<CardSet> availableCardSetList = cardSetMapByPattern
						.get(pattern);

				// 낼 카드가 있어야 낸다.
				if (availableCardSetList != null) {
					//랜덤하게!!
					Collections.shuffle(availableCardSetList);
					for (CardSet cs : availableCardSetList) {
						// 판 위에 있는 카드보다 숫자가 높을 경우
						if (cs.compareTo(playingCardSet) > 0) {
							tempCardList.addAll(cs.getCards());
							break;
						}
					}
				}
			}
		}

		// 현재 선택한 카드를 보여준다
		System.out.println("선택한 카드 : \t " + this.tempCardList);
	}

	/**
	 * 선 카드를 갖고있는지 검사한다.
	 * 
	 * @return
	 */
	public CardSet getHasMahJongStraightCardSet() {
		if (cardSetMapByPattern.get(CardsetPattern.STRAIGHTS) != null) {
			List<CardSet> straightCardSet = cardSetMapByPattern
					.get(CardsetPattern.STRAIGHTS);
			for (CardSet cs : straightCardSet) {
				for (Card c : cs.getCards()) {
					if (c.getSuit().equals(CardSuit.MAH_JONG)) {
						return cs;
					}
				}
			}
		}
		return null;
	}
}
