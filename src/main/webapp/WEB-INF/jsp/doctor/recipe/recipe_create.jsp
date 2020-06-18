<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>On-line pharmacy. Create new recipe</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>


<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_doctor_menu.jsp"/>
</div>


<div id="center_no_right">
    <table>
        <%@include file="_new_recipe_table_header.jsp" %>

        <form action="${pageContext.request.contextPath}/doctor/recipe/create" method="post">
            <tr class="insert_row">

                <td><input list="customer_select" name="customer">
                    <datalist id="customer_select">
                        <c:forEach var="customer" items="${activeCustomers}">
                            <option>${customer.value}, ${customer.key}</option>
                        </c:forEach>
                    </datalist>
                </td>
                <td><input list="medicine_select" name="medicine">
                    <datalist id="medicine_select">
                        <c:forEach var="medicine" items="${availableMedicine}">
                            <option> ${medicine.name}, ${medicine.dosage}</option>
                        </c:forEach>
                    </datalist>
                </td>

                <td><input name="exp_date" class="table_field_high" type="date" size="9" placeholder="ГГГГ-ММ-ДД"
                           required></td>

                <td align="center">
                    <input type="submit" value=" Appoint ">
                    <input type="hidden" name="customerCommand" value="appointRecipe">
                </td>
            </tr>
        </form>

    </table>


</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>


</body>
</html>
