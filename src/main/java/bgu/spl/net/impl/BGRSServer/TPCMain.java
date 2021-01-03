package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.Message;
import bgu.spl.net.impl.MessageEncDecImpl;
import bgu.spl.net.impl.MessagingProtocolImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.TPCServer;

import java.io.IOException;

public class TPCMain {

    public static void main(String[] args){
        try(TPCServer<Message> server=new TPCServer<Message>(7777,()-> new MessagingProtocolImpl(), ()-> new MessageEncDecImpl());)
        {
            server.serve();
        }
        catch (IOException e){e.printStackTrace();}
    }
}
