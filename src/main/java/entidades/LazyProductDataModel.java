/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;
import org.apache.commons.collections.ComparatorUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.util.LocaleUtils;

/**
 *
 * @author acarmonaf
 */
public class LazyProductDataModel extends LazyDataModel<Product> {

    private List<Product> datasource;

    public LazyProductDataModel(List<Product> datasource) {
        this.datasource = datasource;
    }

    @Override
    public Product getRowData(String rowKey) {
        for (Product product : datasource) {
            if (product.getId() == Integer.parseInt(rowKey)) {
                return product;
            }
        }

        return null;
    }

    @Override
    public String getRowKey(Product product) {
        return String.valueOf(product.getId());
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return datasource.size();
    }

    @Override
    public List<Product> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<Product> products = datasource.stream()
                .filter(o -> filter(FacesContext.getCurrentInstance(), filterBy.values(), o))
                .collect(Collectors.toList());

        if (!sortBy.isEmpty()) {
            List<Comparator<Product>> comparators = sortBy.values().stream()
                    .map(o -> new LazySorter(o.getField(), o.getOrder()))
                    .collect(Collectors.toList());
            Comparator<Product> cp = ComparatorUtils.chainedComparator(comparators);
            products.sort(cp);
        }

        return products.subList(offset, Math.min(offset + pageSize, products.size()));
    }

    public static final Object getPropertyValueViaReflection(Object o, String field)
            throws ReflectiveOperationException, IllegalArgumentException, IntrospectionException {
        return new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o);
    }

    private boolean filterYo(FacesContext context, Collection<FilterMeta> filterBy, Product o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            FilterConstraint constraint = filter.getConstraint();
            Object filterValue = filter.getFilterValue();

            if (filter.getField() == null || filter.getFilterValue().toString().isEmpty()) {
                break;
            }

            if (filter.getField().equals("globalFilter")) {
                matching = o.getDescription() == null ? false : o.getDescription().toUpperCase().contains(filterValue.toString().toUpperCase())
                        || o.getType().getName() == null ? false : o.getType().getName().toUpperCase().contains(filterValue.toString().toUpperCase());
            } else {
                try {
                    Object columnValue = String.valueOf(getPropertyValueViaReflection(o, filter.getField()));
                    matching = constraint.isMatching(context, columnValue, filterValue, LocaleUtils.getCurrentLocale());
                } catch (ReflectiveOperationException | IntrospectionException e) {
                    matching = false;
                }
            }

            if (!matching) {
                break;
            }
        }

        return matching;
    }

    private boolean filter(FacesContext context, Collection<FilterMeta> filterBy, Product o) {
        boolean matching = true;

        for (FilterMeta filter : filterBy) {
            if (filter.getField() != null && filter.getFilterValue() != null) {
                String filterValue = filter.getFilterValue().toString();
                if (!filterValue.isEmpty()) {
                    switch (filter.getField()) {
                        case "description":
                            matching = o.getDescription() == null ? false : o.getDescription().toUpperCase().contains(filterValue.toUpperCase());
                            break;
                        case "type.name":
                            matching = o.getType().getName() == null ? false : o.getType().getName().toUpperCase().contains(filterValue.toUpperCase());
                            break;
                        case "date":
                            matching = o.getDateFormatted() == null ? false : o.getDateFormatted().toUpperCase().contains(filterValue.toUpperCase());
                            break;
                        case "globalFilter":
                            matching = (o.getDescription() == null ? false : o.getDescription().toUpperCase().contains(filterValue.toUpperCase()))
                                    || (o.getType() == null ? false : o.getType().getName() == null ? false : o.getType().getName().toUpperCase().contains(filterValue.toUpperCase()))
                                    || (o.getDateFormatted() == null ? false : o.getDateFormatted().toUpperCase().contains(filterValue.toUpperCase()));
                            break;
                    }
                }
                if (!matching) {
                    break;
                }
            }
        }

        return matching;
    }

    public List<Product> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<Product> datasource) {
        this.datasource = datasource;
    }
}
