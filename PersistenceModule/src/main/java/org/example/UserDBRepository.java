package org.example;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserDBRepository implements UserRepository {
    private JdbcUtils dbUtils;

//    private static final Logger logger= LogManager.getLogger(UserDBRepository.class);

    public UserDBRepository(Properties props){
//        logger.info("Initializing UserDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
//        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from user")) {
            try(ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
//                    logger.traceExit(result.getInt("SIZE"));
                    return result.getInt("SIZE");
                }
            }
        }catch(SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return 0;
    }

    @Override
    public void save(User entity) {
//        logger.traceEntry("saving user {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into user (nume_utilizator, parola) values (?,?)")){
            preStmt.setString(1,entity.getNumeUtilizator());
            preStmt.setString(2,entity.getParola());


            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();

    }

    @Override
    public void delete(Long id) {
//        logger.traceEntry("deleting user with {}",id);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from user where id=?")){
            preStmt.setLong(1,id);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();
    }

    @Override
    public void update(User entity) {
        //To do
    }

    @Override
    public User findOne(Long id) {
//        logger.traceEntry("finding user with id {} ",id);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from user where id=?")){
            preStmt.setLong(1,id);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Long idf = result.getLong("id");
                    String numeUser = result.getString("nume_utilizator");
                    String password = result.getString("parola");
                    User entity = new User(numeUser, password);
                    entity.setId(idf);
//                    logger.traceExit(entity);
                    return entity;
                }
            }
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit("No user found with id {}", id);

        return null;
    }

    @Override
    public Iterable<User> findAll() {

        Connection con=dbUtils.getConnection();
        List<User> entities=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from user")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    Long idf = result.getLong("id");
                    String numeUser = result.getString("nume_utilizator");
                    String password = result.getString("parola");
                    User entity = new User(numeUser, password);
                    entity.setId(idf);
                    entities.add(entity);
                }
            }
        } catch (SQLException e) {
//            logger.error(e);
            System.out.println("Error DB "+e);
        }
//        logger.traceExit(entities);
        return entities;
    }



}
