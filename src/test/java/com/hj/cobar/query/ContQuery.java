package com.hj.cobar.query;

import java.util.*;

/**
 * 
 * @author hj
 */
public class ContQuery extends BaseQuery {

	/**
	 * ==============================批量查询、更新、删除时的Where条件设置======================
	 * ============
	 **/
	/** id **/
	private Long id;

	public Long getId() {
		return id;
	}

	public ContQuery setId(Long id) {
		this.id = id;
		return this;
	}

	/** taobaoId **/
	private Long taobaoId;

	public Long getTaobaoId() {
		return taobaoId;
	}

	public ContQuery setTaobaoId(Long taobaoId) {
		this.taobaoId = taobaoId;
		return this;
	}

	/** name **/
	private String name;

	public String getName() {
		return name;
	}

	public ContQuery setName(String name) {
		this.name = name;
		return this;
	}

	private boolean nameLike;

	public ContQuery setNameLike(boolean nameLike) {
		this.nameLike = nameLike;
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
	 * 设置排序按属性：id
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public ContQuery orderbyId(boolean isAsc) {
		orderFields.add(new OrderField("id", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：taobaoId
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public ContQuery orderbyTaobaoId(boolean isAsc) {
		orderFields.add(new OrderField("taobaoId", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：name
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public ContQuery orderbyName(boolean isAsc) {
		orderFields.add(new OrderField("name", isAsc ? "ASC" : "DESC"));
		return this;
	}

	/**
	 * 设置排序按属性：upd_time
	 * 
	 * @param isAsc
	 *            是否升序，否则为降序
	 */
	public ContQuery orderbyUpdTime(boolean isAsc) {
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
			fieldMap.put("taobaoId", "taobaoId");
			fieldMap.put("name", "name");
			fieldMap.put("upd_time", "updTime");
		}
		return fieldMap;
	}

	public String getFields() {
		return this.fields;
	}

	public void setFields(String fields) {
		if (fields == null)
			return;
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
