import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {	
	
	private BorrowBookUI ui;	
	private Member member;
	private Library library;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;	
	private List<Book> pendingBooks;
	private List<Loan> completedLoans;
	private Book book;
	
	
	public BorrowBookControl() {
		this.library = Library.getInstance();
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
		book = library.getBook(bookId);
		if (book == null) {
			ui.display("Invalid bookId");
			return;		}
		if (!book.isAvailable()) {
			ui.display("Book cannot be borrowed");
			return;
		}
		pendingBooks.add(book);
		for (Book book : pendingBooks) {
			ui.display(book.toString());
		}
		if (library.getLoansRemainingForMember(member) - pendingBooks.size() == 0) {
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
			for (Book book : pendingBooks) {
				ui.display(book.toString());
			}
			completedLoans = new ArrayList<Loan>();
			ui.setState(BorrowBookUI.UiState.FINALISING);
			state = ControlState.FINALISING;
		}
	}


	public void commitLoans() {
		if (!state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (Book book : pendingBooks) {
			Loan loan = library.issueLoan(book, member);
			completedLoans.add(loan);			
		}
		ui.display("Completed Loan Slip");
		for (Loan loan : completedLoans) {
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
