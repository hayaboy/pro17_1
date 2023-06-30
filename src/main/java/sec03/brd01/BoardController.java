package sec03.brd01;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;


@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	
	BoardService boardService;
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	
	ArticleVO articleVO;
	HttpSession session;
	
	@Override
	public void init() throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
		
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	
	
	protected void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html; charset=utf-8");
		String action = request.getPathInfo();
		System.out.println("action:" + action);
		
		String nextPage = "";
		
		if(action == null || (action.equals("/"))  || action.equals("/listArticles.do")) {
			
			String _section=request.getParameter("section");
			String _pageNum=request.getParameter("pageNum");
			
			int section = Integer.parseInt(((_section==null)? "1":_section) );
			
			int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
			
			System.out.println("섹션번호  : " + section + "," + "페이지번호" + pageNum);
			
			
			Map pagingMap = new HashMap();
			
			pagingMap.put("section", section);
			pagingMap.put("pageNum", pageNum);
			
			Map articlesMap=boardService.listArticles(pagingMap);
			
			articlesMap.put("section", section);
			articlesMap.put("pageNum", pageNum);
			request.setAttribute("articlesMap", articlesMap);
			
			
//			List<ArticleVO>  articlesList=new ArrayList<ArticleVO>();;
//			articlesList=boardService.listArticles();
//			request.setAttribute("articlesList", articlesList);
			nextPage = "/board01/listArticles.jsp";
			
			
			
			
		}else if(action.equals("/articleForm.do")) {
			nextPage = "/board01/articleForm.jsp";
		}else if(action.equals("/addArticle.do")) {
			
			
			Map<String, String> articleMap=upload(request,response );
			String title = articleMap.get("title");
			String content = articleMap.get("content");
			String imageFileName = articleMap.get("imageFileName");
			
			articleVO.setParentNO(0);
			articleVO.setId("hong");
			articleVO.setTitle(title);
			articleVO.setContent(content);
			articleVO.setImageFileName(imageFileName);
			
			int articleNO=boardService.addArticle(articleVO);
			
			//이미지 파일 유효성 체크
			if(imageFileName!=null && imageFileName.length()!=0) {
				File srcfile=new File(ARTICLE_IMAGE_REPO+"\\"+"temp"+"\\"+imageFileName);
				File desDir=new File(ARTICLE_IMAGE_REPO+"\\" + articleNO);
				desDir.mkdirs();  //File.mkdirs() 메서드는 전체 디렉토리 경로를 생성하면서 디렉토리를 만드는 반면, File.mkdir() 메서드는 단일 디렉토리를 생성합니다.
				System.out.println(desDir.mkdirs()); // 필요한 부모 디렉토리는 이미 있으니 false, 필요한 모든 상위 디렉토리와 함께 디렉토리가 생성된 경우에만 true이고, 그렇지 않으면 false입니다.
				FileUtils.moveFileToDirectory(srcfile, desDir, true); //true로 설정하면 디렉토리가 없을 경우 자동 생성, false로  설정할 경우, 대상 디렉토리가 없을 경우 예외 발생
			}
			
			PrintWriter pw=response.getWriter();
			pw.print("<script>"
					+ "alert('새글 추가함');"
					+ " location.href='"+request.getContextPath()+ "/board/listArticles.do';"
					+ "</script>");
			
			
			
			return;
		
		}else if(action.equals("/viewArticle.do")) {
			
			//글번호 가져와서 그 글번호로 객체를 조회(sql)해서 다시 글 정보를 articleVO에 저장
			String articleNO=request.getParameter("articleNO");
			System.out.println("글번호 :" + articleNO);
			
			ArticleVO article = boardService.viewArticle(Integer.parseInt(articleNO));
			
			request.setAttribute("article", article);
			nextPage = "/board01/viewArticle.jsp";
			
		}else if(action.equals("/modArticle.do")){
			Map<String, String> articleMap = upload(request, response);
			
			int articleNO = Integer.parseInt(articleMap.get("articleNO"));
			
			articleVO.setArticleNO(articleNO);
			String title = articleMap.get("title");
			String content = articleMap.get("content");
			String imageFileName = articleMap.get("imageFileName");
			
			articleVO.setParentNO(0);
			articleVO.setId("hong");
			articleVO.setTitle(title);
			articleVO.setContent(content);
			articleVO.setImageFileName(imageFileName);
			
			boardService.modArticle(articleVO);
			
			
			
			if (imageFileName != null && imageFileName.length() != 0) {
				
				String originalFileName = articleMap.get("originalFileName");
				
				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
				
				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
				destDir.mkdirs();
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
				
				File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
				oldFile.delete();
			}
			
			
			
			PrintWriter pw = response.getWriter();
			pw.print("<script>" + "  alert('글을 수정했습니다.');" + " location.href='" + request.getContextPath()
					+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
			return;
			
			
			
		}else if(action.equals("/removeArticle.do")) {
			int articleNO = Integer.parseInt(request.getParameter("articleNO"));
			List<Integer> articleNOList = boardService.removeArticle(articleNO);
			
			for (int _articleNO : articleNOList) {
				File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
				if (imgDir.exists()) {
					FileUtils.deleteDirectory(imgDir);
				}
			}
			
			
			 PrintWriter pw = response.getWriter(); pw.print("<script>" + "  alert('글을 삭제했습니다.');" +
			 " location.href='" + request.getContextPath() + "/board/listArticles.do';" +
			 "</script>"); 
			 return;
			
			
		}else if(action.equals("/replyForm.do")){
			
			try {
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				System.out.println("답글쓰기에서 넘어온 부모 글번호 : " + parentNO);
				//로그인한 사람이 계속 그 정보를 가지고 다녀야하므로 세션에 저장
				session=request.getSession();
				
				session.setAttribute("parentNO", parentNO);
			}catch(Exception e) {
				
			}
			
			
			
		
			
			nextPage = "/board01/replyForm.jsp";
			
		}else if(action.equals("/addReply.do")){
			session = request.getSession();
			int parentNO = (Integer) session.getAttribute("parentNO");
			System.out.println();
			//이미 부모글 가져왔으므로
			//세션에는 남기지 않는것이 좋다. 추후에 세션셋팅시 키값이 같을 경우 데이터가 잘 못 들어갈 수 있기 때문에 , 가급적 가져온 후에는 삭제하는 버릇을 들이시기 바랍니다.
			
//			session.removeAttribute("parentNO");
			
			//각각에 대한 데이터 타입이 다를 경우 해쉬맵으로 저장
			
			Map<String, String> articleMap = upload(request, response);
			
			String title = articleMap.get("title");
			String content = articleMap.get("content");
			String imageFileName = articleMap.get("imageFileName");
			
			articleVO.setParentNO(parentNO);
			articleVO.setId("lee");
			articleVO.setTitle(title);
			articleVO.setContent(content);
			articleVO.setImageFileName(imageFileName);
			
			int articleNO=boardService.addReply(articleVO);
			
			if (imageFileName != null && imageFileName.length() != 0) {
				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
				destDir.mkdirs();
				FileUtils.moveFileToDirectory(srcFile, destDir, true);
			}
			
			
			PrintWriter pw = response.getWriter();
			pw.print("<script>" + "  alert('답글을 추가했습니다.');" + " location.href='" + request.getContextPath()
					+ "/board/viewArticle.do?articleNO="+articleNO+"';" + "</script>");
			return;
			
			
		}else {
			nextPage = "/board01/listArticles.jsp";
		}
		
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	}
	

	
	public Map<String, String> upload(HttpServletRequest request, HttpServletResponse response){
		
		String encoding = "utf-8";
		
		
		// 새로운 글 쓰기에 대한 데이터를 여러 행태를 저장하지 좋은 키, 밸류의 맵 형태로 저장
		
		Map<String, String> articleMap = 	new HashMap<String, String>();
		
		// 파일 객체
		
		File currentDirPath =new File(ARTICLE_IMAGE_REPO);
		
		
		//파일 업로드 시 사용한 라이브러리(fileupload, common-io)
		// 업로드될 경로 
		
		
		// 파일을 올리는 공간에다가 파일을 올리고자하는 용량과 경로 설정
				// 파일 공간에 대한 설정을 위한 클래스 DiskFileItemFactory

				DiskFileItemFactory factory = new DiskFileItemFactory(); // 파일 설정과 관련된 여러가지 일을 하는 클래스, 파일과 관련된 내용을 항목(item) 형태로
																			// 저장함
																			// 파일 아이템이란 예를 들어, 파일이름, 파일 크기
				factory.setRepository(currentDirPath);

				// DiskFileItemFactory 클래스의 setSizeThreshold() 메서드는 메모리에 보관할 임시 파일의 크기 임계값을 설정하는
				// 데 사용됩니다. 이 메서드에서 사용되는 단위는 바이트(Byte)입니다.

				// setSizeThreshold() 메서드에 전달하는 매개변수는 바이트 단위로 지정되며,
				// 임시 파일의 크기가 이 임계값보다 크면 디스크에 파일이 저장됩니다. 임시 파일의 크기가 임계값보다 작으면 메모리에 보관됩니다.
				// 이렇게 함으로써 작은 파일은 메모리에 보관되어 더 빠른 처리가 가능하고, 큰 파일은 디스크에 저장되어 메모리 사용량을 줄일 수 있습니다.
				factory.setSizeThreshold(1024 * 1024); // 1024 byte * 1024 = 1KB

				// 파일을 올리는 행위를 하는 클래스

				ServletFileUpload upload = new ServletFileUpload(factory);
				System.out.println("파일 업로드 객체 : " + upload);
		
				// FileItem 은 인코딩 타입이 multipart/form-data 일 때 , POST로 요청시 받을 수 있는 항목 클래스
				
		
				
				try {
					
					List<FileItem> items = upload.parseRequest(request);
					
					for (int i = 0; i < items.size(); i++) {
						
						
						System.out.println("---------------------");
						FileItem fileItem = (FileItem) items.get(i);
						System.out.println(fileItem);
						System.out.println(fileItem.isFormField());
						// 폼필드 내용만 가져옴
						if(fileItem.isFormField()) {
							System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
							
							articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
							
							
						}else {
							System.out.println("매개변수 이름 :" + fileItem.getFieldName());
							System.out.println("파일이름 :" + fileItem.getName());
							System.out.println("파일 크기" + fileItem.getSize() + "bytes");

							
							
							if (fileItem.getSize() > 0) {
								System.out.println(fileItem.getName());
								// 이전 버전의 익스플로러에서는 전체 경로를 가져오는 경우에 대비...중간에 혹시 폴더명이 끼여있는지 확인하는 코드
								int idx = fileItem.getName().lastIndexOf("\\");
								
								System.out.println("인덱스" + idx);
								
								if (idx == -1) {
									idx = fileItem.getName().lastIndexOf("/");    //익스플로러의 경우 대비
									System.out.println(idx);
								}
								
								String fileName = fileItem.getName().substring(idx + 1);
								
								//중간에 파일명에 //나 \가 포함될 경우 예외가 발생함, 예외가 발생하지 않도록 하는 코드 추가함
//								String fileName=fileItem.getName() + "\\hi" + "/nice";
								System.out.println("현재 경로" + currentDirPath);
								System.out.println("파일명" + fileName);
								
								articleMap.put(fileItem.getFieldName(), fileName);
								
								File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
								
								fileItem.write(uploadFile);
							}
							
							
							
						}
						
						
					}
					
					
				}catch(Exception e) {
					System.out.println("파일 업로드시 에러");
//					e.printStackTrace();
				}
				
				
				
				
				return articleMap;
				
	}
	
	
	
	
	
}
