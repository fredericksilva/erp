package io.betterlife.domains.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.betterlife.domains.BaseObject;
import io.betterlife.domains.catalog.Product;
import io.betterlife.domains.common.Supplier;
import io.betterlife.domains.financial.Expense;
import io.betterlife.domains.financial.Incoming;
import io.betterlife.domains.security.User;
import io.betterlife.rest.Form;
import io.betterlife.util.condition.FalseCondition;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Author: Lawrence Liu
 * Date: 1/7/15
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "SalesOrder.getById", query = "SELECT c FROM SalesOrder c WHERE c.id = :id AND c.active = TRUE"),
    @NamedQuery(name = "SalesOrder.getAll", query = "SELECT c FROM SalesOrder c WHERE c.active = TRUE")
})
public class SalesOrder extends BaseObject {

    @ManyToOne
    @Form(DisplayRank = 5, RepresentField = "name")
    public Product getProduct() {
        return getValue("product");
    }

    public void setProduct(Product product) {
        setValue("product", product);
    }

    @Transient
    @Form(DisplayRank = 10, RepresentField = "name")
    public Supplier getSupplier() {
        return getProduct().getSupplier();
    }

    @ManyToOne
    @Form(DisplayRank = 15, RepresentField = "displayName")
    public User getTransactor() {
        return getValue("transactor");
    }

    public void setTransactor(User transactor) {
        setValue("transactor", transactor);
    }

    public void setQuantity(BigDecimal quantity) {
        setValue("quantity", quantity);
    }

    @Form(DisplayRank = 20)
    public BigDecimal getQuantity() {
        return getValue("quantity");
    }

    public void setPricePerUnit(BigDecimal ppu) {
        setValue("pricePerUnit", ppu);
    }

    @Form(DisplayRank = 25)
    public BigDecimal getPricePerUnit() {
        return getValue("pricePerUnit");
    }

    public void setLogisticAmount(BigDecimal logisticAmount) {
        setValue("logisticAmount", logisticAmount);
    }

    @Form(DisplayRank = 30)
    public BigDecimal getLogisticAmount() {
        return getValue("logisticAmount");
    }

    @Transient
    @Form(DisplayRank = 35)
    public BigDecimal getAmount() {
        return getPricePerUnit().multiply(getQuantity());
    }

    @Transient
    @Form(DisplayRank = 40)
    public BigDecimal getUnitLogisticAmount() {
        BigDecimal result = BigDecimal.ZERO;
        if (getLogisticAmount() != null && getQuantity() != null) {
            return getLogisticAmount().divide(getQuantity(), 2, BigDecimal.ROUND_HALF_UP);
        }
        return result;
    }

    @Form(DisplayRank = 45)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "CST")
    @Temporal(value = TemporalType.DATE)
    public Date getOrderDate() {
        return getValue("orderDate");
    }

    public void setOrderDate(Date date) {
        setValue("orderDate", date);
    }

    public void setStockOutDate(Date date) {
        setValue("stockOutDate", date);
    }

    @Form(DisplayRank = 47)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = "CST")
    @Temporal(value = TemporalType.DATE)
    public Date getStockOutDate() {
        return getValue("stockOutDate");
    }

    public void setRemark(String remark) {
        setValue("remark", remark);
    }

    @Form(DisplayRank = 50)
    public String getRemark() {
        return getValue("remark");
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "salesOrder")
    @Form(Visible = FalseCondition.class)
    public Incoming getIncoming() {
        return getValue("incoming");
    }

    public void setIncoming(Incoming incoming) {
        setValue("incoming", incoming);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "salesOrder")
    @Form(Visible = FalseCondition.class)
    public List<Expense> getExpenses() {
        return getValue("expenses");
    }

    public void setExpenses(List<Expense> expenses) {
        setValue("expenses", expenses);
    }

}
