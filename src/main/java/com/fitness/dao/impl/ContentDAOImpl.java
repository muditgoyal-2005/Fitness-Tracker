package com.fitness.dao.impl;

import java.sql.*;
import java.util.*;
import com.fitness.dao.ContentDAO;
import com.fitness.model.FitnessContent;
import com.fitness.util.DBConnectionManager;

public class ContentDAOImpl extends JdbcSupport implements ContentDAO {
    private final DBConnectionManager db=DBConnectionManager.getInstance();
    private FitnessContent map(ResultSet r)throws SQLException{FitnessContent v=new FitnessContent();v.setId(r.getLong("id"));v.setUserId(r.getLong("user_id"));v.setTitle(r.getString("title"));v.setBody(r.getString("body"));v.setStatus(r.getString("status"));v.setSubmittedAt(r.getTimestamp("submitted_at").toLocalDateTime());v.setAuthorName(r.getString("author_name"));long reviewer=r.getLong("reviewed_by");v.setReviewedBy(r.wasNull()?null:reviewer);return v;}
    @Override public Optional<FitnessContent> findById(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT f.*,u.name author_name FROM fitness_content f JOIN users u ON u.id=f.user_id WHERE f.id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw failure("Find content",e);}}
    @Override public List<FitnessContent> findAll(){return query("SELECT f.*,u.name author_name FROM fitness_content f JOIN users u ON u.id=f.user_id ORDER BY submitted_at DESC",null,null);}
    @Override public List<FitnessContent> findByStatus(String status){return query("SELECT f.*,u.name author_name FROM fitness_content f JOIN users u ON u.id=f.user_id WHERE f.status=? ORDER BY submitted_at DESC",status,null);}
    @Override public List<FitnessContent> findByUser(long userId){return query("SELECT f.*,u.name author_name FROM fitness_content f JOIN users u ON u.id=f.user_id WHERE f.user_id=? ORDER BY submitted_at DESC",null,userId);}
    private List<FitnessContent> query(String sql,String status,Long id){List<FitnessContent> list=new ArrayList<>();try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(status!=null)p.setString(1,status);else if(id!=null)p.setLong(1,id);try(ResultSet r=p.executeQuery()){while(r.next())list.add(map(r));}return list;}catch(SQLException e){throw failure("List content",e);}}
    @Override public FitnessContent save(FitnessContent v){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO fitness_content(user_id,title,body,status) VALUES(?,?,?,'PENDING')",Statement.RETURN_GENERATED_KEYS)){p.setLong(1,v.getUserId());p.setString(2,v.getTitle());p.setString(3,v.getBody());p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(r.next())v.setId(r.getLong(1));}return v;}catch(SQLException e){throw failure("Submit content",e);}}
    @Override public boolean update(FitnessContent v){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("UPDATE fitness_content SET title=?,body=? WHERE id=? AND status='PENDING'")){p.setString(1,v.getTitle());p.setString(2,v.getBody());p.setLong(3,v.getId());return p.executeUpdate()>0;}catch(SQLException e){throw failure("Update content",e);}}
    @Override public boolean delete(Long id){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM fitness_content WHERE id=?")){p.setLong(1,id);return p.executeUpdate()>0;}catch(SQLException e){throw failure("Delete content",e);}}
    @Override public boolean review(long id,long adminId,String status){if(!Set.of("APPROVED","REJECTED").contains(status))throw new IllegalArgumentException("Invalid review status");try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("UPDATE fitness_content SET status=?,reviewed_by=? WHERE id=? AND status='PENDING'")){p.setString(1,status);p.setLong(2,adminId);p.setLong(3,id);return p.executeUpdate()>0;}catch(SQLException e){throw failure("Review content",e);}}
    @Override public long countPending(){try(Connection c=db.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM fitness_content WHERE status='PENDING'");ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}catch(SQLException e){throw failure("Count pending content",e);}}
}