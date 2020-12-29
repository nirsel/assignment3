package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.Message;
import bgu.spl.net.impl.MessageEncDecImpl;
import bgu.spl.net.impl.MessagingProtocolImpl;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Reactor;
import bgu.spl.net.srv.TPCServer;

import java.io.IOException;

public class ReactorMain {
    public static void main(String[] args){
        try(Reactor<Message> server=new Reactor<Message>(Integer.parseInt(args[1]),Integer.parseInt(args[1]),()-> new MessagingProtocolImpl(), ()-> new MessageEncDecImpl());)
        {
            Database database=Database.getInstance();
            server.serve();
        }
        catch (IOException e){e.printStackTrace();}
    }
}

