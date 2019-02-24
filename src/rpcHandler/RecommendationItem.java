package rpcHandler;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import recommendation.GeoRecommendation;

/**
 * Servlet implementation class RecommendationItem
 */
@WebServlet("/recommendation")
public class RecommendationItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendationItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray array = new JSONArray();
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String userId = request.getParameter("user_id");

		GeoRecommendation recommendation = new GeoRecommendation();
		
		try {
			List<Item> items = recommendation.recommendItems(userId, lat, lon);
			for (Item item : items) {
				array.put(item.toJSONObject());
			}
			RpcHelper.writeJSONArray(response, array);
		} catch (Exception e){
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
