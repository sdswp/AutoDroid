package com.gk.touchstone.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PlanType implements Serializable{
	private String value;
	private Integer id;

	public PlanType(int _id,String _value) {
		id = _id;
		value = _value;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/**
     * 为什么要重写toString()呢？
     * 
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return value;
    }


}
