/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;

/**
 *
 * @author acarmonaf
 */
@ManagedBean(name = "WindowController")
@ViewScoped
public class WindowController {

    private List<Type> types;
    private List<Category> categories;
    private List<Product> products;
    private List<Product> allProducts;
    private Product selectedProduct;
    private int idTypeSelected = 0;
    private int totalStock;
    private boolean updating;
    private BarChartModel barModel;
    private LineChartModel lineModel;
    private LazyDataModel<Product> lazyModel;
    private ExcelOptions excelOpts;

    @PostConstruct
    public void init() {
        types = new ArrayList<>();
        categories = new ArrayList<>();
        products = new ArrayList<>();
        allProducts = new ArrayList<>();
        this.fillTypes();
        this.fillCategories();
        this.fillAllProducts(50);
        createBarModel();
        createLineModel();
        lazyModel = new LazyProductDataModel(products);
        customizeExcel();
    }

    public Type getRandomType() {
        Random r = new Random();
        return types.get(r.nextInt(types.size()));
    }

    public ArrayList<Category> getRandomCategories() {
        Random r = new Random();
        ArrayList<Category> randomCategories = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            randomCategories.add(categories.get(r.nextInt(categories.size())));
        }
        return randomCategories;
    }

    public void fillTypes() {
        types.add(new Type(1, "Type 1"));
        types.add(new Type(2, "Type 2"));
        types.add(new Type(3, "Type 3"));
        types.add(new Type(4, "Type 4"));
        types.add(new Type(5, "Type 5"));
    }

    public void fillCategories() {
        categories.add(new Category(1, "Category 1", 1));
        categories.add(new Category(2, "Category 2", 2));
        categories.add(new Category(3, "Category 3", 3));
        categories.add(new Category(4, "Category 4", 1));
        categories.add(new Category(5, "Category 5", 2));
        categories.add(new Category(6, "Category 6", 3));
        categories.add(new Category(7, "Category 7", 1));
        categories.add(new Category(8, "Category 8", 2));
        categories.add(new Category(9, "Category 9", 3));
    }

    public void fillAllProducts(int total) {
        for (int i = 0; i < total; i++) {
            allProducts.add(new Product((i + 1), "Product " + (i + 1), new Date(), ((int) (Math.random() * 30 + 1)), Math.random() < 0.5, this.getRandomType(), ((int) (Math.random() * 20 + 1)), getRandomCategories()));
        }
    }

    public void openNew() {
        this.selectedProduct = new Product();
        this.idTypeSelected = 1;
        this.updating = false;
    }

    public void updateProduct(Product productToUpdate) {
        this.selectedProduct = productToUpdate;
        this.updating = true;
    }

    public void saveProduct() {
        boolean idExists = false;
        boolean invalid = false;
        for (Type type : types) {
            if (type.getId() == idTypeSelected) {
                selectedProduct.setType(type);
            }
        }
        for (Product product : products) {
            if (product.getId() == selectedProduct.getId()) {
                idExists = true;
            }
        }
        if (this.selectedProduct.getId() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Producto Incompleto", "El campo id no puede estar vacio"));
            invalid = true;
        }

        if (idExists && !updating) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Producto Repetido", "El id ya existe"));
            invalid = true;
        }

        if (selectedProduct.getDescription().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Producto Incompleto", "El campo descripcion no puede estar vacio"));
            invalid = true;
        }

        if (selectedProduct.getDate() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Producto Incompleto", "El campo date no puede estar vacio"));
            invalid = true;
        }

        if (!invalid) {
            if (updating) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("¡Producto Actualizado!"));
            } else {
                this.products.add(this.selectedProduct);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("¡Producto Creado!"));
            }

            PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        }
    }

    public void deleteProduct(Product p) {
        products.remove(p);
    }

    public void orderRows() {
        for (int i = 0; i < selectedProduct.getCategories().size(); i++) {
            selectedProduct.getCategories().get(i).setOrder(i + 1);
        }
    }

    public void createBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Stock");

        List<String> labels = new ArrayList<>();
        List<Number> stock = new ArrayList<>();
        for (Product product : this.products) {
            stock.add(product.getStock());
            labels.add(product.getDescription());
        }
        barDataSet.setData(stock);
        data.setLabels(labels);

        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        linearAxes.setBeginAtZero(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
    }

    public void createLineModel() {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> prices = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Product product : products) {
            prices.add(product.getPrice());
            labels.add(product.getDescription());
        }
        dataSet.setData(prices);
        data.setLabels(labels);

        dataSet.setFill(false);
        dataSet.setLabel("Price");
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setTension(0.1);
        data.addChartDataSet(dataSet);

        lineModel.setData(data);
    }

    public void showProducts() {
        this.products = new ArrayList<>();
        if (idTypeSelected == 0) {
            products = allProducts;
        } else {
            for (Product product : allProducts) {
                if (product.getType().getId() == idTypeSelected) {
                    products.add(product);
                }
            }
        }
        createBarModel();
        createLineModel();
        lazyModel = new LazyProductDataModel(products);
    }

    public void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(sheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

    public void exportExcel() {
        try {
            Workbook wb = new HSSFWorkbook();
            DataFormat format = wb.createDataFormat();

            Sheet sheet = wb.createSheet("Products");
            sheet.setAutoFilter(CellRangeAddress.valueOf("A:G"));

            String[] fields = {"Id", "Description", "Date", "Price", "EnBaja", "Type", "Stock"};

            Row headers = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                CellStyle style = wb.createCellStyle();
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Cell header = headers.createCell(i);
                header.setCellValue(fields[i]);
                header.setCellStyle(style);
            }

            for (int i = 0; i < products.size(); i++) {
                int column = 0;
                Row row = sheet.createRow(i + 1);

                row.createCell(column++).setCellValue(products.get(i).getId());
                row.createCell(column++).setCellValue(products.get(i).getDescription());
                row.createCell(column++).setCellValue(products.get(i).getDateFormatted());

                CellStyle doubleStyle = wb.createCellStyle();
                doubleStyle.setDataFormat(format.getFormat("#.##0,00"));
                Cell priceCell = row.createCell(column++);
                priceCell.setCellValue(products.get(i).getPrice());
                priceCell.setCellStyle(doubleStyle);

                row.createCell(column++).setCellValue(products.get(i).isEnBaja() ? "Si" : "No");
                row.createCell(column++).setCellValue(products.get(i).getType().getName());
                row.createCell(column++).setCellValue(products.get(i).getStock());
            }

            for (int i = 0; i < fields.length; i++) {
                if (fields[i].equalsIgnoreCase("Description")
                        || fields[i].equalsIgnoreCase("Date")) {
                    sheet.setColumnWidth(i, 12 * 256);
                }
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=products.xls");
            wb.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(WindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void customizeExcel() {
        excelOpts = new ExcelOptions();
        excelOpts.setFacetBgColor("#808080");
        excelOpts.setFacetFontSize("12");
        excelOpts.setCellFontSize("10");
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public int getIdTypeSelected() {
        return idTypeSelected;
    }

    public void setIdTypeSelected(int idTypeSelected) {
        this.idTypeSelected = idTypeSelected;
    }

    public int getTotalStock() {
        return products.stream().mapToInt(Product::getStock).sum();
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }

    public void setAllProducts(List<Product> allProducts) {
        this.allProducts = allProducts;
    }

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public LazyDataModel<Product> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Product> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public ExcelOptions getExcelOpts() {
        return excelOpts;
    }

    public void setExcelOpts(ExcelOptions excelOpts) {
        this.excelOpts = excelOpts;
    }
}
