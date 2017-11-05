package fillDB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.PreparedStatement;

public class ReadContent {
	//对于给定的目录路径（xml）
	//将其中的每一行读出，放进数据库

	public static String contentPath = "F:/DJ/videos.xml";//这里是目录的位置

	public static void main(String[] args) throws ClassNotFoundException, SQLException, ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		//连接数据库
		String sqlbone = "";
		String url = "jdbc:mysql://localhost:3306/mooclink?"
				+ "user=root&password=1111&useUnicode=true&characterEncoding=UTF8";
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(url);
		
		//打开文件
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		File inFile = new File(contentPath);
		Document content = builder.parse(inFile);
		Element rootElement = content.getDocumentElement();
		NodeList nodes = rootElement.getChildNodes(); 
		//找到上一层的根
		//System.out.println(nodes.getLength());
		
		if(nodes != null){ 
			int length = nodes.getLength();
			for (int i = 0 ; i < length; i++) 
			{ 
				if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {//循环读每一行
					Element element = (Element)nodes.item(i); 
					String spbh = element.getElementsByTagName("视频编号").item(0).getTextContent();
					String zm = element.getElementsByTagName("章名").item(0).getTextContent();
					String jm = element.getElementsByTagName("节名").item(0).getTextContent();
					String spm = element.getElementsByTagName("视频名").item(0).getTextContent();
					String zmlj = element.getElementsByTagName("字幕路径").item(0).getTextContent();

					//写这一行
					System.out.println(i + " | " + spbh + " | " + zm + " | " + jm + " | " + spm + " | " + zmlj);
					//下面实际上有个写数据库的过程。可是数据库不在
					java.sql.PreparedStatement psql;
					sqlbone = "insert into video(idvideo, chapter, lesson, title, path) values(?,?,?,?,?)";
					psql = conn.prepareStatement(sqlbone);
					psql.setString(1, spbh);
					psql.setString(2, zm);
					psql.setString(3, jm);
					psql.setString(4, spm);
					psql.setString(5, zmlj);
					//System.out.println(sql);
					
					psql.executeUpdate();
				}
			} 
		}/*
		else{
			System.err.println("目录结构错误");
		}*/ 

		
		

		

	}

}
