import entities.Address;
import entities.Employee;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Engine implements Runnable {
    private final EntityManager entityManager;

    Engine(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void run() {
        //2. Remove Objects
     //   this.removeObjects();

        //3. Contains Employee
     //   this.containEmpoyee();

        //4. Employees with Salary Over 50 000
     //   this.salaryOver50k();

        //5.Employees from Department
     //     this.employeeFromDepartment();
        //6. Adding a New Address and Updating Employee
     //  this.addingNewAddressAndUpdatingEmployee();
        //7. Addresses with Employee Count
        this.addressesWithEmployeeCount();


    }

    //2. Remove Objects
    private void removeObjects() {

        List<Town> towns=  this.entityManager
                .createQuery("FROM Town",Town.class)
                .getResultList();
        System.out.println();

        for (Town town : towns) {
            if(town.getName().length()>5){
                this.entityManager.detach(town);
            }
        }

        this.entityManager.getTransaction().begin();

        towns.stream().filter(this.entityManager::contains)
                .forEach(town -> {
                    town.setName(town.getName().toLowerCase());
                    this.entityManager.persist(town);
                });

        this.entityManager.getTransaction().commit();
        this.entityManager.close();
    }

    /*
        3. Contains Employee
     */
    private void containEmpoyee(){
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        this.entityManager.getTransaction().begin();

        try {
            Employee employee = this.entityManager
                    .createQuery("FROM Employee WHERE concat(firstName,' ',lastName)= :name", Employee.class)
                    .setParameter("name", name)
                    .getSingleResult();
            System.out.println("Yes");
        }catch (NoResultException nre){
            System.out.printf("No");
        }
    }

    //4.Employees with Salary Over 50 000
    private void salaryOver50k(){
        List<Employee> firstNames = this.entityManager
                .createQuery("FROM Employee WHERE salary>50000")
                .getResultList();
        for (Employee fname : firstNames) {
            System.out.println(fname.getFirstName());
        }
    }

    //5.Employees from Department
    private void employeeFromDepartment(){
        List<Employee> employees = this.entityManager

                .createQuery("SELECT e FROM Employee AS e " +
                        "JOIN e.department WHERE e.department.name = 'Research and Development' ORDER BY e.salary, e.id",Employee.class)
                .getResultList();

        for (Employee employee : employees) {
            System.out.printf("%s %s %s - %.2f%n",employee.getFirstName(),employee.getLastName(),
                    employee.getDepartment().getName(),employee.getSalary());
        }

    }

    //6. Adding a New Address and Updating Employee
    private void addingNewAddressAndUpdatingEmployee(){
        Scanner scanner = new Scanner(System.in);
        String lastName = scanner.nextLine();

        Address address = new Address();
        address.setText("Vitoshka 15");

        this.entityManager.getTransaction().begin();
        Town town = this.entityManager.createQuery("FROM Town Where name = 'Sofia'",Town.class)
                .getSingleResult();
        address.setTown(town);
        this.entityManager.persist(address);

        Employee employee = this.entityManager.createQuery("FROM Employee  WHERE last_name = :name",Employee.class)
                .setParameter("name",lastName)
                .getSingleResult();

        this.entityManager.detach(employee.getAddress());
        employee.setAddress(address);
        this.entityManager.merge(employee);
        this.entityManager.getTransaction().commit();

    }

    //7. Addresses with Employee Count
    private void addressesWithEmployeeCount(){
        StringBuilder addresses = new StringBuilder();

        this.entityManager
                .createQuery("SELECT a FROM Address AS a ORDER BY a.employees.size DESC ,a.town.id ASC",Address.class)
                .setMaxResults(10)
                .getResultList()
                .forEach(address -> addresses.append(String.format("%s, %s - %d employees%n",
                        address.getText(), address.getTown().getName(),address.getEmployees().size())));
        this.entityManager.close();
        System.out.println(addresses.toString().trim());
    }
}
