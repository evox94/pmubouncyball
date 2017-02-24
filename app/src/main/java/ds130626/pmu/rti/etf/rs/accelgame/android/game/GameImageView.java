package ds130626.pmu.rti.etf.rs.accelgame.android.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.CanvasWrapper;
import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;
import ds130626.pmu.rti.etf.rs.accelgame.view.View;

/**
 * Created by smiljan on 1/31/17.
 */

public class GameImageView extends ImageView implements View {
    Paint paint;
    GameInstance gameInstance;
    CanvasWrapper canvasWrapper;
    PixelConverter pixelConverter;

    public GameImageView(Context context) {
        super(context);
        init();
    }

    public GameImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        canvasWrapper = new CanvasWrapper();
        canvasWrapper.setPaint(paint);
    }

    public GameInstance getGameInstance() {
        return gameInstance;
    }

    public void setGameInstance(GameInstance gameInstance, Map<Integer, Map<String, Object>> levelViewProperties) {
        this.gameInstance = gameInstance;
        canvasWrapper.setLevelViewProperties(levelViewProperties);
    }

    public void setPixelConverter(PixelConverter converter){
        pixelConverter = converter;
        canvasWrapper.setPixelConverter(converter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasWrapper.setCanvas(canvas);
        if(gameInstance!=null){
            gameInstance.redraw(canvasWrapper);
        }
    }

    @Override
    public void refresh() {
        postInvalidate();
    }
}
