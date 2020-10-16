<#-- @ftlvariable name="books" type="java.util.List<me.ixk.app.entity.Books>" -->
<!doctype html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>列表</title>
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
  <h1>图书列表 <a href="/books">首页</a></h1>
  <table>
    <tbody>
    <tr>
      <th>图书编号</th>
      <th>图书名称</th>
      <th>作者</th>
      <th>出版社</th>
      <th>操作</th>
    </tr>
    <#list books as book>
      <tr>
        <td>${book.isbn}</td>
        <td><a href="/books/view/${book.id}">${book.name}</a></td>
        <td>${book.author}</td>
        <td>${book.publisher}</td>
        <td><a href="/books/update/${book.id}">更新</a> <a
              href="/books/delete/${book.id}">删除</a></td>
      </tr>
    </#list>
    </tbody>
  </table>
</section>
</body>
</html>