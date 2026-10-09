package com.fitness.dao.impl;

import java.sql.*;
import java.util.*;
import com.fitness.dao.UserDAO;
import com.fitness.enums.Role;
import com.fitness.exception.DataAccessException;
import com.fitness.model.User;
import com.fitness.util.DBConnectionManager;

public class UserDAOImpl extends JdbcSupport implements UserDAO {
    private final DBConnectionManager db = DBConnectionManager.getInstance();
    private User map(ResultSet rs) throws SQLException {
        User user=new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"), rs.getString("password_hash"),
            rs.getDouble("weight_kg"), rs.getDouble("height_cm"), Role.valueOf(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());return user;
    }
    @Override public Optional<User> findById(Long id) {
        try (Connection c=db.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            p.setLong(1,id); try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}
        } catch(SQLException e){throw failure("Find user by id",e);}
    }
    @Override public Optional<User> findByEmail(String email) {
        try(Connection c=db.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM users WHERE email=?")){
            p.setString(1,email); try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}
        }catch(SQLException e){throw failure("Find user by email",e);}
    }
    @Override public List<User> findAll(){return search("",0,Integer.MAX_VALUE);}
    @Override public List<User> search(String query,int offset,int limit){
        List<User> users=new ArrayList<>();
        try(Connection c=db.getConnection(); PreparedStatement p=c.prepareStatement("SELECT * FROM users WHERE name LIKE ? OR email LIKE ? ORDER BY created_at DESC LIMIT ? OFFSET ?")){
            String term="%"+(query==null?"":query.trim())+"%"; p.setString(1,term);p.setString(2,term);p.setInt(3,limit);p.setInt(4,offset);
            try(ResultSet r=p.executeQuery()){while(r.next())users.add(map(r));} return users;
        }catch(SQLException e){throw failure("Search users",e);}
    }
    @Override public User save(User u){
        String sql="INSERT INTO users(name,email,password_hash,role,weight_kg,height_cm) VALUES(?,?,?,?,?,?)";
        try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            bind(p,u);p.executeUpdate();try(ResultSet keys=p.getGeneratedKeys()){if(keys.next())u.setId(keys.getLong(1));}return u;
        }catch(SQLException e){throw failure("Create user",e);}
    }
    private void bind(PreparedStatement p,User u)throws SQLException{
        p.setString(1,u.getName());p.setString(2,u.getEmail());p.setString(3,u.getPasswordHash());p.setString(4,u.getRole().name());p.setDouble(5,u.getWeightKg());p.setDouble(6,u.getHeightCm());
    }
    @Override public boolean update(User u){
        try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("UPDATE users SET name=?,email=?,password_hash=?,role=?,weight_kg=?,height_cm=? WHERE id=?")){
            bind(p,u);p.setLong(7,u.getId());return p.executeUpdate()>0;
        }catch(SQLException e){throw failure("Update user",e);}
    }
    @Override public boolean updateProfile(User u,boolean replacePassword){
        String sql=replacePassword?"UPDATE users SET name=?,email=?,password_hash=? WHERE id=?":"UPDATE users SET name=?,email=? WHERE id=?";
        try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,u.getName());p.setString(2,u.getEmail());if(replacePassword){p.setString(3,u.getPasswordHash());p.setLong(4,u.getId());}else p.setLong(3,u.getId());return p.executeUpdate()>0;
        }catch(SQLException e){throw failure("Update profile",e);}
    }
    @Override public boolean delete(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM users WHERE id=?")){p.setLong(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw failure("Delete user",e);}}
    @Override public long countUsers(){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM users");ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}catch(SQLException e){throw failure("Count users",e);}}
}