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

public class ReservationDBRepository implements ReservationRepository {
    private JdbcUtils dbUtils;
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

//    private static final Logger logger= LogManager.getLogger(ReservationDBRepository.class);

    public ReservationDBRepository(Properties props){
//        logger.info("Initializing ReservationDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public int size() {
//        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as [SIZE] from reservation")) {
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
    public void save(Reservation entity) {
//        logger.traceEntry("saving reservation {} ",entity);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("insert into reservation (id_trip, nume_client, numar_loc, data) values (?,?,?,?)")){
            preStmt.setLong(1,entity.getIdCursa());
            preStmt.setString(2,entity.getNumeClient());
            preStmt.setInt(3,entity.getNumarLoc());
            preStmt.setString(4,entity.getDataOraRezervare().format(formatter));

            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();

    }

    @Override
    public void delete(Long id) {
//        logger.traceEntry("deleting reservation with {}",id);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from reservation where id=?")){
            preStmt.setLong(1,id);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit();
    }

    @Override
    public void update(Reservation entity) {
        //To do
    }

    @Override
    public Reservation findOne(Long id) {
//        logger.traceEntry("finding reservation with id {} ",id);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from reservation where id=?")){
            preStmt.setLong(1,id);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Long idf = result.getLong("id");
                    Long idCursa = result.getLong("id_trip");
                    String numeClient = result.getString("nume_client");
                    int numarLoc = result.getInt("numar_loc");
                    LocalDateTime data = LocalDateTime.parse(result.getString("data"), formatter);
                    Reservation entity = new Reservation(idCursa, numeClient, numarLoc, data);
                    entity.setId(idf);
//                    logger.traceExit(entity);
                    return entity;
                }
            }
        }catch (SQLException ex){
//            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
//        logger.traceExit("No reservation found with id {}", id);

        return null;
    }

    @Override
    public Iterable<Reservation> findAll() {

        Connection con=dbUtils.getConnection();
        List<Reservation> entities=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from reservation")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    Long idf = result.getLong("id");
                    Long idCursa = result.getLong("id_trip");
                    String numeClient = result.getString("nume_client");
                    int numarLoc = result.getInt("numar_loc");
                    LocalDateTime data = LocalDateTime.parse(result.getString("data"), formatter);
                    Reservation entity = new Reservation(idCursa, numeClient, numarLoc, data);
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