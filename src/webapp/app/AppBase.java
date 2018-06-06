package webapp.app;

import com.ruby.framework.controller.ControllerBase;

public abstract class AppBase extends ControllerBase {
	protected String database;
	
	public AppBase(){
		this.init();
	}
	
	public void init() {
		super.init();
		database = "utility";
	}
}
