package com.websarva.wings.android.myscheduler;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView _lvSched;                  // スケジュールリストのビュー部品
    private List<Map<String, Object>> _scList;  // スケジュールリストのデータ
    // SimpleAdaprterの第4引数fromに使用する定数フィールド
    private static final String[] FROM = {"day", "title"};
    // SimpleAdaprterの第5引数toに使用する定数フィールド
    private static final int[] TO = {R.id.tvDay, R.id.tvTitle};
    //主キー
    private int todoListId = -1;

    TextView tvtest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvtest = findViewById(R.id.tvtest);

        Button bt = findViewById(R.id.btAdd);



        // スケジュール一覧の表示
        ListDisp();
        // スケジュールデータListタップ時のリスナクラスを登録
        _lvSched.setOnItemClickListener(new ListItemClickListener());


    }



    /**
     * リストがタップされたときの処理が記述されたメンバクラス
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //タップされた行番号をtodoListIdに代入
            todoListId = position;

            // タップされた行のデータを取得
            Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);

            Intent intent = new Intent(MainActivity.this,EditActivity.class);



            //idと日付とタイトルを取得
            int _id = (int)item.get("id");
            String day = (String)item.get("day");
            String title = (String)item.get("title");
            String content = (String)item.get("content");

            //第２画面に送るデータを格納
            intent.putExtra("_id",_id);
            intent.putExtra("day",day);
            intent.putExtra("title",title);
            intent.putExtra("content",content);
            intent.putExtra("todoListId",todoListId);



            //intent.putExtra("content",content);

            startActivity(intent);







        }
    }

    @Override
    protected void onRestart() {

        ListDisp();
        super.onRestart();
    }

    /**
     * スケジュールリストを画面表示するメソッド
     */
    private void ListDisp() {

        // 画面部品ListViewを取得し、フィールドに格納
        _lvSched = findViewById(R.id.lvHead);
        // スケジュールデータ全体格納用のListオブジェクトを用意
        _scList = new ArrayList<>();
        // スケジュールデータ1個ずつを格納するMapオブジェクトの変数
        Map<String, Object> sc;

        //データベースヘルパー呼び出し
        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        String daynote = "";
        String titlenote = "";
        String contentnote = "";
        int _id = -1;
        String id = "";

        // リスト表示用データ
        int[] idInt = new int[30];
        String[] dayStr = new String[30];
        String[] titleStr = new String[30];
        String[] contentStr = new String[30];
        try{
            String sql = "SELECT * FROM todolist ";

            Cursor cursor = db.rawQuery(sql,null);

            int count = 0;
            while (cursor.moveToNext()){
                //id、タイトル、日付の値を順番に取得し、それぞれの配列に代入
                _id = cursor.getInt(cursor.getColumnIndex("_id"));
                titlenote  = cursor.getString(cursor.getColumnIndex("title"));
                daynote = cursor.getString(cursor.getColumnIndex("day"));
                contentnote = cursor.getString(cursor.getColumnIndex("content"));


                dayStr[count] = daynote;
                titleStr[count] = titlenote;
                idInt[count] = _id;
                contentStr[count] = contentnote;
                //カウントアップ
                count++;

            }
            //確認用　あとで消す
            Toast.makeText(MainActivity.this,"表示：" + id,Toast.LENGTH_LONG).show();
            tvtest.setText(titlenote + daynote);

            for (int i = 0; i < dayStr.length; i++) {
                // スケジュールデータ1個分のMapオブジェクトの用意
                sc = new HashMap<>();
                sc.put("id", idInt[i]);         // Mapオブジェクトに格納
                sc.put("day", dayStr[i]);       // Mapオブジェクトに格納
                sc.put("title", titleStr[i]);   // Mapオブジェクトに格納
                sc.put("content",contentStr[i]);
                // スケジュールデータ全体格納用のListオブジェクトに1行追加
                _scList.add(sc);
            }
            // SimpleAdapterを生成（スケジュールリストの各行のデータをrow.xmlの書式で対応付けるため）
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, _scList, R.layout.row, FROM, TO);
            // スケジュールデータをSimpleAdapterに登録
            _lvSched.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"エラー"+ e,Toast.LENGTH_LONG).show();

        }finally {
            db.close();
        }







    }

    //(+)ボタンを押したときの処理
    public void onAddButtonClick(View view){
        //インテントを生成してEditActivityを起動
        Intent intent = new Intent(MainActivity.this,EditActivity.class);
        startActivity(intent);


    }
}
