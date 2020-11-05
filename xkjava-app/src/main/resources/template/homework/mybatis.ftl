<#-- @ftlvariable name="bills" type="java.util.List<me.ixk.app.entity.TbBill>" -->
<#-- @ftlvariable name="providers" type="java.util.Map<java.lang.String, me.ixk.app.entity.TbProvider>" -->
<form action="/homework/mybatis/search">
  <label for="provider-id">供应商 ID</label>
  <input type="number" name="provider-id" id="provider-id">
  <label for="is-payment">是否付款</label>
  <select name="is-payment" id="is-payment">
    <option value="1">已付款</option>
    <option value="2">未付款</option>
  </select>
  <label for="product-name">名称</label>
  <input type="text" name="product-name" id="product-name">
  <input type="submit" value="查找">
</form>
<#if bills?size gt 0>
  <table>
    <tbody>
    <tr>
      <th>订单编码</th>
      <th>商品名称</th>
      <th>供应商名称</th>
      <th>账单金额</th>
      <th>是否付款</th>
    </tr>
    <#list bills as bill>
      <tr>
        <td>${bill.billCode}</td>
        <td>${bill.productName}</td>
        <td>${providers[bill.providerId?string].proName}</td>
        <td>${bill.totalPrice}</td>
        <td>${(bill.isPayment == 1)?then("已付款","未付款")}</td>
      </tr>
    </#list>
    </tbody>
  </table>
</#if>