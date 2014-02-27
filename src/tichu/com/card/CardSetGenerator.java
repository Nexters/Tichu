package tichu.com.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tichu.com.enums.CardRank;
import tichu.com.enums.CardSuit;
import tichu.com.enums.CardsetPattern;

public class CardSetGenerator {

	private static Card phoenix = new Card(CardSuit.PHOENIX, CardRank.PHOENIX);

	/**
	 * 카드셋을 생성한다.
	 * 
	 * @param cardList
	 * @return
	 */
	public static List<CardSet> generateCardSet(List<Card> originalCardList) {

		List<Card> cardList = new ArrayList<>(originalCardList);

		// 출력할 카드셋 목록
		List<CardSet> cardSetList = new ArrayList<>();

		// 카드셋을 만들고
		generate(cardList, cardSetList);

		// 봉황처리. 봉황이 있으면 봉황이 들어간 카드셋을 추가한다.
		handlePhoenix(cardList, cardSetList);

		// System.out.println(cardSetList);

		return cardSetList;
	}

	/**
	 * 카드셋 생성
	 * 
	 * @param cardList
	 * @param cardSetList
	 */
	private static void generate(List<Card> cardList, List<CardSet> cardSetList) {
		// 가장 쎈것부터 만들어보자.

		// 판별을 위해 우선 정리한다.

		// 문양별로 정리하고
		Map<CardSuit, List<Card>> cardListMapBySuit = new HashMap<>();

		// 숫자별로 정리하고
		Map<CardRank, List<Card>> cardListMapByRank = new HashMap<>();

		// 카드 정리
		for (Card c : cardList) {
			// 문양별 정리
			if (!cardListMapBySuit.containsKey(c.getSuit())) {
				cardListMapBySuit.put(c.getSuit(), new ArrayList<Card>());
			}
			cardListMapBySuit.get(c.getSuit()).add(c);

			// 숫자별 정리
			if (!cardListMapByRank.containsKey(c.getRank())) {
				cardListMapByRank.put(c.getRank(), new ArrayList<Card>());
			}
			cardListMapByRank.get(c.getRank()).add(c);
		}

		// 문양별 정리에서 봉황은 제외
		cardListMapBySuit.remove(CardSuit.PHOENIX);

		// 확인
		// System.out.println();
		// System.out.println(cardListMapBySuit);
		// System.out.println(cardListMapByRank);

		// #################
		// 스트레이트플러쉬
		final int straight = 5;// 스트레이트 최소 개수

		// 문양이 5개이상 일치하는지 검사한다.
		for (Entry<CardSuit, List<Card>> e : cardListMapBySuit.entrySet()) {
			List<Card> sameSuitCardList = e.getValue();
			// 같은문양이 5개이상 있으면
			if (sameSuitCardList.size() >= straight) {
				checkStraight(sameSuitCardList, straight,
						CardsetPattern.STRAIGHT_FLUSH, cardSetList);
			}
		}

		// #################
		// 포카드, 트리플, 페어
		for (Entry<CardRank, List<Card>> e : cardListMapByRank.entrySet()) {
			List<Card> cards = e.getValue();
			// 같은숫자 카드가 네장이면!!
			// 포카드!
			if (cards.size() == 4) {
				// System.out.println("for card!!");
				// System.out.println(e.getValue());

				// 포카드에선 봉황을 제외한다.
				if (cards.contains(phoenix)) {
					continue;
				}

				CardSet fourCard = new CardSet(CardsetPattern.FOUR_CARDS, cards);
				cardSetList.add(fourCard);
			}

			// 같은숫자 카드가 세장이면!
			// 트리플
			if (cards.size() == 3) {
				// System.out.println("triple!");
				// System.out.println(e.getValue());

				CardSet triple = new CardSet(CardsetPattern.TRIPLE, cards);
				cardSetList.add(triple);
			}

			// 같은숫자 카드가 두장이면!
			// 페어
			if (cards.size() == 2) {
				// System.out.println("pair!");
				// System.out.println(e.getValue());

				CardSet pair = new CardSet(CardsetPattern.PAIR, cards);
				cardSetList.add(pair);
			}
		}

		// ##############
		// 카드셋패턴 별 카드셋. 풀하우스와 스테어를 만들기 위한 준비작업. 이전에 트리플과 페어의 판단이 끝나야 한다.
		Map<CardsetPattern, List<CardSet>> cardSetMapByPattern = new HashMap<>();
		// 정리하고
		for (CardSet cardSet : cardSetList) {
			if (!cardSetMapByPattern.containsKey(cardSet.getPattern())) {
				cardSetMapByPattern.put(cardSet.getPattern(),
						new ArrayList<CardSet>());
			}
			cardSetMapByPattern.get(cardSet.getPattern()).add(cardSet);
		}

		// ###########
		// 풀하우스
		// 트리플과 페어를 갖고있으면
		if (cardSetMapByPattern.containsKey(CardsetPattern.TRIPLE)
				&& cardSetMapByPattern.containsKey(CardsetPattern.PAIR)) {
			// 트리플 기준으로
			for (CardSet tripleCardSet : cardSetMapByPattern
					.get(CardsetPattern.TRIPLE)) {
				// 페어와 조합한다.
				for (CardSet pairCardSet : cardSetMapByPattern
						.get(CardsetPattern.PAIR)) {
					// 카드모임
					List<Card> fullHouseCardList = new ArrayList<>();
					fullHouseCardList.addAll(tripleCardSet.getCards());
					fullHouseCardList.addAll(pairCardSet.getCards());

					//봉황 더미카드가 한장 이하면 정상
					if (phoenixCount(fullHouseCardList) <= 1) {
						// 풀하우스 카드셋 생성
						CardSet fullHouse = new CardSet(
								CardsetPattern.FULL_HOUSES, fullHouseCardList);
						cardSetList.add(fullHouse);
					}

					// System.out.println("full house!");
					// System.out.println(fullHouseCardList);
				}
			}

		}

		// ######
		// 스테어
		if (cardSetMapByPattern.containsKey(CardsetPattern.PAIR)) {
			List<CardSet> pairCardSetList = cardSetMapByPattern
					.get(CardsetPattern.PAIR);
			if (cardSetMapByPattern.get(CardsetPattern.PAIR).size() > 1) {

				// 단순 카드 비교가 아니라 카드셋 비교이기 떄문에 스트레이트플러쉬, 스트레이트와 같은 로직을 사용할 수가 없다.
				for (CardSet cs1 : pairCardSetList) {// 한장씩 들고
					int straightCnt = 0; // 비교 카운트
					int c1Rank = cs1.getCards().get(0).getRank().getValue();// 비교할
					// 카드
					// 숫자
					List<Card> tempCardList = new ArrayList<>();
					tempCardList.addAll(cs1.getCards());

					for (CardSet cs2 : pairCardSetList) {// 한장씩 비교
						int c2Rank = cs2.getCards().get(0).getRank().getValue();// 비교할
						// 카드
						// 숫자

						if (c1Rank != c2Rank) {// 같은 숫자가 아니면

							if (c2Rank == c1Rank + 1) {// 연속이면!!
								straightCnt++;// 연속카운트 +1
								c1Rank = c2Rank; // 비교할 카드숫자를 방금 비교한 카드로 바꾼다.
													// 연속해서 비교하기 위해.
								tempCardList.addAll(cs2.getCards());
							} else {
								break;// 연속 아니면... 끝냄
							}
						}
					}

					// 일정길이 이상 일치하면!
					if (straightCnt > 0) {
						//봉황 더미카드가 한장 이하면 정상
						if (phoenixCount(tempCardList) <= 1) {
							CardSet stairCardSet = new CardSet(
									CardsetPattern.STAIRS, tempCardList);
							cardSetList.add(stairCardSet);
						}
						// System.out.println("stair!");
						// System.out.println(tempCardList);
					}
				}

			}
		}

		// ##########
		// 스트레이트
		checkStraight(cardList, straight, CardsetPattern.STRAIGHTS, cardSetList);

		// 아무 카드셋에도 없는 패를 고른다.
		// Set<Card> hasPatternCardList = new HashSet<>();
		// List<Card> singleCardList = new ArrayList<>(cardList);
		// for (CardSet cs : cardSetList) {
		// hasPatternCardList.addAll(cs.getCards());
		// }
		// singleCardList.removeAll(hasPatternCardList);
		//
		// // 나머지는 쩌리 싱글

		// 모든 카드를 싱글카드로 넣는다.
		for (Card c : cardList) {
			List<Card> card = new ArrayList<>();
			card.add(c);
			CardSet singleCardSet = new CardSet(CardsetPattern.SINGLE_CARD,
					card);
			cardSetList.add(singleCardSet);

			// System.out.println("single..");
			// System.out.println(card);
		}

		// TODO
		// 패를 낼때 상위 패에 영향을 미치지 않는지 확인한다.
		// 상위패는 어떻게 판별할것인가?
		// 폭탄은 우선 절대적으로 판단하고
		// 폭탄이 아닌것은 나중에 생각해보자..
	}

