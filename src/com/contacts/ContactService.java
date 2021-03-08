package com.contacts;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.sql.*;

public class ContactService {

	ArrayList<Contact> contactsIP;
	ArrayList<Contact> fileContact;
	BufferedReader br;
	Connection con;
	
	ContactService()throws Exception
	{
		contactsIP=new ArrayList<Contact>();
		br=new BufferedReader(new InputStreamReader(System.in));
		con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","tikam","tikam");
	}
	
	void inputAddNewContact()throws Exception									
	{
		Contact contact=new Contact();
		
		System.out.println("Enter Contact ID");
		int id=Integer.parseInt(br.readLine());
		
		boolean result=isContactIDValid(id);
		if(result)
			contact.setContactID(id);
		else
		{
			System.out.println("Contact with ID Exists in DB or List");
			return;
		}
		
		System.out.println("Enter Contact Name");
		contact.setContactName(br.readLine().toLowerCase());
		
		System.out.println("Enter Contact Email");
		String email=br.readLine();
		boolean res=isEmailValid(email.toLowerCase());
		if(!res)
		{
			System.out.println("Invalid Email");
			return;
		}
		contact.setContactEmail(email);	
		
		contact.setContactNumber(contactNumberInput());
		
		addNewContact(contact);
	}

	public ArrayList<String> contactNumberInput()throws Exception				
	{
		ArrayList<String> nums=new ArrayList<String>();
		while(true)
		{
			System.out.println("Enter Contact Numbers or quit");
			String ip=br.readLine();
			if(ip.equalsIgnoreCase("quit"))
				break;
			boolean res=isContactNumValid(ip);
			
			if(!res)
			{
				System.out.println("This Contact Number is Invalid, Try Again");
				continue;
			}
					
			
			
			nums.add(ip+",");
		}
		return nums;
	}

	public boolean isEmailValid(String email)									
    { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$"; 
                              
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches(); 
    } 
	
	public boolean isContactNumValid(String num)								
	{
		return (num.length()<=10 && num.length()>=7);
	}
	
	public boolean isContactIDValid(int id)throws Exception						
	{
		PreparedStatement ps=con.prepareStatement("select cid from contact_tbl");
		
		Iterator<Contact> it =contactsIP.iterator();
		while(it.hasNext())
		{
			if(it.next().getContactID()==id)
				return false;
		}
		ResultSet rs=ps.executeQuery();
		while(rs.next())
		{
			if(rs.getInt(1)==id)
				return false;
		}
		return true;
	}
	
	public void addNewContact(Contact contact)throws Exception					
	{
		contactsIP.add(contact);
	}
	
	public void insertContactInDB()												
	{
		try
		{
			PreparedStatement ps=con.prepareStatement("INSERT INTO contact_tbl VALUES (?,?,?,?)");
			
			Iterator<Contact> it=contactsIP.iterator();
			Contact ref=null;
			while(it.hasNext())
			{
				ref=it.next();
				
				ps.setInt(1, ref.getContactID());
				ps.setString(2,ref.getContactName());
				ps.setString(3, ref.getContactEmail());
				String nums=buildContactNum(ref.getContactNumber());
				ps.setString(4, nums);
				
				System.out.println(ps.executeUpdate());
				
			}
			
		}catch(SQLException e) {e.printStackTrace(); }
	}
	
	public String buildContactNum(Iterator<Contact> it)							
	{
		String nums="";
		for(int i=0;i<it.next().getContactNumber().size();i++)
			nums+=it.next().getContactNumber().get(i);
		return nums;
	}
	
	public String buildContactNum(ArrayList<String> al)							
	{
		String nums="";
		for(int i=0;i<al.size();i++)
			nums+=al.get(i);
		return nums;
	}
	
	public void inputSearchContactByName()throws Exception						 
	{
		System.out.println("Enter Contact Name");
		String name=br.readLine();
		
		searchContactByName(name);
	}
	
