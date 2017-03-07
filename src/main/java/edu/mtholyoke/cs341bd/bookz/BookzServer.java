package edu.mtholyoke.cs341bd.bookz;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
	Server jettyServer;
	HTMLView view;
	Model model;

	public BookzServer(String baseURL, int port) throws IOException {
		view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		Map<String,String[]> map= req.getParameterMap();
		
		
		
		
		ServerRequest request = new ServerRequest(req, resp);
		System.out.println(request);

		if("POST".equals(request.method) && "/report".equals(request.path)) {
			model.reportBook(request.getParameter("book", "ERROR"));

			// tell the browser to redirect the user to list of reported books:
			request.resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
			request.resp.setHeader("Location", "/reported");
			try (PrintWriter out = request.resp.getWriter()) {
				out.println("/reported");
			}

			// done.
			return;
		}

		// Our server only supports GET methods.
		if (!"GET".equals(request.method)) {
			return;
		}

		String path = request.path;

		if("/robots.txt".equals(path)) {
			// We're returning a fake file? Here's why: http://www.robotstxt.org/
			resp.setContentType("text/plain");
			try (PrintWriter txt = resp.getWriter()) {
				txt.println("User-Agent: *");
				txt.println("Disallow: /");
			}
			return;
		}

		if("/reported".equals(path)) {
			view.showBookCollection(model.getReportedBooks(), request, "Books that have been reported", Collections.emptyMap());
			return;
		}

		if("/search".equals(path)) {
			String query = request.getParameter("q", "");
			Map<String, String> params = Collections.singletonMap("q", query);
			view.showBookCollection(model.searchBooks(query), request, "Books matching '"+query+"'", params);
			return;
		}

		// Title pages
		String titleCmd = Util.getAfterIfStartsWith("/title/", path);
		if(titleCmd != null) {
			char firstChar = titleCmd.charAt(0);
			view.showBookCollection(this.model.getBooksStartingWith(firstChar), request, "Books starting with '"+firstChar+"'", Collections.emptyMap());
			return;
		}
		String authorCmd=Util.getAfterIfStartsWith("/author/",path);
		System.out.println("authorCmd "+authorCmd);
		if(authorCmd!=null){
			//get the first letter
			char firstChar=authorCmd.charAt(0);
			view.showBookCollectionByAuthor(this.model.getAuthorStartingWith(firstChar),request,"Authors starting with",Collections.emptyMap());
		}

		// Per-book pages
		String bookId = Util.getAfterIfStartsWith("/book/", path);
		if(bookId != null) {
			view.showBookPage(this.model.getBook(bookId), resp);
			return;
		}

		// Front page!
		if ("/front".equals(path) || "/".equals(path)) {
			view.showFrontPage(this.model, resp);
			return;
		}
	}
}