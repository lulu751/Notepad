package com.example.pc.notepad;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HandWriteActivity extends Activity {
    private LinearLayout paintLayout;
    private GridView paint_bottomMenu;
    private LineEditText et_handwrite;
    private ListView lv_popWindow;
    private int[] paintItems = new int[]{2130837545, 2130837533, 2130837530, 2130837539, 2130837536};
    private int select_handwrite_color_index = 0;
    private int select_handwrite_size_index = 0;
    private Bitmap mBitmap;
    private Button btn_save;
    private Button btn_back;
    private PopupWindow popupWindow;
    private TouchView touchView;
    private ArrayList<CharSequence> deleteChar = new ArrayList();
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new Bundle();
            Bundle bundle = msg.getData();
            Bitmap myBitmap = (Bitmap)bundle.getParcelable("bitmap");
            HandWriteActivity.this.InsertToEditText(myBitmap);
        }
    };

    public HandWriteActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(7);
        this.setContentView(2130903041);
        this.getWindow().setFeatureInt(7, 2130903054);
        TextView title = (TextView)this.findViewById(2131427355);
        title.setText("手写");
        this.paint_bottomMenu = (GridView)this.findViewById(2131427333);
        this.paint_bottomMenu.setOnItemClickListener(new HandWriteActivity.MenuClickEvent());
        this.et_handwrite = (LineEditText)this.findViewById(2131427331);
        this.InitPaintMenu();
        this.touchView = (TouchView)this.findViewById(2131427332);
        this.touchView.setHandler(this.handler);
        this.btn_save = (Button)this.findViewById(2131427356);
        this.btn_back = (Button)this.findViewById(2131427354);
        this.btn_save.setOnClickListener(new HandWriteActivity.ClickEvent());
        this.btn_back.setOnClickListener(new HandWriteActivity.ClickEvent());
    }

    public void InitPaintMenu() {
        ArrayList<Map<String, Object>> menus = new ArrayList();

        for(int i = 0; i < this.paintItems.length; ++i) {
            Map<String, Object> item = new HashMap();
            item.put("image", this.paintItems[i]);
            menus.add(item);
        }

        this.paint_bottomMenu.setNumColumns(this.paintItems.length);
        this.paint_bottomMenu.setSelector(2130837505);
        SimpleAdapter mAdapter = new SimpleAdapter(this, menus, 2130903049, new String[]{"image"}, new int[]{2131427347});
        this.paint_bottomMenu.setAdapter(mAdapter);
    }

    private void InsertToEditText(Bitmap mBitmap) {
        int S = 120;
        int imgWidth = mBitmap.getWidth();
        int imgHeight = mBitmap.getHeight();
        double partion = (double)imgWidth * 1.0D / (double)imgHeight;
        double sqrtLength = Math.sqrt(partion * partion + 1.0D);
        double newImgW = (double)S * (partion / sqrtLength);
        double newImgH = (double)S * (1.0D / sqrtLength);
        float scaleW = 80.0F / (float)imgWidth;
        float scaleH = 100.0F / (float)imgHeight;
        Matrix mx = new Matrix();
        mx.postScale(scaleW, scaleH);
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, imgWidth, imgHeight, mx, true);
        SpannableString ss = new SpannableString("1");
        ImageSpan span = new ImageSpan(mBitmap, 0);
        ss.setSpan(span, 0, 1, 17);
        this.et_handwrite.append(ss);
    }

    public void showPaintColorDialog(View parent) {
        Builder alertDialogBuilder = new Builder(this, 2131230721);
        alertDialogBuilder.setTitle("选择画笔颜色：");
        alertDialogBuilder.setSingleChoiceItems(2131034114, this.select_handwrite_color_index, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HandWriteActivity.this.select_handwrite_color_index = which;
                HandWriteActivity.this.touchView.selectHandWriteColor(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public void showPaintSizeDialog(View parent) {
        Builder alertDialogBuilder = new Builder(this, 2131230721);
        alertDialogBuilder.setTitle("选择画笔大小：");
        alertDialogBuilder.setSingleChoiceItems(2131034113, this.select_handwrite_size_index, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HandWriteActivity.this.select_handwrite_size_index = which;
                HandWriteActivity.this.touchView.selectHandWritetSize(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    public String saveBitmap() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String paintPath = "";
        str = str + "write.png";
        File dir = new File("/sdcard/notes/");
        File file = new File("/sdcard/notes/", str);
        if (!dir.exists()) {
            dir.mkdir();
        } else if (file.exists()) {
            file.delete();
        }

        this.et_handwrite.setDrawingCacheEnabled(true);
        Bitmap cutHandwriteBitmap = Bitmap.createBitmap(this.et_handwrite.getDrawingCache());
        this.et_handwrite.setDrawingCacheEnabled(false);

        try {
            paintPath = "/sdcard/notes/" + str;
            FileOutputStream out = new FileOutputStream(file);
            cutHandwriteBitmap.compress(CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException var9) {
            var9.printStackTrace();
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        return paintPath;
    }

    class ClickEvent implements android.view.View.OnClickListener {
        ClickEvent() {
        }

        public void onClick(View v) {
            if (v == HandWriteActivity.this.btn_save) {
                Intent intent = HandWriteActivity.this.getIntent();
                Bundle b = new Bundle();
                String path = HandWriteActivity.this.saveBitmap();
                b.putString("handwritePath", path);
                intent.putExtras(b);
                HandWriteActivity.this.setResult(-1, intent);
                HandWriteActivity.this.finish();
            } else if (v == HandWriteActivity.this.btn_back) {
                HandWriteActivity.this.finish();
            }

        }
    }

    class MenuClickEvent implements OnItemClickListener {
        MenuClickEvent() {
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0:
                    HandWriteActivity.this.showPaintSizeDialog(view);
                    break;
                case 1:
                    HandWriteActivity.this.showPaintColorDialog(view);
                    break;
                case 2:
                    Editable editable = HandWriteActivity.this.et_handwrite.getText();
                    int selectionEnd = HandWriteActivity.this.et_handwrite.getSelectionEnd();
                    System.out.println("end = " + selectionEnd);
                    if (selectionEnd < 1) {
                        HandWriteActivity.this.et_handwrite.setText("");
                    } else {
                        CharSequence deleteCharSeq;
                        if (selectionEnd == 1) {
                            HandWriteActivity.this.et_handwrite.setText("");
                            deleteCharSeq = editable.subSequence(0, 1);
                            HandWriteActivity.this.deleteChar.add(deleteCharSeq);
                        } else {
                            System.out.println("delete");
                            deleteCharSeq = editable.subSequence(0, selectionEnd - 1);
                            CharSequence deleteCharSeqx = editable.subSequence(selectionEnd - 1, selectionEnd);
                            HandWriteActivity.this.et_handwrite.setText(deleteCharSeq);
                            HandWriteActivity.this.et_handwrite.setSelection(selectionEnd - 1);
                            HandWriteActivity.this.deleteChar.add(deleteCharSeqx);
                        }
                    }
                    break;
                case 3:
                    int length = HandWriteActivity.this.deleteChar.size();
                    if (length > 0) {
                        HandWriteActivity.this.et_handwrite.append((CharSequence)HandWriteActivity.this.deleteChar.get(HandWriteActivity.this.deleteChar.size() - 1));
                        HandWriteActivity.this.deleteChar.remove(HandWriteActivity.this.deleteChar.size() - 1);
                    }
                    break;
                case 4:
                    if (HandWriteActivity.this.et_handwrite.getSelectionEnd() > 0) {
                        Builder builder = new Builder(HandWriteActivity.this, 2131230721);
                        builder.setTitle("清空提示");
                        builder.setMessage("您确定要清空所有吗？");
                        builder.setPositiveButton("确定", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HandWriteActivity.this.et_handwrite.setText("");
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton("取消", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
            }

        }
    }
}
