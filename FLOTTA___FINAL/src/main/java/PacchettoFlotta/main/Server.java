package PacchettoFlotta.main;

import PacchettoFlotta.server.*;

public class Server {
    public static void main(String[] args){
        Car1CoapProcess.main(args);
        Car2CoapProcess.main(args);
        Car3CoapProcess.main(args);
        Car4CoapProcess.main(args);
        SwitchCoapProcess.main(args);
    }
}
