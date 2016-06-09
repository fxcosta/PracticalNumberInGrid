package org.unime.practical;

import java.util.*;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.client.Operator;
import org.jppf.node.protocol.Task;

public class AppRunner {

	private final int NUMEROMINIMO = 1;
	private final int NUMEROMAXIMO = 4000;

	public static void main(final String... args) {

		try (JPPFClient jppfClient = new JPPFClient()) {

			AppRunner runner = new AppRunner();

			// runner.executeBlockingJob(jppfClient);

			// runner.executeNonBlockingJob(jppfClient);

			runner.executeMultipleConcurrentJobsNumberByNumber(jppfClient, 5);
			// runner.executeMultipleConcurrentJobsNumberRange(jppfClient, 5);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cria um trabalho para ser submetido para execução
	 * 
	 * @param jobName
	 *            pode ser qualquer nome para job
	 * @return an instance of the {@link org.jppf.client.JPPFJob JPPFJob} class.
	 * @throws Exception
	 *             if an error occurs while creating the job or adding tasks.
	 */
	public JPPFJob createJob(final String jobName, Integer begin) throws Exception {
		JPPFJob job = new JPPFJob();
		job.setName(jobName);

		Task<?> task = job.add(new PracticalNumberCalculationTask(begin));
		task.setId("Tarefa " + jobName);

		return job;
	}

	/**
	 * 
	 * @param jobName
	 * @param begin
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public JPPFJob createJob(final String jobName, int begin, int end) throws Exception {
		JPPFJob job = new JPPFJob();
		job.setName(jobName);

		Task<?> task = job.add(new PracticalNumberCalculationTask(begin, end));
		task.setId("Tarefa " + jobName);

		return job;
	}

	/**
	 * Executa o Job em modo de bloqueio. A aplica��o � bloqueada at� que o Job
	 * termine
	 * 
	 * @param jppfClient
	 *            the {@link JPPFClient} instance which submits the job for
	 *            execution.
	 * @throws Exception
	 *             if an error occurs while executing the job.
	 */
	// public void executeBlockingJob(final JPPFClient jppfClient) throws
	// Exception {
	// // Cria o job
	// JPPFJob job = createJob("modelo de blocking job");
	//
	// // Define o Job como Blocking (Bloquia a aplica��o durante sua execu��o)
	// job.setBlocking(true);
	//
	// // Envia os Jobs e aguarda os rsultados
	// // Os resutados s�o retornados em uma lista de instancias da classe
	// Task<?> ,
	// // na mesma ordem que os trabalhos foram adicionados
	// List<Task<?>> results = jppfClient.submitJob(job);
	//
	// // Processa os resultados
	// processExecutionResults(job.getName(), results);
	// }

	/**
	 * Executa o job sem bloquear a app. A aplica��o tem a responsabilidade
	 * tratar os dados quando acabar as tarefas. Execu��o assincrona
	 * 
	 * @param jppfClient
	 *            the {@link JPPFClient} instance which submits the job for
	 *            execution.
	 * @throws Exception
	 *             if an error occurs while executing the job.
	 */
	// public void executeNonBlockingJob(final JPPFClient jppfClient) throws
	// Exception {
	// // Cria um trabalho
	// JPPFJob job = createJob("Modelo de non-blocking job1");
	//
	// // define a tarefa como non-blocking (Assincrono)
	// job.setBlocking(false);
	//
	// // Envia o Job. essa execu��o � assincrona, n�o aguarda a conclus�o dos
	// jobs para continuar
	// // por esse motivo o retorno � sempre nulo
	// // note que � o mesmo m�todo utilizado em jobs com bloqueio
	// jppfClient.submitJob(job);
	//
	// // A execu��o continua...
	// System.out.println("O(s) job(s) foram submetidos para execu��o...");
	// // ...
	//
	// // Este m�todo aguarda o retorno de todos os jobs
	// List<Task<?>> results = job.awaitResults();
	//
	// // processa os resultados
	// processExecutionResults(job.getName(), results);
	// }

	/**
	 * Executa v�rios jobs simuntaneos no JPPFClient.
	 * <p>
	 * This is an extension of the {@code executeNonBlockingJob()} method, with
	 * one additional step: to ensure that a sufficient number of connections to
	 * the server are present, so that jobs can be submitted concurrently. The
	 * number of connections determines the number of jobs that can be submitted
	 * in parallel. It can be set in the JPPF configuration or dynamically with
	 * the {@link JPPFConnectionPool} API.
	 * <p>
	 * As a result, the call to {@code executeNonBlockingJob(jppfClient)} is
	 * effectively equivalent to
	 * {@code executeMultipleConccurentJobs(jppfClient, 1)}.
	 * <p>
	 * There are many patterns that can be applied to parallel job execution,
	 * you are encouraged to read the <a href=
	 * "http://www.jppf.org/doc/v4/index.php?title=Submitting_multiple_jobs_concurrently">
	 * dedicated section</a> of the JPPF documentation for details and code
	 * samples.
	 * 
	 * @param jppfClient
	 *            the JPPF client which submits the jobs.
	 * @param numberOfJobs
	 *            the number of jobs to execute.
	 * @throws Exception
	 *             if any error occurs.
	 */
	public void executeMultipleConcurrentJobsNumberByNumber(final JPPFClient jppfClient, final int numberOfJobs)
			throws Exception {

		ensureNumberOfConnections(jppfClient, numberOfJobs);

		List<JPPFJob> jobList = new ArrayList<>(numberOfJobs);
		int contador = 0;
		int contador2 = 0;

		for (int i = NUMEROMINIMO; i <= NUMEROMAXIMO; i++) {

			contador++;
			contador2++;
			List<Integer> tarefas = new ArrayList<Integer>();

			// JPPFJob job = createJob("Job Non-Blocking: " + i, i -
			// (NUMEROMAXIMO / numberOfJobs), i);

			JPPFJob job = createJob("Non-Blocking: " + i, i);

			job.setBlocking(false);
			jppfClient.submitJob(job);
			jobList.add(job);
		}

		for (JPPFJob job : jobList) {
			List<Task<?>> results = job.awaitResults();
			processExecutionResults(job.getName(), results);
			jobList = new ArrayList<>(numberOfJobs);
		}
	}

	/**
	 * 
	 * @param jppfClient
	 * @param numberOfJobs
	 * @throws Exception
	 */
	public void executeMultipleConcurrentJobsNumberRange(final JPPFClient jppfClient, final int numberOfJobs)
			throws Exception {

		ensureNumberOfConnections(jppfClient, numberOfJobs);

		List<JPPFJob> jobList = new ArrayList<>(numberOfJobs);
		int contador = 0;
		int contador2 = 0;

		for (int i = NUMEROMINIMO; i <= NUMEROMAXIMO; i++) {

			contador++;
			contador2++;
			List<PracticalNumberCalculationTask> tarefas = new ArrayList<PracticalNumberCalculationTask>();

			if (contador == (NUMEROMAXIMO / numberOfJobs) / 2) {
				contador = 0;

				JPPFJob job = createJob("Non-Blocking: " + i, i - (NUMEROMAXIMO / numberOfJobs) / 2, i);
				jppfClient.submitJob(job);
				jobList.add(job);
			}

			// if (contador2 == 1000) {
			// contador2 = 0;

			for (JPPFJob job : jobList) {
				List<Task<?>> results = job.awaitResults();
				processExecutionResults(job.getName(), results);
				jobList = new ArrayList<>(numberOfJobs);
			}
			// }
		}
	}

	/**
	 * Define o numero de trabalhos para o JPPFClient
	 * 
	 * @param jppfClient
	 *            the JPPF client which submits the jobs.
	 * @param numberOfConnections
	 *            the desired number of connections.
	 * @throws Exception
	 *             if any error occurs.
	 */
	public void ensureNumberOfConnections(final JPPFClient jppfClient, final int numberOfConnections) throws Exception {
		JPPFConnectionPool pool = jppfClient.awaitActiveConnectionPool();

		if (pool.getConnections().size() != numberOfConnections) {
			pool.setSize(numberOfConnections);
		}

		pool.awaitActiveConnections(Operator.AT_LEAST, numberOfConnections);
	}

	/**
	 * Processa os resultados de todas as tarefas.
	 * 
	 * @param jobName
	 *            the name of the job whose results are processed.
	 * @param results
	 *            the tasks results after execution on the grid.
	 */
	public synchronized void processExecutionResults(final String jobName, final List<Task<?>> results) {
		// System.out.printf("Resultados para o Job: '%s' :\n", jobName);
		for (Task<?> task : results) {
			String taskName = task.getId();
			if (task.getThrowable() != null) {
				System.out.println(taskName + ", an exception was raised: " + task.getThrowable().getMessage());
			} else {

				if (task.getResult() != null) {
					System.out.println(taskName + ", resultado da computação: " + task.getResult() + " é prático!");
				} else {
					System.out.println(taskName + ", resultado da computação: Não é prático!");
				}

			}
		}
	}
}
