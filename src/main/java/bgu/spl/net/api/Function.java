package bgu.spl.net.api;

import bgu.spl.net.impl.Message;

public interface Function {
    public Message act(String[] parameters);
}
