package com.hj.cobar.bean;

import java.util.*;
import java.io.Serializable;

/**
 * 
 * @author hj
 * @date 2013-12-11
 */
public class Cont implements Serializable {

	@Override
	public String toString() {
		return "Cont [id=" + id + ", taobaoId=" + taobaoId + ", name=" + name
				+ ", updTime=" + updTime + "]";
	}

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;
	/**
	 * taobaoId
	 */
	private Long taobaoId;
	/**
	 * name
	 */
	private String name;
	/**
	 * upd_time
	 */
	private Date updTime;

	/**
	 * @return id id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return taobaoId taobaoId
	 */
	public Long getTaobaoId() {
		return taobaoId;
	}

	/**
	 * @param taobaoId
	 *            taobaoId
	 */
	public void setTaobaoId(Long taobaoId) {
		this.taobaoId = taobaoId;
	}

	/**
	 * @return name name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return updTime upd_time
	 */
	public Date getUpdTime() {
		return updTime;
	}

	/**
	 * @param updTime
	 *            upd_time
	 */
	public void setUpdTime(Date updTime) {
		this.updTime = updTime;
	}

}