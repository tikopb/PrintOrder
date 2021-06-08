package com.example.laundry.CustomerList;

public class CustomersItem {
    String id;
    String nama;
    String no_hp;
    String alamat;
    String jk;

    public CustomersItem(String id, String nama, String no_hp, String alamat, String jk) {
        this.id=id;
        this.nama=nama;
        this.no_hp=no_hp;
        this.alamat=alamat;
        this.jk=jk;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public String getJk() {
        return jk;
    }
}
