package io.anyway.sherlock.sequence.support;

import io.anyway.sherlock.sequence.SequenceGenerator;

public class PGNextvalSequenceGenerator implements SequenceGenerator {

	@Override
	public long nextval(String name) {
		return 0;
	}

}
