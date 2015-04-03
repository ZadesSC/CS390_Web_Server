package servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Darren on 4/3/2015.
 */
@WebServlet(name = "Servlet")
public class Servlet extends HttpServlet {

    public String databaseURL = "jdbc:mysql://localhost:3306/CS390";
    public String username = "student";
    public String password = "cs390";
    public Connection connection = null;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String req = request.getParameter("keywords").trim();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if(req == null || req.equals(""))
        {
            //do nothing
            out.print("<h1> No Resulsts</h1>");
        }
        String[] split = req.split("[\\W]");
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.databaseURL, this.username, this.password);

            if (split.length == 1)
            {
                String statment = "SELECT url_table_mt.URLID, url_table_mt.URL, url_table_mt.Description, url_table_mt.Image, url_table_mt.Title\n" +
                        "    FROM url_table_mt, word_table_mt\n" +
                        "     WHERE url_table_mt.URLID = word_table_mt.URLID && word_table_mt.Word = ? LIMIT 50";
                PreparedStatement pStatement = this.connection.prepareStatement(statment);
                pStatement.setString(1, split[0]);
                rs = pStatement.executeQuery();
            }
            else {
                String statement = "SELECT *\n" +
                        "FROM (SELECT url_table_mt.URLID, url_table_mt.URL, url_table_mt.Description, url_table_mt.Image, url_table_mt.Title\n" +
                        "    FROM url_table_mt, word_table_mt\n" +
                        "     WHERE url_table_mt.URLID = word_table_mt.URLID && word_table_mt.Word = ?) table1\n" +
                        "INNER JOIN\n" +
                        "  (SELECT url_table_mt.URLID, url_table_mt.URL, url_table_mt.Description, url_table_mt.Image, url_table_mt.Title\n" +
                        "   FROM url_table_mt, word_table_mt\n" +
                        "   WHERE url_table_mt.URLID = word_table_mt.URLID && word_table_mt.Word = ?) table2\n" +
                        "ON table1.URLID = table2.URLID LIMIT 50";

                PreparedStatement pStatement = this.connection.prepareStatement(statement);
                pStatement.setString(1, split[0]);
                pStatement.setString(2, split[1]);
                rs = pStatement.executeQuery();
            }

            while(rs.next()) {
                //<img src="smiley.gif" alt="Smiley face" height="42" width="42">
                if(rs.getString("Image") != null && !rs.getString("Image").equals("https://www.cs.purdue.edu/images/logo.svg"))
                {
                    out.println("<img src=" + rs.getString("Image") + " height=\"100\" width=\"100\">");
                }
                else
                {
                    out.println("<img src=" + "https://www.cs.purdue.edu/images/brand.svg" + " height=\"100\" width=\"100\">");
                }
                out.println("<h2><a href=" + rs.getString("URL") + ">" + rs.getString("Title") +"</a></h2>");
                //out.println("<h1>" + rs.getString("Title") + "</h1>");
                //out.println("<h2>" + rs.getString("URL") + "</h2>");
                out.println("<body>" + rs.getString("Description") + "<body>");
            }



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }
}
