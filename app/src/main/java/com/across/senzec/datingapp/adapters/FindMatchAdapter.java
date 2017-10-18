package com.across.senzec.datingapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.fragments.FindMatchFragment;
import com.across.senzec.datingapp.qbchat.ChatMessageActivity;
import com.across.senzec.datingapp.qbchat.ConversationActivity;
import com.across.senzec.datingapp.qbchat.common.Common;
import com.across.senzec.datingapp.qbchat.holder.QBChatDialogHolder;
import com.across.senzec.datingapp.qbchat.holder.QBUnreadMessageHolder;
import com.across.senzec.datingapp.qbchat.holder.QBUsersHolder;
import com.across.senzec.datingapp.responsemodel.FindMatchResponse;
import com.across.senzec.datingapp.utils.Constants;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.bumptech.glide.Glide;
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
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ravi on 23/9/17.
 */

public class FindMatchAdapter extends RecyclerView.Adapter<FindMatchAdapter.MyViewHolder> implements QBSystemMessageListener, QBChatDialogMessageListener {

    Context mContext;
    LayoutInflater inflater;
    FindMatchResponse resource;
    FindMatchFragment.FindMatchFragmentCommunicator communicator;
    FindMatchFragment findMatchFragment;
    LinearLayout loadingIndicator;
    ProgressDialog progressDialog;
    public FindMatchAdapter(FindMatchFragment.FindMatchFragmentCommunicator communicator, Context mContext, FindMatchResponse resource, LinearLayout loadingIndicator){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.resource = resource;
        this.communicator = communicator;
        this.loadingIndicator = loadingIndicator;

    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfile;
        TextView tvName, tvAge, tvDistance;
        public MyViewHolder(final View itemView) {
            super(itemView);
            ivProfile = (ImageView)itemView.findViewById(R.id.idProfileIV);
            tvName = (TextView)itemView.findViewById(R.id.idNameTV);
            tvAge = (TextView)itemView.findViewById(R.id.idAgeTV);
            tvDistance = (TextView)itemView.findViewById(R.id.idDistanceTV);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingIndicator.setVisibility(View.VISIBLE);
//                    loadingProgressbar();
                    //        Toast.makeText(itemView.getContext(), "Test"+getAdapterPosition()+" - - "+mShortTV.getText(), Toast.LENGTH_LONG).show();

                    //  final QBUser usuario = (QBUser) lsvUsers.getItemAtPosition(i);
                    final String secondUserID = resource.response.get(getAdapterPosition()).user.qbid.toString();

                    QBChatDialog dialogo = DialogUtils.buildPrivateDialog(Integer.parseInt(secondUserID));

                    QBRestChatService.createChatDialog(dialogo).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                            QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                            QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                            QBChatMessage qbChatMessage = new QBChatMessage();
                            qbChatMessage.setRecipientId(Integer.parseInt(secondUserID));
                            qbChatMessage.setBody(qbChatDialog.getDialogId());
                            try {

                                qbSystemMessagesManager.sendSystemMessage(qbChatMessage);

                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }catch (NullPointerException npe){
                                Log.e("FindMatchAdapter", "#Err : "+npe, npe);
                            }
                            //   finish();
                            //              Toast.makeText(itemView.getContext(), "Safal hua", Toast.LENGTH_LONG).show();
                            //itemView.getContext().startActivity(new Intent(itemView.getContext(), ConversationActivity.class));
                            //QBChatDialog qbChatDialog = (QBChatDialog) lsvChats.getAdapter().getItem(position);
                            //    QBChatDialog qbChatDialog1 = (QBChatDialog) resource.response.get(getAdapterPosition());
                          /*  Intent intent = new Intent(itemView.getContext(), ChatMessageActivity.class);
                            intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                            itemView.getContext().startActivity(intent);*/
                            //  communicator.goToChat();
                            //    ConversationClass conversationClass = new ConversationClass(mContext);
                            //    conversationClass.processMessage("", qbChatMessage, getAdapterPosition());
                            //---start
                            System.out.println("Checked");
                            QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    //Lo ponemos en cache
                                    QBChatDialogHolder.getInstance().putDialog(qbChatDialog);
                                    ArrayList<QBChatDialog> qbChatDialogs = QBChatDialogHolder.getInstance().getAllChatDialogs();
                                    //  qbChatDialogs.get(0);
                                    Intent intent = new Intent(itemView.getContext(), ChatMessageActivity.class);
                                    intent.putExtra(Common.DIALOG_EXTRA, qbChatDialog);
                                    itemView.getContext().startActivity(intent);
                                    loadingIndicator.setVisibility(View.GONE);

                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });

                            //--end
                            createChatSesion();
                            UploadChats();

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e("ERROR", e.getMessage());
                            //                  Toast.makeText(itemView.getContext(), "fail hua", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.row_list_match, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //  FindMatchResponse.Response responce = resource.get(i);
        List<FindMatchResponse.Photos> userPhoto = resource.response.get(position).photos;
        FindMatchResponse.User user_detail = resource.response.get(position).user;

        if (userPhoto.size() > 0) {
            FindMatchResponse.Photos photos = userPhoto.get(0);
            Glide.with(mContext.getApplicationContext())
                    .load(photos.url)
                    .into(holder.ivProfile);
        }

        if (user_detail.name.contains(" ")) {
            holder.tvName.setText(user_detail.name.substring(0, user_detail.name.indexOf(" ")));
        } else {
            holder.tvName.setText(user_detail.name);
        }

        if (user_detail.dob.length() > 0) {
            holder.tvAge.setText(findAgeFromDateTime(user_detail.dob));
        }

      /*  if (resource.response.get(position).distance.equalsIgnoreCase(null)) {
            holder.tvDistance.setText("");
        } else {
            holder.tvDistance.setText(resource.response.get(position).distance);
        }*/

        if (resource.response.get(position).distance.contains(".")) {
            holder.tvDistance.setText(resource.response.get(position).distance.substring(0, resource.response.get(position).distance.indexOf('.')));
        } else {
            holder.tvDistance.setText(resource.response.get(position).distance);
        }
    }

    private String findAgeFromDateTime(String dobInTime){
        String date = getDate(dobInTime,"dd/MM/yyyy");

        String[] dateParts = date.split("/");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        String age = getAge(year,month,day);
        return age;
    }
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
    private String getDate(String dobInTime, String dateFormat)
    {

        Long milliSeconds = Long.parseLong(dobInTime)*1000;
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public int getItemCount() {
        if(resource!=null) {
            return resource.response.size();
        }else{
            return 0;
        }
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

        String user, password;
        user = new SharedPrefClass(mContext).getUsrPwdForConversation();
        password = Constants.PASSWORD;

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

                        QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                        qbSystemMessagesManager.addSystemMessageListener(FindMatchAdapter.this);

                        QBIncomingMessagesManager qbIncomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
                        qbIncomingMessagesManager.addDialogMessageListener(FindMatchAdapter.this);
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

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        UploadChats();
    }
    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
    }
    @Override
    public void processMessage(QBChatMessage qbChatMessage) {

        //Ponemos los dialogos en cache
        QBRestChatService.getChatDialogById(qbChatMessage.getBody()).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                //Lo ponemos en cache
                QBChatDialogHolder.getInstance().putDialog(qbChatDialog);

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
    @Override
    public void processError(QBChatException e, QBChatMessage qbChatMessage) {
    }

    public void loadingProgressbar(){
        progressDialog = new ProgressDialog(mContext,R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

}
