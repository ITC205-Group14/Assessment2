import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class Main {

	private static Scanner input;
	private static Library library;
	private static String menu;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;


	private static String getMenu() {
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
			input = new Scanner(System.in);
			library = Library.getInstance();
			calendar = Calendar.getInstance();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (Member m : library.getMembers()) {
				output(m);
			}
			output(" ");
			for (book book : library.getBooks()) {
				output(book);
			}

			menu = getMenu();

			boolean running = false;

			while (!running) {

				output("\n" + simpleDateFormat.format(calendar.Date()));
				String c = input(menu);

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
					running = true;
					break;

				default:
					output("\nInvalid option\n");
					break;
				}

				Library.save();
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
		for (loan loan : library.getCurrentLoans()) {
			output(loan + "\n");
		}
	}



	private static void listBooks() {
		output("");
		for (book book : library.getBooks()) {
			output(book + "\n");
		}
	}



	private static void listMembers() {
		output("");
		for (Member member : library.getMembers()) {
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
			library.checkCurrentLoans();
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
		String callNo = input("Enter call number: ");
		book book = library.addBook(author, title, callNo);
		output("\n" + book + "\n");

	}


	private static void addMember() {
		try {
			String lastName = input("Enter last name: ");
			String firstName  = input("Enter first name: ");
			String email = input("Enter email: ");
			int phoneNumber = Integer.valueOf(input("Enter phone number: ")).intValue();
			Member member = library.addMember(lastName, firstName, email, phoneNumber);
			output("\n" + member + "\n");

		} catch (NumberFormatException e) {
			output("\nInvalid phone number\n");
		}

	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return input.nextLine();
	}



	private static void output(Object object) {
		System.out.println(object);
	}


}
