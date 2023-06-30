package sec03.brd01;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
	
	BoardDAO boardDAO;
	
	public BoardService() {
		boardDAO = new BoardDAO();
	}
	
	
	public List<ArticleVO> listArticles(){
		List<ArticleVO>  articlesList= boardDAO.selectAllArticles();
		return articlesList;
	};
	
	
	
	// 총 글의 갯수를 저장할 객체를 해시맵에 저장(키값을 문자열, 총 글의 갯수를 Integer)
	
	public Map listArticles(Map<String, Integer> pagingMap){
		Map articlesMap=new HashMap();
		
		
		List<ArticleVO> articlesList= boardDAO.selectAllArticles(pagingMap);
		
		int totArticles = boardDAO.selectTotArticles();
		System.out.println(totArticles);
		
		
		
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totArticles", totArticles);
		
		//articlesMap.put("totArticles", 101);
		
		
		return articlesMap;
	}
	
	
	
	public int addArticle(ArticleVO article){
		int articleNO=boardDAO.insertNewArticle(article);
		return articleNO;
	}
	
	
	
	public ArticleVO viewArticle(int articleNo){
		ArticleVO article = null;
		article=boardDAO.selectArticle(articleNo);
		return article;
	}
	
	
	
	
	public void modArticle(ArticleVO article){
		boardDAO.updateArticle(article);
	}
	
	public List<Integer> removeArticle(int articleNo){
		List<Integer> articleNOList= boardDAO.selectRemovedArticles(articleNo); //articleNOList는 글번호가 2번일 경우 2(부모) 자식 3,5,6
		boardDAO.deleteArticle(articleNo);
		return articleNOList;
	}
	
	

	
	public int addReply(ArticleVO articleVO){
		
		int articleNo=boardDAO.insertNewArticle(articleVO);
		
		return articleNo;
	}
	
	
}
