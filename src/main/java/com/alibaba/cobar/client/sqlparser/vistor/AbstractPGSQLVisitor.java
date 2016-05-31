package com.alibaba.cobar.client.sqlparser.vistor;

import com.alibaba.cobar.client.sqlparser.SQLBuilder;
import com.alibaba.cobar.client.sqlparser.SQLParsedResult;
import com.alibaba.cobar.client.sqlparser.SQLVisitor;
import com.alibaba.cobar.client.sqlparser.bean.DatabaseType;
import com.alibaba.cobar.client.sqlparser.bean.Table;
import com.alibaba.cobar.client.util.SQLUtil;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGOutputVisitor;
import com.google.common.base.Optional;

public abstract class AbstractPGSQLVisitor extends PGOutputVisitor implements SQLVisitor {
    
    protected SQLParsedResult parseResult;
    
    protected AbstractPGSQLVisitor() {
        super(new SQLBuilder());
        setPrettyFormat(false);
        parseResult = new SQLParsedResult(getSQLBuilder());
    }
    
    @Override
    public final DatabaseType getDatabaseType() {
        return DatabaseType.POSTGRESQL;
    }
    
    protected void setCurrentTable(final String currentTableName, final Optional<String> currentAlias) {
        Table table = new Table(SQLUtil.getExactlyValue(currentTableName), currentAlias.isPresent() ? Optional.of(SQLUtil.getExactlyValue(currentAlias.get())) : currentAlias);
        parseResult.getTables().add(table);
    }
       
    protected final SQLBuilder getSQLBuilder() {
        return (SQLBuilder) appender;
    }
    
    public final SQLParsedResult getSQLResult(){
    	return this.parseResult;
    }
    
    @Override
    public final void printToken(final String token) {
        getSQLBuilder().appendToken(SQLUtil.getExactlyValue(token));
   }
    
    /**
     * 父类使用<tt>@@</tt>代替<tt>?</tt>,此处直接输出参数占位符<tt>?</tt>
     * 
     * @param x 变量表达式
     * @return false 终止遍历AST
     */
    @Override
    public final boolean visit(final SQLVariantRefExpr x) {
        print(x.getName());
        return false;
    }
    
    @Override
    public final boolean visit(final SQLExprTableSource x) {
        return visit(x, parseResult.addTable(new Table(SQLUtil.getExactlyValue(x.getExpr().toString()), SQLUtil.getExactlyValue(x.getAlias()))));
    }
    
    private boolean visit(final SQLExprTableSource x, final Table table) {
        printToken(table.getName());
        if (table.getAlias().isPresent()) {
            print(' ');
            print(table.getAlias().get());
        }
        for (SQLHint each : x.getHints()) {
            print(' ');
            each.accept(this);
        }
        return false;
    }
    
    /**
     * 将表名替换成占位符.
     * 
     * <p>
     * 1. 如果二元表达式使用别名, 如: 
     * {@code FROM order o WHERE o.column_name = 't' }, 则Column中的tableName为o.
     * </p>
     * 
     * <p>
     * 2. 如果二元表达式使用表名, 如: 
     * {@code FROM order WHERE order.column_name = 't' }, 则Column中的tableName为order.
     * </p>
     * 
     * @param x SQL属性表达式
     * @return true表示继续遍历AST, false表示终止遍历AST
     */
    @Override
    // TODO SELECT [别名.xxx]的情况，目前都是替换成token，解析之后应该替换回去
    public final boolean visit(final SQLPropertyExpr x) {
        if (!(x.getParent() instanceof SQLBinaryOpExpr) && !(x.getParent() instanceof SQLSelectItem)) {
            return super.visit(x);
        }
        if (!(x.getOwner() instanceof SQLIdentifierExpr)) {
            return super.visit(x);
        }
        String tableOrAliasName = ((SQLIdentifierExpr) x.getOwner()).getLowerName();
        if (isBinaryOperateWithAlias(x, tableOrAliasName)) {
            return super.visit(x);
        }
        printToken(tableOrAliasName);
        print(".");
        print(x.getName());
        return false;
    }
    
    /**
     * 判断SQL表达式是否为二元操作且带有别名.
     * 
     * @param x 待判断的SQL表达式
     * @param tableOrAliasName 表名称或别名
     * @return 是否为二元操作且带有别名
     */
    public boolean isBinaryOperateWithAlias(final SQLPropertyExpr x, final String tableOrAliasName) {
        return x.getParent() instanceof SQLBinaryOpExpr && findTableFromAlias(SQLUtil.getExactlyValue(tableOrAliasName)).isPresent();
    }
    
    private Optional<Table> findTableFromAlias(final String alias) {
        for (Table each : parseResult.getTables()) {
            if (each.getAlias().isPresent() && each.getAlias().get().equalsIgnoreCase(SQLUtil.getExactlyValue(alias))) {
                return Optional.of(each);
            }
        }
        return Optional.absent();
    }
    
    @Override
    public boolean visit(final SQLBinaryOpExpr x) {
    	System.out.println("----------");
    	System.out.println(x);
    	System.out.println("----------");
    	return super.visit(x);
    }
    
    @Override
    public boolean visit(final SQLInListExpr x) {
    	
    	return super.visit(x);
    }
    
    @Override
    public boolean visit(final SQLBetweenExpr x) {
    	
    	return super.visit(x);
    }

}
