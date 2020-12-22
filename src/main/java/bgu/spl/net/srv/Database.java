package bgu.spl.net.srv;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	private ConcurrentHashMap<Course, List<User>> registerMap;
	private ConcurrentHashMap<String, User> userMap;
	private static Database singleton=DatabaseHolder.instance;
	private static class DatabaseHolder{
		private static Database instance=new Database();
	}
	//to prevent user from creating new Database
	private Database() {
		// TODO: implement
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return singleton;
	}
	
	/**
	 * loades the courses from the file path specified 
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		// TODO: implement
		return false;
	}


	public boolean adminRegister(String username, String password){
		if (isRegistered(username))
			return false;
		userMap.put(username,new User(username,password,"Admin"));
		return true;
	}

	public boolean studentRegister(String username, String password){
		if (isRegistered(username))
			return false;
		userMap.put(username,new User(username,password,"Student"));
		return true;
	}

	public boolean isRegistered(String username){
		return userMap.containsKey(username);
	}

	public boolean isLogged(String username){
		return userMap.get(username).getLogged();
	}

	public boolean login(String username, String password){
		User user=userMap.get(username);
		if (user.getPassword().equals(password)) {
			user.login();
			return true;
		}
		return false;
	}
	public boolean logOut(String username){
		if (!isLogged(username))
			return false;
		userMap.get(username).unlog();
		return true;
	}




}
