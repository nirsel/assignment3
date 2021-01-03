package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Decoder.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * implementation of the MessageEncoderDecoder that handles Message type.
 */
public class MessageEncDecImpl implements MessageEncoderDecoder<Message> {
    private byte[] bytes=new byte[1<<10];
    private short currentCode=0;
    private int len=0;
    private Decoder decoder;
    private HashMap<Short, Decoder> decodeMap;
    private BaseDecoder baseDec=new BaseDecoder(); //holds an instance of any type of decoder in order to handle any message type
    private NumDecoder numDec=new NumDecoder();
    private StringDecoder stringDec=new StringDecoder();
    private TwoStringDecoder twoStringDec=new TwoStringDecoder();

    /**
     * constructs map that links any opCode that can be received from the client to the relevant decoder
     * according to the format of the message.
     */
    public MessageEncDecImpl() {
        short c1=1, c2=2, c3=3, c4=4, c5=5, c6=6, c7=7, c8=8, c9=9, c10=10, c11=11;
        decoder=null;
        decodeMap=new HashMap<>();
        decodeMap.put(c1,twoStringDec);
        decodeMap.put(c2,twoStringDec);
        decodeMap.put(c3,twoStringDec);
        decodeMap.put(c4,baseDec);
        decodeMap.put(c5,numDec);
        decodeMap.put(c6,numDec);
        decodeMap.put(c7,numDec);
        decodeMap.put(c8,stringDec);
        decodeMap.put(c9,numDec);
        decodeMap.put(c10,numDec);
        decodeMap.put(c11,baseDec);
    }

    /**
     *
     * @param nextByte the next byte to consider for the currently decoded
     * decodes the first two bytes in order to get the opCode of the message, then sends the next bytes
     * to the relevant decoder from the decodeMap to decode the next bytes.
     * @return message - the fully decoded message from the client.
     */
    @Override
    public Message decodeNextByte(byte nextByte) {
        if (len<=1){
            pushByte(nextByte);
            if (len==2){
                currentCode=bytesToShort(bytes);
                decoder=decodeMap.get(currentCode);
                if (currentCode==4|currentCode==11) { //logout or my courses messages has no other parameters
                    len=0;
                    return new Message(currentCode, new String[0]);
                }
            }
            return null;
        }
        else{
            Message message = decoder.decode(nextByte, currentCode);
            if (message!=null) {
                currentCode = 0; //reset the fields of the EncoderDecoder in order to be ready for the next message
                len=0;
            }
            return message;
        }
    }

    private void pushByte(byte nextByte){
        if (len>=bytes.length){
            bytes= Arrays.copyOf(bytes,len*2);
        }
        bytes[len]=nextByte;
        len++;
    }

    /**
     * @param message the message to encode
     * @return the bytes array to send to the clieny
     */
    @Override
    public byte[] encode(Message message) {
        if (message.getOpCode()==13){ //if it is an error message
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
        byte[] bytes=new byte[4]; //if it is an ack message
        byte[] codeByte=shortToBytes(message.getOpCode());
        short code=Short.parseShort(message.getParameters()[0]);
        byte[] paraByte=shortToBytes(code);
        bytes[0]=codeByte[0];
        bytes[1]=codeByte[1];
        bytes[2]=paraByte[0];
        bytes[3]=paraByte[1];
        String[] para=message.getParameters(); //gets the parameters for the message
        String list="\n";
        for (int i=1;i<para.length;i++){
            list=list+para[i]+'\n';
        }
        if (list.length()>1) { //if there are parameters to be encoded other than the input message code
            list = list.substring(0, list.length() - 1) + '\0'; //removes unwanted \n char from the end
            byte[] array = list.getBytes();
            byte[] combined = new byte[4 + array.length];
            for (int i = 0; i < combined.length; i++) {
                if (i < 4)
                    combined[i] = bytes[i];
                else
                    combined[i] = array[i - 4];
            }
            return combined;
        }
        return bytes;

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


