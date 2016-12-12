package wasdev.sample.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import wasdev.sample.functions.*;

/**
 * Servlet implementation class SimpleServlet..
  Servlet implementation class SimpleServlet.....sjlcdsjcktext
  New comment has been added.......
 */
@WebServlet("/SimpleServlet")
public class SimpleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Message message = new Message();
        response.setContentType("text/html");
        response.getWriter().print("Hello World "+message.getMessage()+"!");
    }

}
