package com.alibaba.cobar.client.sequence.support;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.cobar.client.exception.ShardingException;
import com.alibaba.cobar.client.sequence.SequenceGenerator;

public abstract class AbstractPartitionSequenceGenerator implements SequenceGenerator{
	
	protected Log logger = LogFactory.getLog(getClass());

	protected ConcurrentHashMap<String, Future<AtomicLong>> sequenceRepository = new ConcurrentHashMap<String, Future<AtomicLong>>();

	protected volatile long boundaryMaxValue = 0;

	protected long incrStep = 1000;

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
	}
	
	@Override
	public long nextval(final String name) {
		Future<AtomicLong> future = sequenceRepository.get(name);
		AtomicLong nextval = null;
		if (future == null) {
			FutureTask<AtomicLong> newFuture = new FutureTask<AtomicLong>(takeNewBatchSequence(name));
			if (sequenceRepository.putIfAbsent(name, newFuture) == null) {
				newFuture.run();
			}
			if((future= sequenceRepository.get(name))== null){
				return nextval(name);
			}
		}
		try {
			nextval = future.get();
		} catch (Exception e) {
			sequenceRepository.remove(name);
			throw new ShardingException(e);
		}

		long val = nextval.incrementAndGet();
		if(val< boundaryMaxValue){
			return val;
		}
		else if(val== boundaryMaxValue){
			sequenceRepository.remove(name);
			return val;
		}
		
		//out boundary
		for(;;){
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {}
			if(logger.isDebugEnabled()){
				logger.debug("sequence ["+name+"] outboundary, val="+val+",boundaryMaxValue="+boundaryMaxValue);
			}
			Future<AtomicLong> nFuture= sequenceRepository.get(name);
			if(nFuture!= future){
				return nextval(name);
			}
		}
	}
	
	public abstract Callable<AtomicLong> takeNewBatchSequence(String name);
	
}
