package com.across.senzec.datingapp.qbchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.manager.App;
import com.across.senzec.datingapp.models.QbidModel;
import com.across.senzec.datingapp.preference.AppPrefs;
import com.across.senzec.datingapp.qbchat.adapters.UserListsAdapter;
import com.across.senzec.datingapp.qbchat.common.Common;
import com.across.senzec.datingapp.qbchat.holder.QBUsersHolder;
import com.across.senzec.datingapp.requestmodel.FindMatchRequest;
import com.across.senzec.datingapp.responsemodel.ChatUserResponse;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ListingUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ListingUsersActivity.class.getSimpleName().toString();
    ListView lsvUsers;
    Button btnCreateChat;

    String mode = "";
    QBChatDialog qbChatDialog;
    List<QBUser> userAdd = new ArrayList<>();
    private ProgressDialog progressDialog;
    AppPrefs prefs;
    APIInterface apiInterface;
    ChatUserResponse chatUserResponse;
    LinearLayout loadingIndicator;
    ImageView iv_back;

    ArrayList<QbidModel> qbidList = new ArrayList<>();
    ArrayList<QbidModel> qbidListResponse = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText("Choose...");

        iv_back = (ImageView) findViewById(R.id.iv_back);
        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);
//        ProgressClass.getProgressInstance().startProgress(ListingUsersActivity.this);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);

        btnCreateChat = (Button) findViewById(R.id.btnCrearChat);
        lsvUsers = (ListView) findViewById(R.id.lsvUsuarios);


        qbidListResponse = callChatUserApi();

        lsvUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btnCreateChat.setOnClickListener(this);

        if (mode == null && qbChatDialog == null)
             collectAllUsers();
        else {
            if (mode.equals(Common.UPDATE_ADD_MODE))
                LoadUsersAvailable();
            else if (mode.equals(Common.UPDATE_REMOVE_MODE))
                loadUsingUsersInGroup();
        }
        iv_back.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnCrearChat:

                if (mode == null) {

                    if (lsvUsers.getCheckedItemPositions().size() == 1)
                        createPrivateChat(lsvUsers.getCheckedItemPositions());
                    else if (lsvUsers.getCheckedItemPositions().size() > 1)
                        createGroupChat(lsvUsers.getCheckedItemPositions());
                    else
                        Toast.makeText(ListingUsersActivity.this, "Select a friend to chat with", Toast.LENGTH_SHORT).show();

                } else if (mode.equals(Common.UPDATE_ADD_MODE) && qbChatDialog != null) {

                    if (userAdd.size() > 0) {

                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int contadorEleccion = lsvUsers.getCount();
                        SparseBooleanArray checkItemPositions = lsvUsers.getCheckedItemPositions();
                        for (int i = 0; i < contadorEleccion; i++) {
                            if (checkItemPositions.get(i)) {
                                QBUser usuario = (QBUser) lsvUsers.getItemAtPosition(i);
                                requestBuilder.addUsers(usuario);
                            }
                        }

                        //LLamamos a los servicios
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(ListingUsersActivity.this, "User successfully added", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(ListingUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } else if (mode.equals(Common.UPDATE_REMOVE_MODE) && qbChatDialog != null) {

                    if (userAdd.size() > 0) {
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int contadorEleccion = lsvUsers.getCount();
                        SparseBooleanArray checkItemPositions = lsvUsers.getCheckedItemPositions();

                        for (int i = 0; i < contadorEleccion; i++) {
                            if (checkItemPositions.get(i)) {
                                QBUser usuario = (QBUser) lsvUsers.getItemAtPosition(i);
                                requestBuilder.removeUsers(usuario);
                            }
                        }

                        //LLamamos a los servicios
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                Toast.makeText(ListingUsersActivity.this, "User successfully ejected", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });

                    }

                }

                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;

        }
    }

    private void loadUsingUsersInGroup() {

        btnCreateChat.setText("Eject User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                List<Integer> occupantsId = qbChatDialog.getOccupants();
                List<QBUser> listUsuariosYaEnElGrupo = QBUsersHolder.getInstance().getUsersById(occupantsId);
                ArrayList<QBUser> usuarios = new ArrayList<QBUser>();
                usuarios.addAll(listUsuariosYaEnElGrupo);

                UserListsAdapter adapter = new UserListsAdapter(getBaseContext(), usuarios);
                lsvUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                userAdd = usuarios;

            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ListingUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void LoadUsersAvailable() {

        btnCreateChat.setText("Add User");
        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                ArrayList<QBUser> listadoUsuarios = QBUsersHolder.getInstance().getAllUsers();

                //Cogemos toda la info de los Occupants
                List<Integer> occupantsId = qbChatDialog.getOccupants();
                List<QBUser> listUsuariosYaEnElGrupo = QBUsersHolder.getInstance().getUsersById(occupantsId);

                //Borrar todos los usuarios que ya estan en el grupo
                for (QBUser user : listUsuariosYaEnElGrupo)
                    listadoUsuarios.remove(user);
                if (listadoUsuarios.size() > 0) {
                    UserListsAdapter adapter = new UserListsAdapter(getBaseContext(), listadoUsuarios);
                    lsvUsers.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    userAdd = listadoUsuarios;
                }

            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ListingUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void  collectAllUsers() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                try {
                    ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<>();
                    for (QBUser user : qbUsers) {
                        if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                            loadingIndicator.setVisibility(View.GONE);
                        for(int i = 0; i<qbidListResponse.size(); i++)
                        {
                            if(user.getId().toString().contains(qbidListResponse.get(i).getQbid()))
                            qbUserWithoutCurrent.add(user);
                           }

                    }

                    //Cargamos la lista con los usuarios
                    UserListsAdapter adapter = new UserListsAdapter(getBaseContext(), qbUserWithoutCurrent);
                    lsvUsers.setAdapter(adapter);

//                    ProgressClass.getProgressInstance().stopProgress();
                    adapter.notifyDataSetChanged();
               //     loadingIndicator.setVisibility(View.GONE);
                }catch (NullPointerException npe)
                {
                    Log.e("TAG","Error : "+npe, npe);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    // CHAT LIKE RESPONSE
    private ArrayList<QbidModel> callChatUserApi(){

        String userToken = new SharedPrefClass(ListingUsersActivity.this).getUserToken();
        String userId = new SharedPrefClass(ListingUsersActivity.this).getLoggedUser();
//        loadingProgressbar();
        final FindMatchRequest findMatchRequest=new FindMatchRequest();
        findMatchRequest.latitude=28.6961;
        findMatchRequest.longitude=77.1527;
        findMatchRequest.username=userId;
        //findMatchRequest.username=prefs.getString(App.Key.ID_LOGGED);


        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.findMatch(userId,userToken,findMatchRequest).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if (resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        /*if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();*/

                    } else {
                       /* if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        try {
                            ChatUserResponse chatUserResponse = gson.fromJson(resource.toString(), ChatUserResponse.class);
                            if(chatUserResponse != null) {
                                for (int i = 0; i < chatUserResponse.response.size(); i++) {
                                    String strQBID = chatUserResponse.response.get(i).user.qbid.toString();
                                    System.out.println(chatUserResponse.response.get(i).user.qbid.toString());
                                    QbidModel qbidModel = new QbidModel(strQBID);
                                    qbidList.add(qbidModel);
                                    // qbidList.add(strQBID);
                                }
                            }
                        }catch (NullPointerException npe)
                        {
                            Log.e(TAG, "Error : "+npe, npe);
                        }

                    }
                }else{
                    /*if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();*/

                    showErrorDialog("something went wrong from server!");
                }


            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();

                /*if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();*/
            //    showErrorDialog(t.getMessage());
            //    showErrorDialog(t.getMessage());
            }
        });

        return qbidList;
    }

    private void showErrorDialog(String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle("ALERT");
        dialog.setMessage(message);
        dialog.setIcon(R.mipmap.logo_icon);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    public void loadingProgressbar(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }


    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialogo = new ProgressDialog(ListingUsersActivity.this);
        mDialogo.setMessage("Espere...");
        mDialogo.setCanceledOnTouchOutside(false);
        mDialogo.show();

        int contadorEleccion = lsvUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();

        //Recorremos todos los usuarios, si un usuario ha sido seleccionado creamos un dialogo con dicho usuario
        for (int i = 0; i < contadorEleccion; i++) {
            if (checkedItemPositions.get(i)) {
                QBUser usuario = (QBUser) lsvUsers.getItemAtPosition(i);
                occupantIdsList.add(usuario.getId());
            }
        }

        //Creamos chat dialogo
        QBChatDialog dialogo = new QBChatDialog();
        dialogo.setName(Common.createChatDialogName(occupantIdsList));
        dialogo.setType(QBDialogType.GROUP);
        dialogo.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialogo).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialogo.dismiss();
                Toast.makeText(ListingUsersActivity.this, "Dialog created correctly", Toast.LENGTH_SHORT).show();

                //Send system message to recipient id user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());

                //Cogemos todos los ids de los ocupantes del grupo
                for (int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
                    qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                    try {

                        qbSystemMessagesManager.sendSystemMessage(qbChatMessage);

                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialogo = new ProgressDialog(ListingUsersActivity.this);
        mDialogo.setMessage("Espere...");
        mDialogo.setCanceledOnTouchOutside(false);
        mDialogo.show();

        int contadorEleccion = lsvUsers.getCount();

        //Recorremos todos los usuarios, si un usuario ha sido seleccionado creamos un dialogo con dicho usuario
        for (int i = 0; i < contadorEleccion; i++) {
            if (checkedItemPositions.get(i)) {
                final QBUser usuario = (QBUser) lsvUsers.getItemAtPosition(i);

                QBChatDialog dialogo = DialogUtils.buildPrivateDialog(usuario.getId());

                QBRestChatService.createChatDialog(dialogo).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialogo.dismiss();
                        Toast.makeText(ListingUsersActivity.this, "Private chat dialog created correctly", Toast.LENGTH_SHORT).show();

                        //Send system message to recipient id user
                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setRecipientId(usuario.getId());
                        qbChatMessage.setBody(qbChatDialog.getDialogId());
                        try {

                            qbSystemMessagesManager.sendSystemMessage(qbChatMessage);

                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                });
            }
        }
    }

    public void onBackPressed(View view)
    {
        startActivity(new Intent(ListingUsersActivity.this, ConversationActivity.class));
        Toast.makeText(ListingUsersActivity.this, "attachments", Toast.LENGTH_LONG).show();
    }

}
