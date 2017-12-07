package com.cooksys.ftd.assessment.filesharing.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "keywords")
public class Keywords {
	@Override
	public String toString() {
		return "Keywords [keyword=" + keyword + ", fileid=" + fileid + "]";
	}
	@XmlElement(name = "keyword")
	private String keyword;
	@XmlElement(name = "fileid")
	private int fileid;
	private String username;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getFileid() {
		return fileid;
	}
	public void setFileid(int fileid) {
		this.fileid = fileid;
	}
	
	

}
