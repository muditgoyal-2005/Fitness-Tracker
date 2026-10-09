package com.fitness.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import com.fitness.dao.WorkoutDAO;
import com.fitness.enums.Intensity;
import com.fitness.enums.WorkoutType;
import com.fitness.model.Workout;
import com.fitness.util.DBConnectionManager;

public class WorkoutDAOImpl extends JdbcSupport implements WorkoutDAO {
    private final DBConnectionManager db=DBConnectionManager.getInstance();
    private Workout map(ResultSet r)throws SQLException{return new Workout.Builder().id(r.getLong("id")).userId(r.getLong("user_id"))
            .type(WorkoutType.valueOf(r.getString("type"))).durationMinutes(r.getInt("duration_min"))
            .intensity(Intensity.valueOf(r.getString("intensity"))).calories(r.getDouble("calories"))
            .workoutDate(r.getDate("workout_date").toLocalDate()).notes(r.getString("notes")).build();}
    @Override public Optional<Workout> findById(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM workouts WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw failure("Find workout",e);}}
    @Override public List<Workout> findAll(){return query("SELECT * FROM workouts ORDER BY workout_date DESC",0,0,0);}
    @Override public List<Workout> findByUser(long userId,int offset,int limit){return query("SELECT * FROM workouts WHERE user_id=? ORDER BY workout_date DESC,id DESC LIMIT ? OFFSET ?",userId,limit,offset);}
    private List<Workout> query(String sql,long userId,int limit,int offset){List<Workout> list=new ArrayList<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){
        if(sql.contains("WHERE user_id")){p.setLong(1,userId);p.setInt(2,limit);p.setInt(3,offset);}try(ResultSet r=p.executeQuery()){while(r.next())list.add(map(r));}return list;
    }catch(SQLException e){throw failure("List workouts",e);}}
    @Override public List<Workout> findByDateRange(long userId,LocalDate from,LocalDate to){List<Workout> list=new ArrayList<>();String sql="SELECT * FROM workouts WHERE user_id=? AND workout_date BETWEEN ? AND ? ORDER BY workout_date";
        try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,userId);p.setDate(2,java.sql.Date.valueOf(from));p.setDate(3,java.sql.Date.valueOf(to));try(ResultSet r=p.executeQuery()){while(r.next())list.add(map(r));}return list;}catch(SQLException e){throw failure("List workouts by date",e);}}
    @Override public Workout save(Workout w){String sql="INSERT INTO workouts(user_id,type,duration_min,intensity,calories,workout_date,notes) VALUES(?,?,?,?,?,?,?)";try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
        bind(p,w);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(k.next())w.setId(k.getLong(1));}return w;}catch(SQLException e){throw failure("Create workout",e);}}
    private void bind(PreparedStatement p,Workout w)throws SQLException{p.setLong(1,w.getUserId());p.setString(2,w.getType().name());p.setInt(3,w.getDurationMinutes());p.setString(4,w.getIntensity().name());p.setDouble(5,w.getCalories());p.setDate(6,java.sql.Date.valueOf(w.getWorkoutDate()));p.setString(7,w.getNotes());}
    @Override public boolean update(Workout w){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("UPDATE workouts SET type=?,duration_min=?,intensity=?,calories=?,workout_date=?,notes=? WHERE id=? AND user_id=?")){
        p.setString(1,w.getType().name());p.setInt(2,w.getDurationMinutes());p.setString(3,w.getIntensity().name());p.setDouble(4,w.getCalories());p.setDate(5,java.sql.Date.valueOf(w.getWorkoutDate()));p.setString(6,w.getNotes());p.setLong(7,w.getId());p.setLong(8,w.getUserId());return p.executeUpdate()>0;}catch(SQLException e){throw failure("Update workout",e);}}
    @Override public boolean delete(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM workouts WHERE id=?")){p.setLong(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw failure("Delete workout",e);}}
    @Override public Map<String,Double> calorieTotalsByType(long userId){Map<String,Double> totals=new TreeMap<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT type,SUM(calories) total FROM workouts WHERE user_id=? GROUP BY type ORDER BY type")){p.setLong(1,userId);try(ResultSet r=p.executeQuery()){while(r.next())totals.put(r.getString("type"),r.getDouble("total"));}return totals;}catch(SQLException e){throw failure("Aggregate workout types",e);}}
    @Override public double sumCalories(long userId,LocalDate from,LocalDate to){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT COALESCE(SUM(calories),0) FROM workouts WHERE user_id=? AND workout_date BETWEEN ? AND ?")){p.setLong(1,userId);p.setDate(2,java.sql.Date.valueOf(from));p.setDate(3,java.sql.Date.valueOf(to));try(ResultSet r=p.executeQuery()){r.next();return r.getDouble(1);}}catch(SQLException e){throw failure("Sum calories",e);}}
    @Override public long countAll(){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM workouts");ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}catch(SQLException e){throw failure("Count workouts",e);}}
}