	public Contact searchContactByName(String name)								
	{	
		try
		{
			Iterator<Contact> it=contactsIP.iterator();
			while(it.hasNext())
			{
				Contact contact=it.next();
				if(contact.getContactName()==name)
					return contact;
			}
			throw new ContactNotFoundException("Contact Doesn't Exist, Please Try Again\n");
			
		}catch(ContactNotFoundException e) {System.out.println(e.getMessage());return null;}
			
	}
	
	public ArrayList<String> buildNumArrayList(String str)				
	{
		ArrayList<String> al=new ArrayList<String>();
		
		String [] arr=str.split(",");
		
		for(int i=0;i<arr.length;i++)
			al.add(arr[i]);
		
		return al;
	}

	public void inputSearchContactByNumber()throws Exception
	{
		System.out.println("Enter Input Number");
		String ip=br.readLine();
		if(ip.length()>10)
		{
			System.out.println("Invalid Search Value");
			return;
		}
		searchContactByNumber(ip);
	}
	
	public ArrayList<Contact> searchContactByNumber(String ip)				
	{
		ArrayList<Contact> conal=new ArrayList<Contact>();
		
		Iterator<Contact> it=contactsIP.iterator();
		
		while(it.hasNext())
		{
			Contact contact=it.next();
			String nums=buildContactNum(contact.getContactNumber());
			if(nums.contains(ip))
				conal.add(contact);
		}
		
		return conal;		
	}
	
	public boolean isContactPresent(String src,String ip)						
	{
		return (src.contains(ip));
	}
	
	public void inputAddContactNumber()throws Exception
	{
		System.out.println("Enter Contact ID");
		int id=Integer.parseInt(br.readLine());
		
		Contact c=containsContact(id);
		if(c==null)
		{
			System.out.println("Contact Doesn't Exist");
			return;
		}
		
		System.out.println("Enter Contact Number");
		String no=br.readLine();
		if(isContactNumValid(no))
			addContactNumber(id,no);
		else
		{
			System.out.println("Invalid Contact Number");
			return;
		}
		
	}
	
	public void addContactNumber(int id, String no)throws Exception
	{
		Iterator<Contact> it=contactsIP.iterator();
		while(it.hasNext())
		{
			Contact contact=it.next();
			if(contact.getContactID()==id)
			{
				String nums=buildContactNum(contact.getContactNumber());
				nums+=no;
				contact.setContactNumber(this.buildNumArrayList(nums));
			}
		}
		
	}
	
	public void inputRemoveContact()throws Exception
	{
		
		System.out.println("Enter Contact ID");
		int id=Integer.parseInt(br.readLine());
		
		Contact c=containsContact(id);
		try
		{
			if(c==null)
				throw new ContactNotFoundException("Contact Doesn't Exist, Please Try Again");
			
		}catch(ContactNotFoundException e) {System.out.println(e.getMessage());}
		
		this.removeContact(c);
	}
	
	public Contact containsContact(int id)
	{
		Iterator<Contact> it=contactsIP.iterator();
		while(it.hasNext())
		{
			Contact contact=it.next();
			if(contact.getContactID()==id)
				return contact;
		}
		return null;
		
	}
	
	public void removeContact(Contact c)
	{
		contactsIP.remove(c);
	}
	
	public void sortListByName(ArrayList<Contact> al)
	{
		Collections.sort(al, new SortListByNameComparator());
	}
	
	public void readContactsFromFile()throws Exception
	{
		File f=new File("Contact.txt");
		//if(f.exists())
			//System.out.println("OK");
		
		fileContact=new ArrayList<Contact>();
		
		FileInputStream fis=new FileInputStream(f);
		Scanner sc=new Scanner(fis);
	
		
		while(sc.hasNext())
		{
			Contact contact=new Contact();
			
			String [] arr=sc.nextLine().split(",");
			contact.setContactID(Integer.parseInt(arr[0]));
			contact.setContactName(arr[1]);
			contact.setContactEmail(arr[2]);
			String nums="";
			for(int i=3;i<arr.length;i++)
				nums+=arr[i]+",";
			contact.setContactNumber(buildNumArrayList(nums));
			
			fileContact.add(contact);
			
		}
		sc.close();
	}
	
