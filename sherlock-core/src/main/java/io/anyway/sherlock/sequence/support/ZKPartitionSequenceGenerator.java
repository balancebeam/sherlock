package io.anyway.sherlock.sequence.support;

import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import io.anyway.sherlock.sequence.SequenceGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;

public class ZKPartitionSequenceGenerator extends AbstractPartitionSequenceGenerator implements SequenceGenerator, InitializingBean {

	private CuratorFramework client;

	private String zkAddr;

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
	public Callable<AtomicLong> takeNewBatchSequence(String name) {
		return new ZKSequenceBuilderTask(name);
	}
	
	class ZKSequenceBuilderTask implements Callable<AtomicLong> {

		private String name;

		ZKSequenceBuilderTask(String name) {
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
				.retryPolicy(new ExponentialBackoffRetry(1000, 3, 3000)).namespace("pddl");
		client = builder.build();
		client.start();
		try {
			client.blockUntilConnected();
		} catch (Exception e) {
			logger.error("connect zk fail!zkAddr=" + zkAddr, e);
			throw e;
		}
	}


}
