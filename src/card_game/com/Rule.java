package card_game.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import card_game.com.enums.CardRank;
import card_game.com.enums.CardSuit;
import card_game.com.enums.CardsetPattern;

public class Rule {


	/**
	 * 카드패턴이 일치하는지 확인한다.
	 * 
	 * @param cardset
	 *            비교할 카드셋
	 * @return 일치여부
	 */
	public static boolean equalPattern(CardSet thisCardset, CardSet comparedCardset) {
		if (thisCardset.pattern.equals(comparedCardset.pattern)) {
			// TODO 예외처리를 추가해야한다.
			// 스트레이트일때는 패턴도 같고 개수도 똑같아야 한다.
			return true;
		} else {
			// TODO 예외처리
			// 폭탄은 패턴이 달라도 날릴 수가 있다.
			// 폭탄을 날릴때는 언제나 같도록
			return false;
		}
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

		for (Card c1 : chkCardList) {// 한장씩 들고
			int straightCnt = 0; // 비교 카운트
			int c1Rank = c1.rank.getValue();// 비교할 카드 숫자
			List<Card> tempCardList = new ArrayList<>();
			tempCardList.add(c1);
			for (Card c2 : chkCardList) {// 한장씩 비교
				int c2Rank = c2.rank.getValue();// 비교할 카드 숫자

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
				CardSet straightCardSet = new CardSet(cardSetPattern,
						tempCardList);
				cardSetList.add(straightCardSet);
				// System.out.println(cardSetPattern);
				// System.out.println(tempCardList);
			}
		}
	}
	


