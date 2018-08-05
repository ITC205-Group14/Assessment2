import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {

	private static final String libraryFile = "library.obj";
	private static final int loanLimit = 2;
	private static final int loanPeriod = 2;
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 5.0;
	private static final double damageFee = 2.0;

	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;

	private Map<Integer, book> catalog;
	private Map<Integer, member> members;
	private Map<Integer, loan> loans;
	private Map<Integer, loan> currentLoans;
	private Map<Integer, book> damagedBooks;


	private Library() {
		catalog = new HashMap<>();
		members = new HashMap<>();
		loans = new HashMap<>();
		currentLoans = new HashMap<>();
		damagedBooks = new HashMap<>();
		bookId = 1;
		memberId = 1;
		loanId = 1;
	}


	public static synchronized Library getInstance() {
		if (self == null) {
			Path path = Paths.get(libraryFile);
			if (Files.exists(path)) {
				try (ObjectInputStream lof = new ObjectInputStream(new FileInputStream(libraryFile));) {

					self = (Library) lof.readObject();
					Calendar.getInstance().setDate(self.loanDate);
					lof.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}


	public static synchronized void save() {
		if (self != null) {
			self.loanDate = Calendar.getInstance().Date();
			try (ObjectOutputStream lof = new ObjectOutputStream(new FileOutputStream(libraryFile));) {
				lof.writeObject(self);
				lof.flush();
				lof.close();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


	public int getBookID() {
		return bookId;
	}


	public int getMemberID() {
		return memberId;
	}


	private int getNextBookId() {
		return bookId++;
	}


	private int getNextMemberId() {
		return memberId++;
	}


	private int getNextLoanId() {
		return loanId++;
	}


	public List<member> getMembers() {
		Collection<member> memberCollection = members.values();
		ArrayList<member> memberList = new ArrayList<member>(memberCollection);
		return memberList;
	}

	
	public List<book> getBooks() {
		Collection<book> booksCollection = catalog.values();
		ArrayList<book> booksList = new ArrayList<book>(booksCollection);
		return booksList;
	}


	public List<loan> getCurrentLoans() {
		Collection<loan> loansCollection = currentLoans.values();
		ArrayList<loan> loansList = new ArrayList<loan>(loansCollection);
		return loansList;
	}


	public member addMember(String lastName, String firstName, String email, int phoneNumber) {
		int nextMemberId = getNextMemberId();
		member member = new member(lastName, firstName, email, phoneNumber, nextMemberId);
		int memberid = member.getId();
		members.put(memberid, member);
		return member;
	}


	public book addBook(String author, String title, String callNumber) {
		int nextBookId = getNextBookId();
		book book = new book(author, title, callNumber, nextBookId);
		int bookId = book.ID();
		catalog.put(bookId, book);
		return book;
	}


	public member getMember(int memberId) {
		if (members.containsKey(memberId)) {
			return members.get(memberId);
		}
		return null;
	}


	public book getBook(int bookId) {
		if (catalog.containsKey(bookId)) {
			return catalog.get(bookId);
		}
		return null;
	}


	public int getLoanLimit() {
		return loanLimit;
	}


	public boolean memberCanBorrow(member member) {
		if (member.getNumberOfCurrentLoans() == loanLimit ) {
			return false;
		}

		if (member.getFinesOwed() >= maxFinesOwed) {
			return false;
		}

		for (loan loan : member.getLoans()) {
			if (loan.isOverDue()) {
				return false;
			}
		}

		return true;
	}


	public int getLoansRemainingForMember(member member) {
		int currentLoans = member.getNumberOfCurrentLoans();
		int remainingLoans = loanLimit - currentLoans;
		return remainingLoans;
	}


	public loan issueLoan(book book, member member) {
		Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
		int nextLoanId = getNextLoanId();
		int bookId = book.ID();
		loan loan = new loan(nextLoanId, book, member, dueDate);
		int loanId = loan.getId();
		member.takeOutLoan(loan);
		book.Borrow();
		loans.put(loanId, loan);
		currentLoans.put(bookId, loan);
		return loan;
	}


	public loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}


	public double calculateOverDueFine(loan loan) {
		if (loan.isOverDue()) {
			Date dueDate = loan.getDueDate();
			long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
			double fine = daysOverDue * finePerDay;
			return fine;
		}
		return 0.0;
	}


	public void dischargeLoan(loan currentLoan, boolean isDamaged) {
		member member = currentLoan.Member();
		book book  = currentLoan.Book();

		double overDueFine = calculateOverDueFine(currentLoan);
		member.addFine(overDueFine);

		member.dischargeLoan(currentLoan);
		book.Return(isDamaged);
		if (isDamaged) {
			int bookId = book.ID();
			member.addFine(damageFee);
			damagedBooks.put(bookId, book);
		}
		currentLoan.Loan();
		currentLoans.remove(bookId);
	}


	public void checkCurrentLoans() {
		for (loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}
	}


	public void repairBook(book currentBook) {
		int currentBookId = currentBook.ID();
		if (damagedBooks.containsKey(currentBookId)) {
			currentBook.Repair();
			damagedBooks.remove(currentBookId);
		}
		else {
			throw new RuntimeException("Library: repairBook: book is not damaged");
		}
	}


}
