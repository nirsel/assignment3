package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

public class BaseDecoder implements Decoder{
    /**
     * decodes messages that doesn't send any information other than opCode.
     * @param nextByte - next byte to decode.
     * @param code - opCode of the input message.
     * @return the message needed to be processed.
     */
    @Override
    public Message decode(byte nextByte, short code) {
        return new Message(code,new String[0]);
    }
}
