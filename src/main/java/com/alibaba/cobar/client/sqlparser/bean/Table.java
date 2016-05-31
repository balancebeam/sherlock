package com.alibaba.cobar.client.sqlparser.bean;

import com.google.common.base.Optional;

public class Table {
    
    private final String name;
    
    private final Optional<String> alias;
    
    public Table(final String name, final String alias) {
        this(name, Optional.fromNullable(alias));
    }
    
    public Table(final String name, final Optional<String> alias){
    	this.name = name;
    	this.alias = alias;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public Optional<String> getAlias(){
    	return this.alias;
    }
    
    @Override
    public int hashCode(){
    	return name.hashCode();
    }
    
    @Override
    public String toString(){
    	return name+":"+(alias.isPresent()?alias.get():name);
    }
}