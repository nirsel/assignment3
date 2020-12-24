package bgu.spl.net.srv;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
	private HashMap<Integer, Course> numCourseMap;
	private static Database singleton=DatabaseHolder.instance;
	private static class DatabaseHolder{
		private static Database instance=new Database();
	}
	//to prevent user from creating new Database
	private Database() {
		registerMap=new ConcurrentHashMap<>();
		userMap=new ConcurrentHashMap<>();
		numCourseMap=new HashMap<>();
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
		try {
			BufferedReader br = new BufferedReader(new FileReader(coursesFilePath));
			String line;
			while ((line=br.readLine())!=null){
				String[] lineArray=line.split("|");
				int courseNum=Integer.parseInt(lineArray[0]);
				String courseName=lineArray[1];
				int[] array= Stream.of(lineArray[2].substring(1,lineArray[2].length()-1).split(",")).mapToInt(Integer::parseInt).toArray();
				int numOfMaxStudents=Integer.parseInt(lineArray[3]);
				Course course=new Course(courseNum,courseName,array,numOfMaxStudents);
				registerMap.put(course,new LinkedList<>());
				numCourseMap.put(courseNum,course);
			}
			return true;
		}
		catch(FileNotFoundException e){e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	public boolean adminRegister(String username, String password){
		if (isRegistered(username))
			return false;
		userMap.put(username,new Admin(username,password));
		return true;
	}

	public boolean studentRegister(String username, String password){
		if (isRegistered(username))
			return false;
		userMap.put(username,new Student(username,password));
		return true;
	}

	public boolean isRegistered(String username){
		return userMap.containsKey(username);
	}

	public boolean isLogged(User user){
		return user.getLogged();
	}

	public User login(String username, String password){
		User user=userMap.get(username);
		if (user!=null&&!user.getLogged()&user.getPassword().equals(password)) {
			user.login();
			return user;
		}
		return null;
	}
	public boolean logOut(User user){
		if (!user.getLogged())
			return false;
		user.unlog();
		return true;
	}




}
