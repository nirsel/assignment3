package bgu.spl.net.impl;

import java.util.HashMap;

public class Message {
    /**
     * message is an object that holds the relevant information of any transmisson between server
     * and client in this project.
     */
    private short opCode;
    private String[] parameters;


    public Message(short opCode, String[] parameters){
        this.opCode=opCode;
        this.parameters=parameters;
    }

    //getters of the fields

    public String[] getParameters(){
        return parameters;
    }

    public short getOpCode() {
        return opCode;
    }

}
