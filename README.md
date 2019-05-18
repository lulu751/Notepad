# MyNotepad
### 一.主页面
1.记事本主页面

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >
    <ListView
        android:id="@+id/lv_notes"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        ></ListView>
</LinearLayout>
```

效果图：

![](http://ww1.sinaimg.cn/large/006CypLjgy1g339kemshqj30a60g5t9l.jpg)

### 二.增删改查
1.增加记事

```
@Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.bt_back :
                    //当前Activity结束，则返回上一个Activity
                    AddActivity.this.finish();
                    break;

                //将记事添加到数据库中
                case R.id.bt_save :
                    //取得EditText中的内容
                    String context = et_Notes.getText().toString();
                    Log.e("调试1",context);
                    if(context.isEmpty()){
                        Toast.makeText(AddActivity.this, "记事为空!", Toast.LENGTH_LONG).show();
                    }
                    else{

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
                    break;
            }
        }
    }
```

点击加号 跳转至编辑记事的界面

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33ajwblzoj30jk0fsq70.jpg)

2.编辑

```

 class ClickEvent implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.bt_back :
                    //当前Activity结束，则返回上一个Activity
                    AddActivity.this.finish();
                    break;

                //将记事添加到数据库中
                case R.id.bt_save :
                    //取得EditText中的内容
                    String context = et_Notes.getText().toString();
                    Log.e("调试1",context);
                    if(context.isEmpty()){
                        Toast.makeText(AddActivity.this, "记事为空!", Toast.LENGTH_LONG).show();
                    }
                    else{

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
                    break;
            }
        }
    }
 ```
 从数据库中提取数据，点击打钩按钮保存，更新时间戳和内容，并跳转至登录界面

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33an0z8l2j30jt0fv42n.jpg)

3.删除

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
 
 为列表元素添加长按监听器
 
 ![](http://ww1.sinaimg.cn/large/006CypLjgy1g33beakrjxj30a30g63zj.jpg)
 
 4.查询
 
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
```

监听搜索框的内容变化，自动搜索

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33av3os97j30a7040mx6.jpg)

### 三.附加功能

1.添加图片

```
//设置菜单项监听器
	class MenuClickEvent implements OnItemClickListener{
 
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent;
				//添加图片的主要代码 
				intent = new Intent();
				//设定类型为image
				intent.setType("image/*");
				//设置action
				intent.setAction(Intent.ACTION_GET_CONTENT);
				//选中相片后返回本Activity
				startActivityForResult(intent, 1);
		}
		
	}
```

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33b7n3yn6j30de0kph23.jpg)

2.添加录音

```

class ClickEvent implements OnClickListener{
 
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			//点击的是开始录音按钮 
			case  R.id.btn_record :
				//开始录音 
				if(isRecording == 0){
					
					//每一次调用录音，可以录音多次，至多满意为至，最后只将最后一次的录音文件保存，其他的删除
					if(FilePath != null){
						File oldFile = new File(FilePath);
						oldFile.delete();
					}
					
					
					//获得系统当前时间，并以该时间作为文件名
			  		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");  
			        Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间 
			        String   str   =   formatter.format(curDate);  
			        
			        str = str + "record.amr";
			        File dir = new File("/sdcard/notes/");
			        File file = new File("/sdcard/notes/",str);
			        if (!dir.exists()) { 
			        	dir.mkdir(); 
			        } 
			        else{
			        	if(file.exists()){
			        		file.delete();
			        	}
			        }
					
					FilePath = dir.getPath() +"/"+ str;
					//计时器
					mTimer = new Timer();
					
					//将麦克图标设置成不可点击，
					iv_microphone.setClickable(false);
					//将显示的时间设置为00:00:00
					tv_recordTime.setText("00:00:00");
					//将按钮换成停止录音
					isRecording = 1;
					btn_record.setBackgroundResource(R.drawable.tabbar_record_stop);
					
					mRecorder = new MediaRecorder();
					mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mRecorder.setOutputFile(FilePath);
					mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					
					try {
						mRecorder.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mRecorder.start();
					mTimer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							
						}
					},1000, 1000);
					//播放动画
					ad_left.start();
					ad_right.start();
				}
				//停止录音
				else{
					//将按钮换成开始录音
					isRecording = 0;
					btn_record.setBackgroundResource(R.drawable.tabbar_record_start);
					mRecorder.stop();
					mTimer.cancel();
					mTimer = null;
					
					mRecorder.release();
					mRecorder = null;
					
					//将麦克图标设置成可点击，
					iv_microphone.setClickable(true);
					//停止动画
					ad_left.stop();
					ad_right.stop();
					Toast.makeText(ActivityRecord.this, "单击麦克图标试听，再次点击结束试听", Toast.LENGTH_LONG).show();
				}
				break;
			//如果单击的是麦克图标，则可以是进入试听模式，再次点击，停止播放
			case R.id.iv_microphone :
				if(FilePath == null)
					Toast.makeText(ActivityRecord.this, "没有录音广播可以播放，请先录音", Toast.LENGTH_LONG).show();
				else{
					//试听
					if(isPlaying == 0){
						isPlaying = 1;
						mPlayer = new MediaPlayer();
						tv_recordTime.setText("00:00:00");
						mTimer = new Timer();
						mPlayer.setOnCompletionListener(new MediaCompletion());
						try {
							mPlayer.setDataSource(FilePath);
							mPlayer.prepare();
							mPlayer.start();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mTimer.schedule(new TimerTask() {
							
							@Override
							public void run() {
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
								
							}
						}, 1000,1000);
						
						//播放动画
						ad_left.start();
						ad_right.start();
					}
					//结束试听
					else{
						isPlaying = 0;
						mPlayer.stop();
						mPlayer.release();
						mPlayer = null;
						mTimer.cancel();
						mTimer = null;
						//停止动画
						ad_left.stop();
						ad_right.stop();
					}
				}
				break;
			
			//点击确定按钮 
			case R.id.bt_save :
				//将最终的录音文件的路径返回
				Intent intent = getIntent();
				Bundle b = new Bundle();
				b.putString("audio", FilePath);
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				
				ActivityRecord.this.finish();
				break;
			case R.id.bt_back :
				//返回前将录音的文件删除
				if(FilePath != null){
					File oldFile = new File(FilePath);
					oldFile.delete();
				}
				ActivityRecord.this.finish();
				break;
				
			}
		}
		
	}

```
不仅实现了录音的功能，同时，也实现了计时，试听，以及用逐帧动画的功能

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33bf0wbp2j30i80b8426.jpg)


3.添加拍照

```

//如果选择的是拍照
		else if(requestCode == 2){
		System.out.println("-----fjwefowefwef");;
		try {
					
			if(uri != null)
				//这个方法是根据Uri获取Bitmap图片的静态方法 
				bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
				//这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
			else
				extras = data.getExtras();
				bitmap = extras.getParcelable("data");
					
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
```
效果图：

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33bl1i4d2j30al0iuwmb.jpg)

4.画画功能

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/paint_layout"
     >
   
   
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

![](http://ww1.sinaimg.cn/large/006CypLjgy1g33btqk16cj30cj0kygoz.jpg)