	/**
	 * 카드셋을 생성한다.
	 * 
	 * @param cardList
	 * @return
	 */
	public static List<CardSet> generateCardSet(List<Card> cardList) {
		// 출력할 카드셋 목록
		List<CardSet> cardSetList = new ArrayList<CardSet>();
		// 가장 쎈것부터 만들어보자.

		// 판별을 위해 우선 정리한다.

		// 문양별로 정리하고
		Map<CardSuit, List<Card>> cardListMapBySuit = new HashMap<>();

		// 숫자별로 정리하고
		Map<CardRank, List<Card>> cardListMapByRank = new HashMap<>();

		// 카드 정리
		for (Card c : cardList) {
			// 문양별 정리
			if (!cardListMapBySuit.containsKey(c.suit)) {
				cardListMapBySuit.put(c.suit, new ArrayList<Card>());
			}
			cardListMapBySuit.get(c.suit).add(c);

			// 숫자별 정리
			if (!cardListMapByRank.containsKey(c.rank)) {
				cardListMapByRank.put(c.rank, new ArrayList<Card>());
			}
			cardListMapByRank.get(c.rank).add(c);
		}

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
			if (!cardSetMapByPattern.containsKey(cardSet.pattern)) {
				cardSetMapByPattern.put(cardSet.pattern,
						new ArrayList<CardSet>());
			}
			cardSetMapByPattern.get(cardSet.pattern).add(cardSet);
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
					fullHouseCardList.addAll(tripleCardSet.cards);
					fullHouseCardList.addAll(pairCardSet.cards);

					// 풀하우스 카드셋 생성
					CardSet fullHouse = new CardSet(CardsetPattern.FULL_HOUSES,
							fullHouseCardList);
					cardSetList.add(fullHouse);

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
					int c1Rank = cs1.cards.get(0).rank.getValue();// 비교할
																			// 카드
																			// 숫자
					List<Card> tempCardList = new ArrayList<>();
					tempCardList.addAll(cs1.cards);

					for (CardSet cs2 : pairCardSetList) {// 한장씩 비교
						int c2Rank = cs2.cards.get(0).rank.getValue();// 비교할
																				// 카드
																				// 숫자

						if (c1Rank != c2Rank) {// 같은 숫자가 아니면

							if (c2Rank == c1Rank + 1) {// 연속이면!!
								straightCnt++;// 연속카운트 +1
								c1Rank = c2Rank; // 비교할 카드숫자를 방금 비교한 카드로 바꾼다.
													// 연속해서 비교하기 위해.
								tempCardList.addAll(cs2.cards);
							} else {
								break;// 연속 아니면... 끝냄
							}
						}
					}

					// 일정길이 이상 일치하면!
					if (straightCnt > 0) {
						CardSet stairCardSet = new CardSet(
								CardsetPattern.STAIRS, tempCardList);
						cardSetList.add(stairCardSet);
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
		Set<Card> hasPatternCardList = new HashSet<>();
		List<Card> singleCardList = new ArrayList<>(cardList);
		for (CardSet cs : cardSetList) {
			hasPatternCardList.addAll(cs.cards);
		}
		singleCardList.removeAll(hasPatternCardList);

		// 나머지는 쩌리 싱글
		for (Card c : singleCardList) {
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

		return cardSetList;
	}
	
	/**
	 * 패턴에 맞는 카드셋 목록을 받아온다.
	 * @param cardSetList
	 * @return
	 */
	public static Map<CardsetPattern, List<CardSet>> getCardSetByPattern(
			List<CardSet> cardSetList) {
		// 카드셋패턴 별 카드셋.
		Map<CardsetPattern, List<CardSet>> cardSetMapByPattern = new HashMap<>();
		// 정리하고
		for (CardSet cardSet : cardSetList) {
			if (!cardSetMapByPattern.containsKey(cardSet.pattern)) {
				cardSetMapByPattern.put(cardSet.pattern,
						new ArrayList<CardSet>());
			}
			cardSetMapByPattern.get(cardSet.pattern).add(cardSet);
		}

		return cardSetMapByPattern;
	}
	
	
	public static void putCard(Game game, Player player){

		CardsetPattern pattern = game.playingPattern;
		CardSet playingCardSet = game.onDeskCards.peekLast();

		System.out.print("\n my cards : ");
		System.out.println(player.onHandDeck);

		@SuppressWarnings("resource")
		Scanner userInput = new Scanner(System.in);
		
		// 다시 입력모드
		do {

			// 카드를 입력한다.
			do {
				//현재 임시 덱에 있는 카드를 보여준다
				System.out.println("tempCardSetList \t "+player.tempForCardSet);
				
				System.out
						.print("select cards (finish=X, Dragon=Y, Pheonix=P, Dog=G, 1=1, ♥=H, ◇=D, ♣=C, ♤=S ) \n => ");
				String input = userInput.next();

				CardSuit suit = null;
				CardRank rank = null;

				if (input.length() == 1) {
					// 입력이 한 글자일 때
					if ("X".equalsIgnoreCase(input)) {
						// 턴 종료
						break;
					} else if ("Y".equalsIgnoreCase(input)) {
						// 용
						suit = CardSuit.DRAGON;
						rank = CardRank.DRAGON;
					} else if ("P".equalsIgnoreCase(input)) {
						// 봉황
						suit = CardSuit.PHOENIX;
						rank = CardRank.PHOENIX;
					} else if ("G".equalsIgnoreCase(input)) {
						// 개
						suit = CardSuit.DOG;
						rank = CardRank.DOG;
					} else if ("1".equalsIgnoreCase(input)) {
						// 선
						suit = CardSuit.MAH_JONG;
						rank = CardRank.ONE;
					}

				} else if (input.length() == 2) {
					// 입력이 두 글자일 때
					String inputSuit = input.substring(0, 1);
					String inputRank = input.substring(1, 2);

					if ("H".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.JADE;
					} else if ("D".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.SWORD;
					} else if ("C".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.PAGODA;
					} else if ("S".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.STAR;
					}

					if ("J".equalsIgnoreCase(inputRank)) {
						rank = CardRank.JACK;
					} else if ("Q".equalsIgnoreCase(inputRank)) {
						rank = CardRank.QUEEN;
					} else if ("K".equalsIgnoreCase(inputRank)) {
						rank = CardRank.KING;
					} else if ("A".equalsIgnoreCase(inputRank)) {
						rank = CardRank.ACE;
					} else if ("1".equalsIgnoreCase(inputRank)) {
						rank = CardRank.ONE;
					} else if ("2".equalsIgnoreCase(inputRank)) {
						rank = CardRank.TWO;
					} else if ("3".equalsIgnoreCase(inputRank)) {
						rank = CardRank.THREE;
					} else if ("4".equalsIgnoreCase(inputRank)) {
						rank = CardRank.FOUR;
					} else if ("5".equalsIgnoreCase(inputRank)) {
						rank = CardRank.FIVE;
					} else if ("6".equalsIgnoreCase(inputRank)) {
						rank = CardRank.SIX;
					} else if ("7".equalsIgnoreCase(inputRank)) {
						rank = CardRank.SEVEN;
					} else if ("8".equalsIgnoreCase(inputRank)) {
						rank = CardRank.EIGHT;
					} else if ("9".equalsIgnoreCase(inputRank)) {
						rank = CardRank.NINE;
					} 

				} else if (input.length() == 3) {
					// 입력이 두 글자일 때
					String inputSuit = input.substring(0, 1);
					String inputRank = input.substring(1, 3);

					if ("H".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.JADE;
					} else if ("D".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.SWORD;
					} else if ("C".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.PAGODA;
					} else if ("S".equalsIgnoreCase(inputSuit)) {
						suit = CardSuit.STAR;
					}
					
					if ("10".equalsIgnoreCase(inputRank)) {
						rank = CardRank.TEN;
					}
				}

				// 에러처리
				if (suit == null || rank == null) {
					System.out.println("잘못된 입력입니다. 다시 입력해주세요");
					continue;
				}

				// 사용자가 입력한 카드
				Card c = new Card(null, suit, rank, null);

				// 사용자 덱에 있는지 검사
				if (!player.onHandDeck.contains(c)) {
					System.out.println("카드가 없어요!");
				} else {
					// 임시 카드덱에 있는지 검사.
					if (player.onHandDeck.contains(c)) {
						player.tempForCardSet.add(c);
					} else {
						System.out.println("이미 낸 카드에요.");
					}
				}

			} while (true);

			// 임시 카드덱을 생성한다.
			List<CardSet> tempCardSetList = Rule
					.generateCardSet(player.tempForCardSet);
			System.out.println("tempCardSetList \t "+tempCardSetList);
			player.tempForCardSet = new ArrayList<Card>();
			
			if (tempCardSetList == null) {
				System.out.println("잘못된 입력");
			} else if(tempCardSetList.size() == 0){
				break;
			}else {
				CardSet tempCardSet = tempCardSetList.get(0);
				// 낼 수 있는지 검사

				// 게임진행 판 위에 카드가 없을경우
				if (playingCardSet == null) {
					player.handlePutCardSet(game, tempCardSet);
					break;
				} else {
					if ((tempCardSet.pattern.equals(pattern) && tempCardSet
							.compareTo(playingCardSet) > 0)
							|| tempCardSet.pattern.equals(
									CardsetPattern.FOUR_CARDS)
							|| tempCardSet.pattern.equals(
									CardsetPattern.STRAIGHT_FLUSH)) {

						player.handlePutCardSet(game, tempCardSet);
						break;
					}
				}
				// 판 위에 있는 카드보다 숫자가 높을 경우
				// 이 카드를 낸다!!

				// 패턴이 같거나, 폭탄이거나

				// TODO 봉황로직 추가해야한다.
			}

			

		} while (true);

		//userInput.close();
		// 카드를 낸다.

		// 턴을 종료한다.
	}
}
