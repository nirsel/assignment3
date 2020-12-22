package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;

public class MessageEncDecImpl implements MessageEncoderDecoder<Message> {
    private byte[] bytes=new byte[1<<10];
    private int len=0;
    @Override
    public Message decodeNextByte(byte nextByte) {
        if (len==0){

        }
    }

    @Override
    public byte[] encode(Message message) {
        return new byte[0];
    }
}
