/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */

package directory;


import java.io.File;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;



public class ApplicationProperties extends Properties
{
	private static Map instanceMap             = new HashMap(10);

	private String propertyFileID;

	private ApplicationProperties(String propertyFileID)
	{
		this.propertyFileID = propertyFileID;
	}


	public static synchronized ApplicationProperties getInstance()
	{
		ApplicationProperties defaultInstance = null;

		try
		{
			defaultInstance = getInstance("common");
		} catch (Throwable exception)
		{
			throw new RuntimeException(exception);
		}

		return defaultInstance;
	}

	public static synchronized ApplicationProperties getInstance(String propertyFileID) throws MissingResourceException
	{

		//System.out.println("temp id : " + propertyFileID);
		
		ApplicationProperties tempInstance = (ApplicationProperties) instanceMap.get(propertyFileID);
		
		//System.out.println("looking for properties :  " +  "properties." + propertyFileID + "_application");
				

		if (tempInstance == null)
		{
			tempInstance = new ApplicationProperties(propertyFileID);

			PropertyResourceBundle resourceBundle = (PropertyResourceBundle)ResourceBundle.getBundle("properties." + propertyFileID + "_application");           

			Enumeration keys = resourceBundle.getKeys();

			while (keys.hasMoreElements())
			{
				String key   = (String)keys.nextElement();
				String value = resourceBundle.getString(key);

				tempInstance.setProperty(key, value);
			}

			instanceMap.put(propertyFileID, tempInstance);
		}

		return tempInstance;
	}


	public String getProperty(String propertyName)
	{
		String propValue = super.getProperty(propertyName);

		if (propValue == null || propValue.equals(""))
		{

			System.out.println("Unable to find " + propertyName + " for environment "+ propertyFileID);   
			//list(System.out);

			throw new RuntimeException("Property not found: " + propertyName  + " for environment "+ propertyFileID);
		}

		return propValue;
	}

	public String getDirectory(String propertyName)
	{
		String dirName = getProperty(propertyName);

		if (!dirName.endsWith(File.separator))
		{
			dirName += File.separator;
		}

		return dirName;
	}

	public String getProperty(String propertyName, String defaultPropertyValue)
	{
		String propValue = super.getProperty(propertyName);

		if (propValue == null || propValue.equals(""))
		{
			propValue = defaultPropertyValue;
		}

		return propValue;
	}

	public int getIntProperty(String propertyName, int defaultPropertyValue)
	{
		String val = getProperty(propertyName, "" + defaultPropertyValue);

		return Integer.parseInt(val);
	}

	public List getPropertyAsList(String propertyName)
	{

		String[] array = getPropertyAsArray(propertyName);

		List list = new ArrayList(array.length);

		for (int i=0; i<array.length; i++)
		{
			list.add(array[i]);
		}

		return list;
	}


	public String[] getPropertyAsArray(String propertyName) 
	{

		final String propertyValue = getProperty(propertyName, "");

		if (propertyValue == null || propertyValue.length() <= 0)
		{
			return new String[] {};
		}

		String [] propertyArray = propertyValue.toUpperCase().split(",");

		for (int i=0; i <propertyArray.length; i++)
		{
			propertyArray[i] = propertyArray[i].trim();
		}

		return propertyArray;
	}

}