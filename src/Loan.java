import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {
	
	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };
	
	private int Id;
	private Book Book;
	private Member member;
	private Date date;
	private LoanState state;

	
	public Loan(int loanId, Book book, Member member, Date dueDate) {
		this.Id = loanId;
		this.Book = book;
		this.member = member;
		this.date = dueDate;
		this.state = LoanState.CURRENT;
	}

	
	public void checkOverDue() {
		if (state == LoanState.CURRENT &&
			Calendar.getInstance().Date().after(date)) {
			this.state = LoanState.OVER_DUE;			
		}
	}

	
	public boolean isOverDue() {
		return state == LoanState.OVER_DUE;
	}

	
	public Integer getId() {
		return Id;
	}


	public Date getDueDate() {
		return date;
	}
	
	
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder loanDetail = new StringBuilder();
		loanDetail.append("Loan:  ").append(Id).append("\n")
		  .append("  Borrower ").append(member.getId()).append(" : ")
		  .append(member.getLastName()).append(", ").append(member.getFirstName()).append("\n")
		  .append("  Book ").append(Id).append(" : " )
		  .append(Book.Title()).append("\n")
		  .append("  DueDate: ").append(dateFormat).append("\n")
		  .append("  State: ").append(state);		
		return loanDetail.toString();
	}


	public Member member() {
		return member;
	}


	public Book Book() {
		return Book;
	}


	public Loan() {
		state = LoanState.DISCHARGED;		
	}

}