package bgu.spl.net.srv;

import java.util.List;

public class Admin extends User {


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
    }
}
