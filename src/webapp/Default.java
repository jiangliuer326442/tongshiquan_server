package webapp;

import java.io.IOException;

import com.ruby.framework.controller.ControllerBase;

public class Default extends ControllerBase {
	public void index() throws IOException{
		display("index",1);
	}
}
