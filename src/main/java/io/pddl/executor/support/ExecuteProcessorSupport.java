package io.pddl.executor.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.executor.ExecuteUnit;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteProcessor;
import io.pddl.executor.InputWrapper;

public class ExecuteProcessorSupport implements ExecuteProcessor, DisposableBean {

	private Logger logger = LoggerFactory.getLogger(ExecuteProcessorSupport.class);

	private ConcurrentHashMap<String, ExecutorService> executorServiceMapping = new ConcurrentHashMap<String, ExecutorService>();

	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	private long timeout= 30;
	
	public ExecuteProcessorSupport(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					ExecuteProcessorSupport.this.destroy();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		});
	}
	
	public void setTimeout(long timeout){
		this.timeout= timeout;
	}

	public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository) {
		this.shardingDataSourceRepository = shardingDataSourceRepository;
	}

	@Override
	public <IN, OUT> List<OUT> execute(ExecuteContext ctx,List<InputWrapper<IN>> inputs,final ExecuteUnit<IN, OUT> executeUnit) {
		//if one size or transaction or DML operation
		if(inputs.size() == 1 || !ctx.isDQLWithoutTransaction()){
			List<OUT> result = new ArrayList<OUT>(inputs.size());
			for (InputWrapper<IN> each : inputs) {
				try {
					result.add(executeUnit.execute(each.getSQLExecutionUnit().getShardingSql(),each.getInput()));
				} catch (Exception e) {
					logger.error("execute statement error", e);
				}
			}
			return result;
		}
		//if only query ,use concurrency thread query result
		List<Future<OUT>> futures = new ArrayList<Future<OUT>>(inputs.size());
		for (final InputWrapper<IN> each : inputs) {
			ExecutorService executorService = getExecutorService(each.getSQLExecutionUnit().getDataSourceName());
			futures.add(executorService.submit(new Callable<OUT>() {
				@Override
				public OUT call() throws Exception {
					return executeUnit.execute(each.getSQLExecutionUnit().getShardingSql(),each.getInput());
				}
			}));
		}
		List<OUT> result = new ArrayList<OUT>(inputs.size());
		for (Future<OUT> it : futures) {
			try {
				result.add(it.get(timeout,TimeUnit.SECONDS));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("execute statement error", e);
			}
		}
		return result;
	}
	
	private ExecutorService getExecutorService(String dataSourceName) {
		ExecutorService executorService = executorServiceMapping.get(dataSourceName);
		if (executorService == null) {
			executorServiceMapping.putIfAbsent(dataSourceName, createExecutorForDataSourceParition(dataSourceName));
			if (null == (executorService = executorServiceMapping.get(dataSourceName))) {
				return getExecutorService(dataSourceName);
			}
		}
		return executorService;
	}
	

	private ExecutorService createExecutorForDataSourceParition(final String dataSourceName) {
		PartitionDataSource dataSource = shardingDataSourceRepository.getPartitionDataSource(dataSourceName);
		final String method= "createExecutorForDataSource-" + dataSourceName + " data source";
		int poolSize= dataSource.getPoolSize();
		int timeout= dataSource.getTimeout();
		int coreSize = Runtime.getRuntime().availableProcessors();
		if (poolSize < coreSize) {
			coreSize = poolSize;
		}
		ThreadFactory tf = new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "thread created at pddl method [" + method + "]");
				t.setDaemon(true);
				return t;
			}
		};
		BlockingQueue<Runnable> queueToUse = new LinkedBlockingQueue<Runnable>(coreSize);
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, poolSize, timeout, TimeUnit.SECONDS, queueToUse,
				tf, new ThreadPoolExecutor.CallerRunsPolicy());

		return executor;
	}

	@Override
	public void destroy() throws Exception {
		if (!CollectionUtils.isEmpty(executorServiceMapping)) {
			logger.info("shutdown executors of pddl...");
			for (ExecutorService executor : executorServiceMapping.values()) {
				if (executor != null) {
					try {
						executor.shutdown();
						executor.awaitTermination(5, TimeUnit.MINUTES);
					} catch (InterruptedException e) {
						logger.warn("interrupted when shuting down the query executor:\n{}", e);
					}
				}
			}
			executorServiceMapping.clear();
			logger.info("all of the executor services in pddl are disposed.");
		}
	}
}
