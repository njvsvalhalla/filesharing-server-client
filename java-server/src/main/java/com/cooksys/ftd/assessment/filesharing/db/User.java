package com.cooksys.ftd.assessment.filesharing.db;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "user")
public class User {
	@XmlElement(name = "username")
	private String username;
	@XmlElement(name = "passhash")
	private String passhash;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasshash() {
		return passhash;
	}

	public void setPasshash(String passhash) {
		this.passhash = passhash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((passhash == null) ? 0 : passhash.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (passhash == null) {
			if (other.passhash != null)
				return false;
		} else if (!passhash.equals(other.passhash))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", passhash=" + passhash + "]";
	}

//	public User(String username, String passhash) {
//		super();
//		this.username = username;
//		this.passhash = passhash;
//	}
	
	public User() {
	
	}

}
