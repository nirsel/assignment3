package bgu.spl.net.impl;

import bgu.spl.net.api.Function;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.*;

public class MessagingProtocolImpl implements MessagingProtocol<Message> {
    private boolean shouldTerminate=false;
    User user=null;
    private HashMap<Short, Function> functionMap;
    private Database database=Database.getInstance();
    public MessagingProtocolImpl(){
        functionMap=new HashMap<>();
        short c1=1,c2=2,c3=3,c4=4,c5=5,c6=6,c7=7,c8=8,c9=9,c10=10,c11=11,c12=12,c13=13;
        functionMap.put(c1,(parameters)->{ //Admin register
                String[] para=new String[1];
                para[0]=String.valueOf(c1); // get the opCode
                if (user==null&&database.adminRegister(parameters[0],parameters[1]))
                    return getAck(para); //ack message
                else
                    return getError(c1); //error message
        });
        functionMap.put(c2,(parameters)->{ // Student register
            String[] para=new String[1];
            para[0]=String.valueOf(c2);// get the opCode
                if (user==null&&database.studentRegister(parameters[0],parameters[1]))
                    return getAck(para);//ack message
                else
                    return getError(c2);//error message
        });
        functionMap.put(c3,(parameters)->{ // login
            if (user!=null|!database.isRegistered(parameters[0]))
                return getError(c3);//error message
            User user=database.login(parameters[0], parameters[1]); // login user though database
            if (user!=null) {
                this.user = user;
                String[] para = new String[1];
                para[0] = String.valueOf(c3);// get the opCode
                return getAck(para);//ack message
            }
            return getError(c3);//error message

        });
        functionMap.put(c4,(parameters)->{ //logout
            if (!database.logOut(user))
                return getError(c4);//error message
            shouldTerminate=true;
            String[] para=new String[1];
            para[0]=String.valueOf(c4); // get the opCode
            return getAck(para);//ack message
        });
        functionMap.put(c5,(parameters)->{ // register to course
            if(database.registerCourse(user, Integer.parseInt(parameters[0]))){ //if was able to register the user to the course
                String[] para=new String[1];
                para[0]=String.valueOf(c5); // get the opCode
                return getAck(para); // ack message
            }
            return getError(c5); // error message
        });
        functionMap.put(c6, (parameters)->{ // check kdam courses
            if (user==null||user.isAdmin())
                return getError(c6);
            int[] array = database.getKdamCourses(Integer.parseInt(parameters[0])); //get the list of kdam courses through database
            if(array==null)
                return getError(c6); // error msg
            String kdamCourses = "[";
            String[] para=new String[2];
            para[0]=String.valueOf(c6); // get the opCode
            if(array.length==0){ // there are no kdam courses
                para[1]="[]";
                return getAck(para); // ack msg
            }
            for(int i=0;i<array.length-1;i++){
                kdamCourses+= array[i] +",";
            }
            kdamCourses+= array[array.length - 1] +"]";
            para[1]=kdamCourses;
            return  getAck(para); // ack msg
        });
        functionMap.put(c7, (parameters)->{ // course status
            int courseNum=Integer.parseInt(parameters[0]);
            Course course=database.getCourse(courseNum);
            if (user==null||!user.isAdmin()|course==null)
                return getError(c7); //error msg
            String[] para=new String[4];
            para[0]=String.valueOf(c7); // get the opCode
            para[1]="Course: ("+courseNum+") "+course.getCourseName();
            int numOfMax=course.getNumOfMaxStudents(); // get the capacity of the course
            para[2]="Seats Available: "+(numOfMax-database.numOfStudentsRegistered(courseNum))+"/"+numOfMax; // calculates the num of available seats
            List<String> studentList=database.studentList(courseNum); // get the list of students register to this course
            if (studentList.size()==0) // there are no students registered
                para[3]="Students Registered: []";
            else{
                String list = "Students Registered: [";
                String[] stringArray=studentList.toArray(new String[studentList.size()]);
                for(int i=0;i<stringArray.length-1;i++){
                    list+= stringArray[i]+",";
                }
                list+= stringArray[stringArray.length-1]+"]";
                para[3]=list;
            }
            return getAck(para); // ack msg
        });

        functionMap.put(c8,(parameters)->{ // student status
            if (user==null||!user.isAdmin()|!database.isRegistered(parameters[0]))
                return getError(c8); // error msg
            User user=database.getUser(parameters[0]);
            if (user.isAdmin())
                return getError(c8);
            List<Integer> numCourseList=user.getCoursesRegistered(); // get the list of courseNumbers the user is register to.
            List<Course> courseList=new LinkedList<Course>();
            for (Integer num:numCourseList){
                courseList.add(database.getCourse(num)); // get the course associated with a course num
            }
            courseList.sort(Comparator.comparingInt(a->a.getSerialNum())); // sort the courses alphabetically
            String[] para=new String[2];
            para[0]=String.valueOf(c8); // get the opCode
            String list="Student: "+parameters[0]+'\n'+"Courses: [";
            if (courseList.size()>0) {
                for (Course course : courseList) {
                    list = list + course.getCourseNum() + ",";
                }
                list=list.substring(0,list.length()-1);
                list=list+"]";
                para[1]=list;
            }
            else
                para[1]="Student: "+parameters[0]+'\n'+"Courses: []";
            return getAck(para); // ack msg
        });

        functionMap.put(c9,(parameters)->{ //check if registered
            int courseNum=Integer.parseInt(parameters[0]);
            if (user==null||user.isAdmin()|database.getCourse(courseNum)==null)
                return getError(c9); // error msg
            boolean ans = database.registeredToCourse(user,courseNum); // checks if a user is registered to a course through the database
            String[] para=new String[2];
            para[0]=String.valueOf(c9); // get the opCode
            if (ans) // true- user is registered
                para[1]="REGISTERED";
            else //false- user is not registered
                para[1]="NOT REGISTERED";
            return getAck(para); // ack msg
        });

        functionMap.put(c10,(parameters)->{ //unregister from course
            if (user==null||user.isAdmin())
                return getError(c10); // error msg
            int courseNum=Integer.parseInt(parameters[0]);
            if (!database.registeredToCourse(user,courseNum)) //checks if a user is NOT registered to a course through the database
                return getError(c10); // error msg
            boolean ans = database.unregisterFromCourse(user,courseNum); //unregister the user from a course
            if (!ans)
                return getError(c10); // error msg
            String[] para=new String[1];
            para[0]=String.valueOf(c10); // get the opCode
            return getAck(para); // ack msg
        });

        functionMap.put(c11,(parameters)->{ //checks my courses
            if (user==null||user.isAdmin())
                return getError(c11); // error msg
            List<Integer> numCourseList=user.getCoursesRegistered(); // get the list of courses (by course num) the user is register to
            List<Course> courseList=new LinkedList<Course>();
            for (Integer num:numCourseList){
                courseList.add(database.getCourse(num)); // get the course associated with a course num
            }
            courseList.sort(Comparator.comparingInt(a->a.getSerialNum())); // sorts the courses by the order they were added to the system.
            String[] para=new String[2];
            para[0]=String.valueOf(c11); // get the opCode
            String list="[";
            if (courseList.size()>0) {
                for (Course course : courseList) {
                    list = list + course.getCourseNum() + ",";
                }
                list=list.substring(0,list.length()-1);
                list=list+"]";
                para[1]=list;
            }
            else
                para[1]="[]";
            return getAck(para); // ack msg
        });



    }
    @Override
    public Message process(Message msg) {
        //shouldTerminate = ("LOGOUT").equals(msg);
        short code=msg.getOpCode();
        Message ans=functionMap.get(code).act(msg.getParameters());
        return ans;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    private Message getError(short opCode){
        String[] array=new String[1];
        array[0]=String.valueOf(opCode); // get the opCode of the msg that failed
        short code=13; // the code associated with an error msg
        return new Message(code, array);
    }

    private Message getAck(String[] parameters){
        short code = 12; // the code associated with an ack msg
        return new Message(code,parameters);
    }
}
