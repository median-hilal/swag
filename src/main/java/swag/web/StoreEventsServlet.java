package swag.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;

/**
 * Servlet implementation class StoreEventsServlet
 */
@WebServlet("/StoreEventsServlet")
public class StoreEventsServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public StoreEventsServlet() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub


    HttpSession session = request.getSession(false);



    File yourFile = new File("C:\\logs\\" + session.getId() + ".txt");
    yourFile.createNewFile(); // if file already exists will do nothing

    Reader initialReader = request.getReader();
    String targetString = IOUtils.toString(initialReader);
    initialReader.close();

    appendUsingFileWriter(yourFile, "\n" + targetString);


    response.getWriter().append("Served at: ").append(request.getContextPath());
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    // doGet(request, response);
  }

  private static void appendUsingFileWriter(File file, String text) {

    FileWriter fr = null;
    try {
      // Below constructor argument decides whether to append or override
      fr = new FileWriter(file, true);
      fr.write(text);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fr.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
