package com.example.pc.notepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button bt_add;
    private Button bt_setting;
    private SQLiteDatabase db;
    private DatabaseOperation dop;
    private ListView lv_notes;
    private TextView tv_note_id;
    private TextView tv_note_content;
    private SearchView mSearchView;
    private  SimpleCursorAdapter adapter;
    private  Cursor cursor;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_main);
        bt_add = (Button)findViewById(R.id.bt_add);
        bt_add.setOnClickListener(new ClickEvent());
        bt_setting = (Button)findViewById(R.id.bt_setting);
        mSearchView = (SearchView) findViewById(R.id.sv);
        tv_note_content=(TextView) findViewById(R.id.tv_note_content);
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

        //数据库操作
        dop = new DatabaseOperation(this, db);
        lv_notes = (ListView)findViewById(R.id.lv_notes);

    }

    @Override
    protected  void onStart( ){
      super.onStart();
        //显示记事列表
        showNotesList();
       //为记事列表添加长按事件
        lv_notes.setOnItemLongClickListener(new ItemLongClickEvent());
        lv_notes.setOnItemClickListener(new ItemClickEvent());
    }

    //简单列表对话框，用于选择操作
    public void simpleList(final int item_id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.custom_dialog);
        alertDialogBuilder.setTitle("选择操作");
        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        alertDialogBuilder.setItems(R.array.itemOperation, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch(which){
                    //编辑
                    case 0 :
                        Intent intent = new Intent(MainActivity.this,AddActivity.class);
                        intent.putExtra("editModel", "update");
                        intent.putExtra("noteId", item_id);
                        startActivity(intent);
                        break;
                    //删除
                    case 1 :
                        dop.create_db();
                        dop.delete_db(item_id);
                        dop.close_db();
                        //刷新列表显示
                        lv_notes.invalidate();
                        showNotesList();
                        break;
                }
            }
        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }


    //显示记事列表
    private void showNotesList(){
        //创建或打开数据库
        dop.create_db();
        cursor = dop.query_db();
        adapter = new SimpleCursorAdapter(this,
                R.layout.note_item,
                cursor,
                new String[]{"_id","title","time","context"}, new int[]{R.id.tv_note_id,R.id.tv_note_title,R.id.tv_note_time,R.id.tv_note_content});
        lv_notes.setAdapter(adapter);

    }

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
    //记事列表单击监听器
    class ItemClickEvent implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            tv_note_id = (TextView)view.findViewById(R.id.tv_note_id);
            int item_id = Integer.parseInt(tv_note_id.getText().toString());
            Intent intent = new Intent(MainActivity.this,AddActivity.class);
            intent.putExtra("editModel", "update");
            intent.putExtra("noteId", item_id);
            startActivity(intent);

        }
    }
}
