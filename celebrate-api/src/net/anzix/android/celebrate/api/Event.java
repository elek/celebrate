package net.anzix.android.celebrate.api;

import java.util.Date;

public class Event {
	private long id;
	private Date date;
	private String name;

	public Event(String name, Date date) {
		super();
		this.date = date;
		this.name = name;
	}

	public Event(long id, String name, Date date) {
		super();
		this.id = id;
		this.date = date;
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