	public void displayArrayList(ArrayList<?> al)
	{
		Iterator<?> it=al.iterator();
		while(it.hasNext())
		{
			Contact contact=(Contact)it.next();
			System.out.printf("%d   %20s   %20s   %20s \n",contact.getContactID(),contact.getContactName(),contact.getContactEmail(),contact.getContactNumber().toString());
		}
	}
	
	public void serializeContactDetails(ArrayList<Contact>ip)throws Exception
	{
		File f=new File("Contacts.dat");
		FileOutputStream fos=new FileOutputStream(f);
		ObjectOutputStream oos=new ObjectOutputStream(fos);
		
		oos.writeObject(ip);
		
		System.out.println("Object Written Succesfully");
		
		oos.close();
	}
	
	public ArrayList<?> deserializeContact() throws Exception
	{
		
		File f=new File("Contacts.dat");
		FileInputStream fis=new FileInputStream(f);
		ObjectInputStream ois=new ObjectInputStream(fis);
		
		ArrayList<?> list =new ArrayList<>();
		
		Object obj=ois.readObject();
		if(obj instanceof Collection)
			list=new ArrayList<>((Collection<?>)obj);
	
		ois.close();
		
		return list;
	}
	
	public Set<Contact> populateContactFromDB()throws Exception
	{
		Set<Contact> set=new HashSet<Contact>();
		
		PreparedStatement ps=con.prepareStatement("select * from contact_tbl");
		ResultSet rs=ps.executeQuery();
		
		while(rs.next())
		{
			Contact contact=new Contact();
			
			contact.setContactID(rs.getInt(1));
			contact.setContactName(rs.getString(2));
			contact.setContactEmail(rs.getString(3));
			contact.setContactNumber(buildNumArrayList(rs.getString(4)));
			
			set.add(contact);
		}
		
		return set;
	}
	
	public void displaySet(Set<Contact> set)
	{
		Iterator<Contact> it=set.iterator();
		while(it.hasNext())
		{
			Contact contact=it.next();
			System.out.printf("%d   %20s   %20s   %20s",contact.getContactID(),contact.getContactName(),contact.getContactEmail(),contact.getContactNumber().toString());
		}
	}
	
	public void menu()throws Exception
	{
		while(true)
		{
			System.out.println("\n\n1. Add Contact to List\n2. Remove Contact\n3. Search Contact by Name\n4. Search Contact by Num\n5. Add Contact Number\n6. Sort ArrayList\n7. Read From File\n8. Serialize Contact\n9. Deseriaize Contact\n10. Populate Contact from DB\n11. Insert Contact In DB\n12. Exit");
			int opt=Integer.parseInt(br.readLine());
			if(opt==12)
				break;
			switch(opt)
			{
				case 1: this.inputAddNewContact();break;
				case 2: this.inputRemoveContact();break;
				case 3: this.inputSearchContactByName();break;
				case 4: this.inputSearchContactByNumber();break;
				case 5: this.inputAddContactNumber();break;
				case 6: this.sortListByName(this.fileContact);break;
				case 7: this.readContactsFromFile();this.displayArrayList(fileContact);break;
				case 8: this.serializeContactDetails(contactsIP);break;
				case 9: ArrayList<?> al=this.deserializeContact();this.displayArrayList(al);break;
				case 10: Set<Contact> set=this.populateContactFromDB();this.displaySet(set);break;
				case 11: this.insertContactInDB();
				default : this.inputAddNewContact();
			}
		}
	}
	
	public static void main(String[] args)throws Exception {
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		
		ContactService cs=new ContactService();
		
		cs.menu();
	}

}

	