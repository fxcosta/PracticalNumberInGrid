package org.unime.practical;

import org.jppf.node.protocol.AbstractTask;

public class PracticalNumberCalculationTask extends AbstractTask<String> {

	private static final long serialVersionUID = 1L;
	
	private Integer begin;
	private Integer end;
	
	private PracticalNumberCalculation calculator;
	
	/**
	 * Constructor
	 * @param begin
	 * @param end
	 */
	public PracticalNumberCalculationTask(Integer begin, Integer end) {
		super();
		this.begin 		= begin;
		this.end 		= end;
//		this.calculator = new PracticalNumberCalculation();
	}
	
	@Override
	public void run() {
		
		try {
			int conjunto[] = this.getPracticalNumbers(this.begin, this.end);
			String conjuntoPrint = "";
			
			for (int i : conjunto) {
				conjuntoPrint += String.valueOf(i) + ", ";
				
				if (i > 0) {
					System.out.println("Encontramos um número: " + i);
				} else {
					System.err.println("erro");
				}
			}
			
			setResult(conjuntoPrint); //Resultado da execu��o: Lista de resultados. Conjunto ex: (3,4,5)
			
		} catch (Exception e) {
			e.getMessage();
		}
//		
//		System.out.println("Exemplo durante hungouts");
//		System.out.println("Isso � um padr�o do JPPF");
//		
//		setResult("Felix");//modifica eventualmente o resultado do processamento
	}
	
	public int[] getPracticalNumbers(int from, int to) {
		int a[] = new int[to-from];
		int indx = 0;
		
		out:for (int i = from; i <= to;i++) {
			int sum = 0;
			for(int j = 1; j <= i/2; j++) {
				if(i%j == 0) {
					if(sum < j-1)
					continue out;
					sum += j;
				}
			}
			
			if(sum >= i-1) {
				a[indx++] = i;
			}
		}
		
		int count = 0;
		for (int i = 0; i < a.length; i++) {
			if(a[i] != 0)
			count++;
		}
		
		int ans[] = new int[count];
		for (int i = 0; i < count; i++) {
			ans[i] = a[i];
		}
		
		return ans;
		}
}
