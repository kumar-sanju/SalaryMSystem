package com.sanju.salarymsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Overview extends AppCompatActivity {
    Toolbar toolbar;
    GregorianCalendar start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Overview");
        TextView date = (TextView) findViewById(R.id.date);
        TextView message = (TextView) findViewById(R.id.message);
        Calendar cal = Calendar.getInstance();
        int dd;
        int mm;
        int yy;
        dd = cal.get(Calendar.DAY_OF_MONTH);
        mm = cal.get(Calendar.MONTH);
        yy = cal.get(Calendar.YEAR);
        date.setText(dd + "-" + mm + "-" + yy);

        Intent intent = getIntent();
        long startTime = intent.getLongExtra("FROM_DATE", 0);
        long endTime = intent.getLongExtra("TO_DATE", 0);
        start = new GregorianCalendar();
        end = new GregorianCalendar();
        start.setTimeInMillis(startTime);
        end.setTimeInMillis(endTime);
        end = new GregorianCalendar();
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final ArrayList<Data> data;
        SalaryDbHelper dbHelper = new SalaryDbHelper(this);
        data = dbHelper.getDataBetweenDates(start.getTimeInMillis(), end.getTimeInMillis());
        if (data.size() == 0) {
            message.setVisibility(View.VISIBLE);
        }
        MyAdapter myAdapter = new MyAdapter(data);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnItemTouchListener( new RecyclerItemClickListener(Overview.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Overview.this, Information.class);
                        intent.putExtra("ID", data.get(position).getId());
                        startActivity(intent);
                        finish();
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.about:
                startActivity(new Intent(Overview.this, About.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(Overview.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
