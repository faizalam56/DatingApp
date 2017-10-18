package com.across.senzec.datingapp.facebookalbum;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
		ImageCache {
	Handler handler;
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

//	public LruBitmapCache(Handler handler) {
//		this(getDefaultLruCacheSize());
//		this.handler=handler;
//	}

	public LruBitmapCache() {
		this(getDefaultLruCacheSize());
	}
	public LruBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
//		System.out.println("getBitmap url "+url);
		return get(url);
	}

	int c=0;//#W120#H120
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
//		System.out.println("putBitmap  bitmap " + bitmap + " " + c +" "+url);
		c++;
//        writeToFile(bitmap,""+c);
//		HashMap<String ,Bitmap> hashMap=new HashMap();

//		hashMap.put(url.substring(10,url.length()),bitmap);
//		Message message=Message.obtain();
//		message.obj=hashMap;
//		handler.sendMessage(message);
	}

	private void writeToFile(final Bitmap bitmap, final String fileName){
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileOutputStream out = null;
				try {
					File mediaStorageDir = new File(Environment.getExternalStorageDirectory()+"/FB");
					if (! mediaStorageDir.exists()){
						if (! mediaStorageDir.mkdirs()){
						}
					}
					File mediaFile = new File(mediaStorageDir ,fileName + ".jpg" );

					out = new FileOutputStream(mediaFile);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
					// PNG is a lossless format, the compression factor (100) is ignored
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}