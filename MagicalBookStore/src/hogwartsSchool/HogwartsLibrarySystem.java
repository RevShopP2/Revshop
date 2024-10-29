package hogwartsSchool;

import java.util.Scanner;

public class HogwartsLibrarySystem {

    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        initializeLibrary(library);
        LibraryUser student = new LibraryUser("Sen ", false);
        LibraryUser professor = new LibraryUser("Bhairav", true);
        System.out.println("******Welcome to the Hogwarts Library System******");
        boolean exit = false;
        while (!exit) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addNewBook(library, scanner);
                case 2 -> searchAndUseBook(library, scanner);
                case 3 -> borrowBook(student, professor, library, scanner);
                case 4 -> {
                    System.out.println("Hangon---Exiting the Hogwarts Library System.--\n exited");
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void displayMenu() {
        System.out.println("\n--- Hogwarts Library Menu ---");
        System.out.println("1. Add a New Book");
        System.out.println("2. Search for and Use a Book");
        System.out.println("3. Borrow a Book");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
    private static void initializeLibrary(Library library) {
        library.addBook(new SpellBook("Book of Spells", "John"));
        library.addBook(new PotionBook(" Potion-Making", "peter"));
        library.addBook(new DarkArtsBook("Gaint of Darkest Art", "Magnun"));
        System.out.println("Library initialized with magical books.");
        System.out.println("---------------------------------------------------");
    }
    private static void addNewBook(Library library, Scanner scanner) {
        System.out.print("Enter book type 1  SpellBook, 2  PotionBook, 3  DarkArtsBook): ");
        int type = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
        switch (type) {
            case 1 -> library.addBook(new SpellBook(title, author));
            case 2 -> library.addBook(new PotionBook(title, author));
            case 3 -> library.addBook(new DarkArtsBook(title, author));
            default -> System.out.println("Invalid book type.");
        }
    }
    private static void searchAndUseBook(Library library, Scanner scanner) {
        System.out.print("** could you please Enter the title of the book to search ");
        String title = scanner.nextLine();
        MagicalBook book = library.searchBookByTitle(title);
        if (book != null) {
            book.openBook();
            book.useBook();
            book.closeBook();
        } else {
            System.out.println("Book titled "+title +"not found in the library");
        }
    }
    private static void borrowBook(LibraryUser student, LibraryUser professor, Library library, Scanner scanner) {
        System.out.print("Enter the title of the book to borrow: ");
        String title = scanner.nextLine();
        MagicalBook book = library.searchBookByTitle(title);
        if (book == null) {
            System.out.println("Book titled '" + title + "' not found in the library.");
            return;
        }
        System.out.print("Borrower (1: Student, 2: Professor): ");
        int userType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (userType == 1) {
            student.borrowBook(book);
        } else if (userType == 2) {
            professor.borrowBook(book);
        } else {
            System.out.println("Invalid borrower type.");
        }
    }
}

