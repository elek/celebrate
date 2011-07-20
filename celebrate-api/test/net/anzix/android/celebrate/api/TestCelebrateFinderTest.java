package net.anzix.android.celebrate.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class TestCelebrateFinderTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Test
	public void roundDay() throws ParseException{
		Assert.assertEquals(10,CelebrateFinder.roundDay(2.3f));
		Assert.assertEquals(50,CelebrateFinder.roundDay(47f));
		Assert.assertEquals(200,CelebrateFinder.roundDay(112f));
	}
	
	@Test
	public void roundMonth() throws ParseException{
		Assert.assertEquals(3,CelebrateFinder.roundMonth(2.033f));
		Assert.assertEquals(30,CelebrateFinder.roundMonth(22.3f));
		Assert.assertEquals(200,CelebrateFinder.roundMonth(115f));

	}
	
	
	@Test
	public void simple() throws ParseException{
		List<Event> in = new ArrayList();
		
		
		in.add(new Event("birth", sdf.parse("19790326")));
		
		
		List<Celebrate> findFeasts = CelebrateFinder.findFeasts(in);
		for (Celebrate c : findFeasts){
			System.out.println(c.getDate()+" "+c.getReason());
		}
	}
}
