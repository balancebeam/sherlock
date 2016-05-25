package com.alibaba.cobar.client.util;

import com.alibaba.cobar.client.exception.ShardingException;

public class ClassUtil {

	public static <T> T newInstance(final Class<T> target) {

		try {
			return target.newInstance();
		} catch (Exception ex) {
			throw new ShardingException(ex);
		}
	}

}