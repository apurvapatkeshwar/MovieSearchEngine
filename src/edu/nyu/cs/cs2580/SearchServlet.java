package project;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		System.out.println(query);
		try {
			if(se == null){
				se = new SearchEngine();
			}
			Query processedQuery = new Query(query);
			processedQuery.processQuery();
			
			Ranker ranker = Ranker.Factory.getRanker(se.indexer);
			// Ranking.
			Vector<ScoredDocument> scoredDocs = ranker.runQuery(processedQuery, 10);
			if (scoredDocs != null && scoredDocs.size()>0){
				StringBuilder sb = new StringBuilder();
				for(int i =0; i<scoredDocs.size(); i++){
					sb.append("<div>");
					sb.append("<div>"+scoredDocs.get(i).asHtmlResult());
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
