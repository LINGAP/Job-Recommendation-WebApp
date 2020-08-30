package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.LoginRequestBody;
import entity.LoginResponseBody;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
@WebServlet(name = "login", urlPatterns = "/login")
public class Login extends HttpServlet {

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		ObjectMapper mapper = new ObjectMapper();
		LoginResponseBody loginResponseBody;
		
		if (session != null) {
			MySQLConnection connection = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			loginResponseBody = new LoginResponseBody("OK", userId,connection.getFullname(userId));
			connection.close();
		} else {
			loginResponseBody = new LoginResponseBody("Invalid Session",null,null);
			response.setStatus(403);
		}
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(), loginResponseBody);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		LoginRequestBody loginRequestBody = mapper.readValue(request.getReader(),LoginRequestBody.class);

		MySQLConnection connection = new MySQLConnection();
		LoginResponseBody loginResponseBody;
		if (connection.verifyLogin(loginRequestBody.userId, loginRequestBody.password)) {
			HttpSession session = request.getSession();
			session.setAttribute("user_id", loginRequestBody.userId);
			session.setMaxInactiveInterval(600);
			loginResponseBody = new LoginResponseBody("OK",loginRequestBody.userId,connection.getFullname(loginRequestBody.userId));
		} else {
			loginResponseBody = new LoginResponseBody("User Doesn't Exist",null,null);
			response.setStatus(401);
		}
		connection.close();
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(), loginResponseBody);
	}

}
