package com.across.senzec.datingapp.qbchat.holder;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class QBUsersHolder {
    private static QBUsersHolder instance;

    private SparseArray<QBUser> qbUserSparseArray;

    public static synchronized QBUsersHolder getInstance() {
        if (instance == null)
            instance = new QBUsersHolder();
        return instance;
    }

    private QBUsersHolder() {
        qbUserSparseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> usuarios) {
        for (QBUser user : usuarios)
            putUser(user);
    }

    public void putUser(QBUser usuario) {
        qbUserSparseArray.put(usuario.getId(), usuario);
    }

    public QBUser getUserById(int id) {
        return qbUserSparseArray.get(id);
    }

    public List<QBUser> getUsersById(List<Integer> ids) {
        List<QBUser> qbUsuario = new ArrayList<>();
        for (Integer id : ids) {
            QBUser usuario = getUserById(id);
            if (usuario != null)
                qbUsuario.add(usuario);
        }
        return qbUsuario;
    }

    public ArrayList<QBUser> getAllUsers() {

        ArrayList<QBUser> resultado = new ArrayList<>();

        for (int i = 0; i < qbUserSparseArray.size(); i++) {
            resultado.add(qbUserSparseArray.valueAt(i));
        }

        return resultado;

    }

}
