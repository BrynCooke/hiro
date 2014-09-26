package org.jglue.hiro;

import java.io.File;
import java.lang.management.ManagementFactory;

import com.sun.tools.attach.VirtualMachine;

public class Hiro {
	static {
		

	    String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
	    int p = nameOfRunningVM.indexOf('@');
	    String pid = nameOfRunningVM.substring(0, p);

	    File f = new File("/home/bryn/work/jglue/hiro/hiro-agent/target/hiro-agent-0.0.1-SNAPSHOT.jar");
	    
	    try {
	        VirtualMachine vm = VirtualMachine.attach(pid);
	        vm.loadAgent(f.getAbsolutePath());
	        vm.detach();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
		
	}
}
