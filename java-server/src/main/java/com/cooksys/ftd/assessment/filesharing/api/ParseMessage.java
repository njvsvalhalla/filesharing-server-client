package com.cooksys.ftd.assessment.filesharing.api;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.ftd.assessment.filesharing.db.Message;

public class ParseMessage {
	/**
	 * Unmarshall's our command JSON from the client
	 * 
	 * @param String
	 *            (JSON object sent from the client)
	 * @return User object
	 * @throws JAXBException
	 */
	public static Message unmarshall(String input) throws JAXBException {

		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Message.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader json = new StringReader(input);

		Message m = (Message) unmarshaller.unmarshal(json);
		return m;
	}
}
