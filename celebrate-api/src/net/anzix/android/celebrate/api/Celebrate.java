package net.anzix.android.celebrate.api;


import java.util.Date;

public class Celebrate {
	private Event event;
	private Date date;
	private String reason;

	public Celebrate(Event event, Date date, String reason) {
		super();
		this.event = event;
		this.date = date;
		this.reason = reason;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
