package app.domain.entities;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BillingDetail extends BaseEntity{
    private String number;
    private Users owner;

    public BillingDetail() {
    }

    @Column(name="number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    @ManyToOne(targetEntity = Users.class)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }
}
