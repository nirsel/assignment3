package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

public class NumDecoder implements Decoder {
    short parameter;
    int numOfBytes=0;
    byte[] byteArr=new byte[2];
    @Override
    public Message decode(byte nextByte, byte[] bytes, short code) {
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
