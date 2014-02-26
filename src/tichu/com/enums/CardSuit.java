package tichu.com.enums;

public enum CardSuit {
	JADE("♥") // 하트
	, SWORD("◇") // 다이아몬드
	, PAGODA("♣") // 클로버
	, STAR("♤") // 스페이드
	, MAH_JONG("□") // 1카드
	, DOG("≒") // 개
	, PHOENIX("ψ") // 봉황
	, DRAGON("ξ"); // 용

	private String name;

	private CardSuit(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
