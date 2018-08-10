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
	private static final double finePerDay = 1.0;
	private static final double maxFinesOwed = 5.0;
	private static final double damageFee = 2.0;

	private static Library self;
	private int bookId;
	private int memberId;
	private int loanId;
	private Date loanDate;

	private Map<Integer, Book> catalog;
	private Map<Integer, Member> members;
	private Map<Integer, Loan> loans;
	private Map<Integer, Loan> currentLoans;
	private Map<Integer, Book> damagedBooks;


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
					Calendar.getInstance().setTime(self.loanDate);
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


	public List<Member> getMembers() {
		Collection<Member> memberCollection = members.values();
		ArrayList<Member> memberList = new ArrayList<Member>(memberCollection);
		return memberList;
	}

	
	public List<Book> getBooks() {
		Collection<Book> booksCollection = catalog.values();
		ArrayList<Book> booksList = new ArrayList<Book>(booksCollection);
		return booksList;
	}


	public List<Loan> getCurrentLoans() {
		Collection<Loan> loansCollection = currentLoans.values();
		ArrayList<Loan> loansList = new ArrayList<Loan>(loansCollection);
		return loansList;
	}


	public Member addMember(String lastName, String firstName, String email, int phoneNumber) {
		int nextMemberId = getNextMemberId();
		Member member = new Member(lastName, firstName, email, phoneNumber, nextMemberId);
		int memberid = member.getId();
		members.put(memberid, member);
		return member;
	}


	public Book addBook(String author, String title, String callNumber) {
		int nextBookId = getNextBookId();
		Book book = new Book(author, title, callNumber, nextBookId);
		int bookId = book.ID();
		catalog.put(bookId, book);
		return book;
	}


	public Member getMember(int memberId) {
		if (members.containsKey(memberId)) {
			return members.get(memberId);
		}
		return null;
	}


	public Book getBook(int bookId) {
		if (catalog.containsKey(bookId)) {
			return catalog.get(bookId);
		}
		return null;
	}


	public int getLoanLimit() {
		return loanLimit;
	}


	public boolean memberCanBorrow(Member member) {
		if (member.getNumberOfCurrentLoans() == loanLimit ) {
			return false;
		}

		if (member.getFinesOwed() >= maxFinesOwed) {
			return false;
		}

		for (Loan loan : member.getLoans()) {
			if (loan.isOverDue()) {
				return false;
			}
		}

		return true;
	}


	public int getLoansRemainingForMember(Member member) {
		int currentLoans = member.getNumberOfCurrentLoans();
		int remainingLoans = loanLimit - currentLoans;
		return remainingLoans;
	}


	public Loan issueLoan(Book book, Member member, Date loanPeriod) {
		Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
		int nextLoanId = getNextLoanId();
		int bookId = book.ID();
		Loan loan = new Loan(nextLoanId, book, member, dueDate);
		int loanId = loan.getId();
		member.takeOutLoan(loan);
		book.Borrow();
		loans.put(loanId, loan);
		currentLoans.put(bookId, loan);
		return loan;
	}


	public Loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}


	public double calculateOverDueFine(Loan loan) {
		if (loan.isOverDue()) {
			Date dueDate = loan.getDueDate();
			long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
			double fine = daysOverDue * finePerDay;
			return fine;
		}
		return 0.0;
	}


	public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
		Member member = currentLoan.member();
		Book book  = currentLoan.Book();

		double overDueFine = calculateOverDueFine(currentLoan);
		member.addFine(overDueFine);

		member.dischargeLoan(currentLoan);
		book.Return(isDamaged);
		if (isDamaged) {
			int bookId = book.ID();
			member.addFine(damageFee);
			damagedBooks.put(bookId, book);
		}
		currentLoan.Book();
		currentLoans.remove(bookId);
	}


	public void checkCurrentLoans() {
		for (Loan loan : currentLoans.values()) {
			loan.checkOverDue();
		}
	}


	public void repairBook(Book currentBook) {
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