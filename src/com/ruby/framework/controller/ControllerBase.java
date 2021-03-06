package com.ruby.framework.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ruby.framework.model.DbManager;
import com.ruby.framework.view.ViewInterface;

public abstract class ControllerBase  implements ControllerInterface {
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext context;
	//一下内容可由上下文中提取
	protected ViewInterface _view;
	protected DbManager _model;
	
	/**
	 * 传递请求和响应对象
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void set(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
		this.context = context;
		this.request = request;
		this.response = response;
		//从上下文中解析模型和视图组件
		this._view = (ViewInterface)context.getAttribute("_view");
		this._model = (DbManager)context.getAttribute("_model");
	}
	
	/**
	 * 动态调用方法
	 * 调用子类的方法
	 */
	@Override
	public void load(String method) throws IOException{
		init();
		_view.init();
		// TODO Auto-generated method stub
		try {
			this.getClass().getMethod(method, new Class[]{}).invoke(this, new Object[]{});
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		destroy();
	}
	
	/**
	 * 初始化
	 * 在子类中可实现自己的扩展
	 */
	protected void init(){
		
	};
	/**
	 * 结束
	 * 在子类中可实现自己的扩展
	 */
	public void destroy(){};

	@Override
	public void display(String tmpl) throws IOException {
		display(tmpl,10);
	}
	
	//返回字符串的添加模板
	public void display(String tmpl, boolean is_cache) throws IOException {
		response.setHeader("Content-type", "text/html;charset=UTF-8");
        String html_string = _view.display(tmpl,is_cache);
        response.getWriter().write(html_string);
	}
	
	//返回字符串的添加模板
	public void display(String tmpl, long cache_limits) throws IOException {
		response.setHeader("Content-type", "text/html;charset=UTF-8");
        String html_string = _view.display(tmpl,cache_limits);
        response.getWriter().write(html_string);
	}

	@Override
	public void assign(String key, String value) {
		// TODO Auto-generated method stub
		_view.assign(key, value);
	}

	@Override
	public void return_json(int status, String info, Object data) throws IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin","*");
		JSONObject object = new JSONObject();
		object.put("data", data);
		object.put("status", status);
		object.put("info", info);
		response.setContentType("text/json;charset=utf-8");  
		response.setHeader("Cache-Control","no-cache");
		response.getWriter().write(object.toString());
	}
	
	public void return_json(int status, String info, Object data, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin","*");
		JSONObject object = new JSONObject();
		object.put("data", data);
		object.put("status", status);
		object.put("info", info);
		response.setContentType("text/json;charset=utf-8");  
		response.setHeader("Cache-Control","no-cache");
		response.getWriter().write(object.toString());
	}
	
	@Override
	public void return_json(int status, String info) throws IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin","*");
		JSONObject object = new JSONObject();
		object.put("status", status);
		object.put("info", info);
		response.setContentType("text/json;charset=utf-8");  
		response.setHeader("Cache-Control","no-cache");
		response.getWriter().write(object.toString());
	}
	
	public void return_json(int status, String info, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.addHeader("Access-Control-Allow-Origin","*");
		JSONObject object = new JSONObject();
		object.put("status", status);
		object.put("info", info);
		response.setContentType("text/json;charset=utf-8");  
		response.setHeader("Cache-Control","no-cache");
		response.getWriter().write(object.toString());
	}

}
