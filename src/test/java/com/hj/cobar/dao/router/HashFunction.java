package com.hj.cobar.dao.router;

/**
 * 根据某种自定义的hash算法来进行散列,并根据散列的值进行路由
 *  常见的水平切分规则有:
	基于范围的切分, 比如 memberId > 10000 and memberId < 20000
	基于模数的切分, 比如 memberId%128==1 或者 memberId%128==2 或者...
	基于哈希(hashing)的切分, 比如hashing(memberId)==someValue等
 * @author hj
 *
 */
public class HashFunction{
	
	/**
	 * 对三个数据库进行散列分布
	 * 1、返回其他值，没有在配置文件中配置的，如负数等，在默认数据库中查找
	 * 2、比如现在配置文件中配置有三个结果进行散列，如果返回为0，那么apply方法只调用一次，如果返回为2，
	 *   那么apply方法就会被调用三次，也就是每次是按照配置文件的顺序依次的调用方法进行判断结果，而不会缓存方法返回值进行判断
	 * @param taobaoId
	 * @return
	 */
	public int apply(Long taobaoId) {
		//先从缓存获取 没有则查询数据库
		//input 可能是taobaoId，拿taobaoId到缓存里去查用户的DB坐标信息。然后把库的编号输出
		System.out.println("taobaoId："+taobaoId);
		int result = (int)(taobaoId % 3);
		System.out.println("在第"+(result + 1)+"个数据库中");
		return result;
	}
	
	/**
	       注：只调用一次
	    taobaoId：3354
		在第1个数据库中
	  
	         注：调用了三次
	    taobaoId：7043
		在第3个数据库中
		taobaoId：7043
		在第3个数据库中
		taobaoId：7043
		在第3个数据库中
	 */

}
