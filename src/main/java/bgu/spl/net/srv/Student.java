package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public class Student extends User{
    private List<Integer> coursesRegistered;

    public Student(String username, String password) {
        super(username, password);
        coursesRegistered=new ArrayList<>();
    }

    public void registerToCourse(Integer numCourse){
        if (!coursesRegistered.contains(numCourse))
            coursesRegistered.add(numCourse);
    }

    public List<Integer> getCoursesRegistered(){
        return coursesRegistered;
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}