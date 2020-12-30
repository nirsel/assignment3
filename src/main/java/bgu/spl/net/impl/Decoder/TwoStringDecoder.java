package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TwoStringDecoder implements Decoder {
    byte[] byteArray=new byte[1<<10];
    private int len=0;
    private int counter=0;
    String[] parameters=new String[2];
    @Override
    public Message decode(byte nextByte, byte[] bytes, short code) {
        if (nextByte=='\0'){
            if (counter==0){
                parameters[0]=popString();
                counter++;
            }
            else {
                parameters[1]=popString();
                counter=0;
                return new Message(code, parameters);
            }
        }
        decodeNextByte(nextByte);
        return null;
    }

    private void decodeNextByte(byte nextByte){
        if (len>=byteArray.length){
            byteArray= Arrays.copyOf(byteArray,len*2);
        }
        byteArray[len]=nextByte;
        len++;
    }

    private String popString(){
        String result= new String(byteArray,0,len, StandardCharsets.UTF_8);
        len=0;
        return result;
    }
}
