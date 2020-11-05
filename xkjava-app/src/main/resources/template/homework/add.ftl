<#-- @ftlvariable name="providers" type="java.util.List<me.ixk.app.entity.TbProvider>" -->
<h1>新增</h1>
<form action="/homework/mybatis/add" method="post">
  <label for="a-pro-code">供应商代号</label>
  <input type="text" name="pro-code" id="a-pro-code">
  <label for="a-pro-name">供应商名称</label>
  <input type="text" name="pro-name" id="a-pro-name">
  <label for="a-pro-desc">供应商描述</label>
  <input type="text" name="pro-desc" id="a-pro-desc">
  <label for="a-pro-contact">供应商联系人</label>
  <input type="text" name="pro-contact" id="a-pro-contact">
  <label for="a-pro-phone">供应商名电话</label>
  <input type="text" name="pro-phone" id="a-pro-phone">
  <label for="a-pro-address">地址</label>
  <input type="text" name="pro-address" id="a-pro-address">
  <label for="a-pro-fax">传真</label>
  <input type="text" name="pro-fax" id="a-pro-fax">
  <input type="submit" value="添加">
</form>
<h1>删除</h1>
<form action="/homework/mybatis/delete" method="post">
  <label for="d-id">供应商 ID</label>
  <input type="number" name="id" id="d-id">
  <input type="submit" value="删除">
</form>
<h1>修改</h1>
<form action="/homework/mybatis/edit" method="post">
  <label for="e-id">供应商 ID</label>
  <input type="number" name="id" id="e-id">
  <label for="e-pro-code">供应商代号</label>
  <input type="text" name="pro-code" id="e-pro-code">
  <label for="e-pro-name">供应商名称</label>
  <input type="text" name="pro-name" id="e-pro-name">
  <label for="e-pro-desc">供应商描述</label>
  <input type="text" name="pro-desc" id="e-pro-desc">
  <label for="e-pro-contact">供应商联系人</label>
  <input type="text" name="pro-contact" id="e-pro-contact">
  <label for="e-pro-phone">供应商名电话</label>
  <input type="text" name="pro-phone" id="e-pro-phone">
  <label for="e-pro-address">地址</label>
  <input type="text" name="pro-address" id="e-pro-address">
  <label for="e-pro-fax">传真</label>
  <input type="text" name="pro-fax" id="e-pro-fax">
  <input type="submit" value="修改">
</form>

<table>
  <tbody>
  <tr>
    <th>供应商 ID</th>
    <th>供应商代号</th>
    <th>供应商名称</th>
    <th>供应商描述</th>
    <th>供应商联系人</th>
    <th>供应商名电话</th>
    <th>地址</th>
    <th>传真</th>
  </tr>
  <#list providers as provider>
    <tr>
      <td>${provider.id!}</td>
      <td>${provider.proCode!}</td>
      <td>${provider.proName!}</td>
      <td>${provider.proDesc!}</td>
      <td>${provider.proContact!}</td>
      <td>${provider.proPhone!}</td>
      <td>${provider.proAddress!}</td>
      <td>${provider.proFax!}</td>
    </tr>
  </#list>
  </tbody>
</table>