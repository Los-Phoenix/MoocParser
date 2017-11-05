package fillDB;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class SubtitleParser {
	public static String subPath = "G:\\2013video\\10冯诺依曼计算机\\6-7 101报告.srt";
	public static String vid = "03计算机发展与摩尔定律";
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		
		String sqlbone = "";
		String url = "jdbc:mysql://localhost:3306/mooclink?"
				+ "user=root&password=1111&useUnicode=true&characterEncoding=UTF8";
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(url);
		java.sql.PreparedStatement psql;
		
		//当读一行之后，可能是
		//编号
		//时间
		//台词
		
		//打开字幕文件
		InputStreamReader reader = new InputStreamReader(new FileInputStream(subPath),"UTF-16LE");
		BufferedReader in = new BufferedReader(reader);
		
		String regex="\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";//这是时间的正则表达式
		
		String tempLine;
		int sentNum = 1;
		
		String Start = "", End = "";
		
		while((tempLine = in.readLine()) != null){
			if(tempLine.length() == 0){
				continue;
			}
			
			if(Pattern.matches(regex, tempLine)){
				Start = tempLine.split("-->")[0];
				End = tempLine.split("-->")[1];
				continue;
			}
			
			String expectString = "" + sentNum;
			if(tempLine.endsWith(expectString)){
				sentNum++;
				continue;
			}
			
			//最后只剩句子了
			System.out.println(sentNum + " | " + Start + " | " + End + " | " + tempLine);
			
			sqlbone = "insert into sentence(content, vid, start, end) values(?,?,?,?)";
			psql = conn.prepareStatement(sqlbone);
			psql.setString(1, tempLine);
			psql.setString(2, vid);
			psql.setString(3, Start);
			psql.setString(4, End);
			psql.executeUpdate();
		}

	}
}
