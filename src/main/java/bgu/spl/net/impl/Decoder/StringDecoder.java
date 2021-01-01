package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringDecoder implements Decoder {
    byte[] byteArray=new byte[1<<10];
    private int len=0;
    @Override
    public Message decode(byte nextByte, short code) {
        if (nextByte=='\0'){
            String[] para=new String[1];
            para[0]=popString();
            return new Message(code, para);
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
