package ec.richardnarvaez.chatf.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import ec.richardnarvaez.chatf.R;


/**
 * Created by RICHARD on 07/04/2017.
 */

public class GlideUtils {

    private static String TAG = "GlideUtils";

    public static void loadImage(Context context, String url, ImageView imageView) {

        ColorDrawable error = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));
        Drawable load = context.getDrawable(R.drawable.degraded_blue);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(load)
                .error(error)

                .priority(Priority.HIGH);

        Glide.with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(imageView);

    }

    public static void loadProfileIcon(Context context, String url, ImageView imageView) {

        ColorDrawable error = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));
        Drawable load = context.getDrawable(R.drawable.vector_account);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(load)
                .error(error)
                .priority(Priority.HIGH);

        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }

//    public static void loadBlurImage(Context context, String url, final ImageView imageView, int radius) {
//        Glide.with(context)
//                .load(url)
//                .apply(new RequestOptions().transforms(new BlurTransformation(context, radius)))
//                .into(imageView);
//    }

    public static void load2Parts(final Context context, final String url0, final String url1, final ImageView imageView) {

        Glide.with(context)
                .asBitmap()
                .load(url0)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(bitmap);
                        Glide.with(context)
                                .load(url1)
                                //.asBitmap()
                                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    }


                });


//        Glide.with(context)
//                .load(url0)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .bitmapTransform(new BlurTransformation(context, 4))
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        Glide.with(context)
//                                .load(url1)
//                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                .into(imageView);
//                        return false;
//                    }
//                }).into(imageView);

    }

//    public static void load2PartsMax(final Context context, final String url0, final String url1, final ProgressTarget<String, Bitmap> imageView) {
//        try {
//
//            imageView.setModel(url0);
//            Glide.with(context)
//                    .asBitmap()
//                    .load(url0)
//                    //.centerCrop()
//                    // .error(R.drawable.vector_map_marker_circle)
//                    //.placeholder(R.drawable.progress_max)
//                    //.crossFade()
//                    //.thumbnail(0.4f)
//                    //.bitmapTransform(new BlurTransformation(context, 25))
//                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageView);
//
//            /*Glide.with(context)
//                    .load(url0)
//                    .asBitmap()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                            // Do something with bitmap here.
//                            imageView.setImageBitmap(bitmap);
//                            //holder.thumbnail.setImageBitmap(bitmap);
//                            Log.e("GalleryAdapter", "Glide getMedium ");
//
//                            Glide.with(context)
//                                    .load(url1)
//                                    .asBitmap()
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .into(new SimpleTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                                            // Do something with bitmap here.
//                                            imageView.setImageBitmap(bitmap);
//                                            //holder.thumbnail.setImageBitmap(bitmap);
//                                            Log.e("GalleryAdapter", "Glide getLarge ");
//                                        }
//                                    });
//
//                        }
//                    });*/
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public static void loadMax(Context context, final String url, final ImageActivity.MyProgressTarget<Bitmap> imageView) {
//        try {
//
////            imageView.setModel(url);
////
////            Glide.with(context)
////                    .load(url)
////                    .listener(new RequestListener<Drawable>() {
////                        @Override
////                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
////                            return false;
////                        }
////
////                        @Override
////                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
////                            return false;
////                        }
////                    })
////                    .into(imageView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void loadImagewtProgress(Context context, String url, ImageView imageView, final ProgressBar progres) {
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent));
        Drawable drawable = context.getDrawable(R.drawable.degraded_blue);

        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progres.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        progres.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void loadImageWAlert(final ProgressDialog progressDialog, String url, ImageView imageView, Activity context) {

        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent));
        Drawable drawable = context.getDrawable(R.drawable.degraded_blue);


        Glide.with(context)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressDialog.hide();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressDialog.hide();
                        return false;
                    }
                })
                .into(imageView);
    }
}
