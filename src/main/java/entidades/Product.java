/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author acarmonaf
 */
public class Product implements Serializable{
    private int id;
    private String description;
    private Date date;
    private double price;
    private boolean enBaja;
    private Type type;
    private int stock;
    private List<Category> categories;
    
    public Product(){
    }

    public Product(int id, String description, Date date, double price, boolean enBaja, Type type, int stock, List<Category> categories) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.price = price;
        this.enBaja = enBaja;
        this.type = type;
        this.stock = stock;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }
    
    public String getDateFormatted() {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public double getPrice() {
        return price;
    }

    public boolean isEnBaja() {
        return enBaja;
    }

    public Type getType() {
        return type;
    }

    public int getStock() {
        return stock;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setEnBaja(boolean enBaja) {
        this.enBaja = enBaja;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
