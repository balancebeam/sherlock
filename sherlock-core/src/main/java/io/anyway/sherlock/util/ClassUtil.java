package io.anyway.sherlock.util;

import io.anyway.sherlock.exception.ShardingException;

public class ClassUtil {

	public static <T> T newInstance(final Class<T> target) {

		try {
			return target.newInstance();
		} catch (Exception ex) {
			throw new ShardingException(ex);
		}
	}

}