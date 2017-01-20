package io.anyway.sherlock.merger.resultset.memory.row;

/**
 * 结果集数据行接口.
 * 
 * <p>每个数据行表示结果集的一行数据.</p>
 * 
 */
public interface ResultSetRow {
    
    /**
     * 设置数据行数据.
     * 
     * @param columnIndex 列索引, 与JDBC保持一致, 从1开始计数
     * @param value 数据行数据
     */
    void setCell(int columnIndex, Object value);
    
    /**
     * 通过列索引访问数据行数据.
     * 
     * @param columnIndex 列索引, 与JDBC保持一致, 从1开始计数
     * @return 数据行数据
     */
    Object getCell(int columnIndex);
    
    /**
     * 判断列索引是否在数据行范围.
     * 
     * @param columnIndex 列索引, 与JDBC保持一致, 从1开始计数
     * @return 列索引是否在数据行范围
     */
    boolean inRange(int columnIndex);
}
