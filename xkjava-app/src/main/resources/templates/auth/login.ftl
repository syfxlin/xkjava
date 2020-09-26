<#-- @ftlvariable name="$session" type="me.ixk.framework.http.SessionManager" -->
<form method="post" action="/login">
    <div>
        <label for="username">用户名</label>
        <input id="username" name="username" type="text">
    </div>
    <div>
        <label for="password">密码</label>
        <input id="password" name="password" type="password">
    </div>
    <div>
        <label for="remember_me">记住我</label>
        <input id="remember_me" name="remember_me" type="checkbox">
    </div>
    <input type="hidden" name="_token" value="${$session.token()}">
    <input type="submit" value="登录" />
</form>