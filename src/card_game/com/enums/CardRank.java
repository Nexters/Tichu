package card_game.com.enums;

public enum CardRank {
	DOG(0, ""), ONE(1, "1"), TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5,
			"5"), SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"), TEN(
			10, "10"), JACK(11, "J"), QUEEN(12, "Q"), KING(13, "K"), ACE(14,
			"A"), PHOENIX(15, ""), DRAGON(16, "");

	private int value;
	private String name;

	private CardRank(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return this.value;
	}

	public String getName() {
		return name;
	}

}
