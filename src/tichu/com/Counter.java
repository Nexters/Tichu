package tichu.com;

public class Counter {
	private int cnt = 0;

	/**
	 * 플레이어가 작업을 완료할 때 마다 카운터를 증가시킨다.
	 */
	public void check() {
		cnt++;
	}

	/**
	 * 준비상태를 체크한다. 카운터가 4가 되어야(모든 플레이어가 준비되어야) true를 반환한다. 이때 카운터를 초기화시킨다.
	 * 
	 * @return 준비상태.
	 */
	public boolean isReady() {
		if (cnt == 4) {
			cnt = 0;
			return true;
		} else {
			return false;
		}
	}
}
