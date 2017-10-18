package com.across.senzec.datingapp.qbchat.adapters;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.support.v7.app.AlertDialog;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.FrameLayout;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.across.senzec.datingapp.R;
        import com.across.senzec.datingapp.api.APIClient;
        import com.across.senzec.datingapp.api.APIInterface;
        import com.across.senzec.datingapp.qbchat.TimeUtils;
        import com.across.senzec.datingapp.qbchat.holder.QBUsersHolder;
        import com.across.senzec.datingapp.qbchat.pagination.PaginationHistoryListener;
        import com.across.senzec.datingapp.responsemodel.ChatUserResponse;
        import com.across.senzec.datingapp.utils.CircleImageView;
        import com.across.senzec.datingapp.utils.SharedPrefClass;
        import com.bumptech.glide.Glide;
        import com.bumptech.glide.load.resource.drawable.GlideDrawable;
        import com.bumptech.glide.request.RequestListener;
        import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
        import com.bumptech.glide.request.target.Target;
        import com.google.gson.Gson;
        import com.google.gson.JsonObject;
        import com.quickblox.chat.QBChatService;
        import com.quickblox.chat.model.QBChatMessage;

        import java.util.ArrayList;
        import java.util.NoSuchElementException;

        import retrofit2.Call;
        import retrofit2.Callback;

public class MessageChatAdapter extends BaseAdapter {

    private Context context;
    private static final String TAG = MessageChatAdapter.class.getSimpleName();
    private ArrayList<QBChatMessage> qbChatMessages;
    private APIInterface apiInterface;
    private ProgressDialog progressDialog;
    private String strUsrUrl;
    private String sentMessage, receivedMessage;

    private PaginationHistoryListener paginationListener;
    private int previousGetCount = 0;
    //  private ImageView iv_recipient_user;

