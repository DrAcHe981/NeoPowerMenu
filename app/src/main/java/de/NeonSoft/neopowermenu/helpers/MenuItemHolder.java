package de.NeonSoft.neopowermenu.helpers;

public class MenuItemHolder {

		private int iType = -1;
		private String iTitle = "Unknown";
		private boolean iHideDesc = false;
		private boolean iHideOnLockscreen = false;
		private String iText = "";
		private String iGraphic = "";
		private String iOnPage = "";

		public MenuItemHolder() {
		}

		public MenuItemHolder(int type,
		                      String title,
		                      boolean hideDesc,
		                      boolean hideOnLockscreen,
		                      String text,
		                      String graphic,
		                      String onPage) {
				iType = type;
				iTitle = title;
				iHideDesc = hideDesc;
				iHideOnLockscreen = hideOnLockscreen;
				iText = text;
				iGraphic = graphic;
				iOnPage = onPage;
		}

		public void setType(int type) {
				iType = type;
		}
		public int getType() {
				return iType;
		}

		public void setTitle(String title) {
				iTitle = title;
		}
		public String getTitle() {
				return iTitle;
		}

		public void setHideDesc(boolean hideDesc) {
				iHideDesc = hideDesc;
		}
		public boolean getHideDesc() {
				return iHideDesc;
		}

		public void setHideOnLockScreen(boolean hideOnLockscreen) {
				iHideOnLockscreen = hideOnLockscreen;
		}
		public boolean getHideOnLockScreen() {
				return iHideOnLockscreen;
		}

		public void setText(String text) {
				iText = text;
		}
		public String getText() {
				return iText;
		}

		public void setGraphic(String graphic) {
				iGraphic = graphic;
		}
		public String getGraphic() {
				return iGraphic;
		}

		public void setOnPage(String onPage){
				iOnPage = onPage;
		}
		public String getOnPage() {
				return iOnPage;
		}
		
}
