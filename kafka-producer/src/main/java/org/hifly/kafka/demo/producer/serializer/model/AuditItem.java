package org.hifly.kafka.demo.producer.serializer.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AuditItem implements Serializable {

	private static final long serialVersionUID = -4880866185311154965L;
	
	private String user;
	private String path;
	private Map<String, String> params;
	private List<String> payload;
	private String date;
	private String method;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public List<String> getPayload() {
		return payload;
	}

	public void setPayload(List<String> payload) {
		this.payload = payload;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}