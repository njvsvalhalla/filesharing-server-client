package com.cooksys.ftd.assessment.filesharing.api;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.cooksys.ftd.assessment.filesharing.db.Files;

public class GetFileFromClient {
	/**
	 * This umarshall's a JSON object that was sent to the server to a Files
	 * object to be stored into the database.
	 * 
	 * @param String input (JSON object)
	 * @return Files object that was unmarshalled
	 * @throws JAXBException
	 */
	public static Files unmarshall(String input) throws JAXBException {
		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Files.class }, properties);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		StringReader json = new StringReader(input);

		return (Files) unmarshaller.unmarshal(json);
	}
}
