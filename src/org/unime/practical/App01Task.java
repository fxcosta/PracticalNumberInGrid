package org.unime.practical;

import org.jppf.node.protocol.AbstractTask;

public class App01Task extends AbstractTask<String> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void run() {
		  // write your task code here.
		  System.out.println("*** Inicio de Execu��o de tarefa ***");

		  // simply wait for 3 seconds
		  try {
		    Thread.sleep(3000L);
		    System.out.println("*** Fim da tarefa***");
		  } catch(InterruptedException e) {
			  e.printStackTrace();
		    return;
		  }

		  setResult("Resultado do processamento");//pode ser qualquer objeto
		}

}
