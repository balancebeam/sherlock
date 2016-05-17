package com.alibaba.cobar.client.sequence.support;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.cobar.client.exception.ShardingException;
import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class ZookeeperPartitionSequenceGenerator implements SequenceGenerator, InitializingBean {

	private Log logger = LogFactory.getLog(ZookeeperPartitionSequenceGenerator.class);

	private ConcurrentHashMap<String, Future<AtomicLong>> sequenceRepository = new ConcurrentHashMap<String, Future<AtomicLong>>();

	private CuratorFramework client;

	private String zkAddr;

	private volatile long boundaryMaxValue = 0;

	private long incrStep = 1000;

	private int retry = 3;

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	@Override
	public long nextval(String name) {

		Future<AtomicLong> future = sequenceRepository.get(name);
		AtomicLong nextval = null;

		try {
			if (future == null) {
				FutureTask<AtomicLong> newFuture = new FutureTask<>(new DBSequenceBuilderTask(name));
				if (sequenceRepository.putIfAbsent(name, newFuture) == null) {
					newFuture.run();
				}
				if (null == (future = sequenceRepository.get(name))) {
					return nextval(name);
				}
			}
			nextval = future.get();

		} catch (InterruptedException | ExecutionException e) {
			sequenceRepository.remove(name);
			throw new ShardingException(e);
		}

		long val = nextval.incrementAndGet();
		if (val <= boundaryMaxValue) {
			if (val == boundaryMaxValue) {
				sequenceRepository.remove(name);
			}
			return val;
		}

		for (;;) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"sequence [" + name + "] outboundary, val=" + val + ",boundaryMaxValue=" + boundaryMaxValue);
			}
			Future<AtomicLong> future2 = sequenceRepository.get(name);
			if (future2 != future) {
				return nextval(name);
			}
		}
	}

	class DBSequenceBuilderTask implements Callable<AtomicLong> {

		private String name;

		DBSequenceBuilderTask(String name) {
			this.name = name;
		}

		private AtomicLong getCurrentMaxIndex() throws Exception {
			String data = new String(client.getData().forPath("/seq/" + name), Charset.forName("UTF-8"));
			long max = Long.parseLong(data);
			
			client.inTransaction().check().forPath("/seq/" + name).and().setData().forPath("/seq/" + name, String.valueOf(max + incrStep).getBytes(Charset.forName("UTF-8"))).and().commit();
			
			boundaryMaxValue = max + incrStep;
			return new AtomicLong(max);
		}

		@Override
		public AtomicLong call() throws Exception {

			while (true) {
				InterProcessMutex lock = new InterProcessMutex(client, "/lock/" + name);
				try {
					if (null != client.checkExists().forPath("/seq/" + name)) {// 已经存在
						lock.acquire();
						return getCurrentMaxIndex();
					} else {// 不存在
						lock.acquire();
						if (null == client.checkExists().forPath("/seq/" + name)) {
							String data = String.valueOf(incrStep);
							client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
									.forPath("/seq/" + name, data.getBytes(Charset.forName("UTF-8")));
							boundaryMaxValue = incrStep;
							return new AtomicLong(0);
						} else {
							return getCurrentMaxIndex();
						}
					}
				} catch(Exception e){
					
					if(retry-- == 0){
						throw e;
					}
				}finally {
					lock.release();
				}

			}

		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		Builder builder = CuratorFrameworkFactory.builder().connectString(zkAddr)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000)).namespace("cobarclientx");
		client = builder.build();
		client.start();
		try {
			client.blockUntilConnected();
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}

}
