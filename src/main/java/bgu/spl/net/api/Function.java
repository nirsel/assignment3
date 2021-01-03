package bgu.spl.net.api;

import bgu.spl.net.impl.Message;

/**
 * interface that represents a a required function in order to response to each message.
 * the function receives the parameters of the input message and creates the right response message.
 */
public interface Function {
    Message act(String[] parameters);
}
