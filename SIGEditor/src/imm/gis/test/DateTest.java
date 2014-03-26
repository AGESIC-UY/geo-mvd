package imm.gis.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTest {

	public DateTest(){
	    java.text.DateFormat format = java.text.DateFormat.getInstance();
	    SimpleDateFormat sdf = (SimpleDateFormat)format;
	    System.out.println(sdf.toLocalizedPattern());
	    try {
			System.out.println(sdf.parse("12/28/2006"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		System.out.println(args);
		//new DateTest();
	}

}
