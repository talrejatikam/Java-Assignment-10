package com.contacts;

import java.util.Comparator;

public class SortListByNameComparator implements Comparator<Contact>{

	@Override
	public int compare(Contact o1, Contact o2) {
		return o1.getContactName().compareToIgnoreCase(o2.getContactName());
	}

}
