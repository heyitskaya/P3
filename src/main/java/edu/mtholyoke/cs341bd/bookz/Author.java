package edu.mtholyoke.cs341bd.bookz;
import java.util. *;
public class Author {
	String lastName;
	String firstName;
	ArrayList<GutenbergBook> books;
	ArrayList<String> fullName; //last, first
	Integer birthDate;
	Integer deathDate;
	int popularity; //sum of downloads of all books 
	
	public Author(String lastName, String firstName,ArrayList<String> fullName,Integer birthDate, Integer deathDate){
		this.lastName=lastName;
		this.firstName=firstName;
		this.fullName=fullName;
		this.birthDate=birthDate;
		this.deathDate=deathDate;
		books= new ArrayList<GutenbergBook>();
		
	}
	
	public static Comparator<Author> sortByPopularity= new Comparator<Author>() {
		@Override
		public int compare(Author lhs, Author rhs) {
			Integer left= new Integer(lhs.popularity);
			Integer right= new Integer(rhs.popularity);
			return left.compareTo(right);
		}
	};
	
	//is there a point to this?
	public static Comparator<Author> sortByAuthor= new Comparator<Author>() {
		@Override
		public int compare(Author lhs, Author rhs) {
			Integer left= new Integer(lhs.popularity);
			Integer right= new Integer(rhs.popularity);
			return left.compareTo(right);
		}
	};
	
	
	
	//sort by
	//popularity
	//
	
	
}
