package hogwartsSchool;

public class LibraryUser {
	private String name;
    private boolean isProfessor;

    public LibraryUser(String name, boolean isProfessor) {
        this.name = name;
        this.isProfessor = isProfessor;
    }
    public void borrowBook(MagicalBook book) {
        if (book.isRestricted() && !isProfessor) {
            System.out.println("Access denied '"+book.getTitle()+"'can only be accessed and borrowed  by professors.");
        } else {
            System.out.println(name+"borrowed book "+book.getTitle());
        }
    }
}
