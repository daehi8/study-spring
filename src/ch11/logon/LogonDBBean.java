 package ch11.logon;
 
 import java.sql.*;
 import javax.sql.*;

import org.springframework.stereotype.Service;

import javax.naming.*;

// 다른 클래스에서 서비스 호출이 가능하다.
// 표준화(인터페이스상속)를 하지 않았을 경우 서비스 역할 불가능 
 @Service("LogonDBBean")
 public class LogonDBBean{
    
    private Connection getConnection() throws Exception {
      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      DataSource ds = (DataSource)envCtx.lookup("jdbc/orcl");
      return ds.getConnection();
    }
 
    public void insertMember(LogonDataBean member) 
    throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(
            	"insert into MEMBER values (?,?,?,?,?,?,?,sysdate)");
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPasswd());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getJumin1());
            pstmt.setString(5, member.getJumin2());
            pstmt.setString(6, member.getEmail());
            pstmt.setString(7, member.getBlog());
			//pstmt.setTimestamp(8, member.getReg_date());
            pstmt.executeUpdate();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
    }
 
	public int userCheck(String id, String passwd) 
	throws Exception {
		Connection conn = null;
        PreparedStatement pstmt = null;
		ResultSet rs= null;
        String dbpasswd="";
		int x=-1;
        
		try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(
            	"select passwd from MEMBER where id = ?");
            pstmt.setString(1, id);
            rs= pstmt.executeQuery();

			if(rs.next()){
				dbpasswd= rs.getString("passwd"); 
				if(dbpasswd.equals(passwd))
					x= 1; //���� ����
				else
					x= 0; //��й�ȣ Ʋ��
			}else
				x= -1;//�ش� ���̵� ����
			
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
			if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
		return x;
	}

	public int confirmId(String id) 
	throws Exception {
		Connection conn = null;
        PreparedStatement pstmt = null;
		ResultSet rs= null;
        String dbpasswd="";
		int x=-1;
        
		try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(
            	"select id from MEMBER where id = ?");
            pstmt.setString(1, id);
            rs= pstmt.executeQuery();

			if(rs.next())
				x= 1; //�ش� ���̵� ����
			else
				x= -1;//�ش� ���̵� ����		
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
			if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
		return x;
	}


    public LogonDataBean getMember(String id)
    throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LogonDataBean member=null;
        try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(
            	"select * from MEMBER where id = ?");
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                member = new LogonDataBean();
                member.setId(rs.getString("id"));
                member.setPasswd(rs.getString("passwd"));
				member.setName(rs.getString("name"));
                member.setJumin1(rs.getString("jumin1"));
                member.setJumin2(rs.getString("jumin2"));
				member.setEmail(rs.getString("email"));
				member.setBlog(rs.getString("blog"));
                member.setReg_date(rs.getTimestamp("reg_date"));     
			}
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
		return member;
    }
    
    public void updateMember(LogonDataBean dto)
    throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            
            pstmt = conn.prepareStatement(
              "update MEMBER set passwd=?,name=?,email=?,blog=? "+
              "where id=?");
            pstmt.setString(1, dto.getPasswd());
            pstmt.setString(2, dto.getName());
            pstmt.setString(3, dto.getEmail());
            pstmt.setString(4, dto.getBlog());
            pstmt.setString(5, dto.getId());
            
            pstmt.executeUpdate();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
    }
    
    public int deleteMember(String id, String passwd)
    throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs= null;
        String dbpasswd="";
        int x=-1;
        try {
			conn = getConnection();

            pstmt = conn.prepareStatement(
            	"select passwd from MEMBER where id = ?");
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            
			if(rs.next()){
				dbpasswd= rs.getString("passwd"); 
				if(dbpasswd.equals(passwd)){
					pstmt = conn.prepareStatement(
            	      "delete from MEMBER where id=?");
                    pstmt.setString(1, id);
                    pstmt.executeUpdate();
					x= 1; //ȸ��Ż�� ����
				}else
					x= 0; //��й�ȣ Ʋ��
			}
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch(SQLException ex) {}
            if (pstmt != null) try { pstmt.close(); } catch(SQLException ex) {}
            if (conn != null) try { conn.close(); } catch(SQLException ex) {}
        }
		return x;
    }

 }