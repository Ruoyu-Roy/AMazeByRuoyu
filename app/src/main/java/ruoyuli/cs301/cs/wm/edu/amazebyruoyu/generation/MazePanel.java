package ruoyuli.cs301.cs.wm.edu.amazebyruoyu.generation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import  android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import ruoyuli.cs301.cs.wm.edu.amazebyruoyu.R;

public class MazePanel extends View {

    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;


    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
    }

    public void update(Canvas canvas) {
        invalidate();
    }

    public void update() {
        invalidate();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setColor(String color) {
        switch(color) {
            case "white":
                paint.setColor(Color.WHITE);
                break;
            case "black":
                paint.setColor(Color.BLACK);
                break;
            case "red":
                paint.setColor(Color.RED);
                break;
            case "orange":
                paint.setColor(Color.CYAN);
                break;
            case "yellow":
                paint.setColor(Color.YELLOW);
                break;
            case "blue":
                paint.setColor(Color.BLUE);
                break;
            case "gray":
                paint.setColor(Color.GRAY);
                break;
            case "dark gray":
                paint.setColor(Color.DKGRAY);
                break;
            case "green":
                paint.setColor(Color.GREEN);
                break;
        }
    }

    public void setColor(int [] colorArray) {
        //graphics.setColor(new Color(colorArray[0], colorArray[1], colorArray[2]));
        paint.setColor(Color.rgb(colorArray[0], colorArray[1], colorArray[2]));
    }

    public Color getColor(int x1, int x2, int x3) {
        //return new Color(x1, x2, x3);
        return Color.valueOf(x1, x2, x3);
    }

    /**
     * Convert an RGB array to a single RGB int.
     * @param colorArray The input color array
     * @return A color int
     */
    public static int getRGB(int[] colorArray){
        //return new Color(colorArray[0], colorArray[1], colorArray[2]).getRGB();
        return Color.rgb(colorArray[0], colorArray[1], colorArray[2]);
    }

    /**
     * Convert an RGB int to an RGB array.
     * @param colorInt The color int
     * @return A color array
     */
    public static int[] getRGBArray(int colorInt) {
        /*int[] colorArray = new int[3];
        Color color = new Color(colorInt);
        colorArray[0] = color.getRed();
        colorArray[1] = color.getGreen();
        colorArray[2] = color.getBlue();
        return colorArray;*/
        int[] colorArray = new int[3];
        colorArray[0] = Color.red(colorInt);
        colorArray[1] = Color.green(colorInt);
        colorArray[2] = Color.blue(colorInt);
        return colorArray;
    }

    public void fillRect(int x, int y, int width, int height) {
        //graphics.fillRect(x, y, width, height);
        //return this;
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(x, y, x + width, y + height), paint);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        //graphics.fillPolygon(xPoints, yPoints, nPoints);
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.lineTo(xPoints[0], yPoints[0]);
        canvas.drawPath(path, paint);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        //graphics.drawLine(x1, y1, x2, y2);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void fillOval(int x, int y, int width, int height) {
        //graphics.fillOval(x, y, width, height);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawOval(new RectF(x, y, x + width, y+height), paint);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //testMyDrawing();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        //invalidate();
    }

    private void testMyDrawing() {
        setColor("red");
        fillOval(0, 0, 100, 100);
        setColor("green");
        fillOval(100,0,50,50);
        setColor("yellow");
        fillRect(150,0,100,100);
        setColor("blue");
        int [] x = {50, 100, 150, 100, 50, 0};
        int [] y = {100, 100, 150, 200, 200, 150};
        fillPolygon(x, y ,6);
    }
}
