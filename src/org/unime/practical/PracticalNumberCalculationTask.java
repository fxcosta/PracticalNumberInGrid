package org.unime.practical;

import org.jppf.node.protocol.AbstractTask;

public class PracticalNumberCalculationTask extends AbstractTask<String> {

	private static final long serialVersionUID = 1L;

	private Integer begin;
	private Integer end;

	private PracticalNumberCalculation calculator;

	/**
	 * Constructor
	 * 
	 * @param begin
	 * @param end
	 */
	public PracticalNumberCalculationTask(Integer begin, Integer end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	/**
	 * 
	 * @param number
	 */
	public PracticalNumberCalculationTask(Integer number) {
		super();
		this.begin = number;
		this.end = null;
	}

	@Override
	public void run() {

		try {
			String conjuntoPrint = "";

			if (this.end == null) {
				boolean practical = this.isAPracticalNumber(this.begin);

				if (practical) {
					conjuntoPrint += String.valueOf(this.begin) + ", ";
					System.out.println(this.begin + " é um número prático");
				}
			} else {
				
				int conjunto[] = this.getPracticalNumbers(this.begin, this.end);
				for (int i : conjunto) {
					conjuntoPrint += String.valueOf(i) + ", ";
					if (i > 0) {
						System.out.println(i + " é um número prático");
					} else {
						System.err.println("Houve algum problema com o cálculo");
					}
				}
			}

			setResult(conjuntoPrint);

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * Identifica e retorna os números práticos de um determinado range
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public int[] getPracticalNumbers(int from, int to) {
		int a[] = new int[to - from];
		int indx = 0;

		out: for (int i = from; i <= to; i++) {
			int sum = 0;
			for (int j = 1; j <= i / 2; j++) {
				if (i % j == 0) {
					if (sum < j - 1)
						continue out;
					sum += j;
				}
			}

			if (sum >= i - 1) {
				a[indx++] = i;
			}
		}

		int count = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != 0)
				count++;
		}

		int ans[] = new int[count];
		for (int i = 0; i < count; i++) {
			ans[i] = a[i];
		}

		return ans;
	}

	/**
	 * Identifica se o número passado é um número prático
	 * 
	 * @param number
	 * @return
	 */
	public boolean isAPracticalNumber(int number) {
		int sum = 0;
		for (int j = 1; j <= number / 2; j++) {
			if (number % j == 0) {
				if (sum < j - 1) {
					return false;
				}
				sum += j;
			}
		}

		if (sum >= number - 1) {
			return true;
		}

		return false;
	}
}
