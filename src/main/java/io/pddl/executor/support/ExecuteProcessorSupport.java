package io.pddl.executor.support;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.ShardingDataSourceRepository;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteStatementCallback;
import io.pddl.executor.ExecuteStatementProcessor;

/**
 * 多个Statment处理器
 * @author yangzz
 *
 */
public class ExecuteProcessorSupport implements ExecuteStatementProcessor, DisposableBean {

	private Log logger = LogFactory.getLog(ExecuteProcessorSupport.class);

	private ConcurrentHashMap<String, ExecutorService> executorServiceMapping = new ConcurrentHashMap<String, ExecutorService>();

	private ShardingDataSourceRepository shardingDataSourceRepository;
	
	private long timeout= 30;
	
	public ExecuteProcessorSupport(){
		//JVM在停止时需要清空线程池对象
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
	/**
	 * 设置执行Statement操作的超时时间，默认是30秒
	 * @param timeout
	 */
	public void setTimeout(long timeout){
		this.timeout= timeout;
	}

	public void setShardingDataSourceRepository(ShardingDataSourceRepository shardingDataSourceRepository) {
		this.shardingDataSourceRepository = shardingDataSourceRepository;
	}

	@Override
	public <IN extends Statement, OUT> List<OUT> execute(ExecuteContext ctx,List<ExecuteStatementWrapper<IN>> wrappers,final ExecuteStatementCallback<IN, OUT> executeUnit) throws SQLException{
		//如果只有一个Statement对象
		if(wrappers.size() == 1){
			return Collections.singletonList(executeUnit.execute(wrappers.get(0).getSQLExecutionUnit().getShardingSql(),wrappers.get(0).getStatement()));
		} 
		//或者是写操作、带事务操作则需要顺序执行
		if(!ctx.isSimplyDQLOperation()){
			Map<String,List<ExecuteStatementWrapper<IN>>> hash= new HashMap<String,List<ExecuteStatementWrapper<IN>>>();
			for (ExecuteStatementWrapper<IN> each : wrappers) {
				String dataSourceName= each.getSQLExecutionUnit().getDataSourceName();
				if(!hash.containsKey(dataSourceName)){
					hash.put(dataSourceName, new ArrayList<ExecuteStatementWrapper<IN>>());
				}
				hash.get(dataSourceName).add(each);
			}
			if(logger.isInfoEnabled()){
				logger.info("merge ExecuteStatementWrapper by same dataSource name: " + hash);
			}
			List<Future<List<OUT>>> futures = new ArrayList<Future<List<OUT>>>(hash.size());
			for(final Entry<String,List<ExecuteStatementWrapper<IN>>> each: hash.entrySet()){
				ExecutorService executorService = getExecutorService(each.getKey());
				futures.add(executorService.submit(new Callable<List<OUT>>(){
					@Override
					public List<OUT> call() throws Exception {
						List<OUT> rs= new ArrayList<OUT>(each.getValue().size());
						for(ExecuteStatementWrapper<IN> it: each.getValue()){
							try {
								rs.add(executeUnit.execute(it.getSQLExecutionUnit().getShardingSql(),it.getStatement()));
							} catch (Exception e) {
								logger.error("execute statement error", e);
							}
						}
						return rs;
					}
				}));
			}
			List<OUT> result = new ArrayList<OUT>(wrappers.size());
			for (Future<List<OUT>> each : futures) {
				try {
					result.addAll(each.get(timeout,TimeUnit.SECONDS));
				} catch (Exception e) {
					logger.warn("execute statement error", e);
					//TODO 结果吃掉了后续需要做处理
				}
			}
			return result;
		}
		//如果是只读查询需要进行并行处理
		List<Future<OUT>> futures = new ArrayList<Future<OUT>>(wrappers.size());
		for (final ExecuteStatementWrapper<IN> each : wrappers) {
			//根据不同的数据源获取不同的线程池对象
			ExecutorService executorService = getExecutorService(each.getSQLExecutionUnit().getDataSourceName());
			futures.add(executorService.submit(new Callable<OUT>() {
				@Override
				public OUT call() throws Exception {
					return executeUnit.execute(each.getSQLExecutionUnit().getShardingSql(),each.getStatement());
				}
			}));
		}
		List<OUT> result = new ArrayList<OUT>(wrappers.size());
		//依次获取执行结果
		for (Future<OUT> each : futures) {
			try {
				result.add(each.get(timeout,TimeUnit.SECONDS));
			} catch (Exception e) {
				logger.warn("execute statement error", e);
				//TODO 结果吃掉了后续需要做处理
			}
		}
		return result;
	}
	
	private ExecutorService getExecutorService(String dataSourceName) {
		ExecutorService executorService = executorServiceMapping.get(dataSourceName);
		if (executorService == null) {
			executorServiceMapping.putIfAbsent(dataSourceName, createExecutorForParitionDataSource(dataSourceName));
			if (null == (executorService = executorServiceMapping.get(dataSourceName))) {
				return getExecutorService(dataSourceName);
			}
		}
		return executorService;
	}
	
	/*
	 * 根据数据的名称创建对应的线程池对象，各数据源的多并发执行互不影响
	 */
	private ExecutorService createExecutorForParitionDataSource(final String dataSourceName) {
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
		if(logger.isInfoEnabled()){
			logger.info("create executorService(poolSize="+poolSize+",timeout="+timeout+") for partition dataSource: "+dataSourceName);
		}
		return executor;
	}

	@Override
	public void destroy() throws Exception {
		if (!CollectionUtils.isEmpty(executorServiceMapping)) {
			if(logger.isInfoEnabled()){
				logger.info("shutdown executors of pddl...");
			}
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
			if(logger.isInfoEnabled()){
				logger.info("all of the executor services in pddl are disposed.");
			}
		}
	}
}
