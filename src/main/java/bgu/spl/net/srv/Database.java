package bgu.spl.net.srv;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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

	/**
	 * Register a new admin to the system
	 * @param username- the admin's username
	 * @param password- the admin's password
	 * @return true if registration was successful. false otherwise.
	 */
	public boolean adminRegister(String username, String password){
		synchronized (registerLock) {
			if (isRegistered(username))
				return false;
			userMap.put(username, new Admin(username, password));
			return true;
		}
	}
	/**
	 * Register a new student to the system. This method is synchronized to avoid 2 clients trying to register under the same user.
	 * @param username- the student's username
	 * @param password- the student's password
	 * @return true if registration was successful. false otherwise.
	 */

	public boolean studentRegister(String username, String password){
		synchronized (registerLock) {
			if (isRegistered(username))
				return false;
			userMap.put(username, new Student(username, password));
			return true;
		}
	}
	/**
	 * checks if the user is registered.
	 * @param username- the user's username
	 * @return true if the user is registered. false otherwise.
	 */

	public boolean isRegistered(String username){
		return userMap.containsKey(username);
	}
	
	/**
	 * login the user. This method is synchronized (on user) to avoid two clients trying to login under the same user.
	 * @param username- the user's username
	 * @param password- the user's password
	 * @return user- user that logged in
	 */
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
	/**
	 * logout the user.
	 * @param user- the user whom we want to logout.
	 * @return true if logout was successful. false otherwise.
	 */
	public boolean logOut(User user){
		if (user==null||!user.getLogged())
			return false;
		user.unlog();
		return true;
	}
	/**
	 * Register the user to the course. 
	 * This method is partly synchronized (on course) to avoid 2 clients tring to register to this course while number of seats is limited.
	 * @param user- the user to register
	 * @param numCourse- the course number of the course which the user wants to register to.
	 * @return true if registration was successful. false otherwise.
	 */
	public boolean registerCourse(User user, int numCourse){
		if(user==null) {return false;}
		if(!user.getLogged() | user.isAdmin() | !numCourseMap.containsKey(numCourse)) {return false;}
		Course course = numCourseMap.get(numCourse);
		if (user.getCoursesRegistered().contains(numCourse))
			return false;
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
	/**
	 * Retrieves the list of kdam courses associated with a specific course.
	 * @param courseNum- the course number of the course.
	 * @return the list of kdam courses.
	 */
	public int[] getKdamCourses(int courseNum){
		if(!numCourseMap.containsKey(courseNum)){
			return null;
		}
		int[] array=numCourseMap.get(courseNum).getKdamCoursesList();
		List<Course> courseList=new LinkedList<>();
		for (int i=0;i<array.length;i++)
			courseList.add(numCourseMap.get(array[i]));
		courseList.sort(Comparator.comparingInt((a)->a.getSerialNum()));
		int i=0;
		for (Course c:courseList){
			array[i]=c.getCourseNum();
			i++;
		}
		return array;
	}
	 /**
	 * Retrieves the course associated with a course number.
	 * @param numCourse- the course number of the course.
	 * @return the course.
	 */
	public Course getCourse(int numCourse){
		if (!numCourseMap.containsKey(numCourse))
			return null;
		return numCourseMap.get(numCourse);
	}
	/**
	 * Retrieves the number of students registered to the course.
	 * @param courseNum- the course number of the course.
	 * @return the number of students registered or -1 if the course doesn't exist.
	 */
	public int numOfStudentsRegistered(int courseNum){
		if (!numCourseMap.containsKey(courseNum))
			return -1;
		return registerMap.get(numCourseMap.get(courseNum)).size();
	}
	/**
	 * Retrieves the list of students registered to the course sorted alphabetically.
	 * @param courseNum- the course number of the course.
	 * @return the list of students registered.
	 */
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
	/**
	 * checks if the user is registered to this course.
	 * @param numCourse- the course number of the course.
	 * @param user- the useR to register.
	 * @return true if the user is registered to the course. false otherwise.
	 */
	public boolean registeredToCourse(User user, int numCourse){
		if (!numCourseMap.containsKey(numCourse)|user.isAdmin())
			return false;
		return registerMap.get(numCourseMap.get(numCourse)).contains(user);
	}
	/**
	 * Unregister a user from a course.
	 * @param user- the use to unregister.
	 * @param numCourse- the course number of the course.
	 * @return true if unregistering was successful. false otherwise.
	 */
	public boolean unregisterFromCourse(User user,int numCourse){
		if (!registeredToCourse(user,numCourse))
			return false;
		user.removeFromCourse(numCourse);
		Course course=numCourseMap.get(numCourse);
		synchronized (course) {
			registerMap.get(course).remove(user);
		}
		return true;
	}
	/**
	 * retrieves a user given his username.
	 * @param username- the username of the user.
	 * @return the user.
	 */
	public User getUser(String username){return userMap.get(username);}

}
