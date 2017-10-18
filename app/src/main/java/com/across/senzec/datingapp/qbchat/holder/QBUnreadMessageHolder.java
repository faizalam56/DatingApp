package com.across.senzec.datingapp.qbchat.holder;

import android.os.Bundle;

public class QBUnreadMessageHolder {
    private static QBUnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance() {
        QBUnreadMessageHolder qbUnreadMensajeHolder;
        synchronized (QBUnreadMessageHolder.class) {
            if (instance == null)
                instance = new QBUnreadMessageHolder();
            qbUnreadMensajeHolder = instance;
        }
        return qbUnreadMensajeHolder;
    }

    private QBUnreadMessageHolder() {
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return this.bundle;
    }

    public int getUnreadMensajeByDialogId(String id) {
        return this.bundle.getInt(id);
    }

}
