package com.example.minyoung.finding_dog.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.minyoung.finding_dog.R;
import com.example.minyoung.finding_dog.SingerItem;
import com.example.minyoung.finding_dog.SingerItemView;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by minyoung on 2018-06-28.
 */

public class shop_fragment extends Fragment {
    GridView gridView;
    SingerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        gridView = (GridView)view.findViewById(R.id.gridView);

        adapter = new SingerAdapter();
        adapter.addItem(new SingerItem("버팔로", "애완목줄", 2000,R.drawable.img1));
        adapter.addItem(new SingerItem("강쥐언니", "펫방석(레드)", 10000, R.drawable.img2));
        adapter.addItem(new SingerItem("퍼프라스트", "코코로 방석", 15000, R.drawable.img3));
        adapter.addItem(new SingerItem("넉넉이", "애견밥그릇", 3000, R.drawable.img4));
        adapter.addItem(new SingerItem("레인보우", "강아지 원피스", 2500, R.drawable.img5));
        adapter.addItem(new SingerItem("4Legs", "애견 샴푸", 3500, R.drawable.img6));
        adapter.addItem(new SingerItem("토일렛", "강아지 배변판", 7000, R.drawable.img8));
        adapter.addItem(new SingerItem("시저", "간식-쇠고기", 1000, R.drawable.img7));
        adapter.addItem(new SingerItem("시저", "간식-불고기", 1000, R.drawable.img10));
        adapter.addItem(new SingerItem("시저", "간식-양고기", 1000, R.drawable.img9));
        adapter.addItem(new SingerItem("시저", "간식-쇠고기와 치즈", 1000, R.drawable.img12));
        adapter.addItem(new SingerItem("시저", "간식-쇠고기와 참치", 1000, R.drawable.img11));

        gridView.setAdapter(adapter);

        return view;
    }
    class SingerAdapter extends BaseAdapter {
        ArrayList<SingerItem> items = new ArrayList<SingerItem>();

        @Override
        public int getCount(){return items.size();}

        public void addItem(SingerItem item){items.add(item);}

        @Override
        public Object getItem(int position){return items.get(position);}

        @Override
        public long getItemId(int position){return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup){
            SingerItemView view = new SingerItemView(getApplicationContext());

            SingerItem item = items.get(position);
            view.setmanufacturer(item.getmanufacturer());
            view.setproduct(item.getproduct());
            view.setprice(item.getprice());
            view.setImage(item.getResId());

            int numColumns = gridView.getNumColumns();
            int rowIndex = position / numColumns;
            int columnIndex = position % numColumns;
            Log.d("SingerAdapter", "index : " + rowIndex + ", " + columnIndex);
            return view;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