    public MessageChatAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        int size = 0;
        try {
            size =  qbChatMessages.size();
        }catch (NullPointerException npe){
            Log.e(TAG, "Error : "+npe, npe);
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        String url = "";
        try {
            url = qbChatMessages.get(position).getAttachments().iterator().next().getUrl();
        }catch (NoSuchElementException nse)
        {
            Log.e(TAG, "Error : "+nse, nse);
        }catch (NullPointerException npe)
        {
            Log.e(TAG, "Error : "+npe, npe);
        }

        return url;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // CHAT LIKE RESPONSE
    private void callChatUserApi(final CircleImageView photoIV, final String senderID){

        String userToken = new SharedPrefClass(context).getUserToken();
        String userId = new SharedPrefClass(context).getLoggedUser();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterface.chatUser(userId,userToken).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                JsonObject resource = response.body();
                Gson gson = new Gson();
                if (resource!=null) {
                    if (!resource.get("status").getAsBoolean()) {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    } else {
                        if(progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        ChatUserResponse chatUserResponse = gson.fromJson(resource.toString(), ChatUserResponse.class);
                        for(int i=0; i<chatUserResponse.response.size(); i++)
                        {
                            String apiQbid = chatUserResponse.response.get(i).user.qbid.toString();
                            if(apiQbid.contains(senderID)) {

                                strUsrUrl = chatUserResponse.response.get(i).photos.get(0).url.toString();
                                System.out.println(strUsrUrl);
                                //      QbidModel qbidModel = new QbidModel(strQBID);
                                //      qbidList.add(qbidModel);
                                // qbidList.add(strQBID);
                                Glide.with(context).load(strUsrUrl).into(photoIV);
                            }
                        }

                    }
                }else{
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    //            Toast.makeText(context, "something went wrong from server!", Toast.LENGTH_LONG).show();
                }


            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                //  showErrorDialog(t.getMessage());
                //   Toast.makeText(context, "something went wrong from server!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void loadingProgressbar(){

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }
    private void showErrorDialog(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                holder = new ViewHolder();
                if (qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId()) ) {
                    view = inflater.inflate(R.layout.list_message_sent, parent, false);
                    holder.sentRLayout = (RelativeLayout) view.findViewById(R.id.idSentRLayout);
                    holder.sentLLayout = (LinearLayout) view.findViewById(R.id.idSentLLayout);
                    //    holder.sentCardView = (CardView) view.findViewById(R.id.idSentCardView);
                    holder.sentFLayout = (FrameLayout) view.findViewById(R.id.idSentFLayout);

                    holder.tvOutMessage = (TextView) view.findViewById(R.id.tvMessageSent);
                    holder.tvDateSent = (TextView) view.findViewById(R.id.tvDateSent);
                    holder.ivOutAttachment = (ImageView) view.findViewById(R.id.ivxvImageSent);
                    holder.pbOutattachment = (ImageView) view.findViewById(R.id.idSentProgress);
                }else {
                    //
                    view = inflater.inflate(R.layout.list_message_received, parent, false);
                    holder.receiveRLayout = (RelativeLayout) view.findViewById(R.id.idReceiveRLayout);
                    holder.receiveLLayout = (LinearLayout) view.findViewById(R.id.idReceiveLLayout);
                    holder.receiveLLayout1 = (LinearLayout) view.findViewById(R.id.idReceiveLLayout1);
                    //    holder.receiveCardView = (CardView) view.findViewById(R.id.idReceiveCardView);
                    holder.receiveFLayout = (FrameLayout) view.findViewById(R.id.idReceiveFLayout);
                    holder.photoIV = (CircleImageView) view.findViewById(R.id.iv_recipient_user);


                    holder.tvInMessage = (TextView) view.findViewById(R.id.tvMessageReceived);
                    holder.tvDateReceived = (TextView) view.findViewById(R.id.tvDateReceived);
                    holder.ivInAttachment = (ImageView) view.findViewById(R.id.ivxvImageReceived);
                    holder.pbInattachment = (ImageView) view.findViewById(R.id.idRecieveProgress);
                }

                view.setTag(holder);
                //   return view;
            } else {
                holder = (ViewHolder) view.getTag();
            }
            try {
                if (qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId()) ) {

                    try {
                        sentMessage = qbChatMessages.get(position).getBody();
                        if (!sentMessage.equals("null")) {
                            holder.tvOutMessage.setText(sentMessage);
                            holder.tvDateSent.setText(TimeUtils.getDateTime(qbChatMessages.get(position).getDateSent() * 1000));
                            holder.tvOutMessage.setVisibility(View.VISIBLE);
                            holder.ivOutAttachment.setVisibility(View.GONE);
                            holder.pbOutattachment.setVisibility(View.GONE);

                        }
                    }catch (NullPointerException npe){
                        Log.e(TAG, "#ERROR : "+npe, npe);
                    }

                    try {
                        String sentImageURL = qbChatMessages.get(position).getAttachments().iterator().next().getUrl();
                        if(!sentImageURL.equals("null")) {
                            holder.tvOutMessage.setVisibility(View.GONE);
                            holder.ivOutAttachment.setVisibility(View.VISIBLE);
                            holder.tvDateSent.setText(TimeUtils.getDateTime(qbChatMessages.get(position).getDateSent() * 1000));

                            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.pbOutattachment);
                            Glide.with(context).load(R.raw.gif_progress_chat).into(imageViewTarget);
                            holder.pbOutattachment.setVisibility(View.VISIBLE);

                            Glide.with(context)
                                    .load(sentImageURL)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            holder.pbOutattachment.setVisibility(View.GONE);
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            holder.pbOutattachment.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(holder.ivOutAttachment);
                        }
                    } catch (NoSuchElementException nse) {
                        Log.e(TAG, "Error : " + nse, nse);
                    }catch (NullPointerException npe){
                        Log.e(TAG, "#ERROR : "+npe, npe);
                    }
                } else {

                    // MESSAGE RECEIVE SIDE
                }
                try {
                    receivedMessage = qbChatMessages.get(position).getBody();
                    if (!receivedMessage.equals("null") && receivedMessage.length()> 0) {
                        holder.tvInMessage.setText(receivedMessage);
                        holder.tvDateReceived.setText(TimeUtils.getDateTime(qbChatMessages.get(position).getDateSent() * 1000));
                        holder.tvInMessage.setVisibility(View.VISIBLE);
                        holder.ivInAttachment.setVisibility(View.GONE);
                        holder.pbInattachment.setVisibility(View.GONE);
                    }
                }catch (NullPointerException npe){
                    Log.e(TAG, "#ERROR : "+npe, npe);
                }
                try {
                    String receivedURL = qbChatMessages.get(position).getAttachments().iterator().next().getUrl();
                    if(!receivedURL.equals("null") && receivedURL.length()>0) {
                        holder.tvInMessage.setVisibility(View.GONE);
                        holder.ivInAttachment.setVisibility(View.VISIBLE);
                        holder.tvDateReceived.setText(TimeUtils.getDateTime(qbChatMessages.get(position).getDateSent() * 1000));

                        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.pbInattachment);
                        Glide.with(context).load(R.raw.gif_progress_chat).into(imageViewTarget);
                        holder.pbInattachment.setVisibility(View.VISIBLE);


                        Glide.with(context)
                                .load(receivedURL)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        holder.pbInattachment.setVisibility(View.GONE);
                                        return false;
                                    }
                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        holder.pbInattachment.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(holder.ivInAttachment);

                    }} catch (NoSuchElementException nse) {
                    Log.e(TAG, "Error : " + nse, nse);
                }catch (NullPointerException npe){
                    Log.e(TAG, "#ERROR : "+npe, npe);    }

                TextView txvUsuarioMensaje = (TextView) view.findViewById(R.id.txvUsuarioMensaje);
                txvUsuarioMensaje.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName());

                String senderID = qbChatMessages.get(position).getSenderId().toString();
                callChatUserApi(holder.photoIV, senderID);

            }catch (NullPointerException npe)
            {   Log.e("TAG", "Error : "+npe, npe);
            }
            holder.count = position;
        }catch (NullPointerException npe)
        {   Log.e(TAG, "Error : "+npe, npe);     }

        // downloadMore(position);

        return view;
    }

  /*  private void downloadMore(int position) {
        if (position == 0) {
            if (getCount() != previousGetCount) {
                paginationListener.downloadMore();
                previousGetCount = getCount();
            }
        }
    }

    public void setPaginationHistoryListener(PaginationHistoryListener paginationListener) {
        this.paginationListener = paginationListener;
    }*/


    private static class ViewHolder {
        public TextView tvInMessage, tvOutMessage, tvDateSent, tvDateReceived ;
        public ImageView ivInAttachment, ivOutAttachment;
        public ImageView pbInattachment, pbOutattachment;
        public RelativeLayout sentRLayout, receiveRLayout;
        public LinearLayout sentLLayout, receiveLLayout, receiveLLayout1;
        public FrameLayout sentFLayout, receiveFLayout;
        //     public CardView sentCardView, receiveCardView;
        public CircleImageView photoIV;
        int count = 0;
    }

}