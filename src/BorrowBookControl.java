import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {	
	private BorrowBookUI ui;	
	private library library;
	private member member;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;	
	private List<book> pendingBooks;
	private List<loan> completedLoans;
	private book book;
	
	
	public BorrowBookControl() {
		this.library = library.INSTANCE();
		state = ControlState.INITIALISED;
	}
	

	public void setUI(BorrowBookUI ui) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");		
		}
	
		this.ui = ui;
		ui.setState(BorrowBookUI.UiState.READY);
		state = ControlState.READY;		
	}
	
		
	public void swiped(int memberId) {
		if (!state.equals(ControlState.READY)) {
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
		}
	
		member = library.getMember(memberId);
		if (member == null) {
			ui.display("Invalid memberId");
			return;
		}
		if (library.memberCanBorrow(member)) {
			pendingBooks = new ArrayList<>();
			ui.setState(BorrowBookUI.UiState.SCANNING);
			state = ControlState.SCANNING; }
		else {
			ui.display("Member cannot borrow at this time");
			ui.setState(BorrowBookUI.UiState.RESTRICTED); 
		}
	}
	
	
	public void scanned(int bookId) {
		book = null;
		if (!state.equals(ControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}	
		book = library.Book(bookId);
		if (book == null) {
			ui.display("Invalid bookId");
			return;		}
		if (!book.Available()) {
			ui.display("Book cannot be borrowed");
			return;
		}
		pendingBooks.add(book);
		for (book book : pendingBooks) {
			ui.display(book.toString());
		}
		if (library.loansRemainingForMember(member) - pendingBooks.size() == 0) {
			ui.display("Loan limit reached");
			complete();
		}
	}
	
		
	public void complete() {
		if (pendingBooks.size() == 0) {
			cancel();
		}
		else {
			ui.display("\nFinal Borrowing List");
			for (book book : pendingBooks) {
				ui.display(book.toString());
			}
			completedLoans = new ArrayList<loan>();
			ui.setState(BorrowBookUI.UiState.FINALISING);
			state = ControlState.FINALISING;
		}
	}


	public void commitLoans() {
		if (!state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (book book : pendingBooks) {
			loan loan = library.issueLoan(book, member);
			completedLoans.add(loan);			
		}
		ui.display("Completed Loan Slip");
		for (loan loan : completedLoans) {
			ui.display(loan.toString());
		}
		ui.setState(BorrowBookUI.UiState.COMPLETED);
		state = ControlState.COMPLETED;
	}

	
	public void cancel() {
		ui.setState(BorrowBookUI.UiState.CANCELLED);
		state = ControlState.CANCELLED;
	}	
	
}
