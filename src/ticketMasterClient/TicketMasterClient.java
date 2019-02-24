package ticketMasterClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterClient {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "event";
	private static final String API_KEY = "ZPqfRFBrHnZUspBp0DQnR0AvUgBckb9d";
	
	public List<Item> search(double lat, double lon, String keyword) {
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			// some character need to be encoded, like Chinese, space need to be encoded. "hi there" => "hi20%there"
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// convert lat/long to geohash string. (lat/long is deprecated in TicketMaster API)
		final int PRECISION = 8;
		String geoHash = GeoHash.encodeGeohash(lat, lon, PRECISION);
		
		//query string
		String query = String.format("apikey=%s&geoPoint=%s&keywork=%s&radius=%s", API_KEY, geoHash, keyword, 50);
		String url = URL + "?" + query;
		
		try {
			// build http connection
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			
			// get response code, success if 200, otherwise failed.
			int responseCode = connection.getResponseCode();
			System.out.println(String.format("response code: %s", responseCode));
			if(responseCode != 200) {
				return new ArrayList<>();
			}
			
			// get response content
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			JSONObject obj = new JSONObject(response.toString());
			
			if(!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	// Convert JSONArray to a list of item objects
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for(int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if(!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if(!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			if(!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	// helper method
	private String getAddress(JSONObject event) throws JSONException {
		if(!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if(!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for (int i = 0; i < venues.length(); i++) {
					JSONObject obj = venues.getJSONObject(i);
					StringBuilder addressBuilder = new StringBuilder();
					if(!obj.isNull("address")) {
						JSONObject address = obj.getJSONObject("address");
						if(!address.isNull("line1")) {
							addressBuilder.append(address.getString("line1"));
						}
						if(!address.isNull("line2")) {
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line2"));
						}
						if(!address.isNull("line3")) {
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line3"));
						}
					}
					if(!obj.isNull("city")) {
						JSONObject city = obj.getJSONObject("city");
						if(!city.isNull("name")) {
							addressBuilder.append(",");
							addressBuilder.append(city.getString("name"));
						}
					}
					
					String addressStr = new String(addressBuilder);
					if(!addressStr.equals("")) {
						return addressStr;
					}
				}
			}
		}
		return "";
	}
	
	// helper method
	private String getImageUrl(JSONObject event) throws JSONException {
		// return the first image url
		if(!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			for(int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		
		return "";
	}
	
	// helper method
	private Set<String> getCategories(JSONObject event) throws JSONException {
		// return all the categories as tags
		Set<String> categories = new HashSet<>();
		if(!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if(!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		
		return categories;
	}
	
	// test method
	private void testQuery(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		try {
			for(Item event: events) {
				JSONObject obj = event.toJSONObject();
				System.out.println(obj.toString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TicketMasterClient client = new TicketMasterClient();
		client.testQuery(40.712776, -74.005974);
	}
}
