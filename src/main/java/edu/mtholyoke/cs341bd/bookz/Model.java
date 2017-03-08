package edu.mtholyoke.cs341bd.bookz;

import java.io.*;
import java.util.*;

//import src.main.java.edu.mtholyoke.cs341bd.bookz.Author;
//import src.main.java.edu.mtholyoke.cs341bd.bookz.GutenbergBook;

public class Model {
	public static File reportedFile = new File("reported");
	Map<String, GutenbergBook> library;
	public Set<String> reported;
	HashMap<ArrayList<String>,Author> authorLibrary= new HashMap<ArrayList<String>,Author>();

	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);
		// do the hard work:
		DataImport.loadJSONBooks(library);
		authorLibrary=createAuthorLibrary(library);
		//after creating author library we want to go in and set the popularity
		initPopularityInAuthorLibrary();
		loadReportedBooks();
	}
	
	public void initPopularityInAuthorLibrary(){
		for(ArrayList<String> name:authorLibrary.keySet()){
			Author currAuthor=authorLibrary.get(name);
			List<GutenbergBook> booksByAuthor=currAuthor.books;
			
			//given a list of books find the popularity
			int pop=findPopularity(booksByAuthor);
			
			currAuthor.popularity=pop;
			System.out.println("currAuthor.popularity "+currAuthor.popularity);
			
		}
	}
	
	//given a list of books find the popularity
	public int findPopularity(List<GutenbergBook> listOfBooks){
		if(!listOfBooks.isEmpty())
		{
			int pop=0;
			for(GutenbergBook book:listOfBooks){
				pop+=book.downloads;
			}
			return pop/listOfBooks.size(); //find the mean downloads
		}
		else{
			return 0;
		}
	}
	
	//please let this not be wrong
		public HashMap<ArrayList<String>, Author> createAuthorLibrary(Map<String,GutenbergBook> library){
			for(String id:library.keySet()){
				GutenbergBook book=library.get(id); //get the Gutenberg book
				//extract first and last name
				String creator=book.creator;
				ArrayList<String> fullName= new ArrayList<String>();
				String firstName;
				String lastName;
				Integer birthDate=0;
				Integer deathDate=0;
				if(creator==null){
					//we extract whatever is after by
				}
				else{
					
					firstName=getFirstName(creator).trim();
					lastName=getLastName(creator).trim();
					Integer[] dates=getBirthAndDeathDate(creator);
					Integer[] birthAndDeathDate=getBirthAndDeathDate(creator);
					birthDate=birthAndDeathDate[0];
					deathDate=birthAndDeathDate[1];
					System.out.println("creator "+creator);
					System.out.println("birth and death are "+Arrays.toString(birthAndDeathDate));
					fullName.add(lastName);
					fullName.add(firstName); //lastName, firstName
				}
				
				if(authorLibrary.keySet()!=null && authorLibrary.keySet().contains(fullName)){ //if the fullName is already a key
					authorLibrary.get(fullName).books.add(book); //add this book as a book the author has written
				}
				else{ //when it's not in the map
					authorLibrary.put(fullName, new Author(fullName.get(0),fullName.get(1),fullName,birthDate,deathDate)); //remember to initialize some fields in the author class
				}
			}
			return authorLibrary;
		}
		
		//look at this again
		public String getFirstName(String creator){
			String[] array=creator.split(",");
			if(array.length>=1){
				return array[0];
			}
			return "";
		}
		public String getLastName(String creator){
			String[] array=creator.split(",");
			if(array.length>=2){ //we can only access this index if the array is at least length two
				return array[1];
			}
			return ""; //if there is none we just return empty string
		}
	
		public boolean isInteger(String s){
			try{
				Integer.parseInt(s);
				return true;
			}
			catch(NumberFormatException e){
				return false;
			}
		}
	
		public Integer[] getBirthAndDeathDate(String creator){
			
			Integer[] answer= new Integer[2];
			String[] array=creator.split(",");
			
			
			String dates=array[array.length-1];
			dates.replace("?", "");
			System.out.println("dates "+dates);
			
			if(dates.contains("-")){
				String[] birthAndDeath=dates.split("-");
				
			
				birthAndDeath=trimWhiteSpaceInArray(birthAndDeath);
			
				if(birthAndDeath.length>=2){
					
						String birth=birthAndDeath[0];
						String death=birthAndDeath[1];
						
						String trimBirth=birth.trim();
						String trimDeath=death.trim();
						
					
						if(isInteger(trimBirth)){
							answer[0]=Integer.parseInt(trimBirth);
						}
						else{
							answer[0]=null;
						}
						if(isInteger(trimDeath)){
							answer[1]=Integer.parseInt(trimDeath);
						}
						else{
							answer[1]=null;
						}
					
				}
				/**else if(birthAndDeath.length==1){ //when its less than 2
					String birth=birthAndDeath[0];
					String trimBirth=birth.trim();
					answer[0]=Integer.parseInt(trimBirth);
					
				} **/
				
				
			}
			else{ //do sth
				
				return answer;
			}
			
			return answer;
		}
		
		public String[] trimWhiteSpaceInArray(String[] array){
			if(array!=null){
				for(String s:array){
					s=s.trim();
				}
				return array;
			}
			return array;
		}

	private void loadReportedBooks() {
		this.reported = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(reportedFile))) {
			while(true) {
				String line = reader.readLine();
				if(line == null) break;
				String id = line.trim();
				// only load ids if they're really books:
				GutenbergBook book = library.get(id);
				if(book != null) {
					book.reported = true;
					reported.add(id);
				}
			}
		} catch (FileNotFoundException e) {
			// not an error, nothing yet.
		} catch (IOException e) {
			// Crash!
			throw new RuntimeException(e);
		} ;
	}

	private void saveReportedBooks() {
		try (PrintWriter writer = new PrintWriter(reportedFile)) {
			for (String bookId : reported) {
				writer.println(bookId);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void reportBook(String id) {
		GutenbergBook book = library.get(id);
		if(book != null) {
			book.reported = true;
			reported.add(id);
			saveReportedBooks();
		}
	}

	public GutenbergBook getBook(String id) {
		return library.get(id);
	}

	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		return matches;
	}
	
	//kaya
	public List<Author> getAuthorStartingWith(char firstLetter){
		
		System.out.println("authorLibrary size"+authorLibrary.keySet().size());
		System.out.println("firstLetter "+firstLetter);
		char query=Character.toLowerCase(firstLetter);
		List<Author> matches= new ArrayList<Author>();
		for(ArrayList<String> nameList:authorLibrary.keySet()){
			//System.out.println(nameList);
			String lastName = nameList.get(1);
			if(lastName!=null){
				if(lastName.length()!=0){
					System.out.println("query "+query);
					System.out.println("lastNameFirstLetter "+Character.toLowerCase(lastName.charAt(0)));
				if(Character.toLowerCase(lastName.charAt(0))==query){
					matches.add(authorLibrary.get(nameList)); //add the author
				}
				}
			}
		}
		System.out.println("length of authors starting with char "+matches.size());
		return matches;
		
		
		
	}

	public List<GutenbergBook> getRandomBooks(int count) {
		return ReservoirSampler.take(count, library.values());
	}

	public List<GutenbergBook> searchBooks(String query) {
		List<String> terms = Arrays.asList(query.toLowerCase().split("\\s+"));

		List<GutenbergBook> matches = new ArrayList<>();
		for (GutenbergBook book : library.values()) {
			for (String term : terms) {
				if(book.longTitle.toLowerCase().contains(term)) {
					matches.add(book);
					break; // don't look at more words
				}
				if(book.creator.toLowerCase().contains(term)) {
					matches.add(book);
					break; // don't look at more words
				}
			}
		}

		return matches;
	}

	public List<GutenbergBook> getReportedBooks() {
		ArrayList<GutenbergBook> books = new ArrayList<>(reported.size());
		for (String id : reported) {
			books.add(library.get(id));
		}
		return books;
	}
	
	
}
