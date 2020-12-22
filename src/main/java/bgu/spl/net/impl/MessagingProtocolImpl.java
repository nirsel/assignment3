package bgu.spl.net.impl;

import bgu.spl.net.api.Function;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Database;

import java.util.HashMap;

public class MessagingProtocolImpl implements MessagingProtocol<Message> {
    private boolean shouldTerminate=false;
    private HashMap<String, Short> opMap;
    private HashMap<Short, Function> functionMap;
    private Database database=Database.getInstance();
    public MessagingProtocolImpl(){
        short c1=1,c2=2,c3=3,c4=4,c5=5,c6=6,c7=7,c8=8,c9=9,c10=10,c11=11,c12=12,c13=13;
        functionMap.put(c1,(parameters)->{
                if (registerCheck(parameters[0])){
                    return new Message("ERROR "+c1);
                }
                database.adminRegister(parameters[0],parameters[1]);


        })


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

    private boolean registerCheck(String username){
        return database.isRegistered(username);

    }

    private String getError(){

    }
}
