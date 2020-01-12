package alararestaurant.domain.entities;

import javax.persistence.*;

import javax.validation.constraints.Size;
import java.util.List;


@Entity(name = "employees")
public class Employee extends BaseEntity {
    private String name;
    private Integer age;
    private Position position;
    private List<Order> orders;

    public Employee() {
    }

    @Column(name = "name",nullable = false)
    //@Size(min = 3,max = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "age",nullable = false)
//    @Min(15)
//    @Max(80)
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @ManyToOne(targetEntity = Position.class)
    @JoinColumn(name = "position_id",referencedColumnName = "id")//TODO add nullable false
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @OneToMany(targetEntity = Order.class,mappedBy = "employee")
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
