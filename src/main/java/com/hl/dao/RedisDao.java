package com.hl.dao;


public interface RedisDao {
	public Long getWaitSize();
	public Long getManageSize();
	
	public void leftPush(String array_name,String uuid);
	public String pop(String array_name);
	public String getRight(String array_name,Long size);
	
	public void addKey(String key, Object value);
	public Object getValue(String key);
	public java.util.List<Object> getRangeId(String array_name);
	public void deleteKey(String key);
	public void removeListIndex(String array_name,String uuid);
	public void addSelf(String key);
}
