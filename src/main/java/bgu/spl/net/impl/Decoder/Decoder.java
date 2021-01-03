package bgu.spl.net.impl.Decoder;

import bgu.spl.net.impl.Message;

/**
 * interface that represents decoders that handles different kinds of message (according to the format).
 */
public interface Decoder {

    public Message decode(byte nextByte ,short code);
}
