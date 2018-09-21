package com.animal.harness.hodoo.hodooharness.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.animal.harness.hodoo.hodooharness.R;
import com.animal.harness.hodoo.hodooharness.base.BaseFragment;
import com.animal.harness.hodoo.hodooharness.util.HodooUtil;
import com.animal.harness.hodoo.hodooharness.view.StopWatch;

public class ActivityFragment extends BaseFragment implements View.OnClickListener {
    private StopWatch stopWatch;
    private Button stopWatchStart;
    private Button stopWatchReset;
    private TextView tipsIcon;
    private TextView tipsContent;
    private int count = 0;
    private boolean restartState = false;

    @SuppressLint("ObjectAnimatorBinding") ObjectAnimator backgroundColorAnimator;

    private String[] tipsStr = {
            "산책용 목줄을 매면 주저앉는 경우에는 강제로 데려가지 마시고 실내에서 익숙하게 한 후에 서서히 외부로 나오게 하는 것이 좋습니다.",
            "강아지가 흥분하여 제어가 안될 경우에는 목줄을 당기며 \"안돼\"를 강하게 말해 주세요.",
            "일반적으로 분리불안증을 가지고 있는 반려견의 경우에 가장 좋은 처방약은 바로 산책입니다.",
    };

    public ActivityFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //stop_watch
        RelativeLayout wrap = (RelativeLayout) inflater.inflate(R.layout.fragment_stopwatch, container, false);
        RelativeLayout btnWrap = wrap.findViewById(R.id.stop_watch_btn);
        tipsIcon = wrap.findViewById(R.id.tips);
        tipsContent = wrap.findViewById(R.id.tips_content);

        tipsIcon.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tipsContent.getLayoutParams();
                params.setMargins(0, tipsIcon.getHeight() / 2, 0, 0);
                tipsContent.setLayoutParams(params);

                SpannableString ss = new SpannableString(tipsStr[count]);
                ss.setSpan(new Margin(1, tipsIcon.getWidth() + 20), 0, ss.length(), 0);
                tipsContent.setText(ss);
//                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tipsContent.getMeasuredHeight());
//                ((RelativeLayout) tipsContent.getParent()).setLayoutParams(params);
                Log.e(TAG, String.format("tipsContent height : %d", tipsContent.getHeight()));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTipText();
                        count++;
                    }
                }, 1000 * 30);
                tipsIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        stopWatchStart = wrap.findViewById(R.id.stop_watch_start);
        stopWatchReset = wrap.findViewById(R.id.stop_watch_reset);
        stopWatchStart.setOnClickListener(this);
        stopWatchReset.setOnClickListener(this);
        stopWatch = wrap.findViewById(R.id.stop_watch);
        backgroundColorAnimator = ObjectAnimator.ofObject(stopWatchStart,
                "textColor",
                new ArgbEvaluator(),
                Color.WHITE,
                Color.BLACK);
        backgroundColorAnimator.setDuration(1000);

        return wrap;
    }
    private void setTipText() {
        tipsContent.animate().alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                if ( count >= tipsStr.length ) count = 0;
                SpannableString ss = new SpannableString(tipsStr[count]);
                ss.setSpan(new Margin(1, tipsIcon.getWidth() + 20), 0, ss.length(), 0);
                tipsContent.setText(ss);
                tipsContent.animate().alpha(1).setDuration(1000);
                count++;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTipText();
                    }
                }, 1000 * 30);
            }
        }).setDuration(1000);
    }

    @Override
    public void onFragmentSelected() {

    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(final View v) {
        final TransitionDrawable background = (TransitionDrawable) stopWatchStart.getBackground();
        switch (v.getId()) {
            case R.id.stop_watch_start :
                /* start */
                if ( !stopWatch.isStart() ) {
                    stopWatchReset.setVisibility(View.VISIBLE);
                    stopWatchReset.animate().alpha(1).setDuration(1000).withLayer();
                    if ( !restartState ) {
                        v.animate().translationX( -(v.getWidth() / 2 + 50) ).setDuration(500).withLayer();
                    }

                    backgroundColorAnimator.start();
                    background.startTransition(1000);

                    ((TextView) v).setText("중지");
                    if ( stopWatch != null )
                        stopWatch.start();
                    restartState = true;
                }
                /* stop */
                else {
                    if ( !restartState ) {
                        stopWatchReset.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                stopWatchReset.setVisibility(View.GONE);
                            }
                        });
                        v.animate().translationX( 0 ).setDuration(500).withLayer();
                    }
                    stopWatch.stop();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    ((TextView) v).setText("시작");

                }

                break;
            case R.id.stop_watch_reset :
                /* restart */
                if ( stopWatch.isStart() ) {
                    stopWatch.stop();
                    stopWatch.reset();
                    background.reverseTransition(1000);
                    backgroundColorAnimator.reverse();
                    stopWatchStart.setText("시작");
                }

                stopWatchStart.animate().translationX( 0 ).setDuration(500).withLayer();
                v.animate().alpha(0).setDuration(500).withLayer().withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        v.setVisibility(View.GONE);
                    }
                });
                restartState = false;
                Log.e(TAG, "stopWatchReset");
                break;
        }
    }

    class Margin implements LeadingMarginSpan.LeadingMarginSpan2
    {
        private int margin;
        private int lines;

        Margin(int lines, int margin)
        {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public void drawLeadingMargin(Canvas arg0, Paint arg1, int arg2,
                                      int arg3, int arg4, int arg5, int arg6, CharSequence arg7,
                                      int arg8, int arg9, boolean arg10, Layout arg11)
        {
// TODO Auto-generated method stub
        }

        @Override
        public int getLeadingMargin(boolean arg0)
        {
// TODO Auto-generated method stub
            if (arg0) {
                /*
                 * This indentation is applied to the number of rows returned
                 * getLeadingMarginLineCount ()
                 */
                return margin;
            }
            else
            {
// Offset for all other Layout layout ) { }
                /*
                 * Returns * the number of rows which should be applied * indent
                 * returned by getLeadingMargin (true) Note:* Indent only
                 * applies to N lines of the first paragraph.
                 */
                return 0;
            }
        }

        @Override
        public int getLeadingMarginLineCount()
        {
            // TODO Auto-generated method stub
            return lines;
        }

    }
}
