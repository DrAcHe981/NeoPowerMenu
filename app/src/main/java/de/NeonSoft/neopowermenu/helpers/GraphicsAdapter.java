package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.listener.*;
import com.nostra13.universalimageloader.utils.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.util.*;

import android.content.pm.PackageManager.*;

public class GraphicsAdapter extends ArrayAdapter<GraphicItemHolder> {

    Activity mActivity;
    LayoutInflater mInflater;
    ImageLoader mImageLoader;
    PackageManager mPackageManager;

    ArrayList<GraphicItemHolder> mGraphics = new ArrayList<>();

    public GraphicsAdapter(Activity activity, ImageLoader imageLoader, ArrayList<GraphicItemHolder> graphics) {
        super(activity, R.layout.graphicslistitem, graphics);
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
        mImageLoader = imageLoader;
        mGraphics = graphics;
        mPackageManager = mActivity.getPackageManager();
    }

    @Override
    public View getView(final int position, View p2, ViewGroup p3) {
        View InflatedView = mInflater.inflate(R.layout.graphicslistitem, null, false);

        final ViewHolder holder = new ViewHolder();
        holder.imgQueueBg = (ImageView) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.imgQueueBg);
        GraphicDrawable drawable = GraphicDrawable.builder().beginConfig().setContext(mActivity).endConfig().buildRoundRect(null, mActivity.getResources().getColor(R.color.colorPrimaryDarkDarkTheme), (PreferencesGraphicsFragment.int_radius/2) * 3);
        holder.imgQueueBg.setImageDrawable(drawable);
        holder.imgQueue = (ImageView) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.imgQueue);
        holder.imgQueue.setPadding((int) helper.convertDpToPixel(20, mActivity), (int) helper.convertDpToPixel(20, mActivity), (int) helper.convertDpToPixel(20, mActivity), (int) helper.convertDpToPixel(20, mActivity));
        holder.imgProgress = (ProgressBar) InflatedView.findViewById(R.id.imgProgress);
        holder.imgProgress.setVisibility(View.GONE);

        holder.name = (TextView) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.imgName);

        holder.LoadingBar = (LinearLayout) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.graphicslistitemLinearLayout_Loading);

        InflatedView.setTag(holder);
        holder.imgQueue.setTag(position);

        if (mGraphics.get(position).getName().contains(".")) {
            try {
                String string = mPackageManager.getApplicationInfo(mGraphics.get(position).getName(), 0).loadLabel(mPackageManager).toString();
                holder.name.setText(string);
                holder.name.setVisibility(View.VISIBLE);
            } catch (PackageManager.NameNotFoundException e) {
                holder.name.setText(mGraphics.get(position).getName());
                holder.name.setVisibility(View.VISIBLE);
                Log.e("NPM", "No package found for resource " + mGraphics.get(position).getName() + "\n" + e);
            }
        } else {
            try {
                String string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuMain_" + mGraphics.get(position).getName(), "string", MainActivity.class.getPackage().getName()));
                //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                holder.name.setText(string);
                holder.name.setVisibility(View.VISIBLE);
            } catch (Throwable t) {
                try {
                    String string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuBottom_" + mGraphics.get(position).getName(), "string", MainActivity.class.getPackage().getName()));
                    //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                    holder.name.setText(string);
                    holder.name.setVisibility(View.VISIBLE);
                } catch (Throwable t1) {
                    holder.name.setText(mGraphics.get(position).getName());
                    holder.name.setVisibility(View.VISIBLE);
                    Log.w("NPM", "No String found for resource " + mGraphics.get(position).getName() + "\n" + t);
                }
            }
        }

        if (mGraphics.get(position).getName().equalsIgnoreCase("Progress") && mGraphics.get(position).getFile() != null && mGraphics.get(position).getFile().equalsIgnoreCase("Stock")) {
            holder.LoadingBar.setVisibility(View.GONE);
            holder.imgQueue.setVisibility(View.GONE);
            holder.imgProgress.setPadding((int) (PreferencesGraphicsFragment.float_padding), (int) (PreferencesGraphicsFragment.float_padding), (int) (PreferencesGraphicsFragment.float_padding), (int) (PreferencesGraphicsFragment.float_padding));
            holder.imgProgress.setVisibility(View.VISIBLE);
        } else if (mGraphics.get(position).getFile() != null) {
            mImageLoader.displayImage((mGraphics.get(position).getFile().startsWith("file://") ? "" : "file://") + mGraphics.get(position).getFile(),
                    holder.imgQueue, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.imgQueue.setImageBitmap(null);
                            holder.imgQueue.setVisibility(View.INVISIBLE);
                            holder.LoadingBar.setVisibility(View.VISIBLE);
                            super.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                            holder.LoadingBar.setVisibility(View.GONE);
                            holder.imgQueue.setPadding((int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4);
                            holder.imgQueue.setImageBitmap(loadedImage);
                            holder.imgQueue.setVisibility(View.VISIBLE);
                            if (mGraphics.get(position).getName().equalsIgnoreCase("Progress")) {
                                Animation progressAnim = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_right);
                                progressAnim.setRepeatMode(Animation.RESTART);
                                progressAnim.setRepeatCount(Animation.INFINITE);
                                holder.imgQueue.startAnimation(progressAnim);
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.imgQueue.setImageBitmap(null);
                            holder.imgQueue.setVisibility(View.INVISIBLE);
                            holder.LoadingBar.setVisibility(View.INVISIBLE);
                            Log.w("NPM", "Failed to load image '" + imageUri + "': ", failReason.getCause());
                            super.onLoadingStarted(imageUri, view);
                        }
                    });
        } else if (mGraphics.get(position).getRessource() != -1) {
            holder.LoadingBar.setVisibility(View.GONE);
            holder.imgQueue.setVisibility(View.GONE);
            holder.imgQueue.setPadding((int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4, (int) PreferencesGraphicsFragment.float_padding * 4);
            if (mGraphics.get(position).getName().contains(".")) {
                try {
                    Drawable d = mPackageManager.getApplicationIcon(mGraphics.get(position).getName());
                    holder.imgQueue.setImageDrawable(d);
                } catch (PackageManager.NameNotFoundException e) {
                    holder.imgQueue.setImageDrawable(mActivity.getResources().getDrawable(mGraphics.get(position).getRessource()));
                }
            } else {
                holder.imgQueue.setImageDrawable(mActivity.getResources().getDrawable(mGraphics.get(position).getRessource()));
            }
            if (mGraphics.get(position).getFileName().equalsIgnoreCase("Progress")) {
                ((AnimationDrawable) holder.imgQueue.getDrawable()).start();
            }
            holder.imgQueue.setVisibility(View.VISIBLE);
        } else {
            Log.e("NPM", "No graphic info found. (image path or resource id)");
        }

        return InflatedView;
    }

    public void removeFromCache(String path) {
        MemoryCacheUtils.removeFromCache((path.startsWith("file://") ? "" : "file://") + path, MainActivity.imageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache((path.startsWith("file://") ? "" : "file://") + path, MainActivity.imageLoader.getDiscCache());
    }

    public class ViewHolder {
        RelativeLayout.LayoutParams params;
        String imgPath;
        ImageView imgQueue;
        ImageView imgQueueBg;
        ProgressBar imgProgress;
        TextView name;
        LinearLayout LoadingBar;
    }

    public void addAll(ArrayList<GraphicItemHolder> graphics) {
        mGraphics.addAll(graphics);
        notifyDataSetChanged();
    }

    public void add(GraphicItemHolder graphic) {
        mGraphics.add(graphic);
        notifyDataSetChanged();
    }

    public void set(int position, GraphicItemHolder graphic) {
        mGraphics.set(position, graphic);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mGraphics.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mGraphics.clear();
        notifyDataSetChanged();
    }

}
