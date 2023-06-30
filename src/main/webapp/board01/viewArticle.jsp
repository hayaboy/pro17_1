<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Insert title here</title>


<script src="http://code.jquery.com/jquery-latest.min.js"></script>

<script>

					function readURL(input) {
						//console.log(input);  // input이란 <input type="file" name="imageFileName" onchange="readURL(this)"> 
						//console.log(input.files); //input.files란 FileList {0: File, length: 1}
						//console.log(typeof(input.files));
						//console.log(input.files);  //
						//console.log(input.files[0]); //File {name: 'dog.jpg', lastModified: 1687154048942, lastModifiedDate: Mon Jun 19 2023 14:54:08 GMT+0900 (한국 표준시), webkitRelativePath: '', size: 89834, …}

						if (input.files && input.files[0]) {

							let reader = new FileReader();
							//console.log(reader);
							// console.log($("#preview"));  //m.fn.init [img#preview, context: document, selector: '#preview']
							reader.onload = function (event) {
								console.log("이벤트 : ", event);  //ProgressEvent 
								console.log("이벤트 대상 : ", event.target); //FileReader
								console.log("이벤트 대상 결과 : ", event.target.result); // data:image/jpeg
								console.log($("#i_imageFileName").attr('src', event.target.result));
								$("#preview").attr('src', event.target.result);
							}

							reader.readAsDataURL(input.files[0]);
						}



					};



					function backToList(obj) {
						obj.action = "${contextPath}/board/listArticles.do";
						obj.submit();
					}


					function fn_enable(obj) {
						// console.log(obj);  //form 테그
						// console.log(document.getElementById('i_title')); //<input type="text" value="${article.title }" name="title" id="i_title" disabled />

						// console.log(document.getElementById('i_title').disabled); //true
						document.getElementById('i_title').disabled = false;
						document.getElementById("i_content").disabled = false;
						
						var imgFile=document.getElementById("i_imageFileName");
						if(imgFile != null){
							document.getElementById("i_imageFileName").disabled = false;
						}
																							
						// console.log(document.getElementById("tr_btn_modify").style.display);
						
						document.getElementById("tr_btn_modify").style.display = 'block';
						document.getElementById("tr_btn").style.display = "none";
						
						

					}



					function fn_modify_article(obj){
						console.log(obj);
						obj.action="${contextPath}/board/modArticle.do";
						obj.submit();
					}


					
					
					
					function fn_remove_article(url,articleNO){
						console.log(url, articleNO);
						var form = document.createElement("form");
						form.setAttribute("method", "post");
						form.setAttribute("action", url);
						var articleNOInput = document.createElement("input");
						articleNOInput.setAttribute("type","hidden");
						articleNOInput.setAttribute("name","articleNO");
						articleNOInput.setAttribute("value", articleNO);
						form.appendChild(articleNOInput);
						document.body.appendChild(form);
						console.log(form);
						form.submit();
					}
					
					

					
					function fn_reply_form(url, parentNO){
						console.log(url,parentNO); //pro17_1/board/replyForm.do, 부모글 7
						var form = document.createElement("form");
						form.setAttribute("method", "post");
						 form.setAttribute("action", url);
						 var parentNOInput = document.createElement("input");
						 parentNOInput.setAttribute("type","hidden");
					     parentNOInput.setAttribute("name","parentNO");
					     parentNOInput.setAttribute("value", parentNO);
					     console.log(form);
					     console.log(parentNOInput);
					     form.appendChild(parentNOInput);
					     console.log(form);
					     document.body.appendChild(form);
					     console.log(document);
					     form.submit();
					}
					
					
					
					
					
					
					
					
				</script>


</head>

<body>
	<%-- <c:out value="${article.id}"></c:out> --%>

	<form name="frmArticle" method="post" enctype="multipart/form-data">

		<table border="0" align="center">
			<tr>
				<td width="150" align="center" bgcolor="#FF9933">글번호</td>
				<td><input type="text" value="${article.articleNO }" disabled />
					<input type="hidden" name="articleNO" value="${article.articleNO}" />
					<!-- 추후 글 수정시 글 번호 필요함 --></td>
			</tr>

			<tr>
				<td width="150" align="center" bgcolor="#FF9933">작성자 아이디</td>
				<td><input type="text" value="${article.id }" name="id"
					disabled /></td>
			</tr>

			<tr>
				<td width="150" align="center" bgcolor="#FF9933">제목</td>
				<td><input type="text" value="${article.title }" name="title"
					id="i_title" disabled /></td>
			</tr>

			<tr>
				<td width="150" align="center" bgcolor="#FF9933">내용</td>
				<td><textarea rows="10" cols="60" name="content" id="i_content"
						disabled />${article.content }</textarea></td>
			</tr>


		 <c:if test="${not empty article.imageFileName && article.imageFileName != 'null' }"> 
				<tr id="imgrow">

					<td width="20%" align="center" bgcolor="#FF9933" rowspan="2">
						이미지</td>

					<td><input type="hidden" name="originalFileName"
						value="${article.imageFileName }" /> <!--수정 시 원래 이미지 파일명 필요  -->

						<img
						src="${contextPath}/download.do?imageFileName=${article.imageFileName}&articleNO=${article.articleNO }"
						id="preview" /><br></td>

				</tr>


				<tr>
					<td><input type="file" name="imageFileName "
						id="i_imageFileName" disabled onchange="readURL(this);" /></td>
				</tr>



			 </c:if> 



			<tr>
				<td width="20%" align="center" bgcolor="#FF9933">등록일자</td>
				<%-- <td><input type=text value="<fmt:formatDate value=" ${article.writeDate}" />"
									disabled /> --%>
				<td><input type=text value="${article.writeDate}" disabled />
				</td>
			</tr>

			<tr id="tr_btn_modify" style="display: none">
				<td colspan="2" align="center"><input type=button
					value="수정반영하기" onClick="fn_modify_article(frmArticle)"> <input
					type=button value="취소" onClick="backToList(frmArticle)"></td>
			</tr>



			<tr id="tr_btn">
				<td colspan=2 align="center"><input type=button value="수정하기" onClick="fn_enable(this.form)"> 
				<input type=button value="삭제하기" onClick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNO})">
				<input type=button value="리스트로 돌아가기" onClick="backToList(this.form)">
				<input type=button value="답글쓰기" onClick="fn_reply_form('${contextPath}/board/replyForm.do', ${article.articleNO})">
				</td>
			</tr>

		</table>
	</form>



</body>

</html>