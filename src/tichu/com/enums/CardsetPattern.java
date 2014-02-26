package tichu.com.enums;

public enum CardsetPattern {
	SINGLE_CARD(0)
	, PAIR(0)
	, STAIRS(0)
	, TRIPLE(0)
	, STRAIGHTS(0)
	, FULL_HOUSES(0)
	, FOUR_CARDS(1)
	, STRAIGHT_FLUSH(2);
	
	private int value;
	
	private CardsetPattern(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
