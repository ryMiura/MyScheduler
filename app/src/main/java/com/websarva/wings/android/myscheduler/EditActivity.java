package com.websarva.wings.android.myscheduler;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private EditText etDate;
    private EditText etTitle;
    private EditText etContent;

    private Button btDel;
    private Button btSave;

    int _id;
    int todoListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //それぞれのエディットテキスト部品生成
        etDate = findViewById(R.id.etDate);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        //保存と削除ボタン
        btSave = findViewById(R.id.btSave);
        btDel = findViewById(R.id.btDelete);


        //Intent get
        Intent intent = getIntent();
        _id = intent.getIntExtra("_id",0);
        String day = intent.getStringExtra("day");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        todoListId = intent.getIntExtra("todoListId",-2);

        Toast.makeText(EditActivity.this,""+todoListId,Toast.LENGTH_SHORT).show();

        etDate.setText(day);
        etTitle.setText(title);
        etContent.setText(content);

        //戻るアクションバー
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //保存ボタンがタップされたときの処理
    public void onSaveButtonClick(View view){

        String daynote = etDate.getText().toString();
        String titlenote = etTitle.getText().toString();
        String contentnote = etContent.getText().toString();

        DatabaseHelper helper = new DatabaseHelper(EditActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        try{

            if(todoListId == -2){
                String sql = "INSERT INTO todolist(day,title,content) VALUES(?,?,?)";
                SQLiteStatement stmt = db.compileStatement(sql);
                stmt.bindString(1,daynote);
                stmt.bindString(2,titlenote);
                stmt.bindString(3,contentnote);
                stmt.executeInsert();
                Toast.makeText(EditActivity.this,"保存されました",Toast.LENGTH_LONG).show();

            }else{
                String sqldel = "DELETE FROM todolist WHERE _id = "+todoListId;
                SQLiteStatement stmt = db.compileStatement(sqldel);
                stmt.executeUpdateDelete();

                String sql = "INSERT INTO todolist(_id,day,title,content) VALUES(?,?,?,?)";
                stmt = db.compileStatement(sql);
                stmt.bindLong(1,todoListId);
                stmt.bindString(2,daynote);
                stmt.bindString(3,titlenote);
                stmt.bindString(4,contentnote);
                stmt.executeInsert();
                Toast.makeText(EditActivity.this,"更新されました"+todoListId,Toast.LENGTH_LONG).show();
            }





        }catch (Exception e){
            Toast.makeText(EditActivity.this,"エラー",Toast.LENGTH_LONG).show();

        }finally {
            db.close();
        }
        finish();


    }


    //削除ボタンがおされたときの処理
    public void onDeleteButtonClick(View view){


        DatabaseHelper helper = new DatabaseHelper(EditActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try{

            String sql = "DELETE FROM todolist WHERE _id = ?;";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1,_id);
            stmt.executeUpdateDelete();
            Toast.makeText(EditActivity.this,"削除しました" + _id,Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(EditActivity.this,"エラー",Toast.LENGTH_LONG).show();

        }finally {
            db.close();
        }

        finish();




    }




    //Optionがタップされたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //戻るがおされたらアクティビティを終了
        int itemid = item.getItemId();

        if(itemid == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
