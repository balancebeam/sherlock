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
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.executor.ExecuteContext;
import io.pddl.executor.ExecuteStatementCallback;
import io.pddl.executor.ExecuteStatementProcessor;

/**
 * 多个Statment处理器
 * @author yangzz
 *
 */
public class ExecuteProcessorSupport implements ExecuteStatementProcessor {

	private Log logger = LogFactory.getLog(ExecuteProcessorSupport.class);

	private long timeout= 30;
	
	/**
	 * 设置执行Statement操作的超时时间，默认是30秒
	 * @param timeout
	 */
	public void setTimeout(long timeout){
		this.timeout= timeout;
	}

	@Override
	public <IN extends Statement, OUT> List<OUT> execute(
			final ExecuteContext ctx,
			List<ExecuteStatementWrapper<IN>> wrappers,
			final ExecuteStatementCallback<IN, OUT> executeUnit) throws SQLException{
		//如果只有一个Statement对象
		if(wrappers.size() == 1){
			String actualSql= wrappers.get(0).getSQLExecutionUnit().getShardingSql();
			return Collections.singletonList(executeUnit.execute(actualSql,wrappers.get(0).getStatement()));
		} 
		//或者是DML或InTransaction操作则需要顺序执行
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
				ExecutorService executorService = ctx.getShardingDataSourceRepository().getPartitionDataSource(each.getKey()).getExecutorService();
				futures.add(executorService.submit(new Callable<List<OUT>>(){
					@Override
					public List<OUT> call() throws Exception {
						List<OUT> rs= new ArrayList<OUT>(each.getValue().size());
						for(ExecuteStatementWrapper<IN> it: each.getValue()){
							//只有有一个有错就抛出，认为整个操作不成功，合并结果没意义
							rs.add(executeUnit.execute(it.getSQLExecutionUnit().getShardingSql(),it.getStatement()));
						}
						return rs;
					}
				}));
			}
			try {
				List<OUT> result = new ArrayList<OUT>(wrappers.size());
				for (Future<List<OUT>> each : futures) {
					result.addAll(each.get(timeout,TimeUnit.SECONDS));
				}
				return result;
			} catch (Exception e) {
				//cancel other running task，release thread immediately
				for (Future<List<OUT>> each : futures) {
					if(each instanceof FutureTask){
						((FutureTask<List<OUT>>)each).cancel(true);
					}
				}
				throw new SQLException(e.getMessage(),e);
			}
		}
		//如果是只读查询需要进行并行处理
		List<Future<OUT>> futures = new ArrayList<Future<OUT>>(wrappers.size());
		for (final ExecuteStatementWrapper<IN> each : wrappers) {
			//根据不同的数据源获取不同的线程池对象
			ExecutorService executorService = ctx.getShardingDataSourceRepository().getPartitionDataSource(each.getSQLExecutionUnit().getDataSourceName()).getExecutorService();
			futures.add(executorService.submit(new Callable<OUT>() {
				@Override
				public OUT call() throws Exception {
					return executeUnit.execute(each.getSQLExecutionUnit().getShardingSql(),each.getStatement());
				}
			}));
		}
		//依次获取执行结果
		try {
			List<OUT> result = new ArrayList<OUT>(wrappers.size());
			for (Future<OUT> each : futures) {
				result.add(each.get(timeout,TimeUnit.SECONDS));
			}
			return result;
		} catch (Exception e) {
			//cancel other running task，release thread immediately
			for (Future<OUT> each : futures) {
				if(each instanceof FutureTask){
					((FutureTask<OUT>)each).cancel(true);
				}
			}
			throw new SQLException(e.getMessage(),e);
		}
	}
}
