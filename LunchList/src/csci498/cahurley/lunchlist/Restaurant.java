package csci498.cahurley.lunchlist;


public class Restaurant 
{
	private String name = "";
	private String address = "";
	private String type = "";
	
	private int year;
	private int month;
	private int day;
	
	public String getName() {return(name);}
	public void setName(String name) {this.name = name;}
	
	public String getAddress() {return(address);}
	public void setAddress(String address) {this.address = address;}
	
	public String getType() {return(type);}
	public void setType(String type) {this.type = type;}
	
	public int getYear() {return(year);}
	public void setYear(int year) {this.year = year;}
	
	public int getMonth() {return(month);}
	public void setMonth(int month) {this.month = month;}
	
	public int getDay() {return(day);}
	public void setDay(int day) {this.day = day;}
	
	public String toString()
	{
		return(getName());
	}
}
