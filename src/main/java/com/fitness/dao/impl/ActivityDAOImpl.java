package com.fitness.dao.impl;

import java.sql.*;
import java.util.*;
import com.fitness.dao.ActivityDAO;
import com.fitness.model.Activity;
import com.fitness.util.DBConnectionManager;

public class ActivityDAOImpl extends JdbcSupport implements ActivityDAO {
    private final DBConnectionManager db=DBConnectionManager.getInstance();
    @Override public void save(Activity a){String sql="INSERT INTO activity_log(user_id,action,details) VALUES(?,?,?)";try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(a.getUserId()==null)p.setNull(1,Types.BIGINT);else p.setLong(1,a.getUserId());p.setString(2,a.getAction());p.setString(3,a.getDetails());p.executeUpdate();}catch(SQLException e){throw failure("Log activity",e);}}
    @Override public List<Activity> latest(int limit){String sql="SELECT a.*,u.name user_name FROM activity_log a LEFT JOIN users u ON u.id=a.user_id ORDER BY a.created_at DESC LIMIT ?";List<Activity> list=new ArrayList<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,Math.max(1,Math.min(limit,200)));try(ResultSet r=p.executeQuery()){while(r.next()){Activity a=new Activity();a.setId(r.getLong("id"));long uid=r.getLong("user_id");a.setUserId(r.wasNull()?null:uid);a.setAction(r.getString("action"));a.setDetails(r.getString("details"));a.setUserName(r.getString("user_name"));a.setCreatedAt(r.getTimestamp("created_at").toLocalDateTime());list.add(a);}}return list;}catch(SQLException e){throw failure("Read activity",e);}}
    @Override public void deleteOlderThanDays(int days){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM activity_log WHERE created_at < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL ? DAY)")){p.setInt(1,Math.max(1,days));p.executeUpdate();}catch(SQLException e){throw failure("Clean old activity",e);}}
}