package com.cooksys.ftd.assessment.filesharing.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
	
@XmlRootElement(name = "file")
public class Files {

	@XmlElement(name = "absolutePath")
	private String absolutePath;
	@XmlElement(name = "userId")
	private int userId;
	@XmlElement(name = "bArray")
	private byte[] byteArray;
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	public byte[] getByteArray() {
		return byteArray;
	}
	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}
	public int getUserid() {
		return userId;
	}
	public void setUserid(int userid) {
		this.userId = userid;
	}
	@Override
	public String toString() {
		return "Files [absolutePath=" + absolutePath + ", userid=" + userId + "]";
	}
	
	public Files () {
		
	}
	

}
