package com.idan.md5Decoder.controler;

import com.idan.md5Decoder.beans.Server;
import org.springframework.stereotype.Controller;

@Controller
public class MasterController extends ServerController{
    public MasterController(Server thisServer) {
        super(thisServer);
    }
}
