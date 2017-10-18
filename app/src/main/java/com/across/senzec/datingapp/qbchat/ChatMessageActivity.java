package com.across.senzec.datingapp.qbchat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.qbchat.adapters.MessageChatAdapter;
import com.across.senzec.datingapp.qbchat.common.Common;
import com.across.senzec.datingapp.qbchat.holder.QBMessageHolder;
import com.across.senzec.datingapp.utils.Constants;
import com.across.senzec.datingapp.utils.ProgressClass;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bhargavms.dotloader.DotLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ChatMessageActivity extends AppCompatActivity implements View.OnClickListener, QBChatDialogMessageListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = ChatMessageActivity.class.getSimpleName();
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    QBChatDialog qbChatDialog;
    ListView lsvListMessage;
    EditText edtMessage , edtNombreGrupo;
    ImageButton imgbSend, imgbAttachment;
    MessageChatAdapter adapter;
    Toolbar toolbar;
    TextView tv_title;
    //Update user online
    ImageView imgvContentOnline, imgAvatar;
    TextView txvContentOnline;
    //Variables for updating / deleting messages
    int contextMenuIndexClicked = -1;
    boolean isEditMode = false;
    QBChatMessage editMessage;
    //Variables typing ...
    DotLoader dotLoader;
    //ATTACHMENT
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int READ_REQUEST_CODE = 42;

    private Uri fileUri; // file url to store image/video
    File filePhoto;
    Dialog dialog;
    LinearLayout loadingIndicator;
    private LinearLayout attachmentPreviewContainerLayout;
    private TextView progressText;
    private ImageView progressImage, backButton;
    private static int count = 1;
    private static final int defaultListSize = 8;
    private View footer;
    TextView refreshBtn;
    ArrayList<QBChatMessage> qbCustomList;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        font = Typeface.createFromAsset( getAssets(), "fonts/fontawesome-webfont.ttf" );


        loadingIndicator = (LinearLayout) findViewById(R.id.lodingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        startView();
        startConversations();
        collectMessage(defaultListSize);

        imgbSend.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);

        //Add context menu
        registerForContextMenu(lsvListMessage);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

    }
    private void  startView() {
        tv_title=(TextView) findViewById(R.id.tv_title);
        findViewById(R.id.frame_find_match).setVisibility(View.GONE);

        lsvListMessage = (ListView) findViewById(R.id.lsvListaMensajes);
        imgbSend = (ImageButton) findViewById(R.id.imgbEnviar);
        imgbAttachment = (ImageButton) findViewById(R.id.imgbAttachment);
        edtMessage = (EditText) findViewById(R.id.edtmessage);
        imgvContentOnline = (ImageView) findViewById(R.id.imgvContadorOnline);
        imgAvatar = (ImageView) findViewById(R.id.imgvDialogAvatar);
        txvContentOnline = (TextView) findViewById(R.id.txvContadorOnline);
        dotLoader = (DotLoader) findViewById(R.id.dot_loader);
        progressImage = (ImageView)findViewById(R.id.idProgressImage);
        progressText = (TextView)findViewById(R.id.idProgressText);
        backButton = (ImageView)findViewById(R.id.iv_back) ;

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    qbChatDialog.sendIsTypingNotification();
                } catch (XMPPException | SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }catch (NullPointerException npe){
                    Log.e("TAG", "#Error : "+npe, npe);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {

                    qbChatDialog.sendStopTypingNotification();

                } catch (XMPPException | SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }catch (NullPointerException npe){
                    Log.e(TAG, "Error : "+npe, npe);
                }

            }
        });

        imgbAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(ChatMessageActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(ChatMessageActivity.this);
                inflater.inflate(R.menu.file_menu, popup.getMenu());
                popup.show();

                // Hit and Send attachment
                //     funcAttachment();
            }
        });
        lsvListMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentImageURL = lsvListMessage.getItemAtPosition(position).toString();
                //   Toast.makeText(ChatMessageActivity.this, "Output : "+currentImageURL, Toast.LENGTH_LONG).show();
                showDialog(currentImageURL);
            }
        });

        attachmentPreviewContainerLayout = (LinearLayout) findViewById(R.id.layout_attachment_preview_container);


        // SCROLL
        //     footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progress_load_more, null, false);
        //    lsvListMessage.addHeaderView(footer);
        //lsvListMessage.addFooterView(footer);


        // Implementing scroll refresh:-
        refreshBtn = new TextView(ChatMessageActivity.this);
        refreshBtn.setTypeface(font);
        refreshBtn.setGravity(Gravity.CENTER_HORIZONTAL);
        refreshBtn.setText(R.string.reload_chat);
        refreshBtn.setTextSize(16       );
        refreshBtn.setTextColor(getResources().getColor(R.color.colorPink));
        refreshBtn.setBackgroundColor(getResources().getColor(R.color.grey_300));
        refreshBtn.setPadding(0, 12, 0, 12);
        //refreshBtn.setText("Load Previous...");
        lsvListMessage.addHeaderView(refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingIndicator.setVisibility(View.VISIBLE);
                count++;
                collectMessage(count*defaultListSize);
            }
        });
        backButton.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (qbChatDialog.getType() == QBDialogType.GROUP || qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP)
            getMenuInflater().inflate(R.menu.mensaje_chat_grupo_menu, menu);

        return true;

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.mensaje_chat_context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Cogemos el index context menu click
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        contextMenuIndexClicked = info.position;

        switch (item.getItemId()) {
            case R.id.mensaje_chat_actualizar:
                toUpdateMessage();

                break;
            case R.id.mensaje_chat_borrar:
                deleteMessage();

                break;
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chat_group_edit_name:
                editNameGroup();

                break;
            case R.id.chat_group_add_user:
                addUser();

                break;
            case R.id.chat_group_remove_user:
                deleteUser();

                break;
        }

        return true;

    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        //Cached Message

        QBMessageHolder.getInstance().putMessages(qbChatMessage.getDialogId(), qbChatMessage);

        ArrayList<QBChatMessage> messageList = QBMessageHolder.getInstance().getChatMensajesByDialogId(qbChatMessage.getDialogId());

        qbChatMessage.setMarkable(true);
        adapter = new MessageChatAdapter(getBaseContext(), messageList);
        lsvListMessage.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("ERROR", e.getMessage());
    }
    private void deleteUser() {

        Intent intent = new Intent(this, ListingUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE, Common.UPDATE_REMOVE_MODE);
        startActivity(intent);

    }
    private void addUser() {

        Intent intent = new Intent(this, ListingUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA, qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE, Common.UPDATE_ADD_MODE);
        startActivity(intent);

    }
    private void editNameGroup() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_editar_grupo_layout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(view);
        edtNombreGrupo = (EditText) view.findViewById(R.id.edtNombreGrupo);

        //Set Dialog Message
        alertDialogBuilder.setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Establecemos nuevo nombre
                        qbChatDialog.setName(edtNombreGrupo.getText().toString());

                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                        QBRestChatService.updateGroupChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                //     Toast.makeText(ChatMessageActivity.this, "Updated group name", Toast.LENGTH_SHORT).show();
                                //      toolbar.setTitle(qbChatDialog.getName());
                                tv_title.setText(qbChatDialog.getName());
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                //       Toast.makeText(ChatMessageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });

        //Creamos alerta
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    private void deleteMessage() {
        final ProgressDialog borrarDialog = new ProgressDialog(ChatMessageActivity.this);
        borrarDialog.setMessage("Erasing...");
        borrarDialog.show();

        //Ponemos el mensaje para el edittext
        editMessage = QBMessageHolder.getInstance().getChatMensajesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);

        QBRestChatService.deleteMessage(editMessage.getId(), false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                collectMessage(defaultListSize);
                borrarDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ChatMessageActivity.this, "You do not have permission to delete this message", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void toUpdateMessage() {

        //Ponemos el mensaje para el edittext
        editMessage = QBMessageHolder.getInstance().getChatMensajesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        edtMessage .setText(editMessage.getBody());
        isEditMode = true;

    }

    private void collectMessage(final int listSize) {

        qbCustomList = new ArrayList<>();

        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();

        messageGetBuilder.setLimit(500);
        messageGetBuilder.sortAsc("date_sent");


        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(final ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    //cache messages
                    // if( qbChatMessages.size() <= listSize)
                    Collections.reverse(qbChatMessages);
                    System.out.println(qbChatMessages);
                    for(int i = 0; i<listSize; i++)
                    {
                        if(i<qbChatMessages.size())
                        { qbCustomList.add(qbChatMessages.get(i));

                        }else  if(i == qbChatMessages.size())
                        {
                            //  refreshBtn.setVisibility(View.GONE);
                            lsvListMessage.removeHeaderView(refreshBtn);
                        }
                        else {
                            break;
                        }
                    }
                    System.out.println(qbCustomList);
                    Collections.reverse(qbCustomList);
                    loadingIndicator.setVisibility(View.GONE);

                    //QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);
                    //adapter = new MessageChatAdapter(getBaseContext(), qbChatMessages);
                    QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbCustomList);
                    adapter = new MessageChatAdapter(getBaseContext(), qbCustomList);
                       /* adapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                            @Override
                            public void downloadMore() {
                                    count++;
                                    collectMessage(count*defaultListSize);
                            }
                        });*/

                    lsvListMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // save index and top position
                    if(count > 1) {
                        int index = lsvListMessage.getFirstVisiblePosition();
                        View v = lsvListMessage.getChildAt(0);
                        int top = (v == null) ? 0 : (v.getTop() - lsvListMessage.getPaddingTop());
                        // restore index and position
                        lsvListMessage.setSelectionFromTop(index, top);
                    }
                    loadingIndicator.setVisibility(View.GONE);

                   /* if(listSize == 10)
                    {
                        System.out.println(qbChatMessages);
                        System.out.println(qbCustomList);
                    }*/
                    //     }
                }

                @Override
                public void onError(QBResponseException e) {
                    ProgressClass.getProgressInstance().stopProgress();
                    loadingIndicator.setVisibility(View.GONE);
                }
            });
        }

    }
    private void collectBackupMessage(final int listSize) {

        qbCustomList = new ArrayList<>();

        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();

        messageGetBuilder.setLimit(500);


        if (qbChatDialog != null) {
            QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(final ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    //cache messages
                    // if( qbChatMessages.size() <= listSize)
                    Collections.reverse(qbChatMessages);
                    System.out.println(qbChatMessages);
                    for(int i = 0; i<listSize; i++)
                    {
                        if(i<qbChatMessages.size())
                        { qbCustomList.add(qbChatMessages.get(i));

                        }else  if(i == qbChatMessages.size())
                        {
                            //  refreshBtn.setVisibility(View.GONE);
                            lsvListMessage.removeHeaderView(refreshBtn);
                        }
                        else {
                            break;
                        }
                    }
                    System.out.println(qbCustomList);
                    Collections.reverse(qbCustomList);
                    loadingIndicator.setVisibility(View.GONE);

                    //QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbChatMessages);
                    //adapter = new MessageChatAdapter(getBaseContext(), qbChatMessages);
                    QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), qbCustomList);
                    adapter = new MessageChatAdapter(getBaseContext(), qbCustomList);
                       /* adapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                            @Override
                            public void downloadMore() {
                                    count++;
                                    collectMessage(count*defaultListSize);
                            }
                        });*/

                    lsvListMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // save index and top position
                    int index = lsvListMessage.getFirstVisiblePosition();
                    View v = lsvListMessage.getChildAt(0);
                    int top = (v == null) ? 0 : (v.getTop() - lsvListMessage.getPaddingTop());
                    // restore index and position
                    lsvListMessage.setSelectionFromTop(index, top);
                    loadingIndicator.setVisibility(View.GONE);

                    if(listSize == 10)
                    {
                        System.out.println(qbChatMessages);
                        System.out.println(qbCustomList);
                    }
                    //     }
                }

                @Override
                public void onError(QBResponseException e) {
                    ProgressClass.getProgressInstance().stopProgress();
                    loadingIndicator.setVisibility(View.GONE);
                }
            });
        }

    }

    private void  startConversations() {

        if (qbChatDialog.getPhoto() == null && !qbChatDialog.getPhoto().equals("null")) {
            QBContent.getFile(Integer.parseInt(qbChatDialog.getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                @Override
                public void onSuccess(QBFile qbFile, Bundle bundle) {
                    String fileURL = qbFile.getPublicUrl();
                    Picasso.with(getBaseContext())
                            .load(fileURL)
                            .resize(50, 50)
                            .centerCrop()
                            .into(imgAvatar);
                }
                @Override
                public void onError(QBResponseException e) {
                    Log.e("ERROR_IMAGE", "" + e.getMessage());
                }
            });
        }

        try {
            qbChatDialog.initForChat(QBChatService.getInstance());
        }catch (IllegalArgumentException iae)
        {
            Log.e(TAG, "Error : "+iae, iae);
        }catch (NullPointerException npe)
        {
            Log.e(TAG, "Error : "+npe, npe);
        }
        try{
            //Register the listener of the arriving message
            QBIncomingMessagesManager incomingMensaje = QBChatService.getInstance().getIncomingMessagesManager();
            incomingMensaje.addDialogMessageListener(new QBChatDialogMessageListener() {
                @Override
                public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

                }

                @Override
                public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

                }
            });
        }catch (NullPointerException npe)
        {
            Log.e(TAG, "Error : "+npe, npe);
        }
        //Add typing listener
        toShowWritingConversation(qbChatDialog);
        if (qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP) {
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("ERROR", e.getMessage());
                }
            });

        }

        QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogId, QBPresence qbPresence) {

                if (Objects.equals(dialogId, qbChatDialog.getDialogId())) {
                    QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                            //We take the user online
                            try {

                                Collection<Integer> listaOnline = qbChatDialog.getOnlineUsers();

                                TextDrawable.IBuilder builder = TextDrawable.builder()
                                        .beginConfig()
                                        .withBorder(4)
                                        .endConfig()
                                        .round();

                                TextDrawable online = builder.build("", Color.RED);
                                imgvContentOnline.setImageDrawable(online);

                                txvContentOnline.setText(String.format("%d/%d online", listaOnline.size(), qbChatDialog.getOccupants().size()));
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(QBResponseException e) {
                        }
                    });
                }

            }
        };
        try {
            qbChatDialog.addParticipantListener(participantListener);

            qbChatDialog.addMessageListener(this);
            tv_title.setText(qbChatDialog.getName());
        }catch (NullPointerException npe){
            Log.e("TAG", "#Error : "+npe, npe);
        }
    }
    /**
     * Listener para cuando alguien esta escribiendo
     * @param qbChatDialog
     */
    private void toShowWritingConversation(QBChatDialog qbChatDialog) {

        QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer integer) {

                if (dotLoader.getVisibility() != View.VISIBLE)
                    dotLoader.setVisibility(View.VISIBLE);

            }

            @Override
            public void processUserStopTyping(String dialogId, Integer integer) {

                if (dotLoader.getVisibility() != View.INVISIBLE)
                    dotLoader.setVisibility(View.INVISIBLE);

            }
        };
        try {
            qbChatDialog.addIsTypingListener(typingListener);
        }catch(NullPointerException npe){
            Log.e("Chat", "Err : "+npe, npe);
        }

    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.item1:
                //        Toast.makeText(ChatMessageActivity.this, "Item-1", Toast.LENGTH_LONG).show();
                captureImage();
                break;
            case R.id.item2:
                //      Toast.makeText(ChatMessageActivity.this, "Item-2", Toast.LENGTH_LONG).show();
                showStorage();
                break;
            case R.id.item3:
                //     Toast.makeText(ChatMessageActivity.this, "Item-3", Toast.LENGTH_LONG).show();
                break;

        }
        return false;
    }
    // MESSAGE
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgbEnviar:
                if (!isEditMode) {

                    if(edtMessage .getText().toString().trim().length() > 0){
                        QBChatMessage messageChat = new QBChatMessage();
                        messageChat.setBody(edtMessage.getText().toString().trim());
                        messageChat.setSenderId(QBChatService.getInstance().getUser().getId());
                        messageChat.setSaveToHistory(true);
                        messageChat.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                        messageChat.setDateSent(System.currentTimeMillis() / 1000);
                        messageChat.setMarkable(true);
                        try {
                            qbChatDialog.sendMessage(messageChat);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            //Cached Message
                            QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), messageChat);
                            ArrayList<QBChatMessage> mensajes = QBMessageHolder.getInstance().getChatMensajesByDialogId(messageChat.getDialogId());

                            //set the adapter from the list
                            adapter = new MessageChatAdapter(getBaseContext(), mensajes);
                            lsvListMessage.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        edtMessage.setText("");
                        edtMessage.setFocusable(true);
                    }
                } else {
                    final ProgressDialog actualizarDialog = new ProgressDialog(ChatMessageActivity.this);
                    actualizarDialog.setMessage("Updating...");
                    actualizarDialog.show();

                    QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                    messageUpdateBuilder.updateText(edtMessage .getText().toString()).markDelivered().markRead();
                    QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder).performAsync(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            collectMessage(defaultListSize);
                            isEditMode = false;
                            actualizarDialog.dismiss();
                            edtMessage .setText("");
                            edtMessage .setFocusable(true);
                        }
                        @Override
                        public void onError(QBResponseException e) {
                            Log.e("ERROR", e.getMessage());
                        }
                    });
                }
                break;
            case R.id.imgvDialogAvatar:
                Intent seleccionarImagen = new Intent();
                seleccionarImagen.setType("image*//*");
                seleccionarImagen.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(seleccionarImagen, "Select Image"), Common.SELECCIONAR_FOTO);

                break;
            case R.id.iv_back:
                super.onBackPressed();
                break;
        }
    }

    // *****************FOR FILEPATH**********************
    public void funcAttachment(Uri imagePath)
    {
        //  String imagePath = Environment.getExternalStorageDirectory() + "/Download/images.jpg";
        if( new SharedPrefClass(ChatMessageActivity.this).getStorageType().equalsIgnoreCase(Constants.STORAGE_CAMERA))
        {  filePhoto = new File(imagePath.getPath());}
        else if( new SharedPrefClass(ChatMessageActivity.this).getStorageType().equalsIgnoreCase(Constants.STORAGE_INTERNAL) )
        {
            filePhoto = new File(new QbConfig().getPath(ChatMessageActivity.this, imagePath));
        }
        Boolean fileIsPublic = false;
        String[] tags = new String[]{"tag_1", "tag_2"};
        QBContent.uploadFileTask(filePhoto, fileIsPublic, String.valueOf(tags), new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int i) {
                attachmentPreviewContainerLayout.setVisibility(View.VISIBLE);
                Bitmap myBitmap = BitmapFactory.decodeFile(filePhoto.getAbsolutePath());
                progressImage.setImageBitmap(myBitmap);
                //    };
                progressText.setText(String.valueOf(i)+"%");
            }
        }).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                // create a message
                //      Toast.makeText(ChatMessageActivity.this, "Image find", Toast.LENGTH_LONG).show();
                attachmentPreviewContainerLayout.setVisibility(View.GONE);
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setSaveToHistory(true); // Save a message to history

                chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                chatMessage.setMarkable(true);

                // attach a photo
                QBAttachment attachment = new QBAttachment("photo");
                attachment.setId(qbFile.getId().toString());
                attachment.setUrl(qbFile.getPrivateUrl());
                chatMessage.addAttachment(attachment);
                try {
                    qbChatDialog.sendMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                    //Message in cache
                    QBMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(), chatMessage);
                    ArrayList<QBChatMessage> messagesList = QBMessageHolder.getInstance().getChatMensajesByDialogId(chatMessage.getDialogId());
                    //set the adapter from the list
                    adapter = new MessageChatAdapter(getBaseContext(), messagesList);
                    lsvListMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                // error
            }

        });
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        new SharedPrefClass(ChatMessageActivity.this).setStorageType(Constants.STORAGE_CAMERA);
    }
    /**
     * Checking device has camera hardware or not
     * */
    private  void showStorage()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
        new SharedPrefClass(ChatMessageActivity.this).setStorageType(Constants.STORAGE_INTERNAL);
    }
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                QbConfig.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
          //      Log.d(TAG, "Oops! Failed create "
                //        + QbConfig.IMAGE_DIRECTORY_NAME + " directory");
             //   return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void launchUploadActivity(boolean isImage){
        funcAttachment(fileUri);
    }
    private void launchUploadActivity1(Uri uri) throws URISyntaxException {
        funcAttachment(uri);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == Common.SELECCIONAR_FOTO) {
                //Cogemos la URI de la imagen seleccionada, la convertimos a un archivo y la subimos al servidor
                Uri imagenSeleccionadaUri = data.getData();

                final ProgressDialog mDialog = new ProgressDialog(ChatMessageActivity.this);
                mDialog.setMessage("Setting image...");
                mDialog.setCancelable(false);
                mDialog.show();

                try {
                    //Convertimos URI a File
                    InputStream inputStream = getContentResolver().openInputStream(imagenSeleccionadaUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    File file = new File(Environment.getExternalStorageDirectory() + "/image.png");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    int imageSizeKb = (int) file.length() / 1024;
                    if (imageSizeKb >= (1024 * 100)) {
                        //        Toast.makeText(this, "Incorrect size", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Subimos la imagen
                    QBContent.uploadFileTask(file, true, null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {

                            qbChatDialog.setPhoto(qbFile.getId().toString());

                            //Actualizamos la conversacion
                            QBRequestUpdateBuilder requestUpdateBuilder = new QBDialogRequestBuilder();
                            QBRestChatService.updateGroupChatDialog(qbChatDialog, requestUpdateBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                                    mDialog.dismiss();
                                    imgAvatar.setImageBitmap(bitmap);
                                }
                                @Override
                                public void onError(QBResponseException e) {
                                    //          Toast.makeText(ChatMessageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onError(QBResponseException e) {        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //SECOND
            // if the result is capturing Image
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    launchUploadActivity(true);
                } else if (resultCode == RESULT_CANCELED) {

                    Toast.makeText(getApplicationContext(),"User cancelled image capture", Toast.LENGTH_SHORT)                           .show();

                } else {
                    //   Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    launchUploadActivity1(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled selection", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                    /*Toast.makeText(getApplicationContext(),
                            "Sorry! Failed to get file", Toast.LENGTH_SHORT)
                            .show();*/
            }
        }

    }
    private void showDialog(String imageURL) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_image_dialog);

        ImageView popupIV = (ImageView) dialog.findViewById(R.id.ivPopUp);
        ImageButton close = (ImageButton) dialog.findViewById(R.id.btnClose);
        final ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.idProgressbar) ;
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(ChatMessageActivity.this)
                .load(imageURL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(popupIV);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO Close button action
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            qbChatDialog.removeMessageListrener(this);
        }catch (NullPointerException npe){
            Log.e(TAG, "Error : "+npe, npe);
        }
        count = 0;
        //   qbCustomList.clear();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            qbChatDialog.removeMessageListrener(this);
        }catch (NullPointerException npe){
            Log.e(TAG, "Error : "+npe, npe);
        }

    }


}

