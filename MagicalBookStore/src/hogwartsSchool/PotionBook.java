package hogwartsSchool;

public class PotionBook extends MagicalBook implements Readable {
	public void useBook() {
		System.out.println("list of recipes from PotionBook");
	}
	public PotionBook(String title, String author) {
        super(title, author, "potions", false);
    }
	public void summarizeContent() {
		System.out.println("spell book has spells and many other info");
	}
	public void bookmarkPage(int pagenumber) {
		System.out.println("The page: "+pagenumber+"is bookmarked");
	}
}
