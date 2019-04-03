package net.nolanbecker.mileagelog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import net.nolanbecker.mileagelog.data.PrefManager;
import net.nolanbecker.mileagelog.data.dialogs.AddMilesDialog;
import net.nolanbecker.mileagelog.data.model.Entry;
import net.nolanbecker.mileagelog.data.model.Mile;
import net.nolanbecker.mileagelog.data.model.User;
import net.nolanbecker.mileagelog.data.remote.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static Adapter adapter;
    private static RecyclerView recyclerView;
    private static Service service;
    private static SwipeRefreshLayout swipeRefresh;
    private TextView appTitle;
    private static TextView totalMiles;
    private FloatingActionButton fab;
    private LinearLayout titleBar;
    private GoogleSignInClient gsc;
    private static List<Mile> miles;
    public static String SORT;
    public static Date DATE;
    public static String SIMPLE_DATE;
    public static int ID, TOTAL, OLD_TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DATE = getDate();
        TOTAL = 0;
        OLD_TOTAL = 0;

        appTitle = (TextView) findViewById(R.id.appTitle);
        totalMiles = (TextView) findViewById(R.id.totalMiles);
        titleBar = (LinearLayout) findViewById(R.id.titleBar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMilesDialog addMilesDialog = new AddMilesDialog(MainActivity.this);
                addMilesDialog.show();
                addMilesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        if (!addMilesDialog.wasCancelled())
                            if (addMilesDialog.getMiles() < TOTAL) {
                                Snackbar.make(recyclerView, "New Total must be equal to or more than the previous", Snackbar.LENGTH_SHORT).show();
                            } else {
                                swipeRefresh.setRefreshing(true);
                                setMiles(addMilesDialog.getDate(), addMilesDialog.getMiles() - TOTAL);
                            }
                    }
                });
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                signOut();
                return false;
            }
        });

        service = ApiUtils.getService();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new Adapter(this, new ArrayList<Mile>(0), new Adapter.PostItemListener() {
            @Override
            public void onPostClick(int miles, String date, Context context) {
                String niceDate = date;

                try {
                    Date day = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    niceDate = new SimpleDateFormat("MMMM d, yyyy").format(day);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        swipeRefresh.setRefreshing(true);
                        delMiles(date);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setMessage("Delete data for " + niceDate + "?");
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(itemDecoration);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMiles();
            }
        });

        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SORT.equals("month")) {
                    SORT = "year";
                } else if (SORT.equals("year")) {
                    SORT = "month";
                }
                new PrefManager(getApplicationContext()).saveSortType(SORT);
                loadMiles();
                setTitle();
            }
        });

        SORT = new PrefManager(this).getSort();

    }

    public static void loadMiles() {
        service.getMiles(ID).enqueue(new Callback<Entry>() {
            @Override
            public void onResponse(Call<Entry> call, Response<Entry> response) {
                if(response.isSuccessful()) {
                    miles = response.body().getMiles();
                    setTotalMiles();
                    adapter.updateMiles(sortMiles(miles));
                } else {
                    Snackbar.make(recyclerView, "Hmm", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Entry> call, Throwable t) {
                Snackbar.make(recyclerView, "No data available", Snackbar.LENGTH_SHORT).show();
            }
        });
        swipeRefresh.setRefreshing(false);
    }

    public static void setMiles(String date, int miles) {

        boolean update = false;
        int oldMiles = 0;

        for (Mile mile : MainActivity.miles) {
            if (mile.getDate().equals(date)) {
                update = true;
                oldMiles = mile.getMiles();
            }
        }

        if (update) {
            updateMiles(date, miles+oldMiles);
        } else {

            service.setMiles(ID, miles, date).enqueue(new Callback<Entry>() {
                @Override
                public void onResponse(Call<Entry> call, Response<Entry> response) {
                    Snackbar.make(recyclerView, "Added miles", Snackbar.LENGTH_SHORT).show();
                    MainActivity.miles = response.body().getMiles();
                    setTotalMiles();
                    adapter.updateMiles(sortMiles(MainActivity.miles));
                    swipeRefresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<Entry> call, Throwable t) {
                    Snackbar.make(recyclerView, "Failed to add miles", Snackbar.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                }
            });
        }
    }

    public static void updateMiles(String date, int newMiles) {
        service.updateMiles(ID, newMiles, date).enqueue(new Callback<Entry>() {
            @Override
            public void onResponse(Call<Entry> call, Response<Entry> response) {
                Snackbar.make(recyclerView, "Updated miles", Snackbar.LENGTH_SHORT).show();
                miles = response.body().getMiles();
                setTotalMiles();
                adapter.updateMiles(sortMiles(miles));
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Entry> call, Throwable t) {
                Snackbar.make(recyclerView, "Failed to update miles", Snackbar.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void delMiles(String date) {
        service.delMiles(ID, date).enqueue(new Callback<Entry>() {
            @Override
            public void onResponse(Call<Entry> call, Response<Entry> response) {
                Snackbar.make(recyclerView, "Deleted miles", Snackbar.LENGTH_SHORT).show();
                miles = response.body().getMiles();
                setTotalMiles();
                adapter.updateMiles(sortMiles(miles));
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Entry> call, Throwable t) {
                Snackbar.make(recyclerView, "Failed to delete miles", Snackbar.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void setTitle() {
        String newTitle = "";
        if(SORT.equals("month")) {
            newTitle = new SimpleDateFormat("MMMM").format(DATE);
        } else if(SORT.equals("year")) {
            newTitle = new SimpleDateFormat("yyyy").format(DATE);
        } else {
            newTitle = "MileageLog";
        }
        //TextView title = getSupportActionBar().getCustomView().findViewById(R.id.appTitle);
        appTitle.setText(newTitle);
    }

    public Date getDate() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        SIMPLE_DATE = localDate.toString();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(localDate.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void setTotalMiles() {
        OLD_TOTAL = TOTAL;
        TOTAL = 0;
        for (int i=0; i < miles.size(); i++) {
            TOTAL = TOTAL + miles.get(i).getMiles();
        }
        totalMiles.setText(String.valueOf(TOTAL));
    }

    private static List<Mile> sortMiles(List<Mile> miles) {
        final String condition;
        if (SORT.equals("month")) {
            String month = new SimpleDateFormat("MM").format(DATE);
            condition = "-" + month + "-";
        } else if(SORT.equals("year")) {
            String year = new SimpleDateFormat("yyyy").format(DATE);
            condition = year + "-";
        } else {
            condition = "";
        }
        List<Mile> newMiles = miles.stream()
                .filter(item -> item.getDate().contains(condition))
                .collect(Collectors.toList());
        if (newMiles.size() == 0)
            Snackbar.make(recyclerView, "No data available", Snackbar.LENGTH_SHORT).show();
        return newMiles;
    }

    private void signOut() {
        new PrefManager(this).saveLoginInfo("", "", 0);
        login();
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (new PrefManager(this).isLoggedOut()) {
            login();
        } else {
            ID = new PrefManager(this).getId();
            if (ID == 0) {
                signOut();
            } else {
                loadMiles();
                setTitle();
            }
        }
    }
}