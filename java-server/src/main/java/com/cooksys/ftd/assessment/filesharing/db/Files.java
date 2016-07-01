package com.cooksys.ftd.assessment.filesharing.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "files")
public class Files {
	@XmlElement(name = "filePath")
	private String filePath;
	@XmlElement(name = "username")
	private String username;
	@XmlElement(name = "buffer")
	private String buffer;

	public String getAbsolutePath() {
		return filePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.filePath = absolutePath;
	}

	public String getByteArray() {
		return buffer;
	}

	public void setByteArray(String byteArray) {
		this.buffer = byteArray;
	}

	public String getusername() {
		return username;
	}

	public void setusername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Files [absolutePath=" + filePath + ", username=" + username + "]";
	}

	public Files() {

	}

}
