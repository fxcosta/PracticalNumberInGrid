package org.unime.practical;

public class PracticalNumberCalculation {
	
	private static final long serialVersionUID = 1L;
	
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
		 
		//  You could use this sample code to test function
		//  Following main fucntion contains 3 representative test cases
//		public static void main(String[] arg) {
//		PracticalNumberCalculation pn = new PracticalNumberCalculation();
//		//1st test case
//		int[] res = pn.getPracticalNumbers(1, 20);
//		for (int i = 0; i < res.length; i++)
//		System.out.print(res[i] + " ");
//		System.out.println();
//		//2nd test case
//		int[] res2 = pn.getPracticalNumbers(8000, 8200);
//		for (int i = 0; i < res2.length; i++)
//		System.out.print(res2[i] + " ");
//		System.out.println();
//		 
//		//3rd test case
//		int[] res3 = pn.getPracticalNumbers(1000, 1120);
//		for (int i = 0; i< res3.length; i++)
//		System.out.print(res3[i] + " ");
//		System.out.println();
//		}
}


