package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static int i = 0;
	
	/** */ 
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("hello world: " + i++); 
		out.println("<br />");
		out.println("Request Received at " + (new Date(System.currentTimeMillis())) + "\n<br />");
		out.println("characterEncoding=" + request.getCharacterEncoding() + "\n<br />");
		out.println("contentType=" + request.getContentType() + "\n<br />");
		out.println("locale=" + request.getLocale() + "\n<br />");
		out.println("path=" + request.getPathTranslated() + "\n<br />");
		out.println("servletpath=" + request.getServletPath() + "\n<br />");
		
		// what is the working directory ?? 
		if(new File("README.TXT").exists())
			out.println("local path: " + new File("README.TXT").getAbsolutePath() + "<br />");

		@SuppressWarnings("rawtypes")
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
		    out.print(name + " = ");
		    String values[] = request.getParameterValues(name);
		    for (int i = 0; i < values.length; i++) 
		    	out.println(values[i] + "\n<br />");
		}
				
		out.close();
	}
}
