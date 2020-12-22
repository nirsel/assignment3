package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final String password;
    private List<Integer> coursesRegistered;
    private final String status;
    private boolean logged=false;

    public User(String username, String password,String status){
        this.username=username;
        this.password=password;
        this.status=status;
        coursesRegistered=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus(){ return status;}

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
