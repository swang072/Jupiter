package dbClient.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dbClient.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import ticketMasterClient.TicketMasterClient;

public class MySQLConnection implements DBConnection {
	
	private Connection connection;
	
	public MySQLConnection () {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			connection = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		
		if (connection == null) {
			System.err.println("DB connection failed");
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1,  userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (connection == null) {
			System.err.println("DB connection failed");
			return;
		}
		
		try {
			String sql = "DELETE FROM history where user_id = ? AND item_id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1,  userId);
			for (String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (connection == null) {
			System.err.println("DBConnection failed.");
			return new HashSet<>();
		}
		
		Set<String> favoriteItems = new HashSet<>();
		
		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (connection == null) {
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			for (String itemId : itemIds) {
				ps.setString(1, itemId);
				ResultSet rs = ps.executeQuery();
				ItemBuilder builder =  new ItemBuilder();
				while (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					
					favoriteItems.add(builder.build());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (connection == null) {
			System.err.println("DBConnection failed");
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, itemId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String category = rs.getString("category");
				categories.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterClient client = new TicketMasterClient();
		List<Item> items = client.search(lat, lon, term);
		// Save every item that user searched into the database. Deduplicated.
		for (Item item : items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		if (connection == null) {
			System.err.println("DB connction failed");
			return;
		}
		try {
			String sql = "INSERT IGNORE INTO items VALUES(?,?,?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1,  item.getItemId());
			ps.setString(2, item.getName());
			ps.setDouble(3, item.getRating());
			ps.setString(4, item.getAddress());
			ps.setString(5, item.getImageUrl());
			ps.setString(6,  item.getUrl());
			ps.setDouble(7, item.getDistance());
			ps.execute();
			
			sql = "INSERT IGNORE INTO categories VALUES(?,?)";
			ps = connection.prepareStatement(sql);
			ps.setString(1, item.getItemId());
			for (String category : item.getCategories()) {
				ps.setString(2, category);
				ps.execute();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
