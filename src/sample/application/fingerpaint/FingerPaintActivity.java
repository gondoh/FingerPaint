package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;

public class FingerPaintActivity extends Activity implements OnTouchListener{
	Canvas canvas;
	Paint paint;
	
	Path path;
	Bitmap bitmap;
	float x1, y1;
	int w, h;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
        Display disp = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
        this.w = disp.getWidth();
        this.h = disp.getHeight();
        
        this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.paint = new Paint();
        this.path = new Path();
        this.canvas = new Canvas(this.bitmap);
        
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.canvas.drawColor(Color.WHITE);
        iv.setImageBitmap(bitmap);
        iv.setOnTouchListener(this);
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_save:
				this.save();break;
		}
		return super.onOptionsItemSelected(item);
	}
    

    public boolean onTouch(View v, MotionEvent event){
    	float x = event.getX();
    	float y = event.getY();
    	
    	Log.d("x ", String.valueOf(x));
    	Log.d("y ", String.valueOf(y));
    	
    	switch (event.getAction()){
    	case MotionEvent.ACTION_DOWN:
    		path.reset();
    		path.moveTo(x, y);
    		x1 = x;
    		y1 = y;
    		break;
    	case MotionEvent.ACTION_MOVE:
    		path.quadTo(x1, y1, x, y);
    		x1 = x;
    		y1 = y;
    		canvas.drawPath(path, paint);
    		path.reset();
    		path.moveTo(x, y);
    		break;
    	case MotionEvent.ACTION_UP:
    		if (x==x1 && y==y1) y1 = y1+1;
    		path.quadTo(x1, y1, x, y);
    		canvas.drawPath(path, paint);
    		path.reset();
    		break;
    	}
    	ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
    	iv.setImageBitmap(bitmap);
    	
    	return true;
    }
    
    
    public void save(){
    	SharedPreferences prefs = this.getSharedPreferences("FingerPaintPreferences", MODE_PRIVATE);
    	int imageNumber = prefs.getInt("imageNumber", 1);
    	File file = null;
    	
    	if (this.externalMediaChecker()){
    		DecimalFormat df = new DecimalFormat("0000");
    		String path = Environment.getExternalStorageDirectory()+"/mypaint/";
    		File outDir = new File(path);
    		if (!outDir.exists()) outDir.mkdir();
    		
    		do {
    			file = new File(path+"img"+df.format(imageNumber)+".png");
    		} while(file.exists());
    		
    		if (this.writeImage(file)) {
    			SharedPreferences.Editor editor = prefs.edit();
    			editor.putInt("imageNumber", imageNumber);
    			editor.commit();
    		}
    	}
    }
    
    // ファイル書き込み
    private boolean writeImage(File file){
    	try{
    		FileOutputStream fo = new FileOutputStream(file);
    		this.bitmap.compress(CompressFormat.PNG, 100, fo);
    		fo.flush();
    		fo.close();
    		
    	} catch(Exception e) {
    		System.out.println(e.getLocalizedMessage());
    		return false;
    	}
    	return true;
    }
    
    // 保存可能かチェックする
    private boolean externalMediaChecker(){
    	boolean result = false;
    	String status = Environment.getExternalStorageState();
    	if (status.equals(Environment.MEDIA_MOUNTED)) result = true;
    	
    	return result;
    }
    
    // 画像取得
    MediaScannerConnection mc;
    private void scanMedia(final String fp){
    	this.mc = new MediaScannerConnection(this,
    			new MediaScannerConnection.MediaScannerConnectionClient() {
					
					public void onScanCompleted(String path, Uri uri) {
						// TODO 自動生成されたメソッド・スタブ
						
					}
					
					public void onMediaScannerConnected() {
						// TODO 自動生成されたメソッド・スタブ
						
					}
				}
    	);
    }

    
}
