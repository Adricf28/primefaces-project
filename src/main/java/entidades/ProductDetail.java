/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author acarmonaf
 */
@ManagedBean(name = "ProductDetail")
@ViewScoped
public class ProductDetail {

    private int productId;
    private String productDescription;
    private List<Product> products;

    @PostConstruct
    public void init() {
        productId = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("productId"));
        productDescription = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("productDescription");
        products =;
    }

    public void fillAllProducts(int total) {
        products = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < total; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, r.nextInt(365));
            products.add(new Product((i + 1), "Product " + (i + 1), calendar.getTime(), ((int) (Math.random() * 30 + 1)), Math.random() < 0.5, this.getRandomType(), ((int) (Math.random() * 20 + 1)), getRandomCategories()));
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
