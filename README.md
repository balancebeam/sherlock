# PDDL
#### ( Public Distributed Data Layer )

提供基于JDBC规范的客户端分库、分表数据路由方案

## 支持范围
* JDK6+
* PostgreSQL
* 所有持久层框架

## 功能简介

* 多租户
* 读写分离
* 读负载均衡
	* 轮询策略
	* 权重策略
* 全局序列号
* 全局表复制
* 分库
* 分表
* 结果归并处理 
* SQL限流（开发中）
* SQL监控（开发中）
* 分布式事务（开发中）
* 支持的数据库（PostgreSQL、Oracle开发中、MySQL开发中）

## 架构图
![PDDL Architecture](pddl-doc/images/architecture.png)

## 示例代码

* 配置定义
	* 定义分片数据源
	* 定义全局表
	* 定义逻辑表和逻辑子表
	* 定义分库和分表路由规则

```xml
<pddl:data-source id="shardingDataSource" database-type="PostgreSQL" >
	<pddl:data-source-partitions>
		<pddl:data-source-partition name="p0" read-strategy="weight">
			<pddl:master-data-source ref="ds0" weight="200" />
			<pddl:slave-data-source ref="ds0" weight="200" />
			<pddl:slave-data-source ref="ds0" weight="400" />
		</pddl:data-source-partition>
		<pddl:data-source-partition name="p1">
			<pddl:master-data-source ref="ds1" />
		</pddl:data-source-partition>
		<pddl:data-source-partition name="p2">
			<pddl:master-data-source ref="ds2" />
		</pddl:data-source-partition>
	</pddl:data-source-partitions>
	<pddl:tables>
		<pddl:global-table name="stock"/>
		<pddl:logic-table name="t_order" primary-key="order_id" table-postfixes="_0,_1,_2" database-strategy="orderDatabaseStrategy" table-strategy="orderTableStrategy">
			<pddl:logic-child-table name="t_item" primary-key="item_id" foreign-key="order_id">
				<pddl:logic-child-table name="t_item_ext" primary-key="ext_id" foreign-key="item_id"/>
			</pddl:logic-child-table>
		</pddl:logic-table>
	</pddl:tables>
</pddl:data-source>

<pddl:strategy id="orderDatabaseStrategy" sharding-columns="user_id" expression="p${user_id.intValue() % 3}"/>

<pddl:strategy id="orderTableStrategy" sharding-columns="order_id" expression="_${order_id.intValue() % 3}"/>

```