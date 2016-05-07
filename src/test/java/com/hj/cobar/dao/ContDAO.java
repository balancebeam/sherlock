package com.hj.cobar.dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.hj.cobar.bean.Cont;
import com.hj.cobar.common.Result;
import com.hj.cobar.query.ContQuery;

/**
 * @author hj
 * @date 2013-12-11
 */

@Repository
public class ContDAO {

	@Resource
	SqlMapClientTemplate sqlMapClientTemplate;

	public Long addCont(Cont cont) throws SQLException {
		return (Long) this.sqlMapClientTemplate.insert("Cont.insertCont", cont);
	}

	/**
	 * 根据主键查找
	 * 
	 * @throws SQLException
	 */
	public Cont getContByKey(Long id) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Cont result = (Cont) this.sqlMapClientTemplate.queryForObject(
				"Cont.getContByKey", params);
		return result;
	}

	/**
	 * 根据主键批量查找
	 * 
	 * @throws SQLException
	 */
	public List<Cont> getContByKeys(List<Long> idList) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", idList);
		return (List<Cont>) this.sqlMapClientTemplate.queryForList(
				"Cont.getContsByKeys", params);
	}

	/**
	 * 根据主键删除
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Integer deleteByKey(Long id) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Integer row = (Integer) this.sqlMapClientTemplate.delete(
				"Cont.deleteByKey", params);
		return row;
	}

	/**
	 * 根据主键批量删除
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Integer deleteByKeys(List<Long> idList) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", idList);
		Integer row = (Integer) this.sqlMapClientTemplate.delete(
				"Cont.deleteByKeys", params);
		return row;
	}

	/**
	 * 根据主键更新
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Integer updateContByKey(Cont cont) throws SQLException {
		return (Integer) this.sqlMapClientTemplate.update(
				"Cont.updateContByKey", cont);
	}

	@SuppressWarnings("unchecked")
	public Result<Cont> getContListWithPage(ContQuery contQuery) {
		Result<Cont> rs = new Result<Cont>();
		try {
			rs.setCount((Integer) this.sqlMapClientTemplate.queryForObject(
					"Cont.getContListCount", contQuery));
			if (contQuery.getFields() != null && contQuery.getFields() != "") {
				rs.setList((List<Cont>) this.sqlMapClientTemplate.queryForList(
						"Cont.getContListWithPageFields", contQuery));
			} else {
				rs.setList((List<Cont>) this.sqlMapClientTemplate.queryForList(
						"Cont.getContListWithPage", contQuery));
			}
		} catch (Exception e) {
			rs.setSuccess(false);
			rs.setCount(0);
			rs.setList(Collections.EMPTY_LIST);
			rs.setErrorMsg(e.toString());
		}
		return rs;
	}

	@SuppressWarnings("unchecked")
	public List<Cont> getContList(ContQuery contQuery) throws SQLException {
		if (contQuery.getFields() != null && contQuery.getFields() != "") {
			return (List<Cont>) this.sqlMapClientTemplate.queryForList(
					"Cont.getContListFields", contQuery);
		}
		return (List<Cont>) this.sqlMapClientTemplate.queryForList(
				"Cont.getContList", contQuery);
	}

}