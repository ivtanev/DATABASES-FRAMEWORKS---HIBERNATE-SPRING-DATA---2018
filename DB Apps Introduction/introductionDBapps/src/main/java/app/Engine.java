package app;

import java.sql.*;
import java.util.*;

public class Engine implements Runnable{
    private Connection connection;
    Scanner scanner = new Scanner(System.in);

    public Engine(Connection connection){
        this.connection = connection;
    }

    public void run() {
       try {
           this.getVillainsNames();
           this.getMinionNames();
           this.addMinion();
           this.changeTownName();
           this.printAllMinionName();
           this.increaseMinionAge();
           this.increaseAgeStoredProcedure();
       }catch (SQLException e){
           e.printStackTrace();
       }
    }


    /**
     * Problem 2. Get Villains' Names
     */
    private void getVillainsNames() throws SQLException {

        String query = "SELECT v.name, count(v2.minion_id) FROM villains v JOIN minions_villains v2 on v.id = v2.villain_id GROUP BY v.name HAVING count(v2.minion_id) > ? ORDER BY count(v2.minion_id) DESC";

        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setInt(1,Integer.parseInt(scanner.nextLine()));
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()){
            System.out.println(String.format("%s %d",resultSet.getString(1),resultSet.getInt(2)));
        }
    }

    /**
     * Problem 3. Get Minion Names
     */
     private void getMinionNames() throws SQLException {
         Scanner scanner = new Scanner(System.in);
         int input_id = Integer.parseInt(scanner.nextLine());
         int counter=1;
         String query = "SELECT CONCAT('Villain: ',villains.name),m.name,m.age " +
                 "FROM villains JOIN minions_villains v on villains.id = v.villain_id JOIN minions m on v.minion_id = m.id " +
                 "WHERE villains.id = ?;\n";
         PreparedStatement preparedStatement = this.connection.prepareStatement(query);
         preparedStatement.setInt(1,input_id);
         ResultSet resultSet = preparedStatement.executeQuery();

         if(resultSet.next()) {
             System.out.printf("%s%n", resultSet.getString(1));

             while (resultSet.next()) {
                 System.out.println(String.format("%d. %s %d", counter,
                         resultSet.getString(2), resultSet.getInt(3)));
                 counter++;
             }
         }else {
             if(checkVillainExist(input_id)){
                 getVillainName(input_id);
                 System.out.println("<no minions>");
             }else {
                 System.out.printf("No villain with ID %d exists in the database.", input_id);
             }
         }
     }

    private void getVillainName(int input_id) throws SQLException {
         String query = String.format("SELECT name FROM villains WHERE id = %d",input_id);
         PreparedStatement preparedStatement = this.connection.prepareStatement(query);
         ResultSet resultSet = preparedStatement.executeQuery();
         resultSet.next();
        System.out.printf("Villain: %s%n",resultSet.getString(1));
    }

    private boolean checkVillainExist(Integer input_id) throws SQLException {
         String query = "SELECT * FROM villains WHERE id = ?";
         PreparedStatement preparedStatement = this.connection.prepareStatement(query);
         preparedStatement.setInt(1,input_id);
         ResultSet resultSet = preparedStatement.executeQuery();
         if(resultSet.next()){
             return true;
         }
         return false;
    }
    /**
     * Problem 4. Add Minion
     */
    private void addMinion() throws SQLException{

        String[] minionParams = scanner.nextLine().split(" ");
        String[] villainParams = scanner.nextLine().split(" ");

        String minionName = minionParams[1];
        int minionAge = Integer.parseInt(minionParams[2]);
        String townName = minionParams[3];
        String villianName = villainParams[1];
        if(!this.checksIfEntityExists(townName,"towns")){
            this.insertTown(townName);
        }
        if(!this.checksIfEntityExists(villianName,"villains")){
            this.insertVillain(villianName);
        }
        int townId = this.getEntityId(townName,"towns");
        this.insertMinion(minionName,minionAge,townId);

        int minionId = this.getEntityId(minionName,"minions");
        int villainId = this.getEntityId(villianName,"villains");
        this.hireMinion(minionId,villainId);

        System.out.println(String.format("Successfully added %s to be minion of %s.",minionName,villianName));
    }

    private boolean checksIfEntityExists(String name, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE name = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1,name);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return true;
        }
        return false;
    }

    private void insertTown(String townName) throws SQLException {
        String query = "INSERT INTO towns(name,country) VALUES('" + townName + "',NULL)";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);

        preparedStatement.execute();
        System.out.println(String.format("Town %s was added to the database.",townName));
    }

    private void insertVillain(String villainName) throws SQLException {
        String query = String.format("INSERT INTO villains(name, evilness_factor) VALUES('%s', 'evil')",villainName);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
        System.out.println(String.format("Villain %s was added to the database.",villainName));
    }

    private int getEntityId(String name, String tableName) throws SQLException {
        String query = String.format("SELECT id FROM %s WHERE name = '%s'",tableName,name);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        return resultSet.getInt(1);
    }

    private void insertMinion(String minionName, int age,int townId) throws SQLException {
        String query = String.format("INSERT INTO minions(name,age, town_id) VALUES('%s', %d, %d)",minionName,age,townId);

        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
    }

    private void hireMinion(int minionId, int villainId) throws SQLException {
        String query = String.format("INSERT INTO minions_villains(minion_id, villain_id) VALUES(%d, %d)",minionId,villainId);

        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
    }
    /**
     * Problem 5. Change Town Names Casing
     */
    private void changeTownName() throws SQLException {
        String input_country = scanner.nextLine();
        List<String> towns = new ArrayList<String>();
        String query = String.format("SELECT name FROM towns WHERE country = '%s'",input_country);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            towns.add(resultSet.getString("name"));
        }
        updateTowns(towns,input_country);
    }

    private void updateTowns(List<String> towns,String input_country) throws SQLException {

        if(towns.size()>0){
            for (String town : towns) {
                String query = String.format("UPDATE towns SET name = '%s' WHERE country = '%s' And name = '%s'", town.toUpperCase(), input_country, town);
                PreparedStatement preparedStatement = this.connection.prepareStatement(query);
                preparedStatement.execute();
            }
            System.out.println(String.format("%d town names were affected.",towns.size()));
            System.out.printf("%s",towns);
        }else {
            System.out.println("No town names were affected.");
        }
    }

    /**
     * Problem 7. Print All Minion Names
     */
    private void printAllMinionName() throws SQLException {
        List<String> minionsInitial = new ArrayList<String>();
        List<String> minionsArranged = new ArrayList<String>();
        String query = String.format("SELECT name FROM minions");
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            minionsInitial.add(resultSet.getString(1));
        }
        while(minionsInitial.size() > 0)
        {
            minionsArranged.add(minionsInitial.get(0));
            minionsInitial.remove(0);

            if(minionsInitial.size() > 0)
            {
                minionsArranged.add(minionsInitial.get(minionsInitial.size() - 1));
                minionsInitial.remove(minionsInitial.get(minionsInitial.size() - 1));
            }
        }

        for (String s : minionsArranged) {
            System.out.println(s);
        }
    }
    /**
     * Problem 8. Increase Minions Age
     */
    private void increaseMinionAge() throws SQLException{
        String input[] = scanner.nextLine().split(" ");
        for (int i = 0; i < input.length; i++) {
            String query = String.format("SELECT name,age FROM minions WHERE id = %d",Integer.parseInt(input[i]));
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                query = String.format("UPDATE minions SET name = '%s',age = %d WHERE id = %d;",resultSet.getString(1).
                        substring(0,1).toUpperCase() + resultSet.getString(1).substring(1), resultSet.getInt(2) + 1,Integer.parseInt(input[i]));
                PreparedStatement preparedStatement1 = this.connection.prepareStatement(query);
                preparedStatement1.execute();
            }

            query = "SELECT name,age FROM minions";
            PreparedStatement preparedStatement2 = this.connection.prepareStatement(query);
            ResultSet resultSet1 = preparedStatement2.executeQuery();
            while (resultSet1.next()) {
                System.out.println(" " + resultSet1.getString(1) + " " + resultSet1.getInt(2));
            }
        }
    }
    /**
     * Problem 9. Increase Age Stored Procedure
     */
    private void increaseAgeStoredProcedure() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int id= scanner.nextInt();
        CallableStatement callableStatement = this.connection.prepareCall("call usp_get_older(?)");
        callableStatement.setInt(1,(id));
        ResultSet resultSet = callableStatement.executeQuery();
        while (resultSet.next()){
            String query = String.format("UPDATE minions SET age = %d WHERE id = %d;",resultSet.getInt(2)+1,id);
            PreparedStatement preparedStatement1 = this.connection.prepareStatement(query);
            preparedStatement1.execute();
            System.out.printf("%s %d",resultSet.getString(1),resultSet.getInt(2)+1);
        }

    }

}
