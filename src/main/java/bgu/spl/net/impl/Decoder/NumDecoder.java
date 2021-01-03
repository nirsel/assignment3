package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

public class NumDecoder implements Decoder {
    /**
     * decoder that handles message which holds a short number parameter besides the opCode.
     */
    short parameter;
    int numOfBytes=0;
    byte[] byteArr=new byte[2];
    @Override
    /**
     * receives the next byte to decode and the opCode of the input message
     * returns the complete decoded message if decoding is complete, null otherwise.
     */
    public Message decode(byte nextByte, short code) {
        byteArr[numOfBytes]=nextByte;
        numOfBytes++;
        if (numOfBytes==2){
            parameter=bytesToShort(byteArr);
            String[] para=new String[1];
            para[0]=String.valueOf(parameter);
            numOfBytes=0;
            return new Message(code, para);
        }
        return null;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
}
