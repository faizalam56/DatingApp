package com.across.senzec.datingapp.qbchat.holder;

import android.util.Log;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBMessageHolder {

    private static final String TAG = "QBMessageHolder";
    private static QBMessageHolder instance;

    private HashMap<String, ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBMessageHolder getInstance() {
        QBMessageHolder qbMensajeHolder;

        synchronized (QBMessageHolder.class) {
            if (instance == null)
                instance = new QBMessageHolder();
            qbMensajeHolder = instance;
        }
        return qbMensajeHolder;
    }

    private QBMessageHolder() {
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putMessages(String dialogId, ArrayList<QBChatMessage> qbChatMessages) {
        this.qbChatMessageArray.put(dialogId, qbChatMessages);
    }

    public void putMessages(String dialogId, QBChatMessage qbChatMessage) {
        try {
            List<QBChatMessage> lstResultado = (List) this.qbChatMessageArray.get(dialogId);
            lstResultado.add(qbChatMessage);

            ArrayList<QBChatMessage> lstAdded = new ArrayList(lstResultado.size());
            lstAdded.addAll(lstResultado);
            putMessages(dialogId, lstAdded);
        }catch (NullPointerException npe){
            Log.e(TAG, "Error : "+npe, npe);
        }
    }

    public ArrayList<QBChatMessage> getChatMensajesByDialogId(String dialogId) {
        return this.qbChatMessageArray.get(dialogId);
    }

}
