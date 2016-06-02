package io.pddl.testcase.dao;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import io.pddl.testcase.entity.Item;
import io.pddl.testcase.entity.ItemCondition;
import io.pddl.testcase.entity.ItemExt;
import io.pddl.testcase.entity.Order;

@Repository
public class OrderDAO {

	@Resource
	private SqlMapClientTemplate sqlMapClientTemplate;
	
	public void addOrder(Order order) throws SQLException {
		this.sqlMapClientTemplate.insert("Order.addOrder", order);
	}
	
	public void addItem(Item item){
		this.sqlMapClientTemplate.insert("Order.addItem", item);
	}
	
	public void addItemExt(ItemExt itemExt){
		this.sqlMapClientTemplate.insert("Order.addItemExt", itemExt);
	}
	
	public void updateItem(Item item){
		this.sqlMapClientTemplate.update("Order.updateItemByKey",item);
	}
	
	public void deleteItemExt(ItemCondition condition){
		this.sqlMapClientTemplate.delete("Order.deleteItemExtByKeys",condition);
	}
	
	public void deleteItem(ItemCondition condition){
		this.sqlMapClientTemplate.delete("Order.deleteItemByKeys",condition);
	}
	
	public List<Object> queryOrder(Order order){
		return this.sqlMapClientTemplate.queryForList("Order.getOrder", order);
	}
}
