package com.example.truyenchu.model;

public class Truyen {
    private String id;
    private String ten;
    private String tacGia;
    private String anhBia;
    private String moTa;
    private String theLoaiTags; // Lưu các tag thể loại dưới dạng chuỗi, vd: "Kiếm hiệp, Tiên hiệp"
    private double danhGia;
    private int soLuongDanhGia;
    private String trangThai;

    public Truyen() {
        // Firebase cần constructor rỗng
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }
    public String getAnhBia() { return anhBia; }
    public void setAnhBia(String anhBia) { this.anhBia = anhBia; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getTheLoaiTags() { return theLoaiTags; }
    public void setTheLoaiTags(String theLoaiTags) { this.theLoaiTags = theLoaiTags; }
    public double getDanhGia() { return danhGia; }
    public void setDanhGia(double danhGia) { this.danhGia = danhGia; }
    public int getSoLuongDanhGia() { return soLuongDanhGia; }
    public void setSoLuongDanhGia(int soLuongDanhGia) { this.soLuongDanhGia = soLuongDanhGia; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
