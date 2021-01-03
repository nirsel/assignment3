package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Student extends User{
    /**
     * class that represents user of type "Student" in the database.
     */
    private List<Integer> coursesRegistered;

    public Student(String username, String password) {
        super(username, password);
        coursesRegistered=new LinkedList<>();
    }

    public List<Integer> getCoursesRegistered(){
        return coursesRegistered;
    }

    @Override
    public void removeFromCourse(int numCourse) {
        coursesRegistered.remove(coursesRegistered.indexOf(numCourse));
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
