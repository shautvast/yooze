package yooze;

import java.util.Iterator;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;

public class AssTest {
	public static void main(String[] args) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		CtClass c1 = pool.get("yooze.Class1");
		ConstPool constPool = c1.getClassFile().getConstPool();
	
		
		for (Iterator iterator = constPool.getClassNames().iterator();iterator.hasNext();){
			System.out.println(iterator.next());
		}

	}
}
