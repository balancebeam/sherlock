package org.test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTester {

	public static void main(String[] args){
		Kryo kryo = new Kryo(); 
		Registration registration = kryo.register(Employee.class); 
		Employee emp= new Employee();
		emp.setId(123);
		emp.setName("yangzz");
		emp.setSalary(4567);
		Output output=  new Output(1, 4096); 
		kryo.writeObject(output, emp); 
		byte[] bb = output.toBytes(); 
		// System.out.println(bb.length); 
		output.flush(); 
		
		//反序列化 
		Input input = new Input(bb);
		Employee yee= (Employee)kryo.readObject(input,Employee.class); 
		System.out.println(yee.getName()+","+yee.getSalary()); 
		input.close(); 
	}
}
