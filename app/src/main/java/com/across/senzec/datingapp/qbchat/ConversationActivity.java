package com.across.senzec.datingapp.qbchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.qbchat.adapters.ChatDialogsAdapter;
import com.across.senzec.datingapp.qbchat.common.Common;
import com.across.senzec.datingapp.qbchat.holder.QBChatDialogHolder;
import com.across.senzec.datingapp.qbchat.holder.QBUnreadMessageHolder;
import com.across.senzec.datingapp.qbchat.holder.QBUsersHolder;
import com.across.senzec.datingapp.utils.Constants;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener, QBSystemMessageListener, QBChatDialogMessageListener {

    FloatingActionButton fabAddUser;
    ListView lsvChats;

    private APIInterface apiInterface;
    private ProgressDialog progressDialog;
    private String strUsrUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sender");
        setSupportActionBar(toolbar);

        fabAddUser = (FloatingActionButton) findViewById(R.id.fabAddUser);
        lsvChats = (ListView) findViewById(R.id.lsvChats);

        registerForContextMenu(lsvChats);

        fabAddUser.setOnClickListener(this);
        lsvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog) lsvChats.getAdapter().getItem(position);
                new AlertDialog.Builder(ConversationActivity.this)
                        .setMessage(lsvChats.getAdapter().getItem(position).toString())
                        .show();
                /*Intent intent = new Intent(ConversationActivity.this, ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                startActivity(intent);*/
            }
        });

        createChatSesion();

        UploadChats();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_dialog_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.context_borrar_dialog:
                 DeleteConvert(info.position);
                break;
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_dialog_menu_user:
                showUserProfile();

                break;
            default:

                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UploadChats();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fabAddUser:

                Intent intent = new Intent(ConversationActivity.this, ListingUsersActivity.class);
                startActivity(intent);

                break;

        }

    }

    @Override
    public void processMessage(QBChatMessage qbChatMessage) {
        //Ponemos los dialogos en cache
        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                //Lo ponemos en cache
                QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                ArrayList<QBChatDialog> adapterSource = QBChatDialogHolder.getInstance().getAllChatDialogs();
                ChatDialogsAdapter adapters = new ChatDialogsAdapter(getBaseContext(), adapterSource);
                lsvChats.setAdapter(adapters);
                adapters.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
        Log.e("ERROR", "" + e.getMessage());
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        UploadChats();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }

    private void showUserProfile() {
        Intent intent = new Intent(ConversationActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void UploadChats() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        //Cogemos todos los chats que hay
        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                //Ponemos todos los dialogos en cache
                QBChatDialogHolder.getInstance().putDialogs(qbChatDialogs);

                //Configurar unread
                Set<String> setIds = new HashSet<>();
                for (QBChatDialog chatDialog : qbChatDialogs)
                    setIds.add(chatDialog.getDialogId());

                //Coger el mensaje no leido
                QBRestChatService.getTotalUnreadMessagesCount(setIds, QBUnreadMessageHolder.getInstance().getBundle()).performAsync(new QBEntityCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer, Bundle bundle) {
                        //Guardar en cache
                        QBUnreadMessageHolder.getInstance().setBundle(bundle);

                        //Refrescar la lista
                        ChatDialogsAdapter adapter = new ChatDialogsAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                        lsvChats.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    private void createChatSesion() {
        final ProgressDialog mDialogo = new ProgressDialog(ConversationActivity.this);
        mDialogo.setMessage("Please wait...");
        mDialogo.setCanceledOnTouchOutside(false);
        mDialogo.show();

        //Recogemos el usuario y la contraseña del MainActivity
        String user, password;
        //user = getIntent().getStringExtra("user");
        // password = getIntent().getStringExtra("password");
        user = new SharedPrefClass(ConversationActivity.this).getUsrPwdForConversation();
        password = Constants.PASSWORD;

        /*user = "divakar1594";
        password = "12345678";*/

        // We load all users and cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user, password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                //Cogemos el id del usuario
                qbUser.setId(qbSession.getUserId());

                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }

                //Hacemos el login
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialogo.dismiss();

                        //Añadimos un listener para el mensaje de sistema recibido
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(ConversationActivity.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(ConversationActivity.this);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void  DeleteConvert(int index) {

        final QBChatDialog chatDialog = (QBChatDialog) lsvChats.getAdapter().getItem(index);
        QBRestChatService.deleteDialog(chatDialog.getDialogId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                //Borramos esta conversacion de cache y refrescamos el listview
                QBChatDialogHolder.getInstance().deleteConversacion(chatDialog.getDialogId());
                ChatDialogsAdapter adapter = new ChatDialogsAdapter(getBaseContext(), QBChatDialogHolder.getInstance().getAllChatDialogs());
                lsvChats.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

}
