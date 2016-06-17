package io.pddl.sequence.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.pddl.sequence.SequenceGenerator;


/**
 * 方便本地测试，不需要考虑效率问题
 * @author yangzz
 *
 */
public class LocalSequenceGenerator implements SequenceGenerator{

	@Override
	public synchronized long nextval(String name) {
		Long value= 1L;
		File file= new File(System.getProperty("user.dir"),"local.sequence."+name);
		if(file.exists()){
			try {
				byte[] bb= new byte[64];
				InputStream in= new FileInputStream(file);
				int available= in.read(bb);
				value= Long.parseLong(new String(bb,0,available));
				value++;
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			OutputStream out= new FileOutputStream(file);
			out.write(value.toString().getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}
