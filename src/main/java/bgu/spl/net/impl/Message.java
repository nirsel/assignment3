package bgu.spl.net.impl;

import java.util.HashMap;

public class Message {
    private short opCode;
    private String[] parameters;

    public Message(String msg){
        String[] splitMsg=msg.split(" ");
        opCode=setOpCode(splitMsg[0]);
        parameters=new String[splitMsg.length-1];
        for (int i=0;i<parameters.length;i++)
            parameters[i]=splitMsg[i+1];
        byte[] code=splitMsg[0].getBytes();
    }

    public Message(short opCode, String[] parameters){
        this.opCode=opCode;
        this.parameters=parameters;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public String[] getParameters(){
        return parameters;
    }

    public short getOpCode() {
        return opCode;
    }

    private short setOpCode(String msg){
        switch (msg){
            case "ADMINREG":
                opCode=1;
            case "STUDENTREG":
                opCode=2;
            case "LOGIN":
                opCode=3;
            case "LOGOUT":
                opCode=4;
            case "COURSEREG":
                opCode=5;
            case "KDAMCHECK":
                opCode=6;
            case "COURSESTAT":
                opCode=7;
            case "STUDENTSTAT":
                opCode=8;
            case "ISREGISTERED":
                opCode=9;
            case "UNREGISTER":
                opCode=10;
            case "MYCOURSES":
                opCode=11;
            case "ACK":
                opCode=12;
            case "ERROR":
                opCode=13;
        }
        return opCode;
    }

}
