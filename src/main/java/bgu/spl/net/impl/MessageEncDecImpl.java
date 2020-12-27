package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Decoder.*;

import java.nio.charset.StandardCharsets;
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
        decoder=null;
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
            len++;
        }
        else{
            Message message = decoder.decode(nextByte, bytes,currentCode);
            if (message!=null) {
                currentCode = 0;
                len=0;
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
        if (message.getOpCode()==13){
            byte[] bytes=new byte[4];
            byte[] codeByte=shortToBytes(message.getOpCode());
            short code=Short.parseShort(message.getParameters()[0]);
            byte[] paraByte=shortToBytes(code);
            bytes[0]=codeByte[0];
            bytes[1]=codeByte[1];
            bytes[2]=paraByte[0];
            bytes[3]=paraByte[1];
            return bytes;
        }
        byte[] bytes=new byte[4];
        byte[] codeByte=shortToBytes(message.getOpCode());
        short code=Short.parseShort(message.getParameters()[0]);
        byte[] paraByte=shortToBytes(code);
        bytes[0]=codeByte[0];
        bytes[1]=codeByte[1];
        bytes[2]=paraByte[0];
        bytes[3]=paraByte[1];
        String[] para=message.getParameters();
        String list="";
        for (int i=0;i<para.length;i++){
            list=list+para[i]+"\0";
        }
        byte[] array=list.getBytes();
        byte[] combined=new byte[4+array.length];
        for (int i=0;i<combined.length;i++){
            if (i<4)
                combined[i]=bytes[i];
            else
                combined[i]=array[i-4];
        }
        return combined;
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        len++;
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}


