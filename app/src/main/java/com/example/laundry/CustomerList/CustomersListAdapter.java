package com.example.laundry.CustomerList;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.laundry.CustomersList;
import com.example.laundry.R;

import java.util.ArrayList;
import java.util.List;

public class CustomersListAdapter extends RecyclerView.Adapter<CustomersListAdapter.ViewHolder>{
    private List<CustomersItem> itemList;


    //Membuat Konstruktor pada Class RecyclerViewAdapter
    public CustomersListAdapter(List<CustomersItem> itemList){
        this.itemList= itemList;
    }

    //ViewHolder Digunakan Untuk Menyimpan Referensi Dari View-View
    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView Nama, no_hp, alamat, jk;

        ViewHolder(View itemView) {
            super(itemView);
            //Menginisialisasi View-View untuk kita gunakan pada RecyclerView
            Nama = itemView.findViewById(R.id.name);
            no_hp = itemView.findViewById(R.id.no_hp);
            alamat = itemView.findViewById(R.id.alamat);
            jk = itemView.findViewById(R.id.jk);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Membuat View untuk Menyiapkan dan Memasang Layout yang Akan digunakan pada RecyclerView
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Memanggil Nilai/Value Pada View-View Yang Telah Dibuat pada Posisi Tertentu
        final CustomersItem item = itemList.get(position);

        holder.Nama.setText(item.getNama());
        Log.d("hasilid", item.getId());
        holder.alamat.setText(item.getAlamat());
        holder.no_hp.setText(item.getNo_hp());
        holder.jk.setText(item.getJk());

       // https://www.wildantechnoart.net/2018/01/cara-menampilkan-data-sqlite-pada-recyclerview.html

    }

    @Override
    public int getItemCount() {
        //Menghitung Ukuran/Jumlah Data Yang Akan Ditampilkan Pada RecyclerView
        return itemList.size();
    }
}
