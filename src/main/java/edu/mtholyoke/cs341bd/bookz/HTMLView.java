package edu.mtholyoke.cs341bd.bookz;
//import javax.servlet.http.Cookie. *;
import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HTMLView {

	private String metaURL;

	public HTMLView(String baseURL) {
		this.metaURL = "<base href=\"" + baseURL + "\">";
	}

	/**
	 * HTML top boilerplate; put in a function so that I can use it for all the
	 * pages I come up with.
	 * 
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 * @param title
	 *            the title of the page, since that goes in the header.
	 */
	
	
	void printPageStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a class='none' href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
	}

	public String getStaticURL(String resource) {
		return "static/" + resource;
	}

	/**
	 * HTML bottom boilerplate; close all the tags we open in
	 * printPageStart.
	 *
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 */
	void printPageEnd(PrintWriter html) {
		html.println("  </body>");
		html.println("</html>");
	}

	void printSearchForm(PrintWriter html) {
		html.println("<form method='GET' action='/search'>");
		html.println("<input type='text' placeholder='Search...' name='q' />");
		html.println("<input type='submit' value='Go!' />");
		html.println("</form>");
	}
	
	public void logIn(){
		System.out.println("you're logging in ");
	}
	public void printLogin(PrintWriter html){
		html.println("<form action='/login' method='POST'>");
		html.println("<label>Username: ");
		html.println("<input type=\"text\" name=\"user\"> <br>");
		html.println("<input type='submit' value='Login' />");
		html.println("</label>");
		html.println("</form>");
	}
	
	public void printLogout(PrintWriter html){
		html.println("<form action='/logout' method='POST'>");
		html.println("<label>Username: ");
		html.println("<input type='submit' value='Logout' />");
		html.println("</label>");
		html.println("</form>");
	}
	
	public void displayLikesPage(HttpServletResponse resp, HashSet<GutenbergBook> booksLiked) throws IOException{
		System.out.println("NUM BOOKS LIKED "+booksLiked.size());
		try (PrintWriter html = resp.getWriter()) //put try catch outside of forloop
		{	
			printPageStart(html, "Likes"); 
			
			
			for(GutenbergBook book:booksLiked){
				//GutenbergBook currBook=library.get(book);
					String numLikes=Integer.toString(book.numLikes);
					System.out.println("numLikes "+numLikes);
					String usersLiked=book.usersLiked.toString();
					
					/**
					html.println("<div class='book'>");
					html.println("<a class='none' href='/book/"+book.id+"'>");
					html.println("<div class='title'>"+book.title+"</div>");
					if(book.creator != null) {
						html.println("<div class='creator'>" + book.creator + "</div>");
					}
					html.println("<div>Downloads: "+book.downloads+"</div>");
					html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
					html.println("<h5> "+"Number of likes" +numLikes+"</h5>");
					html.println("<h5>" +"Users who liked this book"+ usersLiked+ "</h5>");
					// TODO, finish up fields.
					html.println("</a>");
					html.println("</div>");
					html.close(); **/
					printBookHTML(html,book);
					html.println("<h5> "+"Number of likes" +numLikes+"</h5>");
					html.println("<h5>" +"Users who liked this book"+ usersLiked+ "</h5>");
				
				
				}
			printPageEnd(html); 
			}
	}

	void showFrontPage(Model model, HttpServletResponse resp,ServerRequest req) throws IOException {
		Cookie userCookie = req.getCookie("user");
		String currentUser = null;
		if(userCookie != null) {
			currentUser = userCookie.getValue();
		}
		
		try (PrintWriter html = resp.getWriter()) {
			
			printPageStart(html,"Settings");
			
			html.println("<div class='cloud'>");
		
			if(currentUser == null) {
				//print login form
				printLogin(html);
			
			} else {
				//print logout form
				//how to destroy a cookie
				printLogout(html);
			}
			html.println("</div>");
			printPageEnd(html);
			printSearchForm(html);
			//added this
			html.println("<h3>Browse books by author</h3>");
			for(char letter = 'A'; letter <= 'Z'; letter++) {
				html.println("<a href='/author/"+letter+"'>"+letter+"</a> ");
			}
			//go to the model and get the authors
			html.println("<h3>Browse books by title</h3>");
			for(char letter = 'A'; letter <= 'Z'; letter++) {
				html.println("<a href='/title/"+letter+"'>"+letter+"</a> ");
			}
			// get 5 random books:
			html.println("<h3>Check out these random books</h3>");
			List<GutenbergBook> randomBooks = model.getRandomBooks(5);
			for (GutenbergBook randomBook : randomBooks) {
				printBookHTML(html, randomBook);
			}
			// if reported?
			if(model.reported.size() > 0) {
				html.println("<div class='cloud'><a href='/reported'>Books that have been reported</a></div>");
			}
			if(model.booksLiked.size()>0){
				html.println("<a class='cloud' href='/like'> See what books have been liked</a></div>");
			}
			printPageEnd(html);
		}
	}

	public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			printBookHTML(html, book);
			printPageEnd(html);
		}
		
		//automatically 
	}
	
	private void printAuthorHTML(PrintWriter html, Author author){
		html.println("<div class='author'>");
		String lastName=author.lastName;
		String firstName=author.firstName;
		
		html.println("<a class='none' href='/author/?last="+lastName +"&first="+firstName+"'>");
		html.println("<div class='fullName'>"+author.fullName+"</div>");
		html.println("<div>Birthdate: "+author.birthDate+"</div>");
		html.println("<div>Deathdate: "+author.deathDate+"</div");
		int booksWritten=author.books.size();
		html.println("<div>Books written by this author: "+booksWritten+"</div>");
		html.println("<div>Popularity: "+author.popularity+"</div");
		
		
		html.println("</div>");
		html.println("</div");
	}
	public void printBooksBySingleAuthor(PrintWriter html, Author author){
		if(author!=null && author.books!=null){
			List<GutenbergBook> books=author.books;
			if(books!=null&& books.size()!=0){
				for(GutenbergBook b:books){
					//this brings to another link, maybe write another one?
					printBookHTML(html,b);
				}
			}
		}
	}
	public void printLogoutConfirmation(PrintWriter html){
		html.println("<h1>you are logged out </h1>");
		html.close();
	}
	
	public void printLoginConfirmation(PrintWriter html){
		html.println("<h1>You are logged in</h1>");
		html.close(); //have to close it
	}
	public void printLikedConfirmation(PrintWriter html, GutenbergBook book){
		System.out.println("in confirmation");
		html.println("<h1>Thank you for liking this book</h1>");
		//liking sth sends us cookies
		//get user from the cookie and
		book.numLikes++; //increment the likes
		html.close();
	}
	private void printBookHTML(PrintWriter html, GutenbergBook book) {
		html.println("<div class='book'>");
		html.println("<a class='none' href='/book/"+book.id+"'>");
		html.println("<div class='title'>"+book.title+"</div>");
		if(book.creator != null) {
			html.println("<div class='creator'>" + book.creator + "</div>");
		}
		html.println("<div>Downloads: "+book.downloads+"</div>");
		html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
		// TODO, finish up fields.
		html.println("</a>");
		//like button
		//inlcude in url book id want to increment
		//parameter map
		html.println("<form action='/like' method='POST'>");
		html.println("<input type='hidden' value='" + book.id + "' name='book' />");
		html.println("<input type='submit' value='Like' />");
		html.println("</form>");
		
		
		if(book.reported) {
			html.println("<div class='error'>This catalog entry has some error.</div>");
		} else {
			// report button!
			html.println("<form action='/report' method='POST'>");
			html.println("<input type='hidden' value='" + book.id + "' name='book' />");
			html.println("<input type='submit' value='Report Error' />");
			html.println("</form>");
		}
		html.println("</div>");
	}
	public static int pageSize = 20;
	//only sort by popularity and sort by author first name
	public void showBookCollectionByAuthor(List<Author> theBooks, ServerRequest req, String pageTitle, Map<String, String> pageArgs) throws IOException {
		// get the sort parameter so we know how to sort.
		String sortHow = req.getParameter("sort", "popular");

		/**if(sortHow.equals("title")) {
			Collections.sort(theBooks, GutenbergBook.sortByTitle);
		}**/
		if(sortHow.equals("popular")){
			Collections.sort(theBooks,Author.sortByPopularity);
		}
		else if(sortHow.equals("author")) {
			Collections.sort(theBooks, Author.sortByAuthor);
		} 
		else if(sortHow.equals("birthdate")){
			Collections.sort(theBooks,Author.sortByBirthDate);
		}
		else if(sortHow.equals("deathdate")){
			Collections.sort(theBooks,Author.sortByDeathDate);
		}
		else {
			req.resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Sort.");
			return;
		}
		String pageStr = req.getParameter("p", "1");
		int whichPage = Integer.parseInt(pageStr);
		int zeroBasedPage = whichPage-1;
		// calculate how many total pages there will be for theBooks with pageSize.
		// Math.ceil means round up.
		int totalPages = (int) Math.ceil(theBooks.size() / (double) pageSize);
		if (zeroBasedPage != 0) { // don't say bad page number for no results!
			if (zeroBasedPage < 0 || zeroBasedPage >= totalPages) {
				req.resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad page number.");
				return;
			}
		}
		try (PrintWriter html = req.resp.getWriter()) {
			printPageStart(html, "Bookz: "+pageTitle);
			//removed title
			for (String field : Arrays.asList("author", "popular","birthdate","deathdate")) {
				boolean shouldLink = !sortHow.equals(field);
				
				html.println("<div class='sortButton'>");
				if(shouldLink) {
					// copy any pageArgs if present
					HashMap<String, String> linkArgs = new HashMap<>(pageArgs);
					linkArgs.put("sort", field);
					
					html.println("<a href='"+Util.encodeParametersInURL(linkArgs, req.getURL())+"'>");
					html.println("Sort by "+Util.capitalize(field));
					html.println("</a>");
				} else {
					html.println("Sorted by "+Util.capitalize(field));
				}
				html.println("</div>");
			}
			// What items are on this page?
			int pageStart = zeroBasedPage * pageSize;
			int pageEnd = Math.min(pageStart+pageSize, theBooks.size());

			// Print out which page we're looking at based on the GET parameter.
			html.println("<div class='cloud'>");
			html.println("<div>Displaying page "+whichPage+" of "+totalPages+"</div>");
			html.println("<div>Displaying items "+(pageStart+1)+" to " + pageEnd +" of "+theBooks.size()+"</div>");
			html.println("</div>");

			html.println("<ol start='"+(pageStart+1)+"'>");
			for (int i = pageStart; i < pageEnd; i++) {
				html.println("<li>");
				printAuthorHTML(html, theBooks.get(i));
				html.println("</li>");
			}
			html.println("</ol>");

			printPageButtons(html, whichPage, totalPages, pageArgs, req.getURL());

			printPageEnd(html);
		}
	}
	
	
	//should I change this
	public void showBookCollection(List<GutenbergBook> theBooks, ServerRequest req, String pageTitle, Map<String, String> pageArgs) throws IOException {
		// get the sort parameter so we know how to sort.
		String sortHow = req.getParameter("sort", "popular");

		if(sortHow.equals("title")) {
			Collections.sort(theBooks, GutenbergBook.sortByTitle);
		} else if(sortHow.equals("author")) {
			Collections.sort(theBooks, GutenbergBook.sortByAuthor);
		} else if(sortHow.equals("popular")) {
			Collections.sort(theBooks, GutenbergBook.sortByPopular);
		} else {
			req.resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Sort.");
			return;
		}
		
		String pageStr = req.getParameter("p", "1");
		int whichPage = Integer.parseInt(pageStr);
		int zeroBasedPage = whichPage-1;

		// calculate how many total pages there will be for theBooks with pageSize.
		// Math.ceil means round up.
		int totalPages = (int) Math.ceil(theBooks.size() / (double) pageSize);

		if (zeroBasedPage != 0) { // don't say bad page number for no results!
			if (zeroBasedPage < 0 || zeroBasedPage >= totalPages) {
				req.resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad page number.");
				return;
			}
		}
		
		try (PrintWriter html = req.resp.getWriter()) {
			printPageStart(html, "Bookz: "+pageTitle);
			
			for (String field : Arrays.asList("title", "author", "popular")) {
				boolean shouldLink = !sortHow.equals(field);
				
				html.println("<div class='sortButton'>");
				if(shouldLink) {
					// copy any pageArgs if present
					HashMap<String, String> linkArgs = new HashMap<>(pageArgs);
					linkArgs.put("sort", field);

					html.println("<a href='"+Util.encodeParametersInURL(linkArgs, req.getURL())+"'>");
					html.println("Sort by "+Util.capitalize(field));
					html.println("</a>");
				} else {
					html.println("Sorted by "+Util.capitalize(field));
				}
				html.println("</div>");
			}

			// What items are on this page?
			int pageStart = zeroBasedPage * pageSize;
			int pageEnd = Math.min(pageStart+pageSize, theBooks.size());

			// Print out which page we're looking at based on the GET parameter.
			html.println("<div class='cloud'>");
			html.println("<div>Displaying page "+whichPage+" of "+totalPages+"</div>");
			html.println("<div>Displaying items "+(pageStart+1)+" to " + pageEnd +" of "+theBooks.size()+"</div>");
			html.println("</div>");

			html.println("<ol start='"+(pageStart+1)+"'>");
			for (int i = pageStart; i < pageEnd; i++) {
				html.println("<li>");
				printBookHTML(html, theBooks.get(i));
				html.println("</li>");
			}
			html.println("</ol>");

			printPageButtons(html, whichPage, totalPages, pageArgs, req.getURL());

			printPageEnd(html);
		}
	}

	/**
	 * In this method, we build up a set of pages to show in the bar at the bottom.
	 * We then sort them, and print "..." if there's a big skip, or the ones between each number if it's small.
	 */
	public void printPageButtons(PrintWriter html, int whichPage, int totalPages, Map<String, String> params, String url) {
		// Print page buttons down here:
		html.println("<div class='cloud pageButtons'>");

		// super-fancy printing of page numbers:
		HashSet<Integer> pagesToPrint = new HashSet<>();
		// always print first 3
		for (int i = 1; i <= Math.min(totalPages, 3); i++) {
			pagesToPrint.add(i);
		}
		// always print last 3
		for (int i = Math.max(1, totalPages-3); i <= totalPages; i++) {
			pagesToPrint.add(i);
		}
		// always print next, prev, current:
		pagesToPrint.add(Math.max(1, whichPage-1));
		pagesToPrint.add(whichPage);
		pagesToPrint.add(Math.min(totalPages, whichPage+1));

		if(totalPages <= 10) { // print everything:
			for (int i = 1; i <= totalPages; i++) {
				pagesToPrint.add(i);
			}
		}

		ArrayList<Integer> printMe = new ArrayList<>(pagesToPrint);
		Collections.sort(printMe);

		int prev = 0;
		for (int nextToPrint : printMe) {
			int difference = nextToPrint - prev;

			// by default, only print 1 number at a time:
			int start = nextToPrint;

			// print the previous ones if close enough:
			if (difference < 3) {
				start = prev + 1;
			} else {
				html.println("..."); // print ellipsis.
			}

			// print all the links we need to print:
			for (int page = start; page <= nextToPrint; page++) {
				if (page == whichPage) {
					html.println("<a class='currentPage'>" + page + "</a>");
				} else {
					HashMap<String, String> paramsForLink = new HashMap<>(params);
					paramsForLink.put("p", Integer.toString(page));
					html.println("<a href='" + Util.encodeParametersInURL(paramsForLink, url) + "'>" + page + "</a>");
				}
			}
			prev = nextToPrint;
		}

		html.println("</div>");
	}
}
