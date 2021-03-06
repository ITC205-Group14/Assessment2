import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Loan implements Serializable {

	public static enum LoanState { CURRENT, OVER_DUE, DISCHARGED };

	private int id;
	private Book book;
	private Member member;
	private Date date;
	private LoanState state;


	public Loan(int loanId, Book book, Member member, Date dueDate) {
		this.id = loanId;
		this.book = book;
		this.member = member;
		this.date = dueDate;
		this.state = LoanState.CURRENT;
	}


	public void checkOverDue() {
		if (state == LoanState.CURRENT &&
				Calendar.getInstance().getDate().after(date)) {
			this.state = LoanState.OVER_DUE;
		}
	}


	public boolean isOverDue() {
		return state == LoanState.OVER_DUE;
	}


	public Integer getId() {
		return id;
	}


	public Date getDueDate() {
		return date;
	}


	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = dateFormat.format(date);
		String memberFirstName = member.getFirstName();
		String memberLastName = member.getLastName();
		int memberId = member.getId();
		String bookTitle = book.getTitle();

		StringBuilder loanDetail = new StringBuilder();
		loanDetail.append("Loan:  ").append(id).append("\n")
		.append("  Borrower ").append(memberId).append(" : ")
		.append(memberLastName).append(", ").append(memberFirstName).append("\n")
		.append("  Book ").append(id).append(" : " )
		.append(bookTitle).append("\n")
		.append("  DueDate: ").append(formattedDate).append("\n")
		.append("  State: ").append(state);
		return loanDetail.toString();
	}


	public Member getMember() {
		return member;
	}


	public Book getBook() {
		return book;
	}


	public Loan() {
		state = LoanState.DISCHARGED;
	}


}