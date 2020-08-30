package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.RegisterRequestBody;
import entity.ResultResponse;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Register
 */
@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class Register extends HttpServlet {

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		RegisterRequestBody body = mapper.readValue(request.getReader(), RegisterRequestBody.class);

		MySQLConnection connection = new MySQLConnection();
		ResultResponse resultResponse;
		if (connection.addUser(body.userId, body.password, body.firstName, body.lastName)) {
			resultResponse = new ResultResponse("OK");
		} else {
			resultResponse = new ResultResponse( "User Already Exists");
		}
		connection.close();
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(),resultResponse);
	}

}
