package com.menil.rottenmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/*
 * Created by menil on 02.12.2014.
 */
public class RemoteImageView extends ImageView {

    public static final String TAG = "Remote Image View";
    protected static LruCache<String, Bitmap> cache;

    private OnRemoteImageListener listener;
    private BackgroundDecode bdecode;
    private Animation fadeInAnimation;

    public RemoteImageView(Context context) {
        super(context);
    }

    public RemoteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static LruCache<String, Bitmap> getCache(Context context) {
        if (cache == null) initCache(context);
        return cache;
    }

    public static void clearCache() {
        if (cache != null) {
            cache.evictAll();
            cache = null;
        }

    }

    @SuppressLint("NewApi")
    protected static void initCache(Context context) {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //	Log.d("The entire cache size is "+(cache.size()/1024)+"/"+ (cache.maxSize()/1024)+"MB, there are "+cache.putCount()+ " items inserted and the size of this bitmap is "+(bitmap.getRowBytes() * bitmap.getHeight()/1024/1024)+"MB ("+key+")");
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public static boolean isImageInCache(String url) {
        return cache.get(url) != null;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bdecode != null) {
            bdecode.cancel(true);
        }
    }

    public LruCache<String, Bitmap> getCache() {
        return getCache(getContext());
    }

    public void setOnRemoteImageListener(OnRemoteImageListener listener) {
        this.listener = listener;
    }

    private void prepareFadeInAnimation() {
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    public void setImageURL(String URL) {
        setImageURL(URL, true);
    }

    @SuppressLint("NewApi")
    public synchronized void setImageURL(String URL, boolean animate) {
        setImageDrawable(null);
        if (URL == null) {
            return;
        }

        if (fadeInAnimation == null) {
            prepareFadeInAnimation();
        }

        if (cache == null) initCache(getContext());

        // Temporary solution, until httpsi problem is solved
    /*if(!URL.substring(0, 4).equals("http")){
	URL="http:"+URL;
	}*/

        Bitmap image = cache.get(URL);

        if (image != null) {
            setVisibility(View.VISIBLE);
            Log.d("RemoteImageView", "Set the image from cache " + URL);
            BitmapDrawable d = new BitmapDrawable(getContext().getResources(), image);
            this.setImageDrawable(d);
            if (listener != null) {
                listener.onImageSet(this, true);
            }
        } else if (bdecode == null || bdecode.cancelPotentialWork(URL)) {
            Log.d("RemoteImageView", "Downloading the image... " + URL);

            View loader = ((View) getParent()).findViewById(R.id.image);
            if (loader != null) {
                loader.setVisibility(View.VISIBLE);
            }

            setTag(URL);

            bdecode = new BackgroundDecode(URL, animate);
            bdecode.setCacheRef(cache);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                bdecode.execute();
            } else {
                bdecode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public static interface OnRemoteImageListener {
        public void onImageDownloadStart(RemoteImageView imageView);

        public void onImageDownloadProgress(RemoteImageView imageView, int percentage);

        public void onImageSet(RemoteImageView imageView, boolean isFromCache);
    }

    class BackgroundDecode extends AsyncTask<String, Integer, Object> {

        public static final int PROGRESS_DOWNLOAD_START = 100;

        private String imagePath;
        private boolean isDefault;
        private boolean animate;
        private LruCache<String, Bitmap> cacheRef;


        public BackgroundDecode() {
            isDefault = true;
            imagePath = "";
        }

        public BackgroundDecode(String imagepath, boolean animate) {
            isDefault = false;
            imagePath = imagepath;
            this.animate = animate;
        }

        @Override
        protected Object doInBackground(String... params) {
            if (!isCancelled()) {

                Bitmap b;
                if (!isDefault) {
                    b = downloadImage(imagePath);
                } else {
                    Log.wtf(TAG, "Default image wtf?");
                    b = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.empty_img);

                }

                if (b != null && cacheRef != null) {
                    //Log.d("Friend View", "Putting cached image "+friendId);

                    synchronized (cache) {
                        if (cacheRef.get(imagePath) == null) {
                            cacheRef.put(imagePath, b);
                        }
                    }

                }

                return isCancelled() ? null : b;
            }
            return null;

        }

        private Bitmap downloadImage(String imagePath) {
            return downloadImage(imagePath, 0);
        }

        protected void onProgressUpdate(Integer... values) {
            switch (values[0]) {
                case PROGRESS_DOWNLOAD_START:
                    if (listener != null) {
                        listener.onImageDownloadStart(RemoteImageView.this);
                    }
                    break;
            }
        }

        private Bitmap downloadImage(String imagePath, int tries) {
            Log.d(TAG, "Downloading the image...");
            publishProgress(PROGRESS_DOWNLOAD_START);

            try {
                Bitmap bm;
                if (this.imagePath == null || (this.imagePath.equals("")) || !this.imagePath.subSequence(0, 4).equals("http")) {
                    return null;
                }

                Uri uri = Uri.parse(this.imagePath);
                String encodedName = URLEncoder.encode(uri.getLastPathSegment(), "utf-8");
                Log.d("EncodingDebug", encodedName + " " + uri.getEncodedPath());

                String encodedURL = this.imagePath.substring(0, this.imagePath.lastIndexOf("/") + 1) + encodedName;

                //URL aURL = new URL(URLEncoder.encode(this.imagePath, "utf-8"));
                URL aURL = new URL(encodedURL);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                if (!isCancelled()) {
                    bm = BitmapFactory.decodeStream(bis);
                } else {
                    bm = null;
                }
                bis.close();
                is.close();
                return bm;
            } catch (MalformedURLException e) {
                Log.w(TAG, "Malformed URL: " + imagePath);
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                if (tries > 3) {
                    e.printStackTrace();
                    return null;
                } else {
                    e.printStackTrace();
                    Log.e("Image Task", "Retrying the download...");
                    return downloadImage(imagePath, ++tries);
                }
            }
        }


        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Object bitmap) {
            super.onPostExecute(bitmap);


            if (RemoteImageView.this == null) {
                bdecode = null;
                return;
            }

            if (RemoteImageView.this.getTag() != null && !RemoteImageView.this.getTag().toString().equals(imagePath)) {
                bdecode = null;
                return;
            }

            if (bitmap != null) {
                //	Log.i("Bitmap size: "+((Bitmap)bitmap).getWidth()+"x"+((Bitmap)bitmap).getHeight());
                RemoteImageView.this.setImageBitmap((Bitmap) bitmap);
                if (listener != null) {
                    listener.onImageSet(RemoteImageView.this, false);
                }
                if (animate) {
                    RemoteImageView.this.setVisibility(View.INVISIBLE);
                    fadeInAnimation.reset();
                    RemoteImageView.this.clearAnimation();
                    RemoteImageView.this.startAnimation(fadeInAnimation);
                }

                View loader = ((View) getParent()).findViewById(R.id.image);

                if (loader != null) {
                    loader.setVisibility(View.GONE);
                }


            }
            bdecode = null;
        }

        public boolean cancelPotentialWork(String path) {
            if (!imagePath.equals(path)) {
                // Cancel previous task
                cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
            // No task associated with the ImageView, or an existing task was cancelled
            return true;
        }

        public void setCacheRef(LruCache<String, Bitmap> cacheRef) {
            if (cacheRef != null) {
                this.cacheRef = cacheRef;
            }
        }

    }
}