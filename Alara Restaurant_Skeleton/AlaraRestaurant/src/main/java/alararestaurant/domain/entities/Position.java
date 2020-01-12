package alararestaurant.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.List;

@Entity(name = "positions")
public class Position extends BaseEntity {
    private String name;
    private List<Employee> employees;

    public Position() {
    }

    @Column(name = "name",nullable = false,unique = true)//TODO check for varchar 255 for all names
    //@Size(min = 3,max = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(targetEntity = Employee.class,mappedBy = "position")
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
