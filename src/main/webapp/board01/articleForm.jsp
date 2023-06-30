<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  /> 
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
						console.log($("#preview").attr('src', event.target.result));
						$("#preview").attr('src', event.target.result);
					}

					reader.readAsDataURL(input.files[0]);
				}



			};
			
			
			
			
			function backToList(obj){
				//console.log(obj);
				obj.action="${contextPath}/board/listArticles.do";
				 obj.submit();
			}
			
			
			
			
			
			
			

		</script>

	</head>

	<body>
		<h1 style="text-align:center">새글 쓰기</h1>

		<form method="post" action="${contextPath}/board/addArticle.do" enctype="multipart/form-data">

			<table border=0 align="center">
				<tr>
					<td align="right">글제목: </td>
					<td colspan="2"><input type="text" size="67" maxlength="500" name="title" /></td>
				</tr>
				<tr>
					<td align="right" valign="top"><br>글내용: </td>
					<td colspan=2><textarea name="content" rows="10" cols="65" maxlength="4000"></textarea> </td>
				</tr>
				<tr>

					<td align="right">이미지파일 첨부: </td>
					<td><input type="file" name="imageFileName" onchange="readURL(this)"> </td>
					<td><img id="preview" src="#" width=200 height=200 /> </td>
				</tr>
				<tr>
					<td align="right"> </td>
					<td colspan="2">
						<input type="submit" value="글쓰기" />
						<input type=button value="목록보기" onClick="backToList(this.form)" />
					</td>


				</tr>
			</table>



		</form>
	</body>

	</html>