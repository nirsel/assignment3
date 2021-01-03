package bgu.spl.net.srv;

import java.util.List;

public class Admin extends User {
    /**
     * class that represents user of type "Admin" in the database.
     */


    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public List<Integer> getCoursesRegistered() {
        return null;
    } //admin can't register to courses

    @Override
    public void removeFromCourse(int numCourse) { //admin doesn't have courses
    }
}
