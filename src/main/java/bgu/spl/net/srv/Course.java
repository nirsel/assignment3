package bgu.spl.net.srv;

import java.util.List;

public class Course {
    /**
     * class that represents a course in the registration system.
     */

    private final int courseNum;
    private final String courseName;
    private final int[] kdamCoursesList;
    private final int numOfMaxStudents;
    private final int serialNum; //according to the order in the courses file.
    public Course(int courseNum, String courseName, int[] kdamCoursesList, int numOfMaxStudents, int serialNum){
        this.courseName=courseName;
        this.courseNum=courseNum;
        this.kdamCoursesList=kdamCoursesList;
        this.numOfMaxStudents=numOfMaxStudents;
        this.serialNum=serialNum;
    }

    //getters

    public int getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public int[] getKdamCoursesList() {
        return kdamCoursesList;
    }

    public int getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public int getSerialNum(){return serialNum;}
}
