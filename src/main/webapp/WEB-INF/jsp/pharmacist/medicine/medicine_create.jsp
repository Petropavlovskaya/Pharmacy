<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>On-line pharmacy. Add new item</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_pharmacist_menu.jsp"/>
</div>
<div id="center_no_right">
    <c:if test="${not empty message}">
        <p class="p-red">${message}<br></p>
    </c:if>

    <table>
        <%@include file="../_pharmacist_medicine_table_header.jsp"%>

        <form action="${pageContext.request.contextPath}/pharmacist/medicine/create" method="post">
            <tr class="insert_row">
<%--                <td><textarea name="medicine_name" class="table_field_high" &lt;%&ndash;id="fieldMedName" &ndash;%&gt;
                              &lt;%&ndash;required&ndash;%&gt; pattern="[А-ЯЁ]{1}[а-яё\s-){1,19}"></textarea></td>--%>
                <td><input name="medicine_name" class="table_field_high" type="text"
                           <%--required --%>pattern="[А-ЯЁ]{1}[а-яё\s-){1,19}" ></td>
                <td><input name="indivisible_amount" class="table_field_high" type="text" size="6"
                           <%--required --%>pattern="\d{1,3}" min="1" title="Цифры от 1 до 999"></td>
                <td><input name="amount"  class="table_field_high" type="text" size="6"
                           <%--required--%> pattern="\d{1,5}" min="1" title="Цифры от 1 до 99999"></td>
                <td><input name="dosage" class="table_field_high" type="text" size="8" required ></td>
                <td><input name="exp_date" class="table_field_high" type="date" size="9" placeholder="ГГГГ-ММ-ДД"required></td>
                <td align="center"><input name="recipe_required" class="table_field_high" type="checkbox" > </td>
                <td><input name="price_rub" type="text" size="3"
                           required pattern="\d{0,3}" min="0" align="right" title="Стоимость в рублях от 0 до 999"> руб.<br>
                    <input name="price_kop" type="text" size="2"
                           required pattern="\d{0,2}" min="0" align="right" title="Стоимость в копейках от 0 до 99"> коп.</td>
                <td><textarea name="pharm_form" class="table_field_high" type="text" size="5" required></textarea></td>
                <td align="center"> <input type="submit" value="Create"></td>
            </tr>
            <input type="hidden" name="medicineCommand" value="create" >
            <input type="hidden" name="accountId" value="${accountId}" >
        </form>

    </table>
</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
