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
	
	public String getAuthorURL(){
		return "";
	}
	
	public static Comparator<Author> sortByPopularity= new Comparator<Author>() {
		@Override
		public int compare(Author lhs, Author rhs) {
			Integer left= new Integer(lhs.popularity);
			Integer right= new Integer(rhs.popularity);
			return -left.compareTo(right);
		}
	};
	
	//sort by last name
	public static Comparator<Author> sortByAuthor= new Comparator<Author>() {
		@Override
		public int compare(Author lhs, Author rhs) {
			String left=lhs.firstName;
			String right=rhs.firstName;
			return left.compareTo(right);
		}
	};
	
	public static Comparator<Author> sortByBirthDate= new Comparator<Author>() {
		@Override
		
		public int compare(Author lhs, Author rhs) {
			Integer left;
			if(lhs.birthDate!=null){
				left=lhs.birthDate;
			}
			else{
				left=9999;
			}
			
			Integer right;
			if(rhs.birthDate!=null){
				right=rhs.birthDate;
			}
			else{
				right=9999;
			}
			return left.compareTo(right);
		}
	};
	
	public static Comparator<Author> sortByDeathDate= new Comparator<Author>() {
		@Override
		public int compare(Author lhs, Author rhs) {
			Integer left;
			if(lhs.deathDate!=null){
				left=lhs.deathDate;
			}
			else{
				left=9999;
			}
			
			Integer right;
			if(rhs.deathDate!=null){
				right=rhs.deathDate;
			}
			else{
				right=9999;
			}
			return left.compareTo(right);
		}
	};
	
	
	
	
	
	
	
	
	
	//sort by
	//popularity
	//
	
	
}
