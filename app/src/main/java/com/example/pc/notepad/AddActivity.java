
package com.example.pc.notepad;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.view.View.OnClickListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.widget.AdapterView.OnItemClickListener;

public class AddActivity extends Activity {
    private Button bt_back;
    private Button bt_save;
    private EditText et_Notes;
    private DatabaseOperation dop;
    private SQLiteDatabase db;
    private GridView bottomMenu;
    private TextView tv_title;
    //记录editText中的图片，用于单击时判断单击的是那一个图片
    private List<Map<String,String>> imgList = new ArrayList<Map<String,String>>();
    Intent intent;
    int item_Id;
    String editModel = null;
    //底部按钮

    private int[] bottomItems = {
            R.drawable.tabbar_handwrite,
            R.drawable.tabbar_paint,
            R.drawable.tabbar_microphone,
            R.drawable.tabbar_photo_pressed,
            R.drawable.tabbar_camera,
            R.drawable.tabbar_appendix
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_add);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_add);

        bt_back = (Button)findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new ClickEvent());
        bt_save = (Button)findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new ClickEvent());
        bottomMenu = (GridView)findViewById(R.id.bottomMenu);
        tv_title = (TextView)findViewById(R.id.tv_title);
        dop = new DatabaseOperation(this,db);
        et_Notes = (EditText)findViewById(R.id.et_note);
        intent = getIntent();
        editModel = intent.getStringExtra("editModel");
        item_Id = intent.getIntExtra("noteId", 0);
        //加载数据
        loadData();
        initBottomMenu();
        //为菜单设置监听器
        bottomMenu.setOnItemClickListener(new MenuClickEvent());


    }

    //配置菜单
    public void initBottomMenu(){
        ArrayList<Map<String,Object>> menus = new ArrayList<Map<String,Object>>();
        for(int i = 0;i < bottomItems.length;i++){
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("image",bottomItems[i]);
            menus.add(item);
        }
        bottomMenu.setNumColumns(bottomItems.length);
        bottomMenu.setSelector(R.drawable.bottom_item);
        SimpleAdapter mAdapter = new SimpleAdapter(AddActivity.this, menus,R.layout.item_button, new String[]{"image"}, new int[]{R.id.item_image});
        bottomMenu.setAdapter(mAdapter);
    }

    //设置菜单项监听器
    class MenuClickEvent implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent;
            switch(position){

                //照片
                case 3:
                    //添加图片的主要代码
                    intent = new Intent();
                    //设定类型为image
                    intent.setType("image/*");
                    //设置action
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //选中相片后返回本Activity
                    startActivityForResult(intent, 1);
                    break;

                //附件
                case 5 :
                    break;

            }

        }

    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //取得数据

            Uri uri = data.getData();
            ContentResolver cr = AddActivity.this.getContentResolver();
            Bitmap bitmap = null;
            Bundle extras = null;
            //如果是选择照片
            if(requestCode == 1){
                //取得选择照片的路径

                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                String path = actualimagecursor.getString(actual_image_column_index);
                try {

                    //将对象存入Bitmap中
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //插入图片
                InsertBitmap(bitmap,480,path);

            }

        }
    }
    //将图片等比例缩放到合适的大小并添加在EditText中
    void InsertBitmap(Bitmap bitmap,int S,String imgPath){


        //bitmap = addBigFrame(bitmap,R.drawable.line_age);
        final ImageSpan imageSpan = new ImageSpan(this,bitmap);
        SpannableString spannableString = new SpannableString(imgPath);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), SpannableString.SPAN_MARK_MARK);
        //光标移到下一行
        //et_Notes.append("\n");

        Editable editable = et_Notes.getEditableText();
        int selectionIndex = et_Notes.getSelectionStart();
        spannableString.getSpans(0, spannableString.length(), ImageSpan.class);

        //将图片添加进EditText中
        editable.insert(selectionIndex, spannableString);
        //添加图片后自动空出两行
        et_Notes.append("\n");

        //用List记录该图片的位置及所在路径，用于单击事件
        Map<String,String> map = new HashMap<String,String>();
        map.put("location", selectionIndex+"-"+(selectionIndex+spannableString.length()));
        map.put("path", imgPath);
        imgList.add(map);

    }

    //设置按钮监听器
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
    //截取EditText中的前一部分作为标题，用于显示在主页列表中
    private String getTitle(String context){
        //定义正则表达式，用于匹配路径

        Pattern p=Pattern.compile("/([^\\.]*)\\.\\w{3}");
        Matcher m=p.matcher(context);
        StringBuffer strBuff = new StringBuffer();
        String title = "";
        int startIndex = 0;
        while(m.find()){
            //取出路径前的文字
            if(m.start() > 0){
                strBuff.append(context.substring(startIndex, m.start()));
            }
            //取出路径
            String path = m.group().toString();
            //取出路径的后缀
            String type = path.substring(path.length() - 3, path.length());
            //判断附件的类型
            if(type.equals("amr")){
                strBuff.append("[录音]");
            }
            else{
                strBuff.append("[图片]");
            }
            startIndex = m.end();
            //只取出前15个字作为标题
            if(strBuff.length() > 15){
                //统一将回车,等特殊字符换成空格
                title = strBuff.toString().replaceAll("\r|\n|\t", " ");
                return title;
            }
        }
        strBuff.append(context.substring(startIndex, context.length()));
        //统一将回车,等特殊字符换成空格
        title = strBuff.toString().replaceAll("\r|\n|\t", " ");
        return title;
    }
    //加载数据
    private void loadData(){

        //如果是新增记事模式，则将editText清空
        if(editModel.equals("newAdd")){
            et_Notes.setText("");
        }
        //如果编辑的是已存在的记事，则将数据库的保存的数据取出，并显示在EditText中
        else if(editModel.equals("update")){
            tv_title.setText("编辑记事");
            dop.create_db();
            Cursor cursor = dop.query_db(item_Id);
            cursor.moveToFirst();
            //取出数据库中相应的字段内容
            String context = cursor.getString(cursor.getColumnIndex("context"));
            et_Notes.setText(context);
            dop.close_db();
        }
    }


}

