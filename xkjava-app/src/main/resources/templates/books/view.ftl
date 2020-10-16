<#-- @ftlvariable name="book" type="me.ixk.app.entity.Books" -->
<!doctype html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>详细</title>
  <style>
    body {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  </style>
</head>
<body>
<section>
  <h1>图书详细信息</h1>
  <div>图书名称：${book.name}</div>
  <div>作者：${book.author}</div>
  <div>出版社：${book.publisher}</div>
  <div>出版时间：${book.time.toString()}</div>
  <div>ISBN：${book.isbn}</div>
  <div>图书简介：${book.introduction}</div>
  <a href="/books/list">返回</a>
</section>
</body>
</html>