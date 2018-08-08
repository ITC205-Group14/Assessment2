import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Main {

	private static Scanner in;
	private static library LIB;
	private static String menuText;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;


	private static String getMenuText() {
		StringBuilder sb = new StringBuilder();

		sb.append("\nLibrary Main Menu\n\n")
		.append("  M  : add member\n")
		.append("  LM : list members\n")
		.append("\n")
		.append("  B  : add book\n")
		.append("  LB : list books\n")
		.append("  FB : fix books\n")
		.append("\n")
		.append("  L  : take out a loan\n")
		.append("  R  : return a loan\n")
		.append("  LL : list loans\n")
		.append("\n")
		.append("  P  : pay fine\n")
		.append("\n")
		.append("  T  : increment date\n")
		.append("  Q  : quit\n")
		.append("\n")
		.append("Choice : ");

		return sb.toString();
	}


	public static void main(String[] args) {
		try {
			in = new Scanner(System.in);
			LIB = library.INSTANCE();
			calendar = Calendar.getInstance();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (member member : LIB.Members()) {
				output(member);
			}
			output(" ");
			for (book book : LIB.Books()) {
				output(book);
			}

			menuText = getMenuText();

			boolean quit = false;

			while (!quit) {

				output("\n" + simpleDateFormat.format(calendar.Date()));
				String c = input(menuText);

				switch (c.toUpperCase()) {

				case "M":
					addMember();
					break;

				case "LM":
					listMembers();
					break;

				case "B":
					addBook();
					break;

				case "LB":
					listBooks();
					break;

				case "FB":
					fixBooks();
					break;

				case "L":
					borrowBook();
					break;

				case "R":
					returnBook();
					break;

				case "LL":
					listCurrentLoans();
					break;

				case "P":
					payFine();
					break;

				case "T":
					incrementDate();
					break;

				case "Q":
					quit = true;
					break;

				default:
					output("\nInvalid option\n");
					break;
				}

				library.SAVE();
			}
		} catch (RuntimeException e) {
			output(e);
		}
		output("\nEnded\n");
	}

	private static void payFine() {
		PayFineControl payFineControl = new PayFineControl();
		new PayFineUI(payFineControl).run();
	}


	private static void listCurrentLoans() {
		output("");
		for (loan loan : LIB.CurrentLoans()) {
			output(loan + "\n");
		}
	}


	private static void listBooks() {
		output("");
		for (book book : LIB.Books()) {
			output(book + "\n");
		}
	}


	private static void listMembers() {
		output("");
		for (member member : LIB.Members()) {
			output(member + "\n");
		}
	}


	private static void borrowBook() {
		BorrowBookControl borrowBookControl = new BorrowBookControl();
		new BorrowBookUI(borrowBookControl).run();
	}


	private static void returnBook() {
		ReturnBookControl returnBookControl = new ReturnBookControl();
		new ReturnBookUI(returnBookControl).run();
	}


	private static void fixBooks() {
		FixBookControl fixBookControl = new FixBookControl();
		new FixBookUI(fixBookControl).run();
	}


	private static void incrementDate() {
		try {
			int days = Integer.valueOf(input("Enter number of days: ")).intValue();
			calendar.incrementDate(days);
			LIB.checkCurrentLoans();

			Date date = calendar.Date();
			String formattedDate = simpleDateFormat.format(date);
			output(formattedDate);

		} catch (NumberFormatException e) {
			output("\nInvalid number of days\n");
		}
	}


	private static void addBook() {
		String author = input("Enter author: ");
		String title  = input("Enter title: ");
		String callNumber = input("Enter call number: ");
		book book = LIB.Add_book(author, title, callNumber);
		output("\n" + book + "\n");

	}


	private static void addMember() {
		try {
			String lastName = input("Enter last name: ");
			String firstName  = input("Enter first name: ");
			String email = input("Enter email: ");
			String phoneNumberInput = input("Enter phone number: ");
			int phoneNumber = Integer.valueOf(phoneNumberInput).intValue();
			member member = LIB.Add_mem(lastName, firstName, email, phoneNumber);
			output("\n" + member + "\n");

		} catch (NumberFormatException e) {
			output("\nInvalid phone number\n");
		}

	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return in.nextLine();
	}


	private static void output(Object object) {
		System.out.println(object);
	}


}
