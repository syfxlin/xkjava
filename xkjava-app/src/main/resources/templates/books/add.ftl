<#-- @ftlvariable name="$request" type="me.ixk.framework.http.Request" -->
<!doctype html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>添加</title>
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
  <h1>添加图书</h1>
  <form action="/books/add" method="post">
    <div>
      <label for="name">图书名称：</label>
      <input type="text" id="name" name="name">
    </div>
    <div>
      <label for="author">作者：</label>
      <input type="text" id="author" name="author">
    </div>
    <div>
      <label for="publisher">出版社：</label>
      <input type="text" id="publisher" name="publisher">
    </div>
    <div>
      <label for="time">出版时间：</label>
      <input type="datetime-local" id="time">
      <input type="text" name="time" hidden id="time-up">
      <script>
        document.querySelector('#time').addEventListener('change', () => {
          document.getElementById('time-up').value = document.getElementById('time').value + ':00Z'
        })
      </script>
    </div>
    <div>
      <label for="isbn">ISBN：</label>
      <input type="text" id="isbn" name="isbn"></div>
    <div>
      <label for="introduction">图书简介：</label>
      <input type="text" id="introduction"
             name="introduction">
    </div>
    <div>
      <button type="submit">提交</button>
      <button type="reset">重置</button>
    </div>
  </form>
    <#if $request.has("success")>
      <h2>添加成功，<a href="/books/index">首页</a></h2>
    </#if>
</section>
</body>
</html>