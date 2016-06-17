package io.pddl.util;

import io.pddl.exception.ShardingException;

public class ClassUtil {

	public static <T> T newInstance(final Class<T> target) {

		try {
			return target.newInstance();
		} catch (Exception ex) {
			throw new ShardingException(ex);
		}
	}

}