	/**
	 * 봉황을 갖고있을때 카드조합을 만들기 위한 방법
	 * 
	 * @param cardList
	 * @param cardSetList
	 */
	private static void handlePhoenix(List<Card> cardList,
			List<CardSet> cardSetList) {
		// TODO 봉황로직은 추후 정리할 필요가 있다. 대충 만들어서 돌긴 하는데 이유를 모른다..
		// 봉황이 있으면?! 더미카드를 만들어보자!
		Set<Card> dummyCardList = new HashSet<>();
		boolean hasPhoenix = false;
		for (Card c : cardList) {
			if (c.equals(phoenix)) {
				hasPhoenix = true;
			}
		}

		Set<CardRank> dummyRanks = new HashSet<>();
		if (hasPhoenix) {
			for (Card c : cardList) {
				// System.out.println(c);
				if (!c.getRank().equals(CardRank.DOG)
						&& !c.getRank().equals(CardRank.DRAGON)
						&& !c.getRank().equals(CardRank.ONE)
						&& !c.getRank().equals(CardRank.PHOENIX)) {
					dummyRanks.add(c.getRank());
				}
			}
		}

		for (CardRank rank : dummyRanks) {
			Card dummy = new Card(CardSuit.PHOENIX, rank);
			dummyCardList.add(dummy);
		}

		// System.out.println(dummyCardList);
		cardList.addAll(dummyCardList);

		generate(cardList, cardSetList);
	}

