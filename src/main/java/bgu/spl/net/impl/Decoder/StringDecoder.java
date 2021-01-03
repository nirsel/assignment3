package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringDecoder implements Decoder {
    /**
     * decoder that handles message which has one parameter of string type other than the opCode.
     */
    byte[] byteArray=new byte[1<<10];
    private int len=0;
    @Override
    /**
     *  receives the next byte to decode and the opCode of the input message
     *  returns the complete decoded message if decoding is complete, null otherwise.
     */
    public Message decode(byte nextByte, short code) {
        if (nextByte=='\0'){  // \0 byte marks the end of the message.
            String[] para=new String[1];
            para[0]=popString();
            return new Message(code, para);
        }
        decodeNextByte(nextByte);
        return null;

    }

    /**
     * adds the next byte to decode to the byte array. creates new byte array with doubled size if needed.
     * @param nextByte - next byte to decode.
     */
    private void decodeNextByte(byte nextByte){
        if (len>=byteArray.length){
            byteArray= Arrays.copyOf(byteArray,len*2);
        }
        byteArray[len]=nextByte;
        len++;
    }

    /**
     *
     * @return the decoded string from the bytes array.
     */
    private String popString(){
        String result= new String(byteArray,0,len, StandardCharsets.UTF_8);
        len=0;
        return result;
    }
}
