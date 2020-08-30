package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import db.MySQLConnection;
import entity.Item;
import external.GitHubClient;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("long"));

		GitHubClient client = new GitHubClient();
		List<Item> Items = client.search(lat, lon, null);

		MySQLConnection connection = new MySQLConnection();
		Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);
		connection.close();

		for (Item item:Items){
			item.setFavorite(favoriteItemIds.contains(item.getId()));
		}

		ObjectMapper mapper = new ObjectMapper();
		response.getWriter().println(mapper.writeValueAsString(Items));
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
