package bgu.spl.net.srv;

import java.util.List;

public class Course {

    private final int courseNum;
    private final String courseName;
    private final int[] kdamCoursesList;
    private final int numOfMaxStudents;

    public Course(int courseNum, String courseName, int[] kdamCoursesList, int numOfMaxStudents){
        this.courseName=courseName;
        this.courseNum=courseNum;
        this.kdamCoursesList=kdamCoursesList;
        this.numOfMaxStudents=numOfMaxStudents;
    }

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
}
