package de.NeonSoft.neopowermenu.Preferences;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.theartofdev.edmodo.cropper.*;
import de.NeonSoft.neopowermenu.*;

import de.NeonSoft.neopowermenu.R;
import android.view.View.*;
import java.io.*;
import android.view.animation.*;

public class Cropper extends android.support.v4.app.Fragment
implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener
{
		
		ImageView ImageView_RotateLeft;
		ImageView ImageView_RotateRight;

		LinearLayout LinearLayout_RoundCrop;
		TextView TextView_RoundCrop;
		Switch Switch_RoundCrop;

		RelativeLayout progressHolder;
		ProgressBar progress;
		
		private CropImageView mCropImageView;
		private String mItem;
		private Uri mUri;
		private String mSaveAs;
		
		public Cropper(String item,Uri uri,String saveAs) {
				this.mItem = item;
				this.mUri = uri;
				this.mSaveAs = saveAs;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Cropper";
				MainActivity.actionbar.setTitle("NeoPowerMenu");
				try {
						String string = getResources().getString(getResources().getIdentifier("powerMenuMain_"+mItem,"string",MainActivity.class.getPackage().getName()));
						MainActivity.actionbar.setSubTitle(string);
				} catch (Throwable t) {
						try {
								String string = getResources().getString(getResources().getIdentifier("powerMenuBottom_"+mItem,"string",MainActivity.class.getPackage().getName()));
								MainActivity.actionbar.setSubTitle(string);
						} catch (Throwable t1) {
								Log.e("NPM:cropper","No String found for resource "+mItem+"\n"+t);
						}
				}

				MainActivity.actionbar.setButton(getString(R.string.graphics_Crop).split("\\|")[0],R.drawable.ic_content_content_cut,new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										handleCropResult(null,mCropImageView.getCroppedImage(),null);
								}
						});
						
				View InflatedView = inflater.inflate(R.layout.cropper, container, false);
				
				ImageView_RotateLeft = (ImageView) InflatedView.findViewById(R.id.cropperImageView_RotateLeft);
				ImageView_RotateRight = (ImageView) InflatedView.findViewById(R.id.cropperImageView_RotateRight);
				ImageView_RotateLeft.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										mCropImageView.rotateImage(-90);
								}
						});
				ImageView_RotateRight.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										mCropImageView.rotateImage(90);
								}
						});
				
        mCropImageView = (CropImageView) InflatedView.findViewById(R.id.cropImageView);
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
				
				mCropImageView.setAutoZoomEnabled(true);
				mCropImageView.setFixedAspectRatio(true);
				mCropImageView.setGuidelines(CropImageView.Guidelines.ON_TOUCH);
				mCropImageView.setShowCropOverlay(true);
				mCropImageView.setShowProgressBar(true);
				
				LinearLayout_RoundCrop = (LinearLayout) InflatedView.findViewById(R.id.cropperLinearLayout_CropRound);
				TextView_RoundCrop = (TextView) InflatedView.findViewById(R.id.cropperTextView_CropRound);
				TextView_RoundCrop.setText(getString(R.string.graphics_Crop).split("\\|")[1]);
				Switch_RoundCrop = (Switch) InflatedView.findViewById(R.id.cropperSwitch_CropRound);
				Switch_RoundCrop.setChecked(true);
				Switch_RoundCrop.setClickable(false);
				Switch_RoundCrop.setFocusable(false);
				LinearLayout_RoundCrop.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										Switch_RoundCrop.setChecked(!Switch_RoundCrop.isChecked());
										mCropImageView.setCropShape(Switch_RoundCrop.isChecked() ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE);
								}
						});
						
				progressHolder = (RelativeLayout) InflatedView.findViewById(R.id.cropperRelativeLayout_Progress);
				progressHolder.setVisibility(View.GONE);
				progress = (ProgressBar) InflatedView.findViewById(R.id.cropperProgressBar_Progress);

				progressHolder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// Just prevent touch trough...
								}
						});
				
				if(mUri!=null) {
						mCropImageView.setImageUriAsync(mUri);
				} else {
						MainActivity.changePrefPage(new PreferencesGraphicsFragment(), false);
				}
				
				return InflatedView;
		}

		@Override
		public void onGetCroppedImageComplete(CropImageView p1, Bitmap bitmap, Exception error)
		{
				// TODO: Implement this method
				handleCropResult(null, bitmap, error);
		}

		@Override
		public void onSetImageUriComplete(CropImageView p1, Uri uri, Exception error)
		{
				// TODO: Implement this method
		}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result.getUri(), null, result.getError());
        }
    }
		
    private void handleCropResult(Uri uri, Bitmap bitmap, Exception error) {
        if (error == null) {
            //Intent intent = new Intent(getActivity(), CropResultActivity.class);
            if (uri != null) {
                //intent.putExtra("URI", uri);
                mCropImageView.saveCroppedImageAsync(Uri.parse("file://"+getActivity().getFilesDir().getPath()+"/images/"+mSaveAs));
            } else {
								new saveImageAsync().execute(bitmap);
						}
            //startActivity(intent);
        } else {
            Log.e("NPM:cropper", "Failed to crop image", error);
            //Toast.makeText(getActivity(), "Image crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
		
		class saveImageAsync extends AsyncTask<Bitmap, String, String>
		{

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						if(mSaveAs.contains("Progress")) {
								MainActivity.preferences.edit().putString("ProgressDrawable","file").commit();
						}
						progressHolder.setVisibility(View.VISIBLE);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
				}
				
				@Override
				protected String doInBackground(Bitmap[] p1)
				{
						// TODO: Implement this method
								Bitmap saveBitmap = (mCropImageView.getCropShape()==CropImageView.CropShape.OVAL ? CropImage.toOvalBitmap(p1[0]) : p1[0]);
						Log.i("NPM:cropper","Saving "+(mCropImageView.getCropShape()==CropImageView.CropShape.OVAL ? "oval" : "rectangle")+" shaped image to "+getActivity().getFilesDir().getPath()+"/images/"+mSaveAs);
								FileOutputStream out = null;
								try {
										out = new FileOutputStream(getActivity().getFilesDir().getPath()+"/images/"+mSaveAs);
										saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
								} catch (Throwable t) {
										Log.e("NPM:cropper","Failed to save: "+t.toString());
										return t.toString();
								} finally {
										try
										{
												out.close();
										}
										catch (IOException e)
										{
												Log.e("NPM:cropper","Failed to save: "+e.toString());
												return e.toString();
										}
								}
						return null;
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
						progressHolder.setVisibility(View.GONE);
						if(p1!=null) {
								Toast.makeText(getActivity(), "Failed to save,please try again...\nFor more info check the logs.", Toast.LENGTH_LONG).show();
								Log.e("NPM:cropper","Failed to save cropped image: "+p1);
						} else {
								MainActivity.changePrefPage(new PreferencesGraphicsFragment(), true);
						}
				}
				
		}
		
}
