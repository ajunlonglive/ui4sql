/**
 * UI4SQL V1.0 (https://ui4sql.net)
 * Copyright 2022 PaulsenITSolutions 
 * Licensed under MIT (http://github.com/arnepaulsen/ui4sql/LICENSE) 
 */

/*
*
* Change History
*   Author            Date       Description
*   -------------------------------------------
*   Enrique Meneses (D748705)   Sep. 05    Implemented
* 
*  $Log: Person.java,v $
*  Revision 1.14  2006/11/15 19:57:59  d748705
*  
*/


package directory;

import java.util.List;
import java.util.ArrayList;


public class Person
{
    private boolean valid = false;

    private String firstName = "";
    private String lastName = "";
    private String middleName = "";

    private String nuid = "";
    private String employeeID = "";

    private String departmentName = "";

    private String costCenterName = "";
    private String costCenterCode = "";

    private String jobCode = "";
    private String jobTitle = "";


    private String locationCode = "";
    private String locationName = "";

    private String regionName = "";
    private String regionCode = "";

    private String emailAddress = "";
    private String telephoneNumber = "";

    private String employeeClass = "";

    private boolean provider = false;
    
    private String homeDeployment = "";

    private String customDisplay = "";
    
    /**
     * This is the ID of the deployment
     */
    private String epicAccountOwner = "";

    private List miscAttributes = new ArrayList();

    public String getFirstName()
    {
	return firstName;
    }

    public void setFirstName(String name)
    {
	if(name != null)
	{
	    firstName = name.trim();
	    
	    if(firstName.length() > 1)
	    {
		firstName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	    }
	}	
    }

    public String getLastName()
    {
	return lastName;
    }

    public void setLastName(String name)
    {
	if(name != null)
	{
	    lastName = name.trim();
	    
	    if(lastName.length() > 1)
	    {
		lastName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
	    }
	}	
    }


    public String getMiddleName()
    {
	return middleName;
    }


    public void setMiddleName(String middleName)
    {
	if(middleName != null) 
	{
	    this.middleName = middleName.trim();
	}
	else
	{
	    middleName = null;
	}

    }


    public String getNameWithAbbrviation(String abbreviation)
    {
	String midInitial = "";

	if(lastName == null || firstName == null || lastName.equals("") || firstName.equals("")) return "";

	if(middleName != null && !middleName.trim().equals(""))
	{
	    midInitial = middleName.substring(0, 1).toUpperCase();
	}

	if(abbreviation == null || abbreviation.trim().equals("")) 
	{
	    abbreviation = ""; 
	}
	else
	{
	    abbreviation = " " + abbreviation.trim();
	}

	return lastName + abbreviation + ", " + firstName + " " + midInitial;
    }


    public String getName()
    {
	String midInitial = "";

	if(middleName != null && !middleName.equals(""))
	{
	    midInitial = middleName.substring(0, 1).toUpperCase();
	}

	return lastName + ", " + firstName + " " + midInitial;
    }

    public void setEmployeeClass(String employeeClass)
    {
	this.employeeClass = employeeClass;
    }

    public String getEmployeeClass()
    {
	return employeeClass;
    }


    public void setTelephoneNumber(String phone)
    {
	this.telephoneNumber = phone;
    }

    public String getTelephoneNumber()
    {
	return telephoneNumber;
    }

    public String getNUID()
    {
	return nuid;
    }


    public void setNUID(String nuid)
    {
	if(nuid != null)
	{
	    this.nuid = nuid.toUpperCase();
	}
    }


    public void setCustomDisplay(String customDisplay)
    {
	this.customDisplay = customDisplay;
    }

    public String getCustomDisplay()
    {
	if(customDisplay == null || customDisplay.equals("") || customDisplay.matches(".*null"))
	{
	    customDisplay = nuid;
	}

	return customDisplay;
    }

    public String getEmployeeID()
    {
	return employeeID;
    }

    public void setEmployeeID(String employeeID)
    {
	this.employeeID = employeeID;
    }


    public String getCostCenterName()
    {
	return costCenterName;
    }

    public void setCostCenterName(String costCenterName)
    {
	this.costCenterName = costCenterName;
    }

    public String getCostCenterCode()
    {
	return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode)
    {
	this.costCenterCode = costCenterCode;
    }

    public String getJobTitle()
    {
	return jobTitle;
    }

    public void setJobTitle(String jobTitle)
    {
	this.jobTitle = jobTitle;
    }

    public String getJobCode()
    {
	return jobCode;
    }

    public void setJobCode(String jobCode)
    {
	this.jobCode = jobCode;
    }


    public String getLocationCode()
    {
	return locationCode;
    }

    public void setLocationCode(String locationCode)
    {
	this.locationCode = locationCode;
    }


    public String getLocationName()
    {
	return locationName;
    }

    public void setLocationName(String locationName)
    {
	this.locationName = locationName;
    }

    public String getRegionCode()
    {
	return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
	this.regionCode = regionCode;
    }

    public String getRegionName()
    {
	return regionName;
    }

    public void setRegionName(String regionName)
    {
	this.regionName = regionName;
    }

