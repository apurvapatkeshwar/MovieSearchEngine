package project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project.SearchEngine.Options;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/Search")
public class SearchServlet extends HttpServlet {
	
	SearchEngine se;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String query = request.getParameter("query");
		String mode = request.getParameter("mode");
		
		System.out.println(query);
		System.out.println(mode);
		
		try {
			
			if(se == null){
				se = new SearchEngine();
				SearchEngine.OPTIONS = new Options("/conf/engine.conf");
			}
			
			IndexerMovie index = (IndexerMovie) se.indexer;
			
			Query processedQuery = new Query(query, mode);
			processedQuery.processQuery();
			
			Ranker ranker = Ranker.Factory.getRanker(se.OPTIONS, se.indexer);
			
			// Ranking.
			Vector<ScoredMovie> scoredMovie = ranker.runQuery(processedQuery, 10, mode);
			if (scoredMovie != null && scoredMovie.size()>0){
				StringBuilder sb = new StringBuilder();
				for(int i =0; i<scoredMovie.size(); i++){
					sb.append("<div>");
					sb.append("<div>"+scoredMovie.get(i).asHtmlResult());
					sb.append("</div></div>");
				}
				request.getSession().setAttribute("ResponseBody", sb);
				request.getSession().setAttribute("Query", query);
				request.getSession().setAttribute("Founded", 1);
				request.getRequestDispatcher("Result.jsp").forward(request, response);
			}
			else{
				request.getSession().setAttribute("Founded", 0);
				request.getRequestDispatcher("Result.jsp").forward(request, response);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
