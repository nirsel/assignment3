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
	private Object registerLock=new Object();
	private static class DatabaseHolder{
		private static Database instance=new Database();
	}
	//to prevent user from creating new Database
	private Database() {
		registerMap=new ConcurrentHashMap<>();
		userMap=new ConcurrentHashMap<>();
		numCourseMap=new HashMap<>();
		initialize("./Courses.txt");
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
			int counter=1;
			while ((line=br.readLine())!=null){
				String[] lineArray=line.split("\\|");
				int courseNum=Integer.parseInt(lineArray[0]);
				String courseName=lineArray[1];
				int[] array;
				if (!lineArray[2].equals("[]")) {
					array = Stream.of(lineArray[2].substring(1, lineArray[2].length() - 1).split(",")).mapToInt(Integer::parseInt).toArray();
				}
				else{
					 array=new int[0];
				}
				int numOfMaxStudents=Integer.parseInt(lineArray[3]);
				Course course=new Course(courseNum,courseName,array,numOfMaxStudents, counter);
				registerMap.put(course,new LinkedList<>());
				numCourseMap.put(courseNum,course);
				counter++;
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
		synchronized (registerLock) {
			if (isRegistered(username))
				return false;
			userMap.put(username, new Admin(username, password));
			return true;
		}
	}

	public boolean studentRegister(String username, String password){
		synchronized (registerLock) {
			if (isRegistered(username))
				return false;
			userMap.put(username, new Student(username, password));
			return true;
		}
	}

	public boolean isRegistered(String username){
		return userMap.containsKey(username);
	}

	public boolean isLogged(User user){
		return user.getLogged();
	}

	public User login(String username, String password){
		User user=userMap.get(username);
		synchronized (user) {
			if (user != null && !user.getLogged() & user.getPassword().equals(password)) {
				user.login();
				return user;
			}
		}
		return null;
	}
	public boolean logOut(User user){
		if (user==null||!user.getLogged())
			return false;
		user.unlog();
		return true;
	}
	public boolean registerCourse(User user, int numCourse){
		if(user==null) {return false;}
		if(!user.getLogged() | user.isAdmin() | !numCourseMap.containsKey(numCourse)) {return false;}
		Course course = numCourseMap.get(numCourse);
		synchronized (course) {
			if (course.getNumOfMaxStudents() <= registerMap.get(course).size()) { //check if there is a seat in the course
				return false;
			}
			int[] kdamCourses = course.getKdamCoursesList();
			List<Integer> listOfCourses = user.getCoursesRegistered();
			for (int i = 0; i < kdamCourses.length; i++) {
				if (!listOfCourses.contains(kdamCourses[i])) {
					return false;
				}
			}
			listOfCourses.add(numCourse);
			registerMap.get(course).add(user);
		}
		return true;
	}
	public int[] getKdamCourses(int courseNum){
		if(numCourseMap.containsKey(courseNum)){
			return numCourseMap.get(courseNum).getKdamCoursesList();
		}
		return null;
	}

	public Course getCourse(int numCourse){
		if (!numCourseMap.containsKey(numCourse))
			return null;
		return numCourseMap.get(numCourse);
	}

	public int numOfStudentesRegistered(int courseNum){
		if (!numCourseMap.containsKey(courseNum))
			return -1;
		return registerMap.get(numCourseMap.get(courseNum)).size();
	}

	public List<String> studentList(int courseNum){
		if (!numCourseMap.containsKey(courseNum))
			return null;
		List<User> userList=registerMap.get(numCourseMap.get(courseNum));
		List<String> studentList=new LinkedList<String>();
		for (User user:userList){
			studentList.add(user.getUsername());
		}
		java.util.Collections.sort(studentList);
		return studentList;

	}

	public boolean registeredToCourse(User user, int numCourse){
		if (!numCourseMap.containsKey(numCourse)|user.isAdmin())
			return false;
		return registerMap.get(numCourseMap.get(numCourse)).contains(user);
	}

	public boolean unregisterFromCourse(User user,int numCourse){
		if (!registeredToCourse(user,numCourse))
			return false;
		user.removeFromCourse(numCourse);
		registerMap.get(numCourseMap.get(numCourse)).remove(user);
		return true;
	}

	public static void main(String [] args){
		Database d = Database.getInstance();
		boolean ans= d.initialize("src/main/java/bgu/spl/net/srv/Courses.txt");

	}


}
