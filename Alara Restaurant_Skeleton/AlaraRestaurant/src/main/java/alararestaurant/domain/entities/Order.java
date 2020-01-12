package alararestaurant.domain.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "orders")
public class Order extends BaseEntity{
    private String customer;
    private LocalDate dateTime;
    private OrderType type;
    private Employee employee;
    private List<OrderItem> orderItem;

    public Order() {
    }

    @Column(name = "customer",nullable = false,columnDefinition = "text")
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Column(name = "date_time",nullable = false)
    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    @Enumerated(EnumType.ORDINAL)//TODO check for defaut ForHere and ENUM in db
    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }


    @ManyToOne(targetEntity = Employee.class)
    @JoinColumn(name = "employee_id",referencedColumnName = "id",nullable = false)
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @OneToMany(targetEntity = OrderItem.class,mappedBy = "order")
    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
