package de.NeonSoft.neopowermenu.tour;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.util.*;

import android.support.v4.app.Fragment;

public class tourPage extends Fragment
{
    // Store instance variables
    public static Activity mContext;
    private String title;
    private int page;

    public static ArrayList<String> onlineIds = new ArrayList<String>();

    public tourPage()
    {
        this.page = -1;
        this.title = null;
    }
    // newInstance constructor for creating fragment with arguments


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.page = args.getInt("page");
        this.title = args.getString("title");
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(page != -1) {
            View view = inflater.inflate(R.layout.tourpage, container, false);

            ImageView Image = (ImageView) view.findViewById(R.id.tourPageImage);
            Image.setVisibility(View.INVISIBLE);
            if (tourFragment.pageImages.size() > page) {
                tourFragment.pageImages.set(page, Image);
            } else {
                tourFragment.pageImages.add(Image);
            }

            LinearLayout Holder = (LinearLayout) view.findViewById(R.id.tourPageTextHolder);
            Holder.setVisibility(View.INVISIBLE);
            if (tourFragment.pageTextHolders.size() > page) {
                tourFragment.pageTextHolders.set(page, Holder);
            } else {
                tourFragment.pageTextHolders.add(Holder);
            }
            TextView Title = (TextView) view.findViewById(R.id.tourPageTitle);
            TextView Text = (TextView) view.findViewById(R.id.tourPageText);

            LinearLayout ButtonHolder = (LinearLayout) view.findViewById(R.id.tourPageButton);
            ButtonHolder.setVisibility(View.GONE);
            TextView ButtonText = (TextView) view.findViewById(R.id.tourPageButtonText);

            if (page != -1 && title != null) {
                if (page == 0) {
                    Title.setText(getString(R.string.tourPage_0_Title));
                    Image.setVisibility(View.VISIBLE);
                    Image.setPadding((int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext));
                    Text.setText(getString(R.string.tourPage_0_Text));
                    ButtonText.setText(getString(R.string.tourPage_Skip));
                    ButtonHolder.setVisibility(View.VISIBLE);
                    ButtonHolder.setOnClickListener(tourFragment.finishOnClick);
                    Holder.setVisibility(View.VISIBLE);
                } else if (page == 1) {
                    Title.setText(getString(R.string.tourPage_1_Title));
                    Text.setText(getString(R.string.tourPage_1_Text));
                } else if (page == 2) {
                    Title.setText(getString(R.string.tourPage_2_Title));
                    Text.setText(getString(R.string.tourPage_2_Text));
                } else if (page == 3) {
                    Title.setText(getString(R.string.tourPage_3_Title));
                    Text.setText(getString(R.string.tourPage_3_Text));
                } else if (page == 4) {
                    Title.setText(getString(R.string.tourPage_4_Title));
                    Text.setText(getString(R.string.tourPage_4_Text));
                } else if (page == 5) {
                    Title.setText(getString(R.string.tourPage_5_Title));
                    Text.setText(getString(R.string.tourPage_5_Text));
                } else if (page == 6) {
                    Title.setText(getString(R.string.tourPage_6_Title));
                    Text.setText(getString(R.string.tourPage_6_Text));
                } else if (page == 7) {
                    Title.setText(getString(R.string.tourPage_7_Title));
                    Image.setVisibility(View.VISIBLE);
                    Image.setPadding((int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext), (int) helper.convertDpToPixel(80f, mContext));
                    Text.setText(getString(R.string.tourPage_7_Text));
                }
            }
            return view;
        }
        return null;
    }

}
