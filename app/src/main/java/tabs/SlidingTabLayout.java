package tabs;

/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
public class SlidingTabLayout extends HorizontalScrollView {
    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

    }

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private int mTabViewLayoutId;
    private int mTabViewTextViewId;
    private boolean mDistributeEvenly;

    private ViewPager mViewPager;
    private SparseArray<String> mContentDescriptions = new SparseArray<String>();
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final SlidingTabStrip mTabStrip;

    private static Context contexto;

    //Imágenes de las tabs de Streaming.
    private static int[] imagenesStreaming =
    {
            mx.com.filarmonica.R.drawable.video_icon,
            mx.com.filarmonica.R.drawable.musica_icon_off,
            mx.com.filarmonica.R.drawable.video_icon_off,
            mx.com.filarmonica.R.drawable.musica_icon,
            mx.com.filarmonica.R.drawable.live_icon,
            mx.com.filarmonica.R.drawable.live_icon_on
    };

    //Imágenes de las tabs de noticias.
    private static int[] imagenesNoticias =
    {
            mx.com.filarmonica.R.drawable.fb_on,
            mx.com.filarmonica.R.drawable.fb,
            mx.com.filarmonica.R.drawable.tw_on,
            mx.com.filarmonica.R.drawable.tw,
            mx.com.filarmonica.R.drawable.insta_on,
            mx.com.filarmonica.R.drawable.insta
    };

    /********************** CONSTANTES NOTICIAS *************************/
    //ÍNDICES DE IMÁGENES.
    private static final int FACEBOOK_ON   = 0;
    private static final int FACEBOOK_OFF  = 1;
    private static final int TWITTER_ON    = 2;
    private static final int TWITTER_OFF   = 3;
    private static final int INSTAGRAM_ON  = 4;
    private static final int INSTAGRAM_OFF = 5;

    //ÍNDICES DE POSICIONES DE TABS.
    private static final int POSICION_FACEBOOK  = 0;
    private final static int POSICION_TWITTER   = 1;
    private final static int POSICION_INSTAGRAM = 2;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        contexto = context;
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     *
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }

        //Hacemos que el título de la primera tab en la sección de noticias aparezca en rojo,
        //indicando que esa tab está seleccionada.
        CharSequence descripcion = mTabStrip.getContentDescription();
        if(descripcion.equals("Noticias"))
        {
            TextView tabNoticias  = (TextView) mTabStrip.getChildAt(0);
            tabNoticias.setTextColor(Color.parseColor("#E31836"));
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            tabTitleView.setText(adapter.getPageTitle(i));
            tabView.setOnClickListener(tabClickListener);
            String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            }

            mTabStrip.addView(tabView);
            if (i == mViewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }
        }
    }

    public void setContentDescription(int i, String desc) {
        mContentDescriptions.put(i, desc);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }

            CharSequence descripcion = mTabStrip.getContentDescription();
            if(descripcion.equals("Streaming"))
            {
                //Colocamos las imágenes de las tabs.
                switch(position)
                {
                    case 0:
                    {
                        Drawable imagenTabVideo = contexto.getResources().
                                getDrawable(imagenesStreaming[0]);
                        imagenTabVideo.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringVideo = new SpannableString(" ");
                        ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de video.
                        TextView viewTabVideo = (TextView) mTabStrip.getChildAt(0);
                        viewTabVideo.setText(spannableStringVideo);

                        Drawable imagenTabMusica = contexto.getResources().
                                getDrawable(imagenesStreaming[1]);
                        imagenTabMusica.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringMusica = new SpannableString(" ");
                        ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabMusica = (TextView) mTabStrip.getChildAt(1);
                        viewTabMusica.setText(spannableStringMusica);

                        Drawable imagenTabStreaming = contexto.getResources().
                                getDrawable(imagenesStreaming[4]);
                        imagenTabStreaming.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringStreaming = new SpannableString(" ");
                        ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                        viewTabStreaming.setText(spannableStringStreaming);

                        break;
                    }

                    case 1:
                    {
                        Drawable imagenTabMusica = contexto.getResources().
                                getDrawable(imagenesStreaming[3]);
                        imagenTabMusica.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringMusica = new SpannableString(" ");
                        ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabMusica = (TextView) mTabStrip.getChildAt(1);
                        viewTabMusica.setText(spannableStringMusica);

                        Drawable imagenTabVideo = contexto.getResources().
                                getDrawable(imagenesStreaming[2]);
                        imagenTabVideo.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringVideo = new SpannableString(" ");
                        ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de video.
                        TextView viewTabVideo = (TextView) mTabStrip.getChildAt(0);
                        viewTabVideo.setText(spannableStringVideo);

                        Drawable imagenTabStreaming = contexto.getResources().
                                getDrawable(imagenesStreaming[4]);
                        imagenTabStreaming.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringStreaming = new SpannableString(" ");
                        ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                        viewTabStreaming.setText(spannableStringStreaming);

                        break;
                    }

                    case 2:
                    {
                        Drawable imagenTabMusica = contexto.getResources().
                                getDrawable(imagenesStreaming[1]);
                        imagenTabMusica.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringMusica = new SpannableString(" ");
                        ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabMusica = (TextView) mTabStrip.getChildAt(1);
                        viewTabMusica.setText(spannableStringMusica);

                        Drawable imagenTabVideo = contexto.getResources().
                                getDrawable(imagenesStreaming[2]);
                        imagenTabVideo.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringVideo = new SpannableString(" ");
                        ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de video.
                        TextView viewTabVideo = (TextView) mTabStrip.getChildAt(0);
                        viewTabVideo.setText(spannableStringVideo);

                        Drawable imagenTabStreaming = contexto.getResources().
                                getDrawable(imagenesStreaming[5]);
                        imagenTabStreaming.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringStreaming = new SpannableString(" ");
                        ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        //Sacamos el view donde irá la imagen de música.
                        TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                        viewTabStreaming.setText(spannableStringStreaming);

                        break;
                    }
                }
            }
            else if(descripcion.equals("Noticias"))
            {
                //Cambiamos el texto.
                switch(position)
                {
                    /*
                     * Facebook  - ON
                     * Twitter   - OFF
                     * Instagram - OFF
                     */
                    case 0:
                    {
                        //Facebook.
                        Drawable imagenTabFacebook = contexto.getResources().
                                getDrawable(imagenesNoticias[FACEBOOK_ON]);
                        imagenTabFacebook.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringFacebook = new SpannableString(" ");
                        ImageSpan imageSpanFacebook = new ImageSpan(imagenTabFacebook,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringFacebook.setSpan(imageSpanFacebook, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabFacebook = (TextView) mTabStrip.getChildAt(POSICION_FACEBOOK);
                        viewTabFacebook.setText(spannableStringFacebook);

                        //Twitter.
                        Drawable imagenTabTwitter = contexto.getResources().
                                getDrawable(imagenesNoticias[TWITTER_OFF]);
                        imagenTabTwitter.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringTwitter = new SpannableString(" ");
                        ImageSpan imageSpanTwitter = new ImageSpan(imagenTabTwitter,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringTwitter.setSpan(imageSpanTwitter, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabTwitter = (TextView) mTabStrip.getChildAt(POSICION_TWITTER);
                        viewTabTwitter.setText(spannableStringTwitter);

                        //Instagram.
                        Drawable imagenTabInstagram = contexto.getResources().
                                getDrawable(imagenesNoticias[INSTAGRAM_OFF]);
                        imagenTabInstagram.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringInstagram = new SpannableString(" ");
                        ImageSpan imageSpanInstagram = new ImageSpan(imagenTabInstagram,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringInstagram.setSpan(imageSpanInstagram, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabInstagram = (TextView) mTabStrip.getChildAt(POSICION_INSTAGRAM);
                        viewTabInstagram.setText(spannableStringInstagram);

                        break;
                    }

                    /*
                     * Facebook  - OFF
                     * Twitter   - ON
                     * Instagram - OFF
                     */
                    case 1:
                    {
                        //Facebook.
                        Drawable imagenTabFacebook = contexto.getResources().
                                getDrawable(imagenesNoticias[FACEBOOK_OFF]);
                        imagenTabFacebook.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringFacebook = new SpannableString(" ");
                        ImageSpan imageSpanFacebook = new ImageSpan(imagenTabFacebook,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringFacebook.setSpan(imageSpanFacebook, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabFacebook = (TextView) mTabStrip.getChildAt(POSICION_FACEBOOK);
                        viewTabFacebook.setText(spannableStringFacebook);

                        //Twitter.
                        Drawable imagenTabTwitter = contexto.getResources().
                                getDrawable(imagenesNoticias[TWITTER_ON]);
                        imagenTabTwitter.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringTwitter = new SpannableString(" ");
                        ImageSpan imageSpanTwitter = new ImageSpan(imagenTabTwitter,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringTwitter.setSpan(imageSpanTwitter, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabTwitter = (TextView) mTabStrip.getChildAt(POSICION_TWITTER);
                        viewTabTwitter.setText(spannableStringTwitter);

                        //Instagram.
                        Drawable imagenTabInstagram = contexto.getResources().
                                getDrawable(imagenesNoticias[INSTAGRAM_OFF]);
                        imagenTabInstagram.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringInstagram = new SpannableString(" ");
                        ImageSpan imageSpanInstagram = new ImageSpan(imagenTabInstagram,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringInstagram.setSpan(imageSpanInstagram, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabInstagram = (TextView) mTabStrip.getChildAt(POSICION_INSTAGRAM);
                        viewTabInstagram.setText(spannableStringInstagram);

                        break;
                    }

                    /*
                     * Facebook  - OFF
                     * Twitter   - OFF
                     * Instagram - ON
                     */
                    case 2:
                    {
                        //Facebook.
                        Drawable imagenTabFacebook = contexto.getResources().
                                getDrawable(imagenesNoticias[FACEBOOK_OFF]);
                        imagenTabFacebook.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringFacebook = new SpannableString(" ");
                        ImageSpan imageSpanFacebook = new ImageSpan(imagenTabFacebook,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringFacebook.setSpan(imageSpanFacebook, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabFacebook = (TextView) mTabStrip.getChildAt(POSICION_FACEBOOK);
                        viewTabFacebook.setText(spannableStringFacebook);

                        //Twitter.
                        Drawable imagenTabTwitter = contexto.getResources().
                                getDrawable(imagenesNoticias[TWITTER_OFF]);
                        imagenTabTwitter.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringTwitter = new SpannableString(" ");
                        ImageSpan imageSpanTwitter = new ImageSpan(imagenTabTwitter,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringTwitter.setSpan(imageSpanTwitter, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabTwitter = (TextView) mTabStrip.getChildAt(POSICION_TWITTER);
                        viewTabTwitter.setText(spannableStringTwitter);

                        //Instagram.
                        Drawable imagenTabInstagram = contexto.getResources().
                                getDrawable(imagenesNoticias[INSTAGRAM_ON]);
                        imagenTabInstagram.setBounds(0, 0, 60, 60);
                        SpannableString spannableStringInstagram = new SpannableString(" ");
                        ImageSpan imageSpanInstagram = new ImageSpan(imagenTabInstagram,
                                ImageSpan.ALIGN_BOTTOM);
                        spannableStringInstagram.setSpan(imageSpanInstagram, 0, 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        TextView viewTabInstagram = (TextView) mTabStrip.getChildAt(POSICION_INSTAGRAM);
                        viewTabInstagram.setText(spannableStringInstagram);

                        break;
                    }
                }
            }
        }

    }

    private class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);

                    CharSequence descripcion = mTabStrip.getContentDescription();

                    if(descripcion.equals("Streaming"))
                    {
                        switch(i)
                        {
                            case 0:
                            {
                                Drawable imagenTabVideo = contexto.getResources().
                                        getDrawable(imagenesStreaming[0]);
                                imagenTabVideo.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringVideo = new SpannableString(" ");
                                ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ((TextView) v).setText(spannableStringVideo);

                                Drawable imagenTabMusica = contexto.getResources().
                                        getDrawable(imagenesStreaming[1]);
                                imagenTabMusica.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringMusica = new SpannableString(" ");
                                ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de música.
                                TextView viewTabMusica = (TextView )mTabStrip.getChildAt(1);
                                viewTabMusica.setText(spannableStringMusica);

                                Drawable imagenTabStreaming = contexto.getResources().
                                        getDrawable(imagenesStreaming[4]);
                                imagenTabStreaming.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringStreaming = new SpannableString(" ");
                                ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de música.
                                TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                                viewTabStreaming.setText(spannableStringStreaming);

                                break;
                            }

                            case 1:
                            {
                                Drawable imagenTabMusica = contexto.getResources().
                                        getDrawable(imagenesStreaming[3]);
                                imagenTabMusica.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringMusica = new SpannableString(" ");
                                ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ((TextView) v).setText(spannableStringMusica);

                                Drawable imagenTabVideo = contexto.getResources().
                                        getDrawable(imagenesStreaming[2]);
                                imagenTabVideo.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringVideo = new SpannableString(" ");
                                ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de video.
                                TextView viewTabVideo = (TextView )mTabStrip.getChildAt(0);
                                viewTabVideo.setText(spannableStringVideo);

                                Drawable imagenTabStreaming = contexto.getResources().
                                        getDrawable(imagenesStreaming[4]);
                                imagenTabStreaming.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringStreaming = new SpannableString(" ");
                                ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de música.
                                TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                                viewTabStreaming.setText(spannableStringStreaming);

                                break;
                            }

                            case 2:
                            {
                                Drawable imagenTabMusica = contexto.getResources().
                                        getDrawable(imagenesStreaming[1]);
                                imagenTabMusica.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringMusica = new SpannableString(" ");
                                ImageSpan imageSpanMusica = new ImageSpan(imagenTabMusica,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringMusica.setSpan(imageSpanMusica, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de música.
                                TextView viewTabMusica = (TextView) mTabStrip.getChildAt(1);
                                viewTabMusica.setText(spannableStringMusica);

                                Drawable imagenTabVideo = contexto.getResources().
                                        getDrawable(imagenesStreaming[2]);
                                imagenTabVideo.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringVideo = new SpannableString(" ");
                                ImageSpan imageSpanVideo = new ImageSpan(imagenTabVideo,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringVideo.setSpan(imageSpanVideo, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de video.
                                TextView viewTabVideo = (TextView) mTabStrip.getChildAt(0);
                                viewTabVideo.setText(spannableStringVideo);

                                Drawable imagenTabStreaming = contexto.getResources().
                                        getDrawable(imagenesStreaming[5]);
                                imagenTabStreaming.setBounds(0, 0, 60, 60);
                                SpannableString spannableStringStreaming = new SpannableString(" ");
                                ImageSpan imageSpanStreaming = new ImageSpan(imagenTabStreaming,
                                        ImageSpan.ALIGN_BOTTOM);
                                spannableStringStreaming.setSpan(imageSpanStreaming, 0, 1,
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                //Sacamos el view donde irá la imagen de música.
                                TextView viewTabStreaming = (TextView) mTabStrip.getChildAt(2);
                                viewTabStreaming.setText(spannableStringStreaming);

                                break;
                            }
                        }
                    }
                    return;
                }
            }
        }
    }

    //Método que servirá para poder diferenciar de que actividad se configuran las tabs.
    public void setDescription(String description)
    {
        mTabStrip.setContentDescription(description);
    }
}