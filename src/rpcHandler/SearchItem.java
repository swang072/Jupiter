package rpcHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import dbClient.DBConnection;
import dbClient.DBConnectionFactory;
import entity.Item;


/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// lat, lon is required in request
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		// Do Get from the connection/database
		String term = request.getParameter("term");
		String userId = request.getParameter("user_id");
		
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			List<Item> items = connection.searchItems(lat, lon, term);
			Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId); // get favorite list
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoriteItemIds.contains(item.getItemId()));
				array.put(obj);
			}
			
			RpcHelper.writeJSONArray(response, array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		
//		TicketMasterClient tmClient = new TicketMasterClient();
//		List<Item> items = tmClient.search(lat, lon, null);
//		
//		JSONArray array = new JSONArray();
//		for (Item item : items) {
//			array.put(item.toJSONObject());
//		}
//		
//		RpcHelper.writeJSONArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
