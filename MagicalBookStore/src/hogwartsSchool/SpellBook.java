package hogwartsSchool;

public class SpellBook extends MagicalBook implements Readable {
	public void useBook() {
		System.out.println("list of spells from spellBook");
	}
	public SpellBook(String title, String author) {
        super(title, author, "Spells", false);
    }
	public void summarizeContent() {
		System.out.println("spell book has spells and many other info");
	}
	public void bookmarkPage(int pagenumber) {
		System.out.println("page: "+pagenumber+"is bookmarked");
	}
}
