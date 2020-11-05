<#-- @ftlvariable name="users" type="java.lang.Integer" -->
<!doctype html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>欢迎</title>
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
  <h1>欢迎访问图书管理系统</h1>
  <h3>
    <a href="/books/add">添加图书</a>
    <a href="/books/list">查看图书</a>
  </h3>
  <h4>当前在线人数：${users} 人</h4>
</section>
</body>
</html>