package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinsChange2 {

	public static void main(String[] args) {
		new CoinsChange2().coinChange(new int[]{1, 2, 5}, 11, List.of());
	}

	public int coinChange(int[] coins, int amount, List<Integer> aux) {
		int sum = aux.stream().mapToInt(a -> a).sum();
		if (aux.size() != 0 && sum >= amount) {
			if (sum == amount) {
				return 1;
			}
			return 0;
		}
		int res = 0;
		for (int i = 0; i < coins.length; i++) {
			int c = coins[i];
			List<Integer> newAux = new ArrayList<>(aux);
			newAux.add(c);
			res += coinChange(Arrays.copyOfRange(coins, i, coins.length), amount, newAux);
		}
		return res;
	}


}
