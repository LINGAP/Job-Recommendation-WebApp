package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.HistoryRequestBody;
import entity.ResultResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userId = request.getParameter("user_id");
		
		MySQLConnection connection = new MySQLConnection();
		Set<Item> Items = connection.getFavoriteItems(userId);
		connection.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();
		HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

		MySQLConnection connection = new MySQLConnection();
		connection.setFavoriteItems(body.userId, body.favorite);
		connection.close();

		ResultResponse resultResponse = new ResultResponse("SUCCESS");
		mapper.writeValue(response.getWriter(), resultResponse);
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		ObjectMapper mapper = new ObjectMapper();

		HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

		MySQLConnection connection = new MySQLConnection();
		connection.unsetFavoriteItems(body.userId, body.favorite.getId());
		connection.close();

		ResultResponse resultResponse = new ResultResponse("SUCCESS");
		mapper.writeValue(response.getWriter(), resultResponse);

	}

}
