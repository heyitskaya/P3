package edu.mtholyoke.cs341bd.bookz;

import java.io.*;
import java.util.*;

import javax.servlet.http.Cookie;

//import src.main.java.edu.mtholyoke.cs341bd.bookz.Author;
//import src.main.java.edu.mtholyoke.cs341bd.bookz.GutenbergBook;

public class Model {
	public static File reportedFile = new File("reported");
	public static File booksLikedFile= new File("booksLiked2");
	public static File usersLikedFile= new File("usersLiked");

	
	Map<String, GutenbergBook> library;
	public Set<String> reported;
	HashMap<ArrayList<String>,Author> authorLibrary= new HashMap<ArrayList<String>,Author>();
	HashSet<GutenbergBook> booksLiked;
	HashSet<String> booksLikedID;
	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);
		// do the hard work:
		DataImport.loadJSONBooks(library);
		authorLibrary=createAuthorLibrary(library);
		//after creating author library we want to go in and set the popularity
		//booksLikedFile.delete();
		//usersLikedFile.delete();
		initPopularityInAuthorLibrary();
		loadLikedBooks();
		System.out.println("bookLikedID size "+booksLikedID.size());
		System.out.println("booksLiked size "+booksLiked.size());
		loadReportedBooks();
	}
	public void initPopularityInAuthorLibrary(){
		for(ArrayList<String> name:authorLibrary.keySet()){
			Author currAuthor=authorLibrary.get(name);
			List<GutenbergBook> booksByAuthor=currAuthor.books;
			int pop=findPopularity(booksByAuthor);
			currAuthor.popularity=pop;
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
			//	System.out.println(creator);
				ArrayList<String> fullName= new ArrayList<String>();
				String firstName;
				String lastName;
				Integer birthDate=0;
				Integer deathDate=0;
				if(book.longTitle.contains("by") && creator.length()==0){
					//extract what is after by
					String[] extractedArray=book.longTitle.split("by");
					String stringWeCareAbout=extractedArray[1].trim();
					String[] arrayOfNames=stringWeCareAbout.split(" ");
					//System.out.println("arrayOfNames "+Arrays.toString(arrayOfNames));
					if(arrayOfNames.length==2){ //its actually a name and not sth else
						fullName.add(arrayOfNames[0]);
						fullName.add(arrayOfNames[1]);
					}
				}
				else if(numOccurences('-',creator.trim())>=2){ //we know that it has multiple creators
					String[] array=creator.trim().split(",");
					int size=array.length;
					for(int i=0;i<size;i++){ 
						//we need to see if a string is a date, if it is a date we get the two entries 
						//before the date
						if(isDate(array[i]) && i>=2){ //if s is a date
							String first=array[i-1]; //first name
							String last=array[i-2]; //second name	
							
							ArrayList<String> name= new ArrayList<String>();
							name.add(first);
							name.add(getRidOfDateInString(last));
							if(authorLibrary.keySet()!=null && authorLibrary.keySet().contains(name)){
								authorLibrary.get(name).books.add(book);
							}
							else if(name!=null&& name.size()!=0/** && fullName.get(0)!=null && fullName.get(1)!=null**/&& authorLibrary.keySet()!=null && !authorLibrary.keySet().contains(name)){ //when it's not in the map
								authorLibrary.put(name, new Author(name.get(0),name.get(1),name,birthDate,deathDate)); //remember to initialize some fields in the author class
							}
						}
					}
				}
				else{
					firstName=getFirstName(creator).trim();
					lastName=getLastName(creator).trim();
					Integer[] dates=getBirthAndDeathDate(creator);
					Integer[] birthAndDeathDate=getBirthAndDeathDate(creator);
					birthDate=birthAndDeathDate[0];
					deathDate=birthAndDeathDate[1];
					fullName.add(lastName);
					fullName.add(firstName); //lastName, firstName
				/**	if(book.id.equals("etext7010")){
						System.out.println("etext7010 "+fullName.toString());
					} **/
					//System.out.println("fullName "+fullName);
				}
				if( fullName!=null && authorLibrary.keySet()!=null && authorLibrary.keySet().contains(fullName)){ //if the fullName is already a key
					authorLibrary.get(fullName).books.add(book); //add this book as a book the author has written
				}
				else if(fullName!=null&& fullName.size()!=0/** && fullName.get(0)!=null && fullName.get(1)!=null**/&& authorLibrary.keySet()!=null && !authorLibrary.keySet().contains(fullName)){ //when it's not in the map
					authorLibrary.put(fullName, new Author(fullName.get(0),fullName.get(1),fullName,birthDate,deathDate)); //remember to initialize some fields in the author class
				}
			}
			return authorLibrary;
		}
		public boolean isDate(String s){
			if(s!=null && s.contains("-")){
				return true;
			}
			return false;
		}
		
		public String getRidOfDateInString(String s){
			StringBuilder sb= new StringBuilder("");
			if(s.trim()!=null){
				char[] array=s.toCharArray(); //split by spaces
				//System.out.println("help me "+Arrays.toString(array));
				
				int size=array.length;
				for(int i=0;i<size;i++){
					int ascii=(int)array[i]; //get the ascii of the char
					if((ascii<48 && ascii != 45) || ascii> 57){ //if its not a number or a dash
						sb.append(array[i]);
						
					}
					
				}
				return sb.toString().trim();
			}
			return null;
		} 
		public int numOccurences(char c,String s){
			int count=0;
			if(s!=null){
				char[] array=s.toCharArray();
				int size=array.length;
			
				for(int i=0;i<size;i++){
					if(array[i]==c && isInteger(String.valueOf(array[i-1]))){
						count++;
					}
				}
			}
			return count;
			
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
	private void loadLikedBooks(){
		this.booksLiked= new HashSet<>();
		this.booksLikedID= new HashSet<>();
		try(BufferedReader reader1 = new BufferedReader(new FileReader(booksLikedFile))){
			try(BufferedReader reader2= new BufferedReader(new FileReader(usersLikedFile))){
				while(true){
					String line1= reader1.readLine();
					//String line2=reader2.readLine();
					System.out.println("line1 "+line1);
					//System.out.println("line2 "+line2);
					
					if(line1==null) break;
					//if(line2==null) break;
					String id=line1.trim();
					
					//String stringOfUsers=line2.trim();
					
				//	ArrayList<String> listOfUsers=turnStringToArrayList(stringOfUsers);
					
					GutenbergBook book=library.get(id);
					if(book!=null){
						book.liked=true;
						booksLiked.add(library.get(id));
						booksLikedID.add(id);
					//	book.usersLiked=listOfUsers;
					}
				}
			}
		}
		catch(FileNotFoundException e){
			
		}catch(IOException e){
			throw new RuntimeException(e);
		};
	}
	
/**	private void saveLikedBooks(){
		try (PrintWriter writer = new PrintWriter(booksLikedFile)) {
			for (GutenbergBook bookId : booksLiked) {
				writer.println(bookId);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	} 
	**/
/**	private void loadSavedBooks() {
		try (BufferedReader reader1 = new BufferedReader(new FileReader(booksLikedFile))) 
		{
			try(BufferedReader reader2= new BufferedReader(new FileReader(usersLikedFile)))
			{
				while(true) 
				{
					String line1 = reader1.readLine(); //book id
					System.out.println("line1 "+line1);
					String line2 = reader2.readLine(); // users who have liked this book
					System.out.println("line2 "+line2);
					if(line1 == null) break;
					if(line2==null) break;
					String id = line1.trim();
					String stringOfUsers=line2.trim();
					//if(stringOfUsers.length()!=0){
						ArrayList<String> listOfUsers=turnStringToArrayList(stringOfUsers);
					//}
					// only load ids if they're really books:
					GutenbergBook book = library.get(id);
					if(book != null) 
					{
						book.liked = true;
						booksLikedID.add(id);
						booksLiked.add(library.get(id));
						book.usersLiked=listOfUsers;
						book.numLikes=listOfUsers.size();
					}
				}
			}
	
		}
		
		catch (FileNotFoundException e) {
			// not an error, nothing yet.
		} 
		catch (IOException e) {
			// Crash!
			throw new RuntimeException(e);
		} ;
	} 
	**/
	public ArrayList<String > turnStringToArrayList(String s){
		ArrayList<String> myList = new ArrayList<String>(Arrays.asList(s.split(" ")));
		return myList;
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
	
	private void saveLikedBooks(){
		try(PrintWriter writer1= new PrintWriter(booksLikedFile)){
			
			for(String bookId:booksLikedID){
				writer1.println(bookId);
				
				ArrayList<String> usersLiked=library.get(bookId).usersLiked;
				
				
				try(PrintWriter writer2= new PrintWriter(usersLikedFile)){
					System.out.println("saveLikedBooks usrsLiked tostring "+usersLiked.toString());
					
					writer2.println(usersLiked.toString());
				} catch(FileNotFoundException e){
					throw new RuntimeException(e);
				}
			}
		} catch(FileNotFoundException e){
			throw new RuntimeException(e);
		}
	}
	
	//kaya
	public void likeBook(String id){
		
		//GutenbergBook book = library.get(id);
		if(library.get(id) != null) {
			//library.get(id).numLikes++;
			library.get(id).liked = true;
			booksLikedID.add(id);
			booksLiked.add(library.get(id)); //add the book to our HashSet
			
			saveLikedBooks();
		}
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
	
		char query=Character.toLowerCase(firstLetter);
		List<Author> matches= new ArrayList<Author>();
		for(ArrayList<String> nameList:authorLibrary.keySet()){
			String lastName = nameList.get(1);
			if(lastName!=null){
				if(lastName.length()!=0){
				if(Character.toLowerCase(lastName.charAt(0))==query){
					matches.add(authorLibrary.get(nameList)); //add the author
				}
				}
			}
		}
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
	
	/**public static void main(String args[]){
		try{
			Model m= new Model();
			String s="kaya ni ranjini john";
			ArrayList<String> al=m.turnStringToArrayList(s);
			System.out.println("arrayList" +al.toString());
			int ascii=(int)'-';
			System.out.println("ASCII "+ascii);
		}
		catch(IOException e){
		
		}
		
	}**/
	
	
}
