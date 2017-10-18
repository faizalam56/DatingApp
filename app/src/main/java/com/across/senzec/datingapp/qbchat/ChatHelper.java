package com.across.senzec.datingapp.qbchat;

import com.quickblox.chat.QBChatService;

/**
 * Created by senzec on 5/8/17.
 */

public class ChatHelper {


    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
    }


    }
