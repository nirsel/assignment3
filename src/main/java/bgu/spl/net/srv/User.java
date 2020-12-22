package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<Integer> coursesRegistered;
    private boolean logged=false;

    public User(String username, String password){
        this.username=username;
        this.password=password;
        coursesRegistered=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void registerToCourse(Integer numCourse){
        if (!coursesRegistered.contains(numCourse))
            coursesRegistered.add(numCourse);
    }

    public List<Integer> getCoursesRegistered(){
        return coursesRegistered;
    }

    public boolean getLogged(){
        return logged;
    }

    public void login(){
        logged=true;
    }

    public void unlog(){
        logged=false;
    }
}
