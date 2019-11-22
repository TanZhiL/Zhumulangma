package com.gykj.zhumulangma.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.TintTypedArray;

import com.gykj.zhumulangma.common.R;

import java.util.List;

public class MarqueeView extends View implements Runnable {
    private static final String TAG = "MarqueeView";
    private String string;
    private float speed;
    private int textColor;
    private float textSize;
    private int textdistance;
    private int textDistance1;
    private String black_count;
    private int repetType;
    private float startLocationDistance;
    private boolean isClickStop;
    private boolean isResetLocation;
    private float xLocation;
    private int contentWidth;
    private boolean isRoll;
    private float oneBlack_width;
    private TextPaint paint;
    private Rect rect;
    private int repetCount;
    private boolean resetInit;
    private Thread thread;
    private String content;
    private float textHeight;
    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.speed = 1.0F;
        this.textColor = -16777216;
        this.textSize = 12.0F;
        this.textDistance1 = 10;
        this.black_count = "";
        this.repetType = 1;
        this.startLocationDistance = 1.0F;
        this.isClickStop = false;
        this.isResetLocation = true;
        this.xLocation = 0.0F;
        this.isRoll = false;
        this.repetCount = 0;
        this.resetInit = true;
        this.content = "";
        this.initattrs(attrs);
        this.initpaint();
        this.initClick();
    }

    private void initClick() {
        this.setOnClickListener(v -> {
            if (isClickStop) {
                if (isRoll) {
                    stopRoll();
                } else {
                   continueRoll();
                }
            }

        });
    }

    @SuppressLint({"RestrictedApi"})
    private void initattrs(AttributeSet attrs) {
        TintTypedArray tta = TintTypedArray.obtainStyledAttributes(this.getContext(), attrs, R.styleable.MarqueeView);
        this.textColor = tta.getColor( R.styleable.MarqueeView_marqueeview_text_color, this.textColor);
        this.isClickStop = tta.getBoolean( R.styleable.MarqueeView_marqueeview_isclickalbe_stop, this.isClickStop);
        this.isResetLocation = tta.getBoolean( R.styleable.MarqueeView_marqueeview_is_resetLocation, this.isResetLocation);
        this.speed = tta.getFloat( R.styleable.MarqueeView_marqueeview_text_speed, this.speed);
        this.textSize = tta.getFloat( R.styleable.MarqueeView_marqueeview_text_size, this.textSize);
        this.textDistance1 = tta.getInteger( R.styleable.MarqueeView_marqueeview_text_distance, this.textDistance1);
        this.startLocationDistance = tta.getFloat( R.styleable.MarqueeView_marqueeview_text_startlocationdistance, this.startLocationDistance);
        this.repetType = tta.getInt( R.styleable.MarqueeView_marqueeview_repet_type, this.repetType);
        tta.recycle();
    }

    private void initpaint() {
        this.rect = new Rect();
        this.paint = new TextPaint(1);
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(this.textColor);
        this.paint.setTextSize((float) dp2px(this.textSize));
    }

    public int dp2px(float dpValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.resetInit) {
            this.setTextDistance(this.textDistance1);
            if (this.startLocationDistance < 0.0F) {
                this.startLocationDistance = 0.0F;
            } else if (this.startLocationDistance > 1.0F) {
                this.startLocationDistance = 1.0F;
            }

            this.xLocation = (float)this.getWidth() * this.startLocationDistance;

            this.resetInit = false;
        }

        switch(this.repetType) {
            case 0:
                if ((float)this.contentWidth < -this.xLocation) {
                    this.stopRoll();
                }
                break;
            case 1:
                if ((float)this.contentWidth <= -this.xLocation) {
                    this.xLocation = (float)this.getWidth();
                }
                break;
            case 2:
                if (this.xLocation < 0.0F) {
                    int beAppend = (int)(-this.xLocation / (float)this.contentWidth);

                    if (beAppend >= this.repetCount) {
                        ++this.repetCount;
                        this.string = this.string + this.content;
                    }
                }
                break;
            default:
                if ((float)this.contentWidth < -this.xLocation) {
                    this.stopRoll();
                }
        }

        if (this.string != null) {
            canvas.drawText(this.string, this.xLocation, (float)(this.getHeight() / 2) + this.textHeight / 2.0F, this.paint);
        }

    }

    public void setRepetType(int repetType) {
        this.repetType = repetType;
        this.resetInit = true;
        this.setContent(this.content);
    }

    public void run() {
        while(this.isRoll && !TextUtils.isEmpty(this.content)) {
            try {
                Thread.sleep(10L);
                this.xLocation -= this.speed;
                this.postInvalidate();
            } catch (InterruptedException var2) {
                //var2.printStackTrace();
            }
        }

    }

    public void continueRoll() {
        if (!this.isRoll) {
            if (this.thread != null) {
                this.thread.interrupt();
                this.thread = null;
            }

            this.isRoll = true;
            this.thread = new Thread(this);
            this.thread.start();

        }

    }

    public void stopRoll() {
        this.isRoll = false;
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }

    }

    private void setClickStop(boolean isClickStop) {
        this.isClickStop = isClickStop;
    }

    private void setContinueble(int isContinuable) {
        this.repetType = isContinuable;
    }

    public void setTextDistance(int textdistance2) {
        String black = " ";
        this.oneBlack_width = this.getBlacktWidth();
        textdistance2 = this.dp2px((float)textdistance2);
        int count = (int)((float)textdistance2 / this.oneBlack_width);
        if (count == 0) {
            count = 1;
        }

        this.textdistance = (int)(this.oneBlack_width * (float)count);
        this.black_count = "";

        for(int i = 0; i <= count; ++i) {
            this.black_count = this.black_count + black;
        }

        this.setContent(this.content);
    }

    private float getBlacktWidth() {
        String text1 = "en en";
        String text2 = "enen";
        return this.getContentWidth(text1) - this.getContentWidth(text2);
    }

    private float getContentWidth(String black) {
        if (black != null && black != "") {
            if (this.rect == null) {
                this.rect = new Rect();
            }

            this.paint.getTextBounds(black, 0, black.length(), this.rect);
            this.textHeight = this.getContentHeight();
            return (float)this.rect.width();
        } else {
            return 0.0F;
        }
    }

    private float getContentHeight() {
        FontMetrics fontMetrics = this.paint.getFontMetrics();
        return Math.abs(fontMetrics.bottom - fontMetrics.top) / 2.0F;
    }

    public void setTextColor(int textColor) {
        if (textColor != 0) {
            this.textColor = textColor;
            this.paint.setColor(this.getResources().getColor(textColor));
        }

    }

    public void setTextSize(float textSize) {
        if (textSize > 0.0F) {
            this.textSize = textSize;
            this.paint.setTextSize((float)this.dp2px(textSize));
            this.contentWidth = (int)(this.getContentWidth(this.content) + (float)this.textdistance);
        }

    }

    public void setTextSpeed(float speed) {
        this.speed = speed;
    }

    public void setContent(List<String> strings) {
        this.setTextDistance(this.textDistance1);
        String temString = "";
        if (strings != null && strings.size() != 0) {
            for(int i = 0; i < strings.size(); ++i) {
                temString = temString + (String)strings.get(i) + this.black_count;
            }
        }

        this.setContent(temString);
    }

    public void setContent(String content2) {
        if (!TextUtils.isEmpty(content2)) {
            if (this.isResetLocation) {
                this.xLocation = (float)this.getWidth() * this.startLocationDistance;
            }

            if (!content2.endsWith(this.black_count)) {
                content2 = content2 + this.black_count;
            }

            this.content = content2;
            if (this.repetType == 2) {
                this.contentWidth = (int)(this.getContentWidth(this.content) + (float)this.textdistance);
                this.repetCount = 0;
                int contentCount = this.getWidth() / this.contentWidth + 2;
                this.string = "";

                for(int i = 0; i <= contentCount; ++i) {
                    this.string = this.string + this.content;
                }
            } else {
                if (this.xLocation < 0.0F && this.repetType == 0 && -this.xLocation > (float)this.contentWidth) {
                    this.xLocation = (float)this.getWidth() * this.startLocationDistance;
                }

                this.contentWidth = (int)this.getContentWidth(this.content);
                this.string = content2;
            }

            if (!this.isRoll) {
                this.continueRoll();
            }

        }
    }

    private void setResetLocation(boolean isReset) {
        this.isResetLocation = isReset;
    }

}

