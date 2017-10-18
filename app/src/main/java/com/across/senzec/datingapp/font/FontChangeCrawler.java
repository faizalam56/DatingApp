package com.across.senzec.datingapp.font;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FontChangeCrawler
{
    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    private Typeface typeface,typeface1;

    public FontChangeCrawler(Typeface typeface)
    {
        this.typeface = typeface;
    }

    public FontChangeCrawler(AssetManager assets, String assetsFontFileName,String assetsFontFileName1)
    {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
        typeface1 = Typeface.createFromAsset(assets, assetsFontFileName1);
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
             if(child instanceof TextView )
            {
                // base case
                ((TextView) child).setTypeface(typeface);
            }
             if( child instanceof EditText)
            {
                // base case
                ((EditText) child).setTypeface(typeface1);
            }
        }
    }


}
