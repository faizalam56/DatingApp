package com.across.senzec.datingapp.qbchat.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.api.APIClient;
import com.across.senzec.datingapp.api.APIInterface;
import com.across.senzec.datingapp.qbchat.holder.QBUnreadMessageHolder;
import com.across.senzec.datingapp.responsemodel.ChatUserResponse;
import com.across.senzec.datingapp.utils.SharedPrefClass;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatDialogsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;


    private APIInterface apiInterface;
    private ProgressDialog progressDialog;
    private String strUsrUrl, apiQbid;

    public ChatDialogsAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }

    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog, null);

            TextView txvTitulo, txvMensaje;
            final ImageView img, imgUnread;

            txvMensaje = (TextView) view.findViewById(R.id.txvListaChat);
            txvTitulo = (TextView) view.findViewById(R.id.txvTitulo);
            img = (ImageView) view.findViewById(R.id.imgIconoChatDialog);
            imgUnread = (ImageView) view.findViewById(R.id.imgIconoUnread);

            txvMensaje.setText(qbChatDialogs.get(position).getLastMessage());
            txvTitulo.setText(qbChatDialogs.get(position).getName());

            //Color random para las fotos de la lista
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            if (qbChatDialogs.get(position).getPhoto().equals("null")) {

               /* TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                        .endConfig()
                        .round();

                //Cogemos el primer caracter en mayusculas del texto de la lista para mostrarlo en el ImageView
                TextDrawable drawable = builder.build(txvTitulo.getText().toString().substring(0, 1).toUpperCase(), randomColor);
                img.setImageDrawable(drawable);*/

                String qbid = qbChatDialogs.get(position).getRecipientId().toString();




                imgUrlApi(img, qbid);

               /* Picasso.with(context)
                        .load(qbChatDialogs.get(position).getPhoto())
                        .resize(50, 50)
                        .centerCrop()
                        .into(img);*/


            } else {

                //Nos descargamos el bitmap del servidor y lo establecemos en la conversacion
                QBContent.getFile(Integer.parseInt(qbChatDialogs.get(position).getPhoto())).performAsync(new QBEntityCallback<QBFile>() {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        String fileURL = qbFile.getPublicUrl();
                        Picasso.with(context)
                                .load(fileURL)
                                .resize(50, 50)
                                .centerCrop()
                                .into(img);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR_IMAGE", "" + e.getMessage());
                    }
                });

            }

            //Establecer contador mensajes no leidos
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .endConfig()
                    .round();
            int contador_unread = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialogs.get(position).getDialogId());
            if (contador_unread > 0) {
                TextDrawable unread_drawable = unreadBuilder.build("" + contador_unread, Color.RED);
                imgUnread.setImageDrawable(unread_drawable);
            }

        }
        return view;

    }



    private String imgUrlApi(final ImageView img, final String qbid){

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
                        if(chatUserResponse.response != null) {
                            for (int i = 0; i < chatUserResponse.response.size(); i++) {
                                apiQbid = chatUserResponse.response.get(i).user.qbid.toString();
                                if (apiQbid.contains(qbid)) {
                                    strUsrUrl = chatUserResponse.response.get(i).photos.get(0).url.toString();
                                    System.out.println(strUsrUrl);

                                    //      QbidModel qbidModel = new QbidModel(strQBID);
                                    //      qbidList.add(qbidModel);
                                    // qbidList.add(strQBID);
                                    Picasso.with(context)
                                            .load(strUsrUrl)
                                            .resize(50, 50)
                                            .centerCrop()
                                            .into(img);
                                }
                            }
                        }

                    }
                }else{
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                 //   showErrorDialog("something went wrong from server!");
                }


            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
           //     showErrorDialog(t.getMessage());
            }
        });

        return strUsrUrl;
    }

    public void loadingProgressbar(){

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }
    private void showErrorDialog(String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
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

}
