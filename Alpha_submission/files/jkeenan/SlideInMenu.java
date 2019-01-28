//Jackson Keenan
//Help From https://www.youtube.com/watch?v=YeR7McJIltk
//Has XML sheets as well thought did not submit as uneeded 
package slideInMenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class SlideInMenu extends LinearLayout{
    //GViews
    private View menu;
    private View content;

    //Margin
    protected static final int margin = 100;

    //Menu State
    public enum state {
        CLSD, OPN
    };

    //Positioning
    protected int offSet = 0;
    protected state slideState = state.CLSD;
    public slideInMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public slideInMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public slideInMenu(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.menu = this.getChildAt(0);
        this.content = this.getChildAt(1);
        this.menu.setVisibility(View.GONE);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed)
            this.calc();

        this.menu.layout(left, top, right - margin, bottom);
        this.content.layout(left + this.offSet, top, righ + this.offSet, bottom);
    }
    public void slideInOut() {
        if(this.slideState == CLSD){
            this.menu.setVisibility(View.VISIBLE);
            this.offSet = this.menuWidth();
            this.content.offsetLeftAndRight(offSet);
            this.slideState = state.OPN;
            break;
        }
        else if(this.slideState == OPN){
            this.content.offsetLeftAndRight(-offSet);
                this.offSet = 0;
                this.slideState = state.CLSD;
                this.menu.setVisibility(View.GONE);
                break;
            }
        this.invalidate();
    }

    //Positional Calclation
    private void calc() {
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().width = this.getWidth() - margin;
        this.menu.getLayoutParams().height = this.getHeight();
    }

    private int menuWidth(){
        return this.menu.getLayoutParams().width;
    }
}
