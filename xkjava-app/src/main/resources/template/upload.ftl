<#-- @ftlvariable name="$session" type="me.ixk.framework.http.SessionManager" -->
<form action="/upload" method="post" enctype="multipart/form-data">
    <input type="hidden" name="_token" value="${$session.token()}" />
    <input type="file" name="file" />
    <input type="submit" value="上传" />
</form>