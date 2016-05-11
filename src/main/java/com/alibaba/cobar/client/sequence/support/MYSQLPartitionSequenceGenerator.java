package com.alibaba.cobar.client.sequence.support;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.cobar.client.sequence.SequenceGenerator;

public class MYSQLPartitionSequenceGenerator implements SequenceGenerator,InitializingBean{

	private Log logger = LogFactory.getLog(MYSQLPartitionSequenceGenerator.class);

	private volatile long boundaryMaxValue = 0;

	private long incrStep = 1000;

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIncrStep(long incrStep) {
		this.incrStep = incrStep;
	}
	
	
	@Override
	public long nextval(String name) {
		
		
		return 0;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
}
