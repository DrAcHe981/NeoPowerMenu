package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.listener.*;
import de.NeonSoft.neopowermenu.R;
import java.util.*;
import com.theartofdev.edmodo.cropper.*;
import de.NeonSoft.neopowermenu.*;
import com.nostra13.universalimageloader.cache.memory.*;
import com.nostra13.universalimageloader.utils.*;
import android.net.*;
import android.widget.AbsoluteLayout.*;

public class graphicsAdapter extends BaseAdapter
{

		Context mContext;
		ImageLoader mImageLoader;
		LayoutInflater mInfalter;

		ArrayList<CustomGraphicsList> defaultGraphics = new ArrayList<CustomGraphicsList>();
		ArrayList<CustomGraphicsList> mItems = new ArrayList<CustomGraphicsList>();
		
		public graphicsAdapter(Context context,ImageLoader imageloader) {
				mContext = context;
				mImageLoader = imageloader;
				mInfalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		public void addFallbackGraphics(Object[][] items) {
				for(int i = 0; i < items.length; i++) {
						CustomGraphicsList cgl = new CustomGraphicsList();
						cgl.resName = (String) items[i][0];
						if(items[i][1].getClass().equals(String.class)) {
								cgl.sdcardPath = (String) items[i][1];
								cgl.resId = -1;
						}
						else if(items[i][1].getClass().equals(Integer.class)) {
								cgl.sdcardPath = null;
								cgl.resId = (int) items[i][1];
						}
						cgl.fileName = (String) items[i][2];
						defaultGraphics.add(cgl);
				}
				//notifyDataSetChanged();
		}
		
		public void addAll(Object[][] items) {
				for(int i = 0; i < items.length; i++) {
						CustomGraphicsList cgl = new CustomGraphicsList();
						cgl.resName = (String) items[i][0];
						if(items[i][1].getClass().equals(String.class)) {
								cgl.sdcardPath = (String) items[i][1];
								cgl.resId = -1;
						}
						else if(items[i][1].getClass().equals(Integer.class)) {
								cgl.sdcardPath = null;
								cgl.resId = (int) items[i][1];
						}
						cgl.fileName = (String) items[i][2];
						mItems.add(cgl);
				}
				notifyDataSetChanged();
		}
		
		public void add(Object[] item) {
				CustomGraphicsList cgl = new CustomGraphicsList();
				cgl.resName = (String) item[0];
				if(item[1].getClass().equals(String.class)) {
						cgl.sdcardPath = (String) item[1];
						cgl.resId = -1;
				}
				else if(item[1].getClass().equals(Integer.class)) {
						cgl.sdcardPath = null;
						cgl.resId = (int) item[1];
				}
				cgl.fileName = (String) item[2];
				mItems.add(cgl);
				notifyDataSetChanged();
		}
		public void addAt(int position, Object[] item) {
				CustomGraphicsList cgl = new CustomGraphicsList();
				cgl.resName = (String) item[0];
				if(item[1].getClass().equals(String.class)) {
						cgl.sdcardPath = (String) item[1];
						cgl.resId = -1;
				}
				else if(item[1].getClass().equals(Integer.class)) {
						cgl.sdcardPath = null;
						cgl.resId = (int) item[1];
				}
				cgl.fileName = (String) item[2];
				mItems.add(position,cgl);
				notifyDataSetChanged();
		}
		
		public void remove(int position) {
				mItems.remove(position);
				//clearCache();
				notifyDataSetChanged();
		}
		
		public void clear() {
				mItems.clear();
				//clearCache();
				notifyDataSetChanged();
		}
		
		@Override
		public int getCount()
		{
				// TODO: Implement this method
				return mItems.size();
		}

		@Override
		public Object getItem(int p1)
		{
				// TODO: Implement this method
				return mItems.get(p1);
		}

		@Override
		public long getItemId(int p1)
		{
				// TODO: Implement this method
				return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup p3)
		{
				// TODO: Implement this method
				final ViewHolder holder;
						convertView = mInfalter.inflate(R.layout.graphicslistitem, null);
						holder = new ViewHolder();
						holder.imgQueueBg = (ImageView) convertView.findViewById(de.NeonSoft.neopowermenu.R.id.imgQueueBg);
						GraphicDrawable drawable = GraphicDrawable.builder().beginConfig().setContext(mContext).endConfig().buildRound(null, mContext.getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
						holder.imgQueueBg.setImageDrawable(drawable);
						holder.imgQueue = (ImageView) convertView
								.findViewById(de.NeonSoft.neopowermenu.R.id.imgQueue);
						
						holder.name = (TextView) convertView.findViewById(de.NeonSoft.neopowermenu.R.id.imgName);
								
						holder.LoadingBar = (LinearLayout) convertView.findViewById(de.NeonSoft.neopowermenu.R.id.graphicslistitemLinearLayout_Loading);

						convertView.setTag(holder);
				holder.imgQueue.setTag(position);
				try {
						String string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+mItems.get(position).resName,"string",MainActivity.class.getPackage().getName()));
						//String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
						holder.name.setText(string);
						holder.name.setVisibility(View.VISIBLE);
				} catch (Throwable t) {
						try {
								String string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+mItems.get(position).resName,"string",MainActivity.class.getPackage().getName()));
								//String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
								holder.name.setText(string);
								holder.name.setVisibility(View.VISIBLE);
						} catch (Throwable t1) {
								holder.name.setVisibility(View.GONE);
								Log.e("NPM:graphicsList","No String found for resource "+mItems.get(position).resName+"\n"+t);
						}
				}
				//holder.name.setText((mItems.get(position).sdcardPath==null ? "resId" : "sdcardPath"));
				try {
						if(mItems.get(position).sdcardPath != null) {
								MemoryCacheUtils.removeFromCache((mItems.get(position).sdcardPath.startsWith("file://") ? "" : "file://") + mItems.get(position).sdcardPath,MainActivity.imageLoader.getMemoryCache());
								DiskCacheUtils.removeFromCache((mItems.get(position).sdcardPath.startsWith("file://") ? "" : "file://") + mItems.get(position).sdcardPath,MainActivity.imageLoader.getDiscCache());
						mImageLoader.displayImage((mItems.get(position).sdcardPath.startsWith("file://") ? "" : "file://") + mItems.get(position).sdcardPath,
								holder.imgQueue, new SimpleImageLoadingListener() {
										@Override
										public void onLoadingStarted(String imageUri, View view) {
												holder.imgQueue.setImageBitmap(null);
												holder.imgQueue.setVisibility(View.INVISIBLE);
												holder.LoadingBar.setVisibility(View.VISIBLE);
												holder.LoadingBar.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
												super.onLoadingStarted(imageUri, view);
										}
										@Override
										public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
												holder.LoadingBar.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
												holder.LoadingBar.setVisibility(View.GONE);
												holder.imgQueue.setPadding(0,0,0,0);
												holder.imgQueue.setImageBitmap(loadedImage);
												holder.imgQueue.setVisibility(View.VISIBLE);
												holder.imgQueue.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
										}
										@Override
										public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
												Log.e("NPM:graphicsList","Failed to load image '"+imageUri+"': "+failReason.getCause().toString());
												holder.LoadingBar.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
												holder.LoadingBar.setVisibility(View.GONE);
												holder.imgQueue.setImageDrawable(mContext.getResources().getDrawable(defaultGraphics.get(position).resId));
												holder.imgQueue.setVisibility(View.VISIBLE);
												holder.imgQueue.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
										}
								});
						} else if (mItems.get(position).resId > 0) {
								holder.LoadingBar.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
								holder.LoadingBar.setVisibility(View.GONE);
								holder.imgQueue.setImageDrawable(mContext.getResources().getDrawable(mItems.get(position).resId));
								holder.imgQueue.setVisibility(View.VISIBLE);
								holder.imgQueue.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						} else {
								throw new Exception("No graphic info found. (image path or resource id)");
						}
				} catch (Throwable e) {
						e.printStackTrace();
						Log.e("NPM","GraphicsListAdapter error: "+e);
				}
				
				return convertView;
		}
		
		public class ViewHolder {
				RelativeLayout.LayoutParams params;
				ImageView imgQueue;
				ImageView imgQueueBg;
				TextView name;
				LinearLayout LoadingBar;
		}

		public void clearCache() {
				mImageLoader.clearDiscCache();
				mImageLoader.clearMemoryCache();
		}
		
		public class CustomGraphicsList {

				public String sdcardPath = null;
				public int resId = -1;
				public boolean isSeleted = false;
				public String resName = null;
				public String fileName = null;

		}
}
