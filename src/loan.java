import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {
	
	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };	
	private int id;
	private Book book;
	private Member member;
	private Date date;
	private LoanState state;

	
	public loan(int loanId, Book book, Member member, Date dueDate) {
		this.id = loanId;
		this.book = book;
		this.member = member;
		this.date = dueDate;
		this.state = LoanState.CURRENT;
	}

	
	public void checkOverDue() {
		if (state == LoanState.CURRENT &&
			Calendar.getInstance().Date().after(Date)) {
			this.state = LoanState.OVER_DUE;			
		}
	}

	
	public boolean isOverDue() {
		return state == LoanState.OVER_DUE;
	}

	
	public int getId() {
		return id;
	}


	public Date getDueDate() {
		return date;
	}
	
	
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder newString = new StringBuilder();
		newString.append("Loan:  ").append(id).append("\n")
		  .append("  Borrower ").append(member.getId()).append(" : ")
		  .append(member.getLastName()).append(", ").append(member.getFirstName()).append("\n")
		  .append("  Book ").append(book.id()).append(" : " )
		  .append(book.Title()).append("\n")
		  .append("  DueDate: ").append(dateFormat.format(date)).append("\n")
		  .append("  State: ").append(state);		
		return sb.toString();
	}


	public member Member() {
		return member;
	}


	public book Book() {
		return book;
	}


	public void Loan() {
		state = LoanState.DISCHARGED;		
	}

}