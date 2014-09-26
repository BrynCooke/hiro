package org.jglue.hiro;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;

import javax.inject.Inject;

public class HiroAgent {
	public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException,
			UnmodifiableClassException, InterruptedException {
		Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
		inst.addTransformer(new HiroTransformer(), true);
		for (Class<?> clazz : allLoadedClasses) {
			try {
				for (Field f : clazz.getDeclaredFields()) {
					if (f.isAnnotationPresent(Inject.class)) {
						inst.retransformClasses(clazz);
						continue;
					}
				}

			} catch (UnmodifiableClassException e) {
				System.out.println("Unmodifiable " + clazz);
			}

		}

	}
}
