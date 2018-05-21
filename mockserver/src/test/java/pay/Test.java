package pay;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Test extends HttpServlet {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1915463532411657451L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		
		String username = request.getParameter("username");
		// String password = request.getParameter("password");

		System.out.println("username=" + username);
		// System.out.println("password=" + password);

		response.setContentType("text/html;charset=UTF8");
		response.setContentType("text/html");
//		response.getWriter().println("Test Success!!");
		out.println("Test Success!!");
	}



}
