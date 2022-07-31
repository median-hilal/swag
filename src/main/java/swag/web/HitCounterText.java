package swag.web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HitCounterText
 */
@WebServlet("/HitCounterText")
public class HitCounterText extends HttpServlet {
    
    private static int count = 0;
    private String filePath ="/hit-counter-text/hitcount.properties";
   
    public void init()throws ServletException {
        
        filePath = getServletContext().getRealPath(filePath); 
        BufferedReader bR = null;
        try {
            bR = new BufferedReader(new FileReader(filePath));
            String countString = bR.readLine();
            count = Integer.parseInt(countString);
            return;
        }
        catch (FileNotFoundException fNfE) {
            String error = "Couldn't find file " + filePath + ":\n" + fNfE.getMessage();
            System.err.println(error);
            log(error);
        }
        catch (IOException iOe) {
            String error = "Couldn't read file " + filePath + ":\n" + iOe.getMessage();
            System.err.println(error);
            log(error);
        }
        catch (NumberFormatException nFe) {
            String error = "Couldn't read file " + filePath + ":\n" + nFe.getMessage();
            System.err.println(error);
            log(error);
        }
        finally {
            try {
                if(bR !=null)
                    bR.close();
            }
            catch(IOException iOe) {
                String error = "Problem closing the bufferedReader stream to " + filePath + ":\n" + iOe.getMessage();
                System.err.println(error); 
                log(error);
            }
        }
    }
   
    //Increases the hitcount and saves to disk every 10th request
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        int localCount;
        synchronized(this) {
            localCount = ++count;
            if(localCount % 10 == 0)
                saveCount();
        }
        out.println(localCount);
    }
    
    
    public void destroy() {
        
        saveCount();
    }
    
    //Creates a file and writes the new hit count as a string
    public void saveCount() {
        
        PrintWriter pW = null;
        try {
            pW = new PrintWriter(new FileWriter(filePath));
            pW.println(count);
            return;
        }
        catch(IOException iOe) { 
            String error = "Couln't write to " + filePath + ":\n" + iOe.getMessage();
            System.err.println(error);
            log(error);
        }
       
        finally {
            try {    
                if(pW != null) {
                    pW.close();
                }
            }
            catch(Exception e) {
                String error = "Problem closing the PrintWriter stream to " + filePath + ":\n" + e.getMessage();
                System.err.println(error);
                log(error);
            }
        }
    }
} // End class HitCountText