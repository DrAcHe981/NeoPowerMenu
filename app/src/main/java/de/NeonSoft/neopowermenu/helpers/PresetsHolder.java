package de.NeonSoft.neopowermenu.helpers;

import android.widget.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

public class PresetsHolder
{
		public static int TYPE_ERROR = -1;
		public static int TYPE_INTERNAL = 1;
		public static int TYPE_ONLINE = 2;
		public static int TYPE_LOADMORE = 3;
		public static int TYPE_NOMORE = 4;

		private int pType = TYPE_INTERNAL;
		private String pPresetId = "none";
		private String pName = "< Error >";
		private String pDescription = "This is an empty holder, this should normally never happen...";
		private boolean pHasGraphics = false;
		private downloadHelper pDownloadHelper = null;
		private LinearLayout pPresetRoot = null;
		private LinearLayout pPresetLayout = null;
		private ImageView pPresetImageView = null;
		private String pOldDescription = "Nothing in here...";
		private TextView pPresetTextView = null;
		private ProgressBar pPresetProgressBar = null;
		
		public PresetsHolder() {
		}
		public PresetsHolder(int type, 
												 String id,
												 String name, 
												 String desc, 
												 boolean hasGraphics,
												 downloadHelper helper,
												 LinearLayout root,
												 LinearLayout layout,
												 ImageView imageView,
												 String oldDesc,
												 TextView textView,
												 ProgressBar progressBar) {
				this.pType = type;
				this.pPresetId = id;
				this.pName = name;
				this.pDescription = desc;
				this.pHasGraphics = hasGraphics;
				this.pDownloadHelper = helper;
				this.pPresetRoot = root;
				this.pPresetLayout = layout;
				this.pPresetImageView = imageView;
				this.pOldDescription = oldDesc;
				this.pPresetTextView = textView;
				this.pPresetProgressBar = progressBar;
		}
		
		public void setType(int type) {
				this.pType = type;
		}
		public int getType() {
				return this.pType;
		}
		
		public void setId(String id) {
				this.pPresetId = id;
		}
		public String getId() {
				return this.pPresetId;
		}
		
		public void setName(String name) {
				this.pName = name;
		}
		public String getName() {
				return this.pName;
		}

		public void setDescription(String description) {
				this.pDescription = description;
		}
		public String getDescription() {
				return this.pDescription;
		}
		
		public void setHasGraphics(boolean hasGraphics) {
				this.pHasGraphics = hasGraphics;
		}
		public boolean getHasGraphics() {
				return this.pHasGraphics;
		}

		public void setDownloadHelper(downloadHelper helper) {
				this.pDownloadHelper = helper;
		}
		public downloadHelper getDownloadHelper() {
				return this.pDownloadHelper;
		}

		public void setRoot(LinearLayout root) {
				this.pPresetRoot = root;
		}
		public LinearLayout getRoot() {
				return this.pPresetRoot;
		}

		public void setLayout(LinearLayout layout) {
				this.pPresetLayout = layout;
		}
		public LinearLayout getLayout() {
				return this.pPresetLayout;
		}

		public void setImageView(ImageView imageview) {
				this.pPresetImageView = imageview;
		}
		public ImageView getImageView() {
				return this.pPresetImageView;
		}

		public void setOldDescription(String oldDesc) {
				this.pOldDescription = oldDesc;
		}
		public String getOldDescription() {
				return this.pOldDescription;
		}

		public void setTexView(TextView textView) {
				this.pPresetTextView = textView;
		}
		public TextView getTextView() {
				return this.pPresetTextView;
		}

		public void setProgressBar(ProgressBar progressBar) {
				this.pPresetProgressBar = progressBar;
		}
		public ProgressBar getProgessBar() {
				return this.pPresetProgressBar;
		}
}
