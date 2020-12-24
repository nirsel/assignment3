package bgu.spl.net.impl;

import bgu.spl.net.api.Function;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.HashMap;

public class MessagingProtocolImpl implements MessagingProtocol<Message> {
    private boolean shouldTerminate=false;
    User user;
    private HashMap<Short, Function> functionMap;
    private Database database=Database.getInstance();
    public MessagingProtocolImpl(){
        short c1=1,c2=2,c3=3,c4=4,c5=5,c6=6,c7=7,c8=8,c9=9,c10=10,c11=11,c12=12,c13=13;
        functionMap.put(c1,(parameters)->{
                if (database.adminRegister(parameters[0],parameters[1]))
                    return new Message //ack message
                else
                    return new Message //error message
        });
        functionMap.put(c2,(parameters)->{
                if (database.studentRegister(parameters[0],parameters[1]))
                    return new Message//ack message
                else
                    return new Message//error message
        });
        functionMap.put(c3,(parameters)->{
            if (!database.isRegistered(parameters[0]))
                return new Message//error message
            else{
                User user=database.login(parameters[0], parameters[1]);
                if (user!=null){
                    this.user=user;
                    return new Message//ack message
                }
                return new Message//error message
            }
        });
        functionMap.put(c4,(parameters)->{
            if (!database.logOut(user))
                return new Message//error message
            shouldTerminate=true;
            return new Message//ack message
        });
        functionMap.put(c5,(parameters)->{

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


    private String getError(){

    }
}
