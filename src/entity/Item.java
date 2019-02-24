package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// the needed information of the event, 
// processing the raw JSON data from TicketMaster API and database
public class Item {
	private String itemId; 
	private String name;
	private double rating;
	private String address;
	private Set<String> categories; // tags of the event
	private String imageUrl; // image source
	private String url; // link of the event
	private double distance; // how far is the event from you
	
	public static class ItemBuilder {
		// builder pattern (builder class has to be static)
		// 	1. disable modifying/setting after construction
		// 	2. avoid initialization mistakes
		private String itemId; 
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		} 
		public Item build() {
			return new Item(this);
		}
	}
	
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}

	// interacting with front-end in JSON format
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId)
				.put("name", name)
				.put("rating", rating)
				.put("address", address)
				.put("categories", new JSONArray(categories))
				.put("imageUrl", imageUrl)
				.put("url", url)
				.put("distance", distance);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	

}
