package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dbClient.DBConnection;
import dbClient.DBConnectionFactory;
import entity.Item;


public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendItems = new ArrayList<>();
		
		DBConnection connection = DBConnectionFactory.getConnection();
		Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
		
		Map<String, Integer> mapCategories = new HashMap<>();
		for(String favoriteItem : favoriteItems) {
			Set<String> categories = connection.getCategories(favoriteItem);
			for (String category : categories) {
				mapCategories.put(category, mapCategories.getOrDefault(category, 0) + 1);
			}
			
		}
		// sorting using a list
		List<Map.Entry<String, Integer>> categoryList = new ArrayList<>(mapCategories.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		}); 
		
		// 
		Set<String> visitedItemIds = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			// from TicketMaster API, get items with specific categories
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for (Item item : items) {
				if (!favoriteItems.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
					recommendItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
		}
		return recommendItems;
	}

}
