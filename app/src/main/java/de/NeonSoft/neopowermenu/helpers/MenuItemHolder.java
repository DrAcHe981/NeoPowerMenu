package de.NeonSoft.neopowermenu.helpers;

import java.util.ArrayList;

public class MenuItemHolder {

    private int iType = -1;
    private boolean iHideDesc = false;
    private boolean iHideOnLockscreen = false;
    private String iGraphic = "";
    private String iPageId = "";
    private String iOnPage = "";
    private boolean iFillEmpty = false;
    private boolean iLockedWithPassword = false;
    private boolean iHideText = false;
    private boolean iHorizontal = true;
    private Boolean iHideFirstItemInFolder = false;

    private ArrayList<String> iTitle = new ArrayList<>();
    private ArrayList<String> iText = new ArrayList<>();
    private ArrayList<String> iShortcutUri = new ArrayList<>();

    public MenuItemHolder() {
    }

    public void setType(int type) {
        iType = type;
    }
    public int getType() {
        return iType;
    }

    public void setTitle (ArrayList<String> title) {
        iTitle = title;
    }
    public String getTitle(int index) {
        try {
            return iTitle.get(index-1);
        } catch (Throwable ignored) {
            return "";
        }
    }
    public ArrayList<String> getTitles() {
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

    public void setText (ArrayList<String> text) {
        iText = text;
    }
    public String getText(int index) {
        try {
            return iText.get(index-1);
        } catch (Throwable ignored) {
            return "";
        }
    }
    public ArrayList<String> getTexts() {
        return iText;
    }

    public void setGraphic(String graphic) {
        iGraphic = graphic;
    }
    public String getGraphic() {
        return iGraphic;
    }

    public void setOnPage(String onPage) {
        iOnPage = onPage;
    }
    public String getOnPage() {
        return iOnPage;
    }

    public void setFillEmpty(boolean fillEmpty) {
        iFillEmpty = fillEmpty;
    }
    public boolean getFillEmpty() {
        return iFillEmpty;
    }

    public void setLockedWithPassword(boolean lockWithPassword) { iLockedWithPassword = lockWithPassword; }
    public boolean getLockedWithPassword() {
        return iLockedWithPassword;
    }

    public void setHideText(boolean hideText) { iHideText = hideText; }
    public boolean getHideText() {
        return iHideText;
    }

    public void setShortcutUri (ArrayList<String> uri) {
        iShortcutUri = uri;
    }
    public String getShortcutUri(int index) {
        try {
            return iShortcutUri.get(index-1);
        } catch (Throwable ignored) {
            return "";
        }
    }
    public ArrayList<String> getShortcutUris() {
        return iShortcutUri;
    }

    public void setHorizontal(boolean horizontal) {
        iHorizontal = horizontal;
    }
    public boolean getHorizontal() {
        return iHorizontal;
    }

    public void setHideFirstItemInFolder(boolean show) {
        this.iHideFirstItemInFolder = show;
    }
    public Boolean getHideFirstItemInFolder() {
        return this.iHideFirstItemInFolder;
    }

    public void setPageId(String id) {
        this.iPageId = id;
    }
    public String getPageId() {
        return this.iPageId;
    }
}
