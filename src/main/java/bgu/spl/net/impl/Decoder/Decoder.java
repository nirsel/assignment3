package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

public interface Decoder {

    public Message decode(byte nextByte, byte[] bytes,short code);
}