    public String getEmailAddress()
    {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
	this.emailAddress = emailAddress;
    }

    public void setProvider(boolean provider)
    {
	this.provider = provider;
    }

    public void setDepartmentName(String departmentName)
    {
	this.departmentName = departmentName;
    }

    public String getDepartmentName()
    {
	return departmentName;
    }

    public void setProvider(String provider)
    {
	this.provider = (provider.toUpperCase().charAt(0) == 'Y');
    }

    public boolean isProvider()
    {
	return provider;
    }
    
    public String getHomeDeployment()
    {
    	return homeDeployment;
    }
    
    public void setHomeDeployment(String value)
    {
    	homeDeployment = value;
    }


    public void addMiscAttribute(String name, String value)
    {
	String attrName = name.toUpperCase();

	attrName = attrName.replaceAll(" ", "");

	miscAttributes.add(new String[] {attrName, value});
    }


    public List getMiscAttributes()
    {
	return miscAttributes;
    }



    public String getMiscAttribute(String attributeName)
    {
	String attributeValue = null;

	String attrName = attributeName.toUpperCase();

	attrName = attrName.replaceAll(" ", "");

	for(int i=0; i<miscAttributes.size(); i++)
	{
	    String [] attribute = (String[])miscAttributes.get(i);
	    if(attribute[0].toUpperCase().equals(attrName.toUpperCase()))
	    {
		attributeValue = attribute[1];
		break;
	    }
	}

	return attributeValue;
    }

    public String[] getMiscAttributeArray(String attributeName)
    {
	String attributeValue = null;

	String attrName = attributeName.toUpperCase();

	attrName = attrName.replaceAll(" ", "");

	for(int i=0; i<miscAttributes.size(); i++)
	{
	    String [] attribute = (String[])miscAttributes.get(i);

	    if(attribute[0].equals(attrName.toUpperCase()))
	    {
		attributeValue = attribute[1];
		break;
	    }
	}

	String [] attributeValueArray = null;

	if(attributeValue != null)
	{
	    attributeValueArray = attributeValue.split("::");
	}

	return attributeValueArray;
    }


    public String[] getMiscAttributeArray(String attributeName, String matchingPattern)
    {
	String attributeValue = null;

	String attrName = attributeName.toUpperCase();

	attrName = attrName.replaceAll(" ", "");

	for(int i=0; i<miscAttributes.size(); i++)
	{
	    String [] attribute = (String[])miscAttributes.get(i);

	    if(attribute[0].equals(attrName.toUpperCase()))
	    {
		attributeValue = attribute[1];
		break;
	    }
	}

	String [] attributeValueArray = null;
	String [] matchedAttributes   = null;

	if(attributeValue != null)
	{
	    attributeValueArray = attributeValue.split("::");

	    int matchedAttrCount = 0;
	    for(int i=0; i<attributeValueArray.length; i++)
	    {
		if(attributeValueArray[i].toUpperCase().matches(".*" + matchingPattern.toUpperCase() + ".*"))
		{
		    matchedAttrCount++;
		}
	    }

	    matchedAttributes = new String[matchedAttrCount];

	    int index = 0;
	    for(int i=0; i<attributeValueArray.length; i++)
	    {
		if(attributeValueArray[i].toUpperCase().matches(".*" + matchingPattern.toUpperCase() + ".*"))
		{
		    matchedAttributes[index++] = attributeValueArray[i];
		}
	    }
	}

	return matchedAttributes;
    }



    public boolean isValid()
    {
	return valid;
    }

    public void setValid(boolean valid)
    {
	this.valid = valid;
    }


    public String toString()
    {
	String tempString = "{firstName: " + firstName   +
	", lastName: "  + lastName    +
	", middleName: " + middleName +
	", nuid: " + nuid +          
	", employeeID: " +  employeeID +   	       
	", departmentName: " + departmentName	+       
	", costCenterName: " + costCenterName +
	", costCenterCode: " + costCenterCode	+       
	", jobCode: " + jobCode +      
	", jobTitle: " + jobTitle +      	       
	", locationCode: " + locationCode + 
	", locationName: " + locationName + 	       
	", regionName: " +  regionName +      
	", regionCode: " +  regionCode +  		       
	", emailAddress: " + emailAddress +  	       
	", provider: " +  provider;

	int miscAttributeCount = miscAttributes.size();

	tempString += " [";
	for(int i=0; i<miscAttributeCount; i++)
	{
	    String [] attribute = (String[])miscAttributes.get(i);

	    tempString = tempString + attribute[0] + ": " + attribute[1];

	    if(i<miscAttributeCount-1)
	    {
		tempString += ",";
	    }
	}
	tempString += "]}";

	return tempString;
    }

    /**
     * gets the ID of the deployment.  This value
     * corresponds to the value stored in "9" on 
     * the epic systems.
     */
    public String getEpicAccountOwner() {
        return epicAccountOwner;
    }

    /**
     * Sets the ID of the deployment. This value
     * corresponds to the value stored in "9" on 
     * the epic systems.
     */
    public void setEpicAccountOwner(String epicAccountOwner) {
        this.epicAccountOwner = epicAccountOwner;
    }

}

