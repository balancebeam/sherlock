package com.hj.cobar.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author hj
 */
public class AccountQuery extends BaseQuery {

	/**
	 * ==============================批量查询、更新、删除时的Where条件设置======================
	 * ============
	 **/
	/** 帐号表自增主键 **/
	private Long id;

	public Long getId() {
		return id;
	}

	public AccountQuery setId(Long id) {
		this.id = id;
		return this;
	}

	/** 用户名 **/
	private String username;

	public String getUsername() {
		return username;
	}

	public AccountQuery setUsername(String username) {
		this.username = username;
		return this;
	}

	private boolean usernameLike;

	public AccountQuery setUsernameLike(boolean usernameLike) {
		this.usernameLike = usernameLike;
		return this;
	}

	/** 密码 **/
	private String password;

	public String getPassword() {
		return password;
	}

	public AccountQuery setPassword(String password) {
		this.password = password;
		return this;
	}

	private boolean passwordLike;

	public AccountQuery setPasswordLike(boolean passwordLike) {
		this.passwordLike = passwordLike;
		return this;
	}

	/** 随机加密密钥 **/
	private String salt;

	public String getSalt() {
		return salt;
	}

	public AccountQuery setSalt(String salt) {
		this.salt = salt;
		return this;
	}

	private boolean saltLike;

	public AccountQuery setSaltLike(boolean saltLike) {
		this.saltLike = saltLike;
		return this;
	}

	/** 登录次数 **/
	private Integer loginNum;

	public Integer getLoginNum() {
		return loginNum;
	}

	public AccountQuery setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
		return this;
	}

	/** 上一次登录时间 **/
	private Date lastLoginTimeStart;

	public Date getLastLoginTimeStart() {
		return lastLoginTimeStart;
	}

	public AccountQuery setLastLoginTimeStart(Date lastLoginTime) {
		this.lastLoginTimeStart = lastLoginTime;
		return this;
	}

	private Date lastLoginTimeEnd;

	public Date getLastLoginTimeEnd() {
		return lastLoginTimeEnd;
	}

	public AccountQuery setLastLoginTimeEnd(Date lastLoginTime) {
		this.lastLoginTimeEnd = lastLoginTime;
		return this;
	}

	private Date lastLoginTimeEqual;

	public Date getLastLoginTimeEqual() {
		return lastLoginTimeEqual;
	}

	public AccountQuery setLastLoginTimeEqual(Date lastLoginTime) {
		this.lastLoginTimeEqual = lastLoginTime;
		return this;
	}

	/**
	 * ==============================批量查询时的Order条件顺序设置==========================
	 * ========
	 **/
	public class OrderField {
		public OrderField(String fieldName, String order) {
			super();
			this.fieldName = fieldName;
			this.order = order;
		}

		private String fieldName;
		private String order;

		public String getFieldName() {
			return fieldName;
		}

		public OrderField setFieldName(String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		public String getOrder() {
			return order;
		}

		public OrderField setOrder(String order) {
			this.order = order;
			return this;
		}
	}

	/**
	 * ==============================批量查询时的Order条件顺序设置==========================
	 * ========
	 **/
	/** 排序列表字段 **/
	private List<OrderField> orderFields = new ArrayList<OrderField>();

	/**
	 * 设置排序按属性：帐号表自增主键
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyId(boolean isAsc) {
		orderFields.add(new OrderField("id", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：用户名
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyUsername(boolean isAsc) {
		orderFields.add(new OrderField("username", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：密码
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyPassword(boolean isAsc) {
		orderFields.add(new OrderField("password", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：随机加密密钥
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbySalt(boolean isAsc) {
		orderFields.add(new OrderField("salt", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：登录次数
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyLoginNum(boolean isAsc) {
		orderFields.add(new OrderField("login_num", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：上一次登录时间
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyLastLoginTime(boolean isAsc) {
		orderFields.add(new OrderField("last_login_time", isAsc ? "ASC"
				: "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：帐号创建时间
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyAddTime(boolean isAsc) {
		orderFields.add(new OrderField("add_time", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：修改时间
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public AccountQuery orderbyUpdTime(boolean isAsc) {
		orderFields.add(new OrderField("upd_time", isAsc ? "ASC" : "DESC"));
		return this;
	}

	private String fields;
	/**
	 * 提供自定义字段使用
	 */
	private static Map<String, String> fieldMap;

	private static Map<String, String> getFieldSet() {
		if (fieldMap == null) {
			fieldMap = new HashMap<String, String>();
			fieldMap.put("id", "id");
			fieldMap.put("username", "username");
			fieldMap.put("password", "password");
			fieldMap.put("salt", "salt");
			fieldMap.put("login_num", "loginNum");
			fieldMap.put("last_login_time", "lastLoginTime");
			fieldMap.put("add_time", "addTime");
			fieldMap.put("upd_time", "updTime");
		}
		return fieldMap;
	}

	public String getFields() {
		return this.fields;
	}

	public void setFields(String fields) {
		if(fields == null) return;
		String[] array = fields.split(",");
		StringBuffer buffer = new StringBuffer();
		for (String field : array) {
			if (getFieldSet().containsKey(field)) {
				buffer.append(field).append(" as ")
						.append(getFieldSet().get(field)).append(" ,");
			}
			if (getFieldSet().containsKey("`" + field + "`")) {
				buffer.append("`" + field + "`").append(" as ")
						.append(getFieldSet().get(field)).append(" ,");
			}
		}
		if (buffer.length() != 0) {
			this.fields = buffer.substring(0, buffer.length() - 1);
		} else {
			this.fields = " 1 ";// 没有一个参数可能会报错
		}
	}
}
