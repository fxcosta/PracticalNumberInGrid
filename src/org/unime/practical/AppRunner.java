package org.unime.practical;

import java.util.*;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.client.Operator;
import org.jppf.node.protocol.Task;

public class AppRunner {

	private final int NUMEROMINIMO = 1;
	private final int NUMEROMAXIMO = 40000;
	
  public static void main(final String...args) {

    // Cria o JPPF Client
    // e conecta-se com um ou vários drivers JPPF
    try (JPPFClient jppfClient = new JPPFClient()) {

      // Cria uma instancia do app.
      AppRunner runner = new AppRunner();

      // Cria e executa um job que bloqueia a app no nó que o processa
//      runner.executeBlockingJob(jppfClient);

      // Cria e executa um job que não bloqueia a app no nó que o processa
//      runner.executeNonBlockingJob(jppfClient);

      // Cria e executa 3 jobs em paralelo
      runner.executeMultipleConcurrentJobs(jppfClient, 5);

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Cria um trabalho para ser submetido para execução
   * @param jobName pode ser qualquer nome para job
   * @return an instance of the {@link org.jppf.client.JPPFJob JPPFJob} class.
   * @throws Exception if an error occurs while creating the job or adding tasks.
   */
  public JPPFJob createJob(final String jobName, int begin, int end) throws Exception {
    // Cria o JPPF job
    JPPFJob job = new JPPFJob();
    
    // Da ao trabalho um nome, para que possamos controla-lo
    job.setName(jobName);

    // Adiciona uma tarefa ao job.
    Task<?> task = job.add(new PracticalNumberCalculationTask(begin, end));
    
    // Atribui um nome a tarefa
    task.setId(jobName + " - Exemplo de Tarefa");

    // As tarefas são executadas de forma assincrona
    // mas os resultados são devolvidos na mesma ordem
    return job;
  }

  /**
   * Executa o Job em modo de bloqueio. A aplica��o � bloqueada at� que o Job termine
   * @param jppfClient the {@link JPPFClient} instance which submits the job for execution.
   * @throws Exception if an error occurs while executing the job.
   */
//  public void executeBlockingJob(final JPPFClient jppfClient) throws Exception {
//    // Cria o job
//    JPPFJob job = createJob("modelo de blocking job");
//
//    // Define o Job como Blocking (Bloquia a aplica��o durante sua execu��o)
//    job.setBlocking(true);
//
//    // Envia os Jobs e aguarda os rsultados
//    // Os resutados s�o retornados em uma lista de instancias da classe Task<?> ,
//    // na mesma ordem que os trabalhos foram adicionados 
//    List<Task<?>> results = jppfClient.submitJob(job);
//
//    // Processa os resultados
//    processExecutionResults(job.getName(), results);
//  }

  /**
   * Executa o job sem bloquear a app. A aplica��o tem a responsabilidade tratar os dados quando acabar as tarefas. Execu��o assincrona
   * @param jppfClient the {@link JPPFClient} instance which submits the job for execution.
   * @throws Exception if an error occurs while executing the job.
   */
//  public void executeNonBlockingJob(final JPPFClient jppfClient) throws Exception {
//    // Cria um trabalho
//    JPPFJob job = createJob("Modelo de non-blocking job1");
//
//    // define a tarefa como non-blocking (Assincrono) 
//    job.setBlocking(false);
//
//    // Envia o Job. essa execu��o � assincrona, n�o aguarda a conclus�o dos jobs para continuar
//    // por esse motivo o retorno � sempre nulo
//    // note que � o mesmo m�todo utilizado em jobs com bloqueio
//    jppfClient.submitJob(job);
//
//    // A execu��o continua...
//    System.out.println("O(s) job(s) foram submetidos para execu��o...");
//    // ...
//
//    // Este m�todo aguarda o retorno de todos os jobs 
//    List<Task<?>> results = job.awaitResults();
//
//    // processa os resultados
//    processExecutionResults(job.getName(), results);
//  }

  /**
   * Executa v�rios jobs simuntaneos no JPPFClient.
   * <p>This is an extension of the {@code executeNonBlockingJob()} method, with one additional step:
   * to ensure that a sufficient number of connections to the server are present, so that jobs can be submitted concurrently.
   * The number of connections determines the number of jobs that can be submitted in parallel.
   * It can be set in the JPPF configuration or dynamically with the {@link JPPFConnectionPool} API.
   * <p>As a result, the call to {@code executeNonBlockingJob(jppfClient)} is effectively
   * equivalent to {@code executeMultipleConccurentJobs(jppfClient, 1)}.
   * <p>There are many patterns that can be applied to parallel job execution, you are encouraged to read
   * the <a href="http://www.jppf.org/doc/v4/index.php?title=Submitting_multiple_jobs_concurrently">dedicated section</a>
   * of the JPPF documentation for details and code samples. 
   * @param jppfClient the JPPF client which submits the jobs.
   * @param numberOfJobs the number of jobs to execute.
   * @throws Exception if any error occurs.
   */
  public void executeMultipleConcurrentJobs(final JPPFClient jppfClient, final int numberOfJobs) throws Exception {
  //Degine o numero de jobs em paralelo neste JPPFClient
    ensureNumberOfConnections(jppfClient, numberOfJobs);

  //Lista para armazenar os jobs; Utilizada para colher os resultados posteriormente
    final List<JPPFJob> jobList = new ArrayList<>(numberOfJobs);

    // Cria e submete os trabalhos
    for (int i = 1; i <= NUMEROMAXIMO; i += (NUMEROMAXIMO/numberOfJobs)) {
    	
      // Cria os trabalhos com nomes diferentes
      JPPFJob job = createJob("Template concurrent job " + i, i, i+80);

      // define o job como non-blocking. (assincrono)
      job.setBlocking(false);

      // envia o job para execução sem parar o processamento (assincrono)
      jppfClient.submitJob(job);

      // add o job a lista para uso posterior
      jobList.add(job);
    }

    // Equando os Jobs est�o executando podemos fazer outras coisas
    System.out.println("Equando os Jobs estão executando podemos fazer outras coisas");

    // aguarda a execução de todos os jobs e tarefas do job e processa o resultado
    for (JPPFJob job: jobList) {
      // aguarda a execução de todos os jobs e tarefas do job
      List<Task<?>> results = job.awaitResults();

      // Processa o resultado
      processExecutionResults(job.getName(), results);
    }
  }

  /**
   * Define o numero de trabalhos para o JPPFClient  
   * @param jppfClient the JPPF client which submits the jobs.
   * @param numberOfConnections the desired number of connections.
   * @throws Exception if any error occurs.
   */
  public void ensureNumberOfConnections(final JPPFClient jppfClient, final int numberOfConnections) throws Exception {
    // wait until the client has at least one connection pool with at least one avaialable connection
    JPPFConnectionPool pool = jppfClient.awaitActiveConnectionPool();

    // Se o a quantidade definida no pool for diferente do definido pela app
    if (pool.getConnections().size() != numberOfConnections) {
      // define a quantidade de conexões
      pool.setSize(numberOfConnections);
    }

    // Espera até que todas as conexões estejam disponíveis no pool
    pool.awaitActiveConnections(Operator.AT_LEAST, numberOfConnections);
  }

  /**
   * Processa os resultados de todas as tarefas.
   * @param jobName the name of the job whose results are processed. 
   * @param results the tasks results after execution on the grid.
   */
  public synchronized void processExecutionResults(final String jobName, final List<Task<?>> results) {
    // print a results header
    System.out.printf("Resultados para o Job: '%s' :\n", jobName);
    // Processa os resultados
    for (Task<?> task: results) {
      String taskName = task.getId();
      // if the task execution resulted in an exception
      if (task.getThrowable() != null) {
        // process the exception here ...
        System.out.println(taskName + ", an exception was raised: " + task.getThrowable ().getMessage());
      } else {
        // processamento dos resultados das tarefas ...
        System.out.println(taskName + ", resultado da execução: " + task.getResult());
      }
    }
  }
}
