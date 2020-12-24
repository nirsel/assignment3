package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Decoder.*;

import java.util.Arrays;
import java.util.HashMap;

public class MessageEncDecImpl implements MessageEncoderDecoder<Message> {
    private byte[] bytes=new byte[1<<10];
    private short currentCode=0;
    private int len=0;
    private Decoder decoder;
    private HashMap<Short, Decoder> decodeMap;
    private BaseDecoder baseDec=new BaseDecoder();
    private NumDecoder numDec=new NumDecoder();
    private StringDecoder stringDec=new StringDecoder();
    private TwoStringDecoder twoStringDec=new TwoStringDecoder();

    public MessageEncDecImpl() {
        decodeMap=new HashMap<>();
        decodeMap.put((short) 1,twoStringDec);
        decodeMap.put((short) 2,twoStringDec);
        decodeMap.put((short) 3,twoStringDec);
        decodeMap.put((short) 4,baseDec);
        decodeMap.put((short) 5,numDec);
        decodeMap.put((short) 6,numDec);
        decodeMap.put((short) 7,numDec);
        decodeMap.put((short) 8,stringDec);
        decodeMap.put((short) 9,numDec);
        decodeMap.put((short) 10,numDec);
        decodeMap.put((short) 11,baseDec);
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (len<=1){
            pushByte(nextByte);
        }
        else if (len==2){
            currentCode=bytesToShort(bytes);
            decoder=decodeMap.get(currentCode);
        }
        else{
            Message message = decoder.decode(nextByte, bytes,currentCode);
            if (message!=null) {
                currentCode = 0;
            }
            return message;
        }
        return null;
    }

    private void pushByte(byte nextByte){
        if (len>=bytes.length){
            bytes= Arrays.copyOf(bytes,len*2);
        }
        bytes[len]=nextByte;
        len++;
    }

    @Override
    public byte[] encode(Message message) {
        return new byte[0];
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        len++;
        return result;
    }
}


