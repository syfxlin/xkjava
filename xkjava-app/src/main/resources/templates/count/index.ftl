<#-- @ftlvariable name="countHistory" type="java.lang.Long" -->
<#-- @ftlvariable name="countOnline" type="java.lang.Long" -->
<#-- @ftlvariable name="usersList" type="java.util.List<me.ixk.app.entity.Users>" -->
<h2>当前在线人数：${countOnline}</h2>
<h2>历史在线人数：${countHistory}</h2>
<h2>当前登录的用户：</h2>
<ul>
    <#list usersList as user>
      <li>${user}</li>
    </#list>
</ul>