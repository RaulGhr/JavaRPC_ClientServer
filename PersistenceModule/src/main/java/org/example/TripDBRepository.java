package org.example;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TripDBRepository implements TripRepository {
    private JdbcUtils dbUtils;
    DateTimeFormatter formatter = DataFormatter.getFormatter();


//    private static final Logger logger= LogManager.getLogger(TripDBRepository.class);

    public TripDBRepository(Properties props){
//        logger.info("Initializing TripDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
//        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from trip")) {
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
    public void save(Trip entity) {
//        logger.traceEntry("saving trip {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into trip (destinatie, data_ora_plecare, locuri_disponibile) values (?,?,?)")){
            preStmt.setString(1,entity.getDestinatie());
            preStmt.setString(2,entity.getDataOraPlecare().format(formatter));
            preStmt.setInt(3,entity.getLocuriDisponibile());


            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();

    }

    @Override
    public void delete(Long id) {
//        logger.traceEntry("deleting trip with {}",id);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from trip where id=?")){
            preStmt.setLong(1,id);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();
    }

    @Override
    public void update(Trip entity) {
//        logger.traceEntry("updating trip {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("update trip set destinatie=?, data_ora_plecare=?, locuri_disponibile=? where id=?")){
            preStmt.setString(1,entity.getDestinatie());
            preStmt.setString(2,entity.getDataOraPlecare().format(formatter));
            preStmt.setInt(3,entity.getLocuriDisponibile());
            preStmt.setLong(4,entity.getId());

            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();
    }

    @Override
    public Trip findOne(Long id) {
//        logger.traceEntry("finding trip with id {} ",id);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from trip where id=?")){
            preStmt.setLong(1,id);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Long idf = result.getLong("id");
                    String destinatie = result.getString("destinatie");
                    String dataOraPlecare = result.getString("data_ora_plecare");
                    LocalDateTime parsedDateTime = LocalDateTime.parse(dataOraPlecare, formatter);
                    int locuriDisponibile = result.getInt("locuri_disponibile");
                    Trip entity = new Trip(destinatie, parsedDateTime, locuriDisponibile);
                    entity.setId(idf);
//                    logger.traceExit(entity);
                    return entity;
                }
            }
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit("No trip found with id {}", id);

        return null;
    }

    @Override
    public Iterable<Trip> findAll() {

        Connection con=dbUtils.getConnection();
        List<Trip> entities=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from trip")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    Long idf = result.getLong("id");
                    String destinatie = result.getString("destinatie");
                    String dataOraPlecare = result.getString("data_ora_plecare");
                    LocalDateTime parsedDateTime = LocalDateTime.parse(dataOraPlecare, formatter);
                    int locuriDisponibile = result.getInt("locuri_disponibile");
                    Trip entity = new Trip(destinatie, parsedDateTime, locuriDisponibile);
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
