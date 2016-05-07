package com.hj.cobar.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import com.hj.cobar.bean.Cont;
import com.hj.cobar.common.Result;
import com.hj.cobar.dao.ContDAO;
import com.hj.cobar.query.ContQuery;
import com.hj.cobar.service.ContService;

/**
 * @author hj
 * @since 2013-12-11
 */
@Service("contService")
public class ContServiceImpl implements ContService {

	private static final Log log = LogFactory.getLog(ContServiceImpl.class);

	@Resource
	ContDAO contDAO;

	/**
	 * 插入数据库
	 * 
	 * @return
	 */
	public Long addCont(Cont cont) {
		try {
			return contDAO.addCont(cont);
		} catch (SQLException e) {
			log.error("dao addCont error.:" + e.getMessage(), e);
		}
		return 0L;
	}

	/**
	 * 根据主键查找
	 */
	public Cont getContByKey(Long id) {
		try {
			return contDAO.getContByKey(id);
		} catch (SQLException e) {
			log.error("dao getContbyKey error.:" + e.getMessage(), e);
		}
		return null;
	}

	public List<Cont> getContByKeys(List<Long> idList) {
		try {
			return contDAO.getContByKeys(idList);
		} catch (SQLException e) {
			log.error("dao getContsByKeys erorr." + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 根据主键删除
	 * 
	 * @return
	 */
	public Integer deleteByKey(Long id) {
		try {
			return contDAO.deleteByKey(id);
		} catch (SQLException e) {
			log.error("dao deleteByKey error. :" + e.getMessage(), e);
		}
		return -1;
	}

	public Integer deleteByKeys(List<Long> idList) {
		try {
			return contDAO.deleteByKeys(idList);
		} catch (SQLException e) {
			log.error("dao deleteByKeys error. s:" + e.getMessage(), e);
		}
		return -1;
	}

	/**
	 * 根据主键更新
	 * 
	 * @return
	 */
	public Integer updateContByKey(Cont cont) {
		try {
			return contDAO.updateContByKey(cont);
		} catch (SQLException e) {
			log.error("dao updateCont error.cont:" + e.getMessage(), e);
		}
		return -1;
	}

	public Result<Cont> getContListWithPage(ContQuery contQuery) {
		Result<Cont> rs = contDAO.getContListWithPage(contQuery);
		if (!rs.isSuccess()) {
			log.error("get Cont error." + rs.getErrorMsg());
		}
		return rs;
	}

	public List<Cont> getContList(ContQuery contQuery) {
		try {
			return contDAO.getContList(contQuery);
		} catch (SQLException e) {
			log.error("get Cont list error." + e.getMessage(), e);
		}
		return Collections.emptyList();
	}

}
