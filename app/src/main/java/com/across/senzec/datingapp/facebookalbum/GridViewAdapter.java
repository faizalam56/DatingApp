package com.across.senzec.datingapp.facebookalbum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.across.senzec.datingapp.R;
import com.across.senzec.datingapp.controller.AppController;

import java.io.ByteArrayOutputStream;
import java.util.List;



public class GridViewAdapter extends BaseAdapter {

	private static final String TAG = "GRIDVIEW_ADAPTER";
	private Activity activity;
	private LayoutInflater inflater;
	private List<FbImage> movieItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public GridViewAdapter(Activity activity, List<FbImage> movieItems) {
		this.activity = activity;
		this.movieItems = movieItems;


	}

	public int getCount() {
		return movieItems.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.gridview_item, null);

		if (imageLoader == null) {
			imageLoader = AppController.getInstance().getImageLoader();
		}

		final NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.image);

		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Drawable d = image.getDrawable();
				Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
				Uri uri=getImageUri(bitmap);

				Intent intent=new Intent();
				intent.setData(uri);
				activity.setResult(activity.RESULT_OK,intent);
				activity.finish();
			}
		});
		FbImage m = movieItems.get(position);
		image.setImageUrl(m.getFbImageUrl(), imageLoader);
		return convertView;
	}
	public Uri getImageUri(Bitmap inImage) {
		Uri uri = Uri.EMPTY;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, "Title", null);
		try{
			return Uri.parse(path);
		}catch (NullPointerException npe){
			Log.e(TAG, "Error : "+npe, npe);
		}
		return uri;
	}
}