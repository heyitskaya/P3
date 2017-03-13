package edu.mtholyoke.cs341bd.bookz;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author jfoley.
 */
public class GutenbergBook {
	public String id;
	public String title;
	public String longTitle;
	public String creator;
	public String uploaded;
	public List<String> maybeWikipedias = new ArrayList<>();
	public List<String> libraryOfCongressSubjectHeading = new ArrayList<>();
	public List<String> libraryOfCongressSubjectCode = new ArrayList<>();
	public int downloads;
	public boolean reported = false;
	public int numLikes;
	public ArrayList<String> usersLiked= new ArrayList<String>();

	public int getBookNumber() {
		return Integer.parseInt(Objects.requireNonNull(Util.getAfterIfStartsWith("etext", id)));
	}

	public String getGutenbergURL() {
		return "http://www.gutenberg.org/ebooks/" + getBookNumber();
	}

	public static Comparator<GutenbergBook> sortByTitle = new Comparator<GutenbergBook>() {
		@Override
		public int compare(GutenbergBook lhs, GutenbergBook rhs) {
			return lhs.title.compareTo(rhs.title);
		}
	};
	public static Comparator<GutenbergBook> sortByAuthor = new Comparator<GutenbergBook>() {
		@Override
		public int compare(GutenbergBook lhs, GutenbergBook rhs) {
			return lhs.creator.compareTo(rhs.creator);
		}
	};
	public static Comparator<GutenbergBook> sortByPopular = new Comparator<GutenbergBook>() {
		@Override
		public int compare(GutenbergBook lhs, GutenbergBook rhs) {
			return -Integer.compare(lhs.downloads, rhs.downloads);
		}
	};
}
