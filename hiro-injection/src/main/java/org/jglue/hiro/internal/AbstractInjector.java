package org.jglue.hiro.internal;

import org.jglue.hiro.Injector;

public abstract class AbstractInjector implements Injector {

	private Injector parent;
	protected AbstractInjector(Injector parent) {
		this.parent = parent;
	}

	protected Injector getParent() {
		return parent;
	}

}
