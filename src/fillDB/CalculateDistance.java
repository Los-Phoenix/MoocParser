package fillDB;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


//给每一个句子一个标号
//给每一个句子建立实体列表
public class CalculateDistance {

	public static void caculateDist(Connection con) throws SQLException{
		
		

		String sql="Select m1e, f_wiki_id, m2e, e2.wiki_id s_wiki_id from"
				+"    (Select m1e, e1.wiki_id f_wiki_id, m2e  "
				+"    from entity e1, "
				+"        (SELECT distinct m1.entity m1e, m2.entity m2e "
				+ "       FROM mooclink.mention m1, mooclink.mention m2 "
				+ "       where m1.vid = m2.vid ) S1"
				+"    where e1.id = S1.m1e) S2, entity e2"
				+"  where e2.id = m2e;";
		PreparedStatement st = con.prepareStatement(sql);
		//System.out.println("originalTermVector::"+originalTermVector);
		ResultSet rs=st.executeQuery(sql), countrs;

		int cnt = 0, fe ,feWiki, se, seWiki, interCount, unionCount;
		double dist;

		while(rs.next()){

			fe = rs.getInt(1);
			feWiki = rs.getInt(2);
			se = rs.getInt(3);
			seWiki = rs.getInt(4);
			
			//节约时间
			sql = "SELECT * FROM distance WHERE f_wiki_id = ? and s_wiki_id = ? ;";
			st = con.prepareStatement(sql);
			
			st.setInt(1, feWiki);
			st.setInt(2, seWiki);
			System.out.println(st);
			countrs = st.executeQuery();
			countrs.last();
			
			if(countrs.isLast()){
				System.out.println("Duplicated!!");
				continue;
			}
			
			//求交
			sql = "SELECT DISTINCT COUNT(pl1.inlinks) "
					+ "FROM page_inlinks pl1, page_inlinks pl2"
					+ " WHERE pl1.inlinks = pl2.inlinks and pl1.id = ? and pl2.id = ? ;";
			st = con.prepareStatement(sql);
			
			st.setInt(1, feWiki);
			st.setInt(2, seWiki);
			System.out.println(st);
			countrs = st.executeQuery();
			countrs.next();
			interCount = countrs.getInt(1);

			sql = "SELECT DISTINCT COUNT(inlinks) "
					+ " FROM page_inlinks"
					+ " WHERE id = ? or id = ?";
			st = con.prepareStatement(sql);
			st.setInt(1, feWiki);
			st.setInt(2, seWiki);
			System.out.println(st);
			countrs = st.executeQuery();
			countrs.next();
			unionCount = countrs.getInt(1);

			if(unionCount != 0){
				dist = (double) interCount / (double) unionCount;
			}else{
				dist = 0;
			}

			try{
				sql = "INSERT INTO distance VALUES(?,?,?,?,?)";
				st = con.prepareStatement(sql);
				st.setInt(1, feWiki);
				st.setInt(2, seWiki);
				st.setInt(3, unionCount);
				st.setInt(4, interCount);
				st.setDouble(5, dist);
				System.out.println(st);
				st.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println(++cnt);
		}

	}
	public static void main(String[] args) throws SQLException, Exception {
		// TODO Auto-generated method stub
		String url = "jdbc:mysql://localhost:3306/mooclink?"
				+ "user=root&password=1111&useUnicode=true&characterEncoding=UTF8";
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url);

		caculateDist(con);
	}

}
