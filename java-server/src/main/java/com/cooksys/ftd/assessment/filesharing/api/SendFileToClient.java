package com.cooksys.ftd.assessment.filesharing.api;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.cooksys.ftd.assessment.filesharing.db.Files;

public class SendFileToClient {

	/**
	 * Takes a Files object, f, and marshall's it to a JSON object, and returns
	 * the string. Used to send a file back to the client.
	 * 
	 * @param file object (F)
	 * @return JSON object as a string
	 * @throws JAXBException
	 */

	public static String marshall(Files f) throws JAXBException {
		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Files.class }, properties);
		StringWriter sw = new StringWriter();
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(f, sw);
		return sw.toString();
	}
}
