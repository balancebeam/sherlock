package com.hj.cobar.service;

import java.util.List;

import com.hj.cobar.bean.Cont;
import com.hj.cobar.common.Result;
import com.hj.cobar.query.ContQuery;

/**
 * @author hj
 * @since 2013-12-11
 */
public interface ContService {
	/**
	 * 基本插入
	 * 
	 * @return
	 */
	public Long addCont(Cont cont);

	/**
	 * 根据主键查询
	 */
	public Cont getContByKey(Long id);

	/**
	 * 根据主键批量查询
	 */
	public List<Cont> getContByKeys(List<Long> idList);

	/**
	 * 根据主键删除
	 * 
	 * @return
	 */
	public Integer deleteByKey(Long id);

	/**
	 * 根据主键批量删除
	 * 
	 * @return
	 */
	public Integer deleteByKeys(List<Long> idList);

	/**
	 * 根据主键更新
	 * 
	 * @return
	 */
	public Integer updateContByKey(Cont cont);

	/**
	 * 根据条件查询分页查询
	 * 
	 * @param contQuery
	 *            查询条件
	 * @return
	 */
	public Result<Cont> getContListWithPage(ContQuery contQuery);

	/**
	 * 根据条件查询
	 * 
	 * @param contQuery
	 *            查询条件
	 * @return
	 */
	public List<Cont> getContList(ContQuery contQuery);
}
