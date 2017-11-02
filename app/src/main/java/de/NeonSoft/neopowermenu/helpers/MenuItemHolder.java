package de.NeonSoft.neopowermenu.helpers;

import android.content.pm.ResolveInfo;

public class MenuItemHolder {

    private int iType = -1;
    private ResolveInfo iRI = null;
    private String iTitle1 = "";
    private String iTitle2 = "";
    private String iTitle3 = "";
    private boolean iHideDesc = false;
    private boolean iHideOnLockscreen = false;
    private String iText1 = "";
    private String iText2 = "";
    private String iText3 = "";
    private String iGraphic = "";
    private String iOnPage = "";
    private boolean iFillEmpty = false;
    private boolean iLockedWithPassword = false;
    private boolean iHideText = false;
    private String iShortcutUri1 = "";
    private String iShortcutUri2 = "";
    private String iShortcutUri3 = "";

    public MenuItemHolder() {
    }

    public MenuItemHolder(int type,
                          String title,
                          boolean hideDesc,
                          boolean hideOnLockscreen,
                          String text,
                          String graphic,
                          String onPage,
                          boolean fillEnmpty,
                          boolean lockWithPassword,
                          boolean hideText) {
        iType = type;
        iTitle1 = title;
        iHideDesc = hideDesc;
        iHideOnLockscreen = hideOnLockscreen;
        iText1 = text;
        iGraphic = graphic;
        iOnPage = onPage;
        iFillEmpty = fillEnmpty;
        iLockedWithPassword = lockWithPassword;
        iHideText = hideText;
    }

    public void setType(int type) {
        iType = type;
    }

    public int getType() {
        return iType;
    }

    public void setResolverInfo(ResolveInfo ri) { iRI = ri; }

    public ResolveInfo getResolverinfo() { return iRI; }

    public void setTitle(String title1, String title2, String title3) {
        iTitle1 = title1;
        iTitle2 = title2;
        iTitle3 = title3;
    }

    public String getTitle(int index) {
        switch (index) {
            case 1:
                return iTitle1;
            case 2:
                return iTitle2;
            case 3:
                return iTitle3;
        }
        return "";
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

    public void setText(String text1, String text2, String text3) {
        iText1 = text1;
        iText2 = text2;
        iText3 = text3;
    }

    public String getText(int index) {
        switch (index) {
            case 1:
                return iText1;
            case 2:
                return iText2;
            case 3:
                return iText3;
        }
        return "";
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

    public void setShortcutUri(String shortcutUri1, String shortcutUri2, String shortcutUri3) {
        iShortcutUri1 = shortcutUri1;
        iShortcutUri2 = shortcutUri2;
        iShortcutUri3 = shortcutUri3;
    }

    public String getShortcutUri(int index) {
        switch (index) {
            case 1:
                return iShortcutUri1;
            case 2:
                return iShortcutUri2;
            case 3:
                return iShortcutUri3;
        }
        return "";
    }
}
