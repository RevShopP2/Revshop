package hogwartsSchool;

public class DarkArtsBook extends MagicalBook implements Readable {
	public void useBook() {
		System.out.println("DarkArts Book with restricted access");
	}
	public DarkArtsBook(String title, String author) {
        super(title, author, "DarkArts", true);
    }
	public void summarizeContent() {
		System.out.println("This has info on darkarts and is highly accesible to only particular people");
	}
	public void bookmarkPage(int pagenumber) {
		System.out.println("The page: "+pagenumber+"is bookmarked");
	}
}
