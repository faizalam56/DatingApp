//package com.example.senzec.datingapp.facebookalbum;
//
//import android.app.Application;
//import android.text.TextUtils;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.ImageLoader;
//import com.android.volley.toolbox.Volley;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//
//
//
//
//public class AppController extends Application {
//
//	public static final String TAG = AppController.class.getSimpleName();
//	private RequestQueue mRequestQueue;
//	private ImageLoader mImageLoader;
//
//
//	private static AppController mInstance;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		FacebookSdk.sdkInitialize(getApplicationContext());
//		AppEventsLogger.activateApp(this);
//		mInstance = this;
//	}
//
//	public static synchronized AppController getInstance() {
//		return mInstance;
//	}
//
//	public RequestQueue getRequestQueue() {
//		if (mRequestQueue == null) {
//			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//		}
//
//		return mRequestQueue;
//	}
//
//	public ImageLoader getImageLoader() {
//
//		getRequestQueue();
//		if (mImageLoader == null) {
//
//			mImageLoader = new ImageLoader(this.mRequestQueue,
//					new LruBitmapCache());
//		}
//		return this.mImageLoader;
//	}
//
////	public ImageLoader getImageLoader(Handler handler) {
////
////		getRequestQueue();
////		if (mImageLoader == null) {
////
////			mImageLoader = new ImageLoader(this.mRequestQueue,
////					new LruBitmapCache(handler));
////		}
////		return this.mImageLoader;
////	}
//
//	public <T> void addToRequestQueue(Request<T> req, String tag) {
//		// set the default tag if tag is empty
//		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//		getRequestQueue().add(req);
//	}
//
//	public <T> void addToRequestQueue(Request<T> req) {
//		req.setTag(TAG);
//		getRequestQueue().add(req);
//	}
//
//	public void cancelPendingRequests(Object tag) {
//		if (mRequestQueue != null) {
//			mRequestQueue.cancelAll(tag);
//		}
//	}
//
//
//
////	public RequestQueue getRequestQueue() {
////		if (mRequestQueue == null) {
////			Cache cache = new DiskBasedCache(this.getCacheDir(), 10 * 1024 * 1024);
////			Network network = new BasicNetwork(new HurlStack());
////			mRequestQueue = new RequestQueue(cache, network);
////			// Don't forget to start the volley request queue
////			mRequestQueue.start();
////		}
////		return mRequestQueue;
////	}
//
//
//
//}