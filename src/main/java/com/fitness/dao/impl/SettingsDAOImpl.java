package com.fitness.dao.impl;

import java.sql.*;
import java.util.*;
import com.fitness.dao.SettingsDAO;
import com.fitness.util.DBConnectionManager;

public class SettingsDAOImpl extends JdbcSupport implements SettingsDAO {
    private final DBConnectionManager db=DBConnectionManager.getInstance();
    @Override public Map<String,String> findAll(){Map<String,String> values=new TreeMap<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT setting_key,setting_value FROM system_settings ORDER BY setting_key");ResultSet r=p.executeQuery()){while(r.next())values.put(r.getString(1),r.getString(2));return values;}catch(SQLException e){throw failure("Read settings",e);}}
    @Override public void put(String key,String value){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO system_settings(setting_key,setting_value) VALUES(?,?) ON DUPLICATE KEY UPDATE setting_value=VALUES(setting_value)")){p.setString(1,key);p.setString(2,value);p.executeUpdate();}catch(SQLException e){throw failure("Save setting",e);}}
}