package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    private final String username;
    private final String password;

    private boolean logged=false;

    public User(String username, String password){
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public abstract boolean isAdmin();


    public boolean getLogged(){
        return logged;
    }

    public void login(){
        logged=true;
    }

    public void unlog(){
        logged=false;
    }

    public abstract List<Integer> getCoursesRegistered();

    public abstract void removeFromCourse(int numCourse);
}
