/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private String productDescription;
    private List<Product> products;
    private List<Product> productsFiltered;

    @PostConstruct
    public void init() {
        productDescription = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("productDescription");
        fillProducts();
    }

    public void fillProducts() {
        products = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Categoria 1", 1));
        categories.add(new Category(2, "Categoria 2", 2));
        categories.add(new Category(3, "Categoria 3", 3));

        products.add(new Product(1, "Product 1", new Date(), 22.0, true, new Type(1, "Tipo 1"), 0, categories));
        products.add(new Product(2, "Product 2", new Date(), 10.0, false, new Type(2, "Tipo 2"), 15, categories));
        products.add(new Product(3, "Product 3", new Date(), 43.0, true, new Type(3, "Tipo 3"), 8, categories));
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

    public List<Product> getProductsFiltered() {
        return productsFiltered;
    }

    public void setProductsFiltered(List<Product> productsFiltered) {
        this.productsFiltered = productsFiltered;
    }
}
