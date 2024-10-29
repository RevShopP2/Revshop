package hogwartsSchool;

import java.util.ArrayList;
import java.util.List;

public class Library {
	private List<MagicalBook> book;

    public Library() {
        book = new ArrayList<>();
    }

    public void addBook(MagicalBook books) {
    	System.out.println("your book was adding :"+books.getTitle());
        book.add(books);
    }

    public void removeBook(MagicalBook books) {
    	System.out.println("your book was removing :"+books.getTitle());
        book.remove(books);
    }

    public List<MagicalBook> listAllBooks() {
        return book;
    }

    public MagicalBook searchBookByTitle(String title) {
        for (MagicalBook book : book) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        System.out.println("Book Vth title '" + title + "' not found.");
        return null;
    }
}
