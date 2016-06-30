package com.cooksys.ftd.assessment.filesharing.api;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.ftd.assessment.filesharing.db.User;

public class RegisterUser {
	
	public User unmarshall(String input) throws JAXBException {

		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		JAXBContext jc = JAXBContext.newInstance(new Class[] { User.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader json = new StringReader(input);
		
		User u = (User) unmarshaller.unmarshal(json);
		return u;
	}

}
