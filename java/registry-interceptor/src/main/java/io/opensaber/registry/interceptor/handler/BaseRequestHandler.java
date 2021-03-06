package io.opensaber.registry.interceptor.handler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.opensaber.registry.middleware.util.Constants;

/**
 * 
 * @author jyotsna
 *
 */
public class BaseRequestHandler{
	
	protected RequestWrapper requestWrapper;
	protected HttpServletRequest request;
	
	public void setRequest(HttpServletRequest request) throws IOException{
		this.request = request;
	}

	
	public void setRequestWrapper() throws IOException{
		if(requestWrapper==null){
			requestWrapper = new RequestWrapper(request);
		}
	}
	
	public String getRequestBody() throws IOException{
		setRequestWrapper();
		return requestWrapper.getBody();
	}
	
	public String getRequestHeaderByName(String name) throws IOException{
		setRequestWrapper();
		return requestWrapper.getHeader(name);
	}
	
	public void setRequestAttributes(Map<String,Object> attributeMap){
		for(Map.Entry<String, Object> entry: attributeMap.entrySet()){
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}
	
	public HttpServletRequest getRequest(){
		return request;
	}
	
	public Map<String,Object> getRequestBodyMap() throws IOException{
		Map<String,Object> requestBodyMap = new HashMap<String,Object>();
		requestBodyMap.put(Constants.ATTRIBUTE_NAME, getRequestBody());
		return requestBodyMap;
	}
	
	public Map<String,Object> getRequestHeaderMap() throws IOException{
		setRequestWrapper();
		Map<String,Object> requestHeaderMap = new HashMap<String,Object>();
		Enumeration<String> headerNames = requestWrapper.getHeaderNames();
		if(headerNames!=null){
			while(headerNames.hasMoreElements()){
				String header = headerNames.nextElement();
				requestHeaderMap.put(header, requestWrapper.getHeader(header));
			}
		}
		return requestHeaderMap;
	}
	
	public Map<String,Object> getRequestAttributeMap() throws IOException{
		setRequestWrapper();
		Map<String,Object> requestAttributeMap = new HashMap<String,Object>();
		Enumeration<String> attributeNames = requestWrapper.getAttributeNames();
		if(attributeNames!=null){
			while(attributeNames.hasMoreElements()){
				String header = attributeNames.nextElement();
				requestAttributeMap.put(header, requestWrapper.getHeader(header));
			}
		}
		return requestAttributeMap;
	}
	
	public Map<String,Object> getRequestParameterMap() throws IOException{
		setRequestWrapper();
		Map<String,Object> requestParameterMap = new HashMap<String,Object>();
		requestParameterMap.putAll(requestWrapper.getParameterMap());
		return requestParameterMap;
	}

}
