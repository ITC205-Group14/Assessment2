import java.io.Serializable;


@SuppressWarnings("serial")
public class book implements Serializable {
	
	private String Title;
	private String Author;
	private String CallNo;
	private int Id;	
	private enum State { AVAILABLE, ON_LOAN, DAMAGED, RESERVED };
	private State state;
	
	
	public book(String author, String title, String callNo, int id) {
		this.Author = author;
		this.Title = title;
		this.CallNo = callNo;
		this.Id = id;
		this.state = State.AVAILABLE;
	}
	
	public String toString() {
		StringBuilder bookDetails = new StringBuilder();
		bookDetails.append("Book: ").append(Id).append("\n")
		  .append("  Title:  ").append(Title).append("\n")
		  .append("  Author: ").append(Author).append("\n")
		  .append("  CallNo: ").append(CallNo).append("\n")
		  .append("  State:  ").append(state);
		
		return bookDetails.toString();
	}

	public Integer ID() {
		return Id;
	}
	

	public String Title() {
		return Title;
	}
	
	
	public boolean Available() {
		return state == State.AVAILABLE;
	}
	
	
	public boolean On_loan() {
		return state == State.ON_LOAN;
	}
	
	
	public boolean Damaged() {
		return state == State.DAMAGED;
	}

	
	public void Borrow() {
		if (state.equals(State.AVAILABLE)) {
			state = State.ON_LOAN;
		}
		else {
			throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", state));}		
		}


	public void Return(boolean DAMAGED) {
		if (state.equals(State.ON_LOAN)) {
			if (DAMAGED) {
				state = State.DAMAGED;
			}
			else {
				state = State.AVAILABLE;}
		}
		else {
			throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", state));}		
		}

	
	public void Repair() {
		if (state.equals(State.DAMAGED)) {
			state = State.AVAILABLE;}
		else {
			throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", state));}
	}
}