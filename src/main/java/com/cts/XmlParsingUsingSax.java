package com.cts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParsingUsingSax {


	static List<Account> accounts=new ArrayList<Account>();
	public List getElement(String path) {

    try {

	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser = factory.newSAXParser();

	DefaultHandler handler = new DefaultHandler() {
		
		boolean record = false;
		boolean accountNumber = false;
		boolean description = false;
		boolean startBalance = false;
		boolean mutation = false;
		boolean endBalance = false;
		Account accnt = null;
		@Override
	    public void startElement(String uri, String localName, String qName,
	            Attributes atts) throws SAXException {
	        if(qName.equals("record")) {
	        	accnt = new Account();
	            for(int i = 0; i < atts.getLength(); i++) {
	            	accnt.attributes.put(atts.getQName(i),atts.getValue(i));
	            }
	        }
	        if(qName.equalsIgnoreCase("accountNumber")) { accountNumber = true; }
	        if(qName.equalsIgnoreCase("description"))  { description = true;  }
	        if(qName.equalsIgnoreCase("startBalance"))  { startBalance = true;  }
	        if(qName.equalsIgnoreCase("mutation"))  { mutation = true;  }
	        if(qName.equalsIgnoreCase("endbalance"))  { endBalance = true;  }
	    }

	    @Override
	    public void endElement(String uri, String localName, String qName)
	            throws SAXException {
	        if(qName.equals("record")) {
	            accounts.add(accnt);
	            accnt = null;
	        }
	        if(qName.equalsIgnoreCase("accountNumber")) { accountNumber = false; }
	        if(qName.equalsIgnoreCase("description"))  { description = false;  }
	        if(qName.equalsIgnoreCase("startBalance"))  { startBalance = false;  }
	        if(qName.equalsIgnoreCase("mutation"))  { mutation = false;  }
	        if(qName.equalsIgnoreCase("endbalance"))  { endBalance = false;  }
	    }

	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	        if (accountNumber) {
	        	accnt.accountNumber = new String(ch, start, length);
	        }
	        if (description) {
	        	accnt.description = new String(ch, start, length);
	        }
	        if (startBalance) {
	        	accnt.startBalance = new String(ch, start, length);
	        }
	        if (mutation) {
	        	accnt.mutation = new String(ch, start, length);
	        }
	        if (endBalance) {
	        	accnt.endBalance = new String(ch, start, length);
	        }
	    }

	
		};

		saxParser.parse(path,handler);
		// saxParser.parse("c:\\kk\\records.xml",handler);	
 
     } catch (Exception e) {
       e.printStackTrace();
     }
  
	return accounts;
   }

}