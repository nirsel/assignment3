package bgu.spl.net.impl;

import bgu.spl.net.api.Function;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MessagingProtocolImpl implements MessagingProtocol<Message> {
    private boolean shouldTerminate=false;
    User user=null;
    private HashMap<Short, Function> functionMap;
    private Database database=Database.getInstance();
    public MessagingProtocolImpl(){
        functionMap=new HashMap<>();
        short c1=1,c2=2,c3=3,c4=4,c5=5,c6=6,c7=7,c8=8,c9=9,c10=10,c11=11,c12=12,c13=13;
        functionMap.put(c1,(parameters)->{//todo: check if can register while logged to another user
                String[] para=new String[1];
                para[0]=String.valueOf(c1);
                if (user==null&&database.adminRegister(parameters[0],parameters[1]))
                    return getAck(para); //ack message
                else
                    return getError(c1); //error message
        });
        functionMap.put(c2,(parameters)->{
            String[] para=new String[1];
            para[0]=String.valueOf(c2);
                if (user==null&&database.studentRegister(parameters[0],parameters[1]))
                    return getAck(para);//ack message
                else
                    return getError(c2);//error message
        });
        functionMap.put(c3,(parameters)->{
            if (user!=null|!database.isRegistered(parameters[0]))
                return getError(c3);//error message
            User user=database.login(parameters[0], parameters[1]);
            if (user!=null) {
                this.user = user;
                String[] para = new String[1];
                para[0] = String.valueOf(c3);
                return getAck(para);//ack message
            }
            return getError(c3);//error message

        });
        functionMap.put(c4,(parameters)->{
            if (!database.logOut(user))
                return getError(c4);//error message
            shouldTerminate=true;
            String[] para=new String[1];
            para[0]=String.valueOf(c4);
            return getAck(para);//ack message
        });
        functionMap.put(c5,(parameters)->{
            if(database.registerCourse(user, Integer.parseInt(parameters[0]))){
                String[] para=new String[1];
                para[0]=String.valueOf(c5);
                return getAck(para); // ack message
            }
            return getError(c5); // error message (5)
        });
        functionMap.put(c6, (parameters)->{
            int[] array = database.getKdamCourses(Integer.parseInt(parameters[0]));
            if(array==null)
                return getError(c6); // error msg
            String kdamCourses = "[";
            String[] para=new String[2];
            para[0]=String.valueOf(c6);
            if(array.length==0){
                para[1]="[]";
                return getAck(para); // ack msg
            }
            for(int i=0;i<array.length-1;i++){
                kdamCourses+= String.valueOf(array[i])+",";
            }
            kdamCourses+= array[array.length - 1] +"]";
            para[1]=kdamCourses;
            return  getAck(para); // ack msg
        });
        functionMap.put(c7, (parameters)->{
            int courseNum=Integer.parseInt(parameters[0]);
            int[] kdam=database.getKdamCourses(courseNum);
            if (user==null||!user.isAdmin()|kdam==null)
                return getError(c7);
            String[] para=new String[4];
            para[0]=String.valueOf(c7);
            Course course=database.getCourse(courseNum);
            para[1]="Course: ("+courseNum+") "+course.getCourseName();
            para[2]="Seats Available: "+database.numOfStudentesRegistered(courseNum)+"/"+course.getNumOfMaxStudents();
            List<String> studentList=database.studentList(courseNum);
            if (studentList.size()==0)
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
            return getAck(para);
        });

        functionMap.put(c8,(parameters)->{
            if (user==null||!user.isAdmin()|!database.isRegistered(parameters[0]))
                return getError(c8);

            List<Integer> numCourseList=database.getUser(parameters[0]).getCoursesRegistered();
            List<Course> courseList=new LinkedList<Course>();
            for (Integer num:numCourseList){
                courseList.add(database.getCourse(num));
            }
            courseList.sort(Comparator.comparingInt(a->a.getSerialNum()));
            String[] para=new String[2];
            para[0]=String.valueOf(c8);
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
            return getAck(para);
        });

        functionMap.put(c9,(parameters)->{
            if (user==null||user.isAdmin())
                return getError(c9);
            int courseNum=Integer.parseInt(parameters[0]);
            boolean ans = database.registeredToCourse(user,courseNum);
            String[] para=new String[2];
            para[0]=String.valueOf(c9);
            if (ans)
                para[1]="REGISTERED";
            else
                para[1]="NOT REGISTERED";
            return getAck(para);
        });

        functionMap.put(c10,(parameters)->{ //todo:check if need to return error if wasn't registered from the beginning
            if (user==null||user.isAdmin())
                return getError(c10);
            int courseNum=Integer.parseInt(parameters[0]);
            if (!database.registeredToCourse(user,courseNum))
                return getError(c10);
            boolean ans = database.unregisterFromCourse(user,courseNum);
            if (!ans)
                return getError(c10);
            String[] para=new String[1];
            para[0]=String.valueOf(c10);
            return getAck(para);
        });

        functionMap.put(c11,(parameters)->{
            if (user==null||user.isAdmin())
                return getError(c11);
            List<Integer> numCourseList=user.getCoursesRegistered();
            List<Course> courseList=new LinkedList<Course>();
            for (Integer num:numCourseList){
                courseList.add(database.getCourse(num));
            }
            courseList.sort(Comparator.comparingInt(a->a.getSerialNum()));
            String[] para=new String[2];
            para[0]=String.valueOf(c11);
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
            return getAck(para);
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
        array[0]=String.valueOf(opCode);
        short code=13;
        return new Message(code, array);
    }

    private Message getAck(String[] parameters){
        short code = 12;
        return new Message(code,parameters);
    }
}
