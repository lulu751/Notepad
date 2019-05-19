
基本要求：每个人必须完成

（1） NoteList中显示条目增加时间戳显示


效果图：

![](https://i.loli.net/2019/05/19/5ce0e6a229ef295055.png)

核心代码：

```
//取得当前时间
                        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm");
                        Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间
                        String   time   =   formatter.format(curDate);
                        //截取EditText中的前一部分作为标题，用于显示在主页列表中
                        String title = getTitle(context);

                        //打开数据库
                        dop.create_db();
                        //判断是更新还是新增记事
                        if(editModel.equals("newAdd")){
                            //将记事插入到数据库中
                            dop.insert_db(title,context,time);
                        }
                        //如果是编辑则更新记事即可
                        else if(editModel.equals("update")){
                            dop.update_db(title,context,time,item_Id);
                        }
                        dop.close_db();
                        //结束当前activity
                        AddActivity.this.finish();
                    }
 ```                   


 （2）添加笔记查询功能（根据标题查询）
 
 
 效果图：
 
 ![](https://i.loli.net/2019/05/19/5ce0e71fb41aa93583.png)
 
 核心代码：
 
 ```
 //搜索框内容变化监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//点击提交按钮时
                Toast.makeText(MainActivity.this, "Submit---提交", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//搜索框内容变化时
                Toast.makeText(MainActivity.this, "改变", Toast.LENGTH_SHORT).show();
                cursor = dop.Search_query_db(newText);
                adapter.swapCursor(cursor);
                return true;
            }
        });

        //搜索框展开时点击叉叉按钮关闭搜索框的点击事件
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "关闭搜索框", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
 ```
 
 
 附加功能：
 （1）添加记事便签以及长按实现编辑与删除功能
 
 
 效果图：
 
 1、添加功能：
 
 ![](https://i.loli.net/2019/05/19/5ce0e7a9f17c013330.png)
 
 
 2、 长按实现编辑与删除：
 
  
 ![](https://i.loli.net/2019/05/19/5ce0e73d6063316438.png)
 
 

 核心代码：
 
 1、添加功能：
 
```
  class ClickEvent implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.bt_add :
                    Intent intent = new Intent(MainActivity.this,AddActivity.class);
                    intent.putExtra("editModel", "newAdd");
                    startActivity(intent);
            }
        }
    }
```
 2、长按实现编辑与删除功能：
 ```
   //记事列表长按监听器
    class ItemLongClickEvent implements OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            tv_note_id = (TextView)view.findViewById(R.id.tv_note_id);
            int item_id = Integer.parseInt(tv_note_id.getText().toString());
            simpleList(item_id);
            return true;
        }
    }
    ```
 
 
 （2）手写添加记事功能
 
 
 效果图：
 
 ![](https://i.loli.net/2019/05/19/5ce0e7d4948c492356.png)
 
 ![](https://i.loli.net/2019/05/19/5ce0e7e2f32f492641.png)
 
 ![](https://i.loli.net/2019/05/19/5ce0e7ef6a8e656782.png)
 
 ![](https://i.loli.net/2019/05/19/5ce0e7fa7589790005.png)
 
 
 核心代码：
 
 布局代码：
 
 ```
 <?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/white"
     >
 
  	<FrameLayout
 	android:layout_width="fill_parent"
  	android:layout_height="wrap_content"
  	android:id="@+id/finger_layout"  
  	>
        
        <com.example.notes.LineEditText
        android:id="@+id/et_handwrite"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical"
        android:fadingEdge="vertical"
        android:inputType="textMultiLine"
        android:gravity="top"
        android:textSize="20sp"
        android:layout_margin="5dp"
        android:focusable="true"
        android:lineSpacingExtra="10dp"
        android:textColor="#00000000"
        android:background="#00000000"
        
        />
        
 	  <com.example.notes.TouchView
        	android:id="@+id/touch_view"
        	android:layout_width="fill_parent"
        	android:layout_height="wrap_content"
        	android:background="@android:color/transparent" >
     </com.example.notes.TouchView>
  	
    
    </FrameLayout>
    <ImageView 
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:src="@drawable/line"
      android:layout_above="@+id/paintBottomMenu"
      />
   
    <GridView 
       android:id="@+id/paintBottomMenu" 
       android:layout_width="match_parent"
       android:layout_height="45dp"
       android:numColumns="auto_fit"
       android:background="@drawable/navigationbar_bg"
       android:horizontalSpacing="10dp"
       android:layout_alignParentBottom="true"
       ></GridView>
 
</RelativeLayout>
```
逻辑代码：

```

	
	public LineEditText(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context,attrs);
		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//得到EditText的总行数
		int lineCount = getLineCount();
		Rect r = mRect;
		Paint p = mPaint;
		//为每一行设置格式 
		for(int i = 0; i < lineCount;i++){
			//取得每一行的基准Y坐标，并将每一行的界限值写到r中
			int baseline = getLineBounds(i, r);
			//设置每一行的文字带下划线
			canvas.drawLine(r.left, baseline+20, r.right, baseline+20, p);
		}
	}
}

 
	private Bitmap  mBitmap,myBitmap;
	private Canvas  mCanvas;
	private Path    mPath;
	private Paint   mBitmapPaint;
	private Paint mPaint;
	private Handler bitmapHandler;
	GetCutBitmapLocation getCutBitmapLocation;
	private Timer timer;
	DisplayMetrics dm;
	private int w,h;
	public TouchView(Context context) {
        super(context);
        dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        w = dm.widthPixels;
        h = dm.heightPixels;
		initPaint();
    }
    
	public TouchView(Context context, AttributeSet attrs) {
		super(context,attrs);
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
        h = dm.heightPixels;
		initPaint();
	}
	//设置handler
	public void setHandler(Handler mBitmapHandler){
		bitmapHandler = mBitmapHandler;
	}
	
	//初始化画笔，画布
	private void initPaint(){
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF00FF00);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);  
        getCutBitmapLocation = new GetCutBitmapLocation();
        
        //画布大小 
        mBitmap = Bitmap.createBitmap(w, h, 
            Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
        
        mCanvas.drawColor(Color.TRANSPARENT);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        timer = new Timer(true);
	}
	
 
	/**
	 * 处理屏幕显示
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case 1:	
				myBitmap = getCutBitmap(mBitmap); 
				Message message = new Message();
				message.what=1;
				Bundle bundle = new Bundle();;
				bundle.putParcelable("bitmap",myBitmap);
				message.setData(bundle);
				bitmapHandler.sendMessage(message);
				RefershBitmap();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/**
	 * 发送消息给handler更新ACTIVITY		
	 */
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what=1;
			Log.i("线程", "来了");
			handler.sendMessage(message);
		}
	};
	
	//切割画布中的字并返回
	public Bitmap getCutBitmap(Bitmap mBitmap){
		//得到手写字的四周位置，并向外延伸10px
		float cutLeft = getCutBitmapLocation.getCutLeft() - 10;
		float cutTop = getCutBitmapLocation.getCutTop() - 10;
		float cutRight = getCutBitmapLocation.getCutRight() + 10;
		float cutBottom = getCutBitmapLocation.getCutBottom() + 10;
		
		cutLeft = (0 > cutLeft ? 0 : cutLeft);
		cutTop = (0 > cutTop ? 0 : cutTop);
		
		cutRight = (mBitmap.getWidth() < cutRight ? mBitmap.getWidth() : cutRight);
		cutBottom = (mBitmap.getHeight() < cutBottom ? mBitmap.getHeight() : cutBottom);
		
		//取得手写的的高度和宽度 
		float cutWidth = cutRight - cutLeft;
		float cutHeight = cutBottom - cutTop;
		
		Bitmap cutBitmap = Bitmap.createBitmap(mBitmap, (int)cutLeft, (int)cutTop, (int)cutWidth, (int)cutHeight);
		if (myBitmap!=null ) {
			myBitmap.recycle();
			myBitmap= null;
		}
		
		return cutBitmap;
	}
	
	//刷新画布
	private void RefershBitmap(){
		initPaint();
		invalidate();
		if(task != null)
			task.cancel();
	}
	
    @Override
    protected void onDraw(Canvas canvas) {            
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //显示旧的画布       
        canvas.drawPath(mPath, mPaint);  //画最后的path
    }
    
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    
     //手按下时
    private void touch_start(float x, float y) {
        mPath.reset();//清空path
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if(task != null)
        	task.cancel();//取消之前的任务
        task = new TimerTask() {
			
			@Override
			public void run() {
				Message message = new Message();
				message.what=1;
				Log.i("线程", "来了");
				handler.sendMessage(message);
			}
		};
        getCutBitmapLocation.setCutLeftAndRight(mX,mY);
    }
    //手移动时
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, x, y);
            // mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);//源代码是这样写的，可是我没有弄明白，为什么要这样？
            mX = x;
            mY = y;
            if(task != null)
            	task.cancel();//取消之前的任务
            task = new TimerTask() {
    			
    			@Override
    			public void run() {
    				Message message = new Message();
    				message.what=1;
    				Log.i("线程", "来了");
    				handler.sendMessage(message);
    			}
    		};
            getCutBitmapLocation.setCutLeftAndRight(mX,mY);
          
        }
    }
    //手抬起时
    private void touch_up() {
        //mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
        
        if (timer!=null) {
			if (task!=null) {
				task.cancel();
				task = new TimerTask() {
					public void run() {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				};
				timer.schedule(task, 1000, 1000);				//2200秒后发送消息给handler更新Activity
			}
		}else {
			timer = new Timer(true);
			timer.schedule(task, 1000, 1000);					//2200秒后发送消息给handler更新Activity
		}
        
    }
    
    //处理界面事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate(); //刷新
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
 
}


	   //处理界面
	    Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				Bundle bundle = new Bundle();
				bundle = msg.getData();
				Bitmap myBitmap = bundle.getParcelable("bitmap");	
				InsertToEditText(myBitmap);
			}
	   };


	private LineEditText et_handwrite;      
	et_handwrite = (LineEditText)findViewById(R.id.et_handwrite);
                    

	   //将手写字插入到EditText中
	    private void InsertToEditText(Bitmap mBitmap){
	    		    	
			int imgWidth = mBitmap.getWidth();
			int imgHeight = mBitmap.getHeight();
			//缩放比例
			float scaleW = (float) (80f/imgWidth);
			float scaleH = (float) (100f/imgHeight);
			
			Matrix mx = new Matrix();
			//对原图片进行缩放
			mx.postScale(scaleW, scaleH);
			
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, imgWidth, imgHeight, mx, true);
			//将手写的字插入到edittext中
			SpannableString ss = new SpannableString("1");
			ImageSpan span = new ImageSpan(mBitmap, ImageSpan.ALIGN_BOTTOM);
			ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			et_handwrite.append(ss);
	   }

 ```
 
