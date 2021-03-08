package com.contacts;

import java.io.Serializable;
import java.util.ArrayList;

public class Contact implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int contactID;
	private String contactName;
	private String contactEmail;
	private ArrayList<String> contactNumber;
	
	Contact()
	{
		contactID=0;
		contactName=contactEmail="";
		contactNumber=new ArrayList<String>();
	}

	public int getContactID() {
		return contactID;
	}

	public void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public ArrayList<String> getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(ArrayList<String> contactNumber) {
		this.contactNumber = contactNumber;
	}

	
	
}