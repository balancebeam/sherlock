package com.alibaba.cobar.client.hystrix;

import java.util.concurrent.Semaphore;

public class CobarHystrixDescriptor {
	//控制并行执行数or或使用不同线程池
	private Semaphore semaphore;
	//控制等待时间
	private int holdingtime;
}
