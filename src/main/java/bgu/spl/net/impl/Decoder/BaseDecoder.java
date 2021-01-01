package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

public class BaseDecoder implements Decoder{

    @Override
    public Message decode(byte nextByte, short code) {
        return new Message(code,new String[0]);
    }
}