	/**
	 * 봉황을 두장 낼 수 없게 예외처리.
	 * 
	 * @param c
	 * @return
	 */
	private static int phoenixCount(List<Card> cardList) {
		int total = 0;
		// 봉황 더미카드가 몇장인지 확인한다.
		for (Card cd : cardList) {
			if (cd.getSuit().equals(CardSuit.PHOENIX)) {
				total++;
			}
		}
		return total;
	}

	/**
	 * 스트레이트를 판별한다.
	 * 
	 * @param chkCardList
	 *            스트레이트를 판별할 카드 리스트
	 * @param straight
	 *            스트레이트 개수
	 * @param cardSetPattern
	 *            카드패턴. 스트레이트플러쉬인지, 그냥 스트레이트인지.
	 * @param cardSetList
	 */
	public static void checkStraight(List<Card> chkCardList, int straight,
			CardsetPattern cardSetPattern, List<CardSet> cardSetList) {

		// 스트레이트플러쉬를 판별할 때에는 봉황카드를 제외한다.
		if (cardSetPattern.equals(CardsetPattern.STRAIGHT_FLUSH)) {
			chkCardList.remove(phoenix);
		}

		for (Card c1 : chkCardList) {// 한장씩 들고
			int straightCnt = 0; // 비교 카운트
			int c1Rank = c1.getRank().getValue();// 비교할 카드 숫자
			List<Card> tempCardList = new ArrayList<>();
			tempCardList.add(c1);

			for (Card c2 : chkCardList) {// 한장씩 비교
				int c2Rank = c2.getRank().getValue();// 비교할 카드 숫자

				if (c1Rank != c2Rank) {// 같은 숫자가 아니면

					if (c2Rank == c1Rank + 1) {// 연속이면!!
						straightCnt++;// 연속카운트 +1
						c1Rank = c2Rank; // 비교할 카드숫자를 방금 비교한 카드로 바꾼다.
											// 연속해서 비교하기 위해.
						tempCardList.add(c2);
					} else {
						break;// 연속 아니면... 끝냄
					}
				}
			}

			// 한장의 비교가 끝나면 몇번 연속되었는지 확인한다. 5장이상이 연속되려면, 연속카운트는 4 이상이여야
			// 한다.
			if (straightCnt >= straight - 1) {
				//봉황 더미카드가 한장 이하면 정상
				if (phoenixCount(tempCardList) <= 1) {
					CardSet straightCardSet = new CardSet(cardSetPattern,
							tempCardList);
					cardSetList.add(straightCardSet);
				}
				// System.out.println(cardSetPattern);
				// System.out.println(tempCardList);
			}
		}
	}

	/**
	 * 패턴에 맞는 카드셋 목록을 받아온다.
	 * 
	 * @param cardSetList
	 * @return
	 */
	public static Map<CardsetPattern, List<CardSet>> getCardSetByPattern(
			List<CardSet> cardSetList) {
		// 카드셋패턴 별 카드셋.
		Map<CardsetPattern, List<CardSet>> cardSetMapByPattern = new HashMap<>();
		// 정리하고
		for (CardSet cardSet : cardSetList) {
			if (!cardSetMapByPattern.containsKey(cardSet.getPattern())) {
				cardSetMapByPattern.put(cardSet.getPattern(),
						new ArrayList<CardSet>());
			}
			cardSetMapByPattern.get(cardSet.getPattern()).add(cardSet);
		}

		return cardSetMapByPattern;
	}

}
