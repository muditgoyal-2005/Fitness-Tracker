package com.fitness.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.fitness.dao.GoalDAO;
import com.fitness.model.Goal;
import com.fitness.util.DBConnectionManager;

public class GoalDAOImpl extends JdbcSupport implements GoalDAO {
    private final DBConnectionManager db=DBConnectionManager.getInstance();
    private Goal map(ResultSet r)throws SQLException{Goal g=new Goal();g.setId(r.getLong("id"));g.setUserId(r.getLong("user_id"));g.setGoalType(r.getString("goal_type"));g.setTargetValue(r.getDouble("target_value"));g.setStartDate(r.getDate("start_date").toLocalDate());g.setEndDate(r.getDate("end_date").toLocalDate());g.setStatus(r.getString("status"));return g;}
    @Override public Optional<Goal> findById(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM goals WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw failure("Find goal",e);}}
    @Override public List<Goal> findAll(){return query("SELECT * FROM goals ORDER BY end_date",null);}
    @Override public List<Goal> findByUser(long id){return query("SELECT * FROM goals WHERE user_id=? ORDER BY end_date",id);}
    private List<Goal> query(String sql,Long id){List<Goal> list=new ArrayList<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(id!=null)p.setLong(1,id);try(ResultSet r=p.executeQuery()){while(r.next())list.add(map(r));}return list;}catch(SQLException e){throw failure("List goals",e);}}
    @Override public Goal save(Goal g){String sql="INSERT INTO goals(user_id,goal_type,target_value,start_date,end_date,status) VALUES(?,?,?,?,?,?)";try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bind(p,g);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(r.next())g.setId(r.getLong(1));}return g;}catch(SQLException e){throw failure("Create goal",e);}}
    private void bind(PreparedStatement p,Goal g)throws SQLException{p.setLong(1,g.getUserId());p.setString(2,g.getGoalType());p.setDouble(3,g.getTargetValue());p.setDate(4,java.sql.Date.valueOf(g.getStartDate()));p.setDate(5,java.sql.Date.valueOf(g.getEndDate()));p.setString(6,g.getStatus());}
    @Override public boolean update(Goal g){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("UPDATE goals SET goal_type=?,target_value=?,start_date=?,end_date=?,status=? WHERE id=? AND user_id=?")){p.setString(1,g.getGoalType());p.setDouble(2,g.getTargetValue());p.setDate(3,java.sql.Date.valueOf(g.getStartDate()));p.setDate(4,java.sql.Date.valueOf(g.getEndDate()));p.setString(5,g.getStatus());p.setLong(6,g.getId());p.setLong(7,g.getUserId());return p.executeUpdate()>0;}catch(SQLException e){throw failure("Update goal",e);}}
    @Override public boolean delete(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM goals WHERE id=?")){p.setLong(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw failure("Delete goal",e);}}
}