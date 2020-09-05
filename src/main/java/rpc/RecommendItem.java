package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;

import entity.Item;
import recommendation.Recommendation;
/**
 * Servlet implementation class RecommendItem
 */
@WebServlet(name = "RecommendationServlet", urlPatterns = {"/recommendation"})
public class RecommendItem extends HttpServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
  	protected void doGet(HttpServletRequest request, HttpServletResponse response)
  			throws ServletException, IOException {
  		HttpSession session = request.getSession(false);
  		if (session == null) {
  			response.setStatus(403);
  			return;
  		}
  		String userId = session.getAttribute("user_id").toString();

  		double lat = Double.parseDouble(request.getParameter("lat"));
  		double lon = Double.parseDouble(request.getParameter("lon"));

  		Recommendation recommendation = new Recommendation();
  		List<Item> Items = recommendation.recommendItems(userId, lat, lon);

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getWriter(),Items);
  	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
