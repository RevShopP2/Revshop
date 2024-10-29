package hogwartsSchool;

public abstract class MagicalBook {
	private String title;
	private String author;
	private String subject;
	private boolean isRestricted;
	public MagicalBook(String title, String author, String subject, boolean isRestricted) {
		this.title = title;
		this.author = author;
		this.subject = subject;
		this.isRestricted = isRestricted;
	}
	public void openBook() {
		System.out.println("---opening book---");
	}
	public void closeBook() {
		System.out.println("---closing book---");
	}
	public abstract void useBook();
	public boolean isRestricted() {
        return isRestricted;
    }

    protected void setRestricted(boolean isRestricted) {
        this.isRestricted = isRestricted;
    }
    public String getTitle() {
        return title;
    }
}
