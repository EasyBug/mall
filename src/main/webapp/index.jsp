<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<html>
<body>
<h2>Hello World!</h2>


Spring mvc上传文件
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springMVC上传文件"/>
</form>

富文本上传文件
<form name="form1" action="/manage/product/rich_text_img_Upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本上传文件"/>
</form>
</body>
</html>
