<#-- @ftlvariable name="$session" type="me.ixk.framework.http.SessionManager" -->
<form method="post" action="/register">
    <div>
        <label for="username">用户名</label>
        <input id="username" name="username" type="text">
    </div>
    <div>
        <label for="nickname">昵称</label>
        <input id="nickname" name="nickname" type="text">
    </div>
    <div>
        <label for="email">邮箱</label>
        <input id="email" name="email" type="text">
    </div>
    <div>
        <label for="password">密码</label>
        <input id="password" name="password" type="password">
    </div>
    <div>
        <label for="password_confirmed">确认密码</label>
        <input id="password_confirmed" name="password_confirmed" type="password">
    </div>
    <input type="hidden" name="_token" value="${$session.token()}">
    <input type="submit" value="注册"/>
</form>