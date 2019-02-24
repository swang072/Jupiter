package dbClient.mysql;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;


public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Step 1: Connect to MySQL.
			System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
			if (conn == null) {
				System.out.println("connection fails.");
				return;
			}
			
			System.out.println("Import done successfully");
			
			String sql;
			Statement statement = conn.createStatement();
			
			// Step 2: Drop old tables
			sql = "DROP TABLE IF EXISTS categories";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS history";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS items";
			statement.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS users";
			statement.executeUpdate(sql);
			
			// Step3: create new tables, define new schema
			sql = "CREATE TABLE items (" + 
					"item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "PRIMARY KEY (item_id)" 
					+ ")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";

			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE categories ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			sql = "CREATE TABLE history (" + 
					"user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_edit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (item_id, user_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			statement.executeUpdate(sql);
			
			// Step 4: insert user
			sql = "INSERT INTO users VALUES('1234', '123erqjk13123', 'Vince', 'Zhang')";
			statement.executeUpdate(sql);
			System.out.println("Test inserting user successfully!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
