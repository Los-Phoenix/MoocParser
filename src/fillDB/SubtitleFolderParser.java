package fillDB;

import java.io.BufferedReader;
import java.io.File;
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

public class SubtitleFolderParser {
	public static String Folder = "G:\\2013video\\01计算的机械化与自动化";//指定一个文件夹

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		//String subPath;//这个是路径
		String vid;//这个是分析出来的标题
		String chapter = "单元一 计算装置与计算机", 
				lesson = "1.1 计算工具的机械化与计算过程的自动化";
		int id = 0;

		File f = null;
		File[] paths;

		String sqlbone = "";

		String url = "jdbc:mysql://localhost:3306/mooclink?"
				+ "user=root&password=1111&useUnicode=true&characterEncoding=UTF8";
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(url);

		java.sql.PreparedStatement psql;

		//清空两个数据库表
		sqlbone = "truncate sentence";
		psql = conn.prepareStatement(sqlbone);
		psql.executeUpdate();

		sqlbone = "truncate video";
		psql = conn.prepareStatement(sqlbone);
		psql.executeUpdate();





		//打开文件夹
		f = new File(Folder);
		paths = f.listFiles();
		for(File subPath:paths)
		{
			if(!subPath.toString().endsWith("srt")){
				continue;
			}





			vid = subPath.getName().substring(0, subPath.getName().lastIndexOf("."));
			id ++;
			sqlbone = "insert into video(chapter, lesson, title, path, idvideo) values(?,?,?,?,?)";
			psql = conn.prepareStatement(sqlbone);
			psql.setString(1, chapter);
			psql.setString(2, lesson);
			psql.setString(3, vid);
			//System.out.println();
			psql.setString(4, subPath.toString().replace(Folder.substring(0,Folder.indexOf('\\',Folder.indexOf('\\', 0) + 1) + 1), ".\\"));
			psql.setInt(5, id);
			psql.executeUpdate();

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
				psql.setInt(2, id);
				psql.setString(3, Start);
				psql.setString(4, End);
				psql.executeUpdate();
			}
		}

	}
}
