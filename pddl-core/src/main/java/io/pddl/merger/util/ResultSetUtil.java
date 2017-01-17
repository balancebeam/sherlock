package io.pddl.merger.util;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import io.pddl.exception.ShardingException;
import io.pddl.sqlparser.bean.OrderColumn;
import io.pddl.sqlparser.bean.OrderColumn.OrderType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 结果集工具类.
 * 
 * @author xiong.j
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResultSetUtil {
    
    /**
     * 根据返回值类型返回特定类型的结果.
     * 
     * @param value 原始结果
     * @param convertType 返回值类型
     * @return 特定类型的返回结果
     */
    public static Object convertValue(final Object value, final Class<?> convertType) {
        if (null == value) {
            return convertNullValue(convertType);
        } 
        if (value.getClass() == convertType) {
            return value;
        }
        if (value instanceof Number) {
            return convertNumberValue(value, convertType);
        }
        if (value instanceof Date) {
            return convertDateValue(value, convertType);
        }
        if (String.class.equals(convertType)) {
            return value.toString();
        } else {
            return value;
        }    
    }
    
    private static Object convertNullValue(final Class<?> convertType) {
        if ("byte".equals(convertType.getName())) {
            return (byte) 0;
        } else if ("short".equals(convertType.getName())) {
            return (short) 0;
        } else if ("int".equals(convertType.getName())) {
            return 0;
        } else if ("long".equals(convertType.getName())) {
            return 0L;
        } else if ("double".equals(convertType.getName())) {
            return 0D;
        } else if ("float".equals(convertType.getName())) {
            return 0F;
        } else {
            return null;
        }
    }
    
    private static Object convertNumberValue(final Object value, final Class<?> convertType) {
        Number number = (Number) value;
        if ("byte".equals(convertType.getName())) {
            return number.byteValue();
        } else if ("short".equals(convertType.getName())) {
            return number.shortValue();
        } else if ("int".equals(convertType.getName())) {
            return number.intValue();
        } else if ("long".equals(convertType.getName())) {
            return number.longValue();
        } else if ("double".equals(convertType.getName())) {
            return number.doubleValue();
        } else if ("float".equals(convertType.getName())) {
            return number.floatValue();
        } else if ("java.math.BigDecimal".equals(convertType.getName())) {
            return new BigDecimal(number.toString());
        } else if ("java.lang.Object".equals(convertType.getName())) {
            return value;
        } else if ("java.lang.String".equals(convertType.getName())) {
            return value.toString();
        } else {
            throw new ShardingException("Unsupported data type:%s", convertType);
        }
    }
    
    private static Object convertDateValue(final Object value, final Class<?> convertType) {
        Date date = (Date) value;
        if ("java.sql.Date".equals(convertType.getName())) {
            return new java.sql.Date(date.getTime());
        } else if ("java.sql.Time".equals(convertType.getName())) {
            return new Time(date.getTime());
        } else if ("java.sql.Timestamp".equals(convertType.getName())) {
            return new Timestamp(date.getTime());
        } else {
            throw new ShardingException("Unsupported Date type:%s", convertType);
        }
    }
    
    /**
     * 根据排序类型比较大小.
     * 
     * @param thisValue 待比较的值
     * @param otherValue 待比较的值
     * @param orderType 排序类型
     * @return 负数，零和正数分别表示小于，等于和大于
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static int compareTo(final Comparable thisValue, final Comparable otherValue, final OrderColumn.OrderType orderType) {
        return OrderType.ASC == orderType ? thisValue.compareTo(otherValue) : -thisValue.compareTo(otherValue);
    }
}
