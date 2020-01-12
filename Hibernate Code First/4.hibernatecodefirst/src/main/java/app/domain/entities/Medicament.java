package app.domain.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "prescribed_medicaments")
public class Medicament {
    private Integer id;
    private String name;

    private Set<Patient> patients;

    public Medicament() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany
    @JoinTable(name = "medicament_patients",
    joinColumns = @JoinColumn(name = "medicament_id",referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "patient_id",referencedColumnName = "id"))
    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }
}
