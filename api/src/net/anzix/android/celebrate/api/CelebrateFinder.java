package net.anzix.android.celebrate.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CelebrateFinder {
	
	public static float getMonthNo(Date old, Date current) {
		int y1 = current.getYear();
		int y2 = old.getYear();
		float offset = (old.getDate() - current.getDate()) / 30f;		
		return (y1 - y2) * 12 + current.getMonth() - old.getMonth() - offset;

	}

	public static float getDayNo(Date ed, Date now) {
		return Math.round((now.getTime() - ed.getTime())
				/ (1000 * 60 * 60 * 24));
	}

	public static List<Celebrate> findFeasts(List<Event> event) {
		List<Celebrate> result = new ArrayList<Celebrate>();
		Date now = new Date();
		
		for (Event e : event) {
			Date ed = e.getDate();
			if (ed==null) {
				continue;
			}			
			
			float monthNo = getMonthNo(ed, now);
			

			float yearNo = monthNo / 12f;
			float dayNo = getDayNo(ed,now);

			Date c = new Date(ed.getYear() + (int) Math.ceil(yearNo),
					ed.getMonth(), ed.getDate());
			result.add(new Celebrate(e, c, "" + ((int) Math.ceil(yearNo))
					+ " year"));

			int nextMonthNo = roundMonth(monthNo);
			Date c1 = new Date(ed.getYear(), ed.getMonth() + nextMonthNo,
					ed.getDate());
			result.add(new Celebrate(e, c1, "" + nextMonthNo + " month"));

			int nextDayNo = roundDay(dayNo);
			c1 = new Date(ed.getYear(), ed.getMonth(), ed.getDate() + nextDayNo);
			result.add(new Celebrate(e, c1, "" + nextDayNo + " day"));

		}
		Collections.sort(result, new Comparator<Celebrate>(){

			public int compare(Celebrate o1, Celebrate o2) {				
				return o1.getDate().compareTo(o2.getDate());
			}});
		return result;
	}



	public static int roundDay(float day) {
		if (day < 100) {
			return (int) Math.ceil(day / 10) * 10;
		} else if (day < 1000) {
			return (int) Math.ceil(day / 100) * 100;
		} else {
			return (int) Math.ceil(day / 1000) * 1000;
		}

	}

	public static int roundMonth(float month) {
		if (month < 12) {
			return (int) Math.ceil(month);
		} else if (month < 100) {
			return (int) Math.ceil(month / 10) * 10;
		} else if (month < 1000) {
			return (int) Math.ceil(month / 100) * 100;
		} else {
			return (int) Math.ceil(month / 1000) * 1000;
		}
	}

	

}
