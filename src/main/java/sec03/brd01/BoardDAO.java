package sec03.brd01;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class BoardDAO {

	private Connection conn;
	private DataSource dataFactory;
	private PreparedStatement pstmt;

	public BoardDAO() {
		// JNDI방식의 연결로 MemberDAO 객체를 초기화

		try {
			Context ctx = new InitialContext(); // 컨텍스트 작업을 위한 객체
			Context envContext = (Context) ctx.lookup("java:/comp/env"); // 오라클인지 mysql인지의 환경을 찾기위한 컨텍스트
			dataFactory = (DataSource) envContext.lookup("jdbc/oracle");
		} catch (NamingException e) {
			System.out.println("톰캣의 context.xml에 정의되어 있는 이름부분에서 정확하지 않은 에러");
//					e.printStackTrace();
		}
	}

	public List<ArticleVO> selectAllArticles() {

		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();

		try {
			conn = dataFactory.getConnection();

			String query = "select " + "level,articleno, parentno, title, content,  id, writedate" + " from t_board"
					+ " start with parentNO=0" + " CONNECT by prior articleno = parentno"
					+ " order siblings by articleno desc ";
			System.out.println(query);

			pstmt = conn.prepareStatement(query);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int level = rs.getInt("level");
				int articleNO = rs.getInt("articleNO");
				int parentNO = rs.getInt("parentNO");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");

				ArticleVO article = new ArticleVO();

				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				articlesList.add(article);

			}

			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return articlesList;
	}

	
	
	
	public List<ArticleVO> selectAllArticles(Map pagingMap){
		
		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
		int section = (Integer)pagingMap.get("section");
		int pageNum=(Integer)pagingMap.get("pageNum");
		
		try {
			conn = dataFactory.getConnection();
			
			
			String query=
					
					
					"select *"
					+ " from (select rownum as rownum2, lvl, articleno, parentno, title, content,  id, writedate "
					+ "      from  ( select level as lvl,articleno, parentno, lpad(' ', 2*(level-1))||title as  title, content,  id, writedate "
					+ "               from t_board "
					+ "               start with parentNO=0 "
					+ "               CONNECT by prior articleno = parentno "
					+ "               order siblings by articleno desc)) "
					+ " where rownum2 between(?-1)*100+(?-1)*10+1 and (?-1)*100+?*10" ;
			
			
			   System.out.println(query);
			   pstmt= conn.prepareStatement(query);
			   
			   pstmt.setInt(1, section);
			   pstmt.setInt(2, pageNum);
			   pstmt.setInt(3, section);
			   pstmt.setInt(4, pageNum);
			   ResultSet rs =pstmt.executeQuery();
			
			   
			   while(rs.next()) {
				   int level = rs.getInt("lvl");
				      int articleNO = rs.getInt("articleNO");
				      int parentNO = rs.getInt("parentNO");
				      String title = rs.getString("title");
				      String id = rs.getString("id");
				      Date writeDate= rs.getDate("writeDate");
				      
				      ArticleVO article = new ArticleVO();
				      
				      article.setLevel(level);
				      article.setArticleNO(articleNO);
				      article.setParentNO(parentNO);
				      article.setTitle(title);
				      article.setId(id);
				      article.setWriteDate(writeDate);
				      
				      
				      articlesList.add(article);
				      
			   }
			   
			   rs.close();
			   pstmt.close();
			   conn.close();
		} catch (SQLException e) {
			System.out.println("페이징정보 저장한 아티클리스트 조회시 에러");
//			e.printStackTrace();
		}
		
		return articlesList;
	}
	
	
	
	
	
	
	
	
	
	public int getNewArticleNO() {
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT  max(articleNO) from t_board ";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(query);
			if(rs.next()) {
				return rs.getInt(1)+1;
			}
			rs.close();
			pstmt.close();
			conn.close();
			
		}catch(Exception e) {
			System.out.println("글 추가시 새로운 다음 글번호 가져오다 에러남 ");
		}
		return 0;
	};
	
	public int insertNewArticle(ArticleVO article){
		int articleNO=getNewArticleNO();
		try {
			conn = dataFactory.getConnection();
			
			int parentNO = article.getParentNO();
			System.out.println("부모글번호:" + parentNO);
			String title = article.getTitle();
			String content = article.getContent();
			String id = article.getId();
			String imageFileName = article.getImageFileName();
			
			
			String query = "INSERT INTO t_board (articleNO, parentNO, title, content, imageFileName, id)"
					+ " VALUES (?, ? ,?, ?, ?, ?)";
			
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			
			pstmt.setInt(1, articleNO);
			pstmt.setInt(2, parentNO);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			
		}catch(Exception e) {
			System.out.println("새로운 글 추가시 에러");
		}
		return articleNO;
		
	}
	
	
	public ArticleVO selectArticle(int articleNo){
		ArticleVO article=new ArticleVO();
		
		try {
			conn = dataFactory.getConnection();
			
			String query ="select articleNO,parentNO,title,content, imageFileName,id,writeDate"
                    +" from t_board" 
                    +" where articleNO=?";
			
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			
			pstmt.setInt(1, articleNo);
			ResultSet rs =pstmt.executeQuery();
			
			while(rs.next()) {
				int _articleNO =rs.getInt("articleNO");
				int parentNO=rs.getInt("parentNO");
				String title = rs.getString("title");
				String content =rs.getString("content");
			    String imageFileName = rs.getString("imageFileName"); 
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				
				article.setArticleNO(_articleNO);
				article.setParentNO (parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setImageFileName(imageFileName);
				article.setId(id);
				article.setWriteDate(writeDate);
				
			}
			
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("글 번호로 해당 글 객체 가져오는데 에러남");
//			e.printStackTrace();
		}
		
		
		
		
		
		return article;
	}
	
		
	public void updateArticle(ArticleVO article){
		int articleNO = article.getArticleNO();
		String title = article.getTitle();
		String content = article.getContent();
		String imageFileName = article.getImageFileName();
		
		try {
			conn = dataFactory.getConnection();
			
			String query = "update t_board  set title=?,content=?";
			
			if (imageFileName != null && imageFileName.length() != 0) {
				query += ",imageFileName=?";
			}
			
			query += " where articleNO=?";
			
			
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			
			if (imageFileName != null && imageFileName.length() != 0) {
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNO);
			} else {
				pstmt.setInt(3, articleNO);
			}
			
			
			
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			
			
			
			
		} catch (SQLException e) {
			System.out.println("글 수정시 에러");
			//e.printStackTrace();
		}
		
		
		
		
	}
	
	
	
	public void deleteArticle(int articleNo){
		try {
			conn = dataFactory.getConnection();
			
			String query =	
			"delete from t_board"
			+ " where articleno in(select articleno "
			+ " from t_board"
			+ " start with articleno=?"
			+ " connect by prior articleno=parentno )" ;
			
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("삭제시 에러");
			//e.printStackTrace();
		}
	}
	
	
	
	public List<Integer>  selectRemovedArticles(int articleNo){
		List<Integer> articleNOList = new ArrayList<Integer>();
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT articleNO FROM  t_board  ";
			query += " START WITH articleNO = ?";
			query += " CONNECT BY PRIOR  articleNO = parentNO";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNo);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				articleNo = rs.getInt("articleNO");
				articleNOList.add(articleNo);
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleNOList;
	}
	
	
	
	
	public int  selectTotArticles(){
		
		try {
			conn = dataFactory.getConnection();
			String query = "select count(articleNO) from t_board ";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				return (rs.getInt(1));
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
	
}
