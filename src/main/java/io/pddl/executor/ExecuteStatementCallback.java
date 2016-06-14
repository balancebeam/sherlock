package io.pddl.executor;

import java.sql.SQLException;
import java.sql.Statement;

public interface ExecuteStatementCallback<T extends Statement, OUT> {
    /**
     * Statement操作的回调定义
     * @param actualSql sharding table真实的SQL
     * @param statement Statement|PreparedStatement
     * @return String|Number|Boolean
     * @throws SQLException
     */
    OUT execute(String actualSql,T statement) throws SQLException;
}
