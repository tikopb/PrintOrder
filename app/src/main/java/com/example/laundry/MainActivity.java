package com.example.laundry;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final int cellCount=2;
    Button excel,btnListCustomer;
    ProgressDialog dialog;
    String query;
    SQLiteDatabase db;
    protected Cursor cursor;
    DataHelper dataHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataHelper = new DataHelper(getApplicationContext());
        excel = findViewById(R.id.excel);
        btnListCustomer = findViewById(R.id.btnListCustomer);

        btnListCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomersList.class));
            }
        });

// click on excel to select a file
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectfile();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                }
            }
        });
      /*  db = dataHelper.getWritableDatabase();
        query = "DELETE FROM customers";
        db.execSQL(query); */


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectfile();
            } else {
                Toast.makeText(MainActivity.this, "Permission Not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectfile() {
        // select the file from the file storage
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 102);

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (resultCode == RESULT_OK) {
                String filepath = data.getData().getPath();
                // If excel file then only select the file
                if (filepath.endsWith(".xlsx") || filepath.endsWith(".xls")) {
                    readfile(data.getData());
                }
                // else show the error
                else {
                    Toast.makeText(this, "Please Select an Excel file to upload", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void readfile(final Uri file) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                final HashMap<String, Object> parentmap = new HashMap<>();

                try {
                    XSSFWorkbook workbook;

                    // check for the input from the excel file
                    try (InputStream inputStream = getContentResolver().openInputStream(file)) {
                        workbook = new XSSFWorkbook(inputStream);
                    }
                    final String timestamp = "" + System.currentTimeMillis();
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    int rowscount = sheet.getPhysicalNumberOfRows();
                    if (rowscount > 0) {
                        // check row wise data
                        for (int r = 0; r < rowscount; r++) {
                            Row row = sheet.getRow(r);
                            Log.e("rowwww", String.valueOf(row.getPhysicalNumberOfCells()));
                            if (row.getPhysicalNumberOfCells() > 0) {

                                // get cell data
                                String nama = getCellData(row, 0, formulaEvaluator);
                                String jk = getCellData(row, 1, formulaEvaluator);
                                String no_hp = getCellData(row, 2, formulaEvaluator);
                                String alamat = getCellData(row, 3, formulaEvaluator);



                                // initialise the hash map and put value of a and b into it
                                HashMap<String, Object> quetionmap = new HashMap<>();
                                quetionmap.put("nama", nama);
                                quetionmap.put("jk", jk);
                                quetionmap.put("no_hp", no_hp);
                                quetionmap.put("alamat", alamat);
                                String id = UUID.randomUUID().toString();
                                parentmap.put(id, quetionmap);
                                Log.d("hasil", String.valueOf(quetionmap));

                                db = dataHelper.getWritableDatabase();
                                query = "INSERT INTO customers VALUES ('" + id + "','"+nama+"','"+no_hp+"','"+jk+"','"+alamat+"');";
                                db.execSQL(query);

                            } else {
                                dialog.dismiss();
                                Thread thread = new Thread(){
                                    public void run(){
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "eror", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                };
                                thread.start();
                                return;
                            }
                        }

                        dialog.dismiss();
                       /* cursor = db.rawQuery("SELECT * FROM customers ",null);
                        cursor.moveToFirst();
                        if (cursor.getCount()>0)
                        {
                            cursor.moveToPosition(0);
                            Log.d("hasil customer", String.valueOf(cursor.getString(0)));
                        } */



                    }
                    // show the error if file is empty
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                Toast.makeText(MainActivity.this, "File is empty", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                }
                // show the error message if failed
                // due to file not found
                catch (final FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // show the error message if there
                // is error in input output
                catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private String getCellData(Row row, int cellposition, FormulaEvaluator formulaEvaluator) {

        String value = "";

        // get cell fom excel sheet
        Cell cell = row.getCell(cellposition);
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return value + cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return value + cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                return value + cell.getStringCellValue();
            default:
                return value;
        }
    }

}