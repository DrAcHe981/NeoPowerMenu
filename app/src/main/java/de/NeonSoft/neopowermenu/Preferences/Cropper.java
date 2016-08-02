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

public class Cropper extends android.support.v4.app.Fragment
implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener
{
		
		ImageView ImageView_RotateLeft;
		ImageView ImageView_RotateRight;

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

				MainActivity.actionbar.setButton(getString(R.string.graphics_Crop),R.drawable.ic_content_content_cut,new OnClickListener() {

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
				
				if(mUri!=null) {
						mCropImageView.setImageUriAsync(mUri);
				} else {
						MainActivity.fragmentManager.beginTransaction().setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesGraphicsFragment()).commit();
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
								Bitmap saveBitmap = (mCropImageView.getCropShape()==CropImageView.CropShape.OVAL ? CropImage.toOvalBitmap(bitmap) : bitmap);
								FileOutputStream out = null;
								try {
										out = new FileOutputStream(getActivity().getFilesDir().getPath()+"/images/"+mSaveAs);
										saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
								} catch (Throwable t) {
										Log.e("NPM:cropper","Failed to save: "+t.toString());
								} finally {
										try
										{
												out.close();
										}
										catch (IOException e)
										{
												Log.e("NPM:cropper","Failed to save: "+e.toString());
										}
								}
            }
						MainActivity.fragmentManager.beginTransaction().setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesGraphicsFragment()).commit();
            //startActivity(intent);
        } else {
            Log.e("NPM:cropper", "Failed to crop image", error);
            //Toast.makeText(getActivity(), "Image crop failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
		
}
