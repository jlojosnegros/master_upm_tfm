package org.jlom.master_upm.tfm.micronaut.stream_control;


import io.micronaut.runtime.Micronaut;
import org.jlom.master_upm.tfm.micronaut.stream_control.view.StartTime;

public class StreamControlApplication {
	public static void main(String[] args) {
		StartTime.getInstance().setStartTime(System.currentTimeMillis());
		Micronaut.run(StreamControlApplication.class, args);
	}

}
