import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {
	
	private static Calendar self;
	private static Calendar calendar;
	private static Calendar date;
	
	
	private Calendar() {
		calendar = Calendar.getInstance();
	}
	
	public static Calendar getInstance() {
		if (self == null) {
			self = new Calendar();
		}
		return self;
	}	
	
	public void add(Calendar date, Date loanPeriod) {
		calendar.add(Calendar.date, loanPeriod);
		
	}

	public synchronized void setTime(Date date) {
		try {
			calendar.setTime(date);
	        
		}
		catch (Exception e) {
			throw new RuntimeException(e);}
		}	
	

	private void set(int time, int i) {
		try {
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);  
        calendar.set(java.util.Calendar.MINUTE, 0);  
        calendar.set(java.util.Calendar.SECOND, 0);  
        calendar.set(java.util.Calendar.MILLISECOND, 0);		
	}
		catch (Exception e) {
			throw new RuntimeException(e);}
	}
		
	public synchronized Date getTime() {
		try {
	        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);  
	        calendar.set(java.util.Calendar.MINUTE, 0);  
	        calendar.set(java.util.Calendar.SECOND, 0);  
	        calendar.set(java.util.Calendar.MILLISECOND, 0);
			return calendar.getTime();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

	public synchronized Date getDueDate(Date loanPeriod) {
		Date now = calendar.getTime();
		calendar.add(Calendar.date, loanPeriod);
		Date dueDate = calendar.getTime();
		calendar.setTime(now);
		return dueDate;
	}
	
	public synchronized long getDaysDifference(Date targetDate) {
		long diffMillis = targetDate.getTime() - targetDate.getTime();
	    long diffDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
	    return diffDays;
	}
}