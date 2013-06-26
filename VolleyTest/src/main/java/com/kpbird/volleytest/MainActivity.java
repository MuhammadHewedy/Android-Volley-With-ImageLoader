package com.kpbird.volleytest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.arrow.cloud.mws.dto.partSearch.PartSearchResult;
import com.arrow.cloud.mws.dto.partSearch.PartSearchReturn;
import com.kpbird.volleytest.toolbox.GsonRequest;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {

    private String TAG = this.getClass().getSimpleName();
    private ListView lstView;
    private RequestQueue mRequestQueue;
    private ArrayList<PartSearchResult> arrNews;
    private LayoutInflater lf;
    private VolleyAdapter va;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lf = LayoutInflater.from(this);


        arrNews = new ArrayList<PartSearchResult>();
        va = new VolleyAdapter();

        lstView = (ListView) findViewById(R.id.listView);
        lstView.setAdapter(va);
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "http://mobapi.arrow.com/MWS/jsonServlet?function=doPartSearch&p0=bav99&p1=1&p2=20&p3=1&p4=&p5=&p6=itest";

        pd = ProgressDialog.show(this, null, null);

        GsonRequest<PartSearchReturn> gsonRequest = new GsonRequest<PartSearchReturn>(Request
                .Method.GET, url, PartSearchReturn.class,
                new Response.Listener<PartSearchReturn>() {

                    @Override
                    public void onResponse(PartSearchReturn response) {
                        if (response != null) {
                            arrNews.addAll(Arrays.asList(response.getPartSearchResults()));
                            va.notifyDataSetChanged();
                        }
                        pd.dismiss();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                mRequestQueue.cancelAll(this);
            }
        }
        );

        mRequestQueue.add(gsonRequest);

    }

    class VolleyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrNews.size();
        }

        @Override
        public Object getItem(int i) {
            return arrNews.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null) {
                vh = new ViewHolder();
                view = lf.inflate(R.layout.row_listview, null);
                vh.tvTitle = (TextView) view.findViewById(R.id.txtTitle);
                vh.tvDesc = (TextView) view.findViewById(R.id.txtDesc);
                vh.tvDate = (TextView) view.findViewById(R.id.txtDate);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }

            PartSearchResult nm = arrNews.get(i);
            vh.tvTitle.setText(nm.getPartNumber());
            vh.tvDesc.setText(nm.getDescription());
            vh.tvDate.setText(nm.getManName());
            return view;
        }

        class ViewHolder {
            TextView tvTitle;
            TextView tvDesc;
            TextView tvDate;

        }

    }
}
