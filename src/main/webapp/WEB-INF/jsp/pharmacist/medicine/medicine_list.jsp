<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>On-line pharmacy. Medicine items</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
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
    <table>
        <%@include file="../_pharmacist_medicine_table_header.jsp" %>


        <%--        Next form view if item was selected to edit--%>
        <c:if test="${!empty editMedicine}">

            <form action="${pageContext.request.contextPath}/pharmacist/medicine/list" method="post">
                <tr class="insert_row">
                    <td><textarea name="medicine_name" class="table_field_high"
                                  required pattern="[А-ЯЁ]{1}[а-яё\s-){1,19}"> ${editMedicine.name}</textarea></td>
                    <td><input name="indivisible_amount" class="table_field_high" type="text" size="10"
                               value="${editMedicine.indivisible_amount}"
                               required pattern="\d{1,3}" min="1" title="Цифры от 1 до 999"></td>

                    <td><input name="amount" class="table_field_high" type="text" value="${editMedicine.amount}"
                               size="10"
                               required pattern="\d{1,5}" min="1" title="Цифры от 1 до 99999"></td>
                    <td><input name="dosage" class="table_field_high" type="text" value="${editMedicine.dosage}"
                               size="10"
                               required></td>
                    <td><input name="exp_date" class="table_field_high" type="date" value="${editMedicine.exp_date}"
                               size="12"
                               placeholder="ГГГГ-ММ-ДД" required></td>
                    <c:choose>
                        <c:when test="${editMedicine.recipe_required == true}">
                            <td><input name="recipe_required" class="table_field_high" type="checkbox" checked></td>
                        </c:when>
                        <c:otherwise>
                            <td><input name="recipe_required" class="table_field_high" type="checkbox"></td>
                        </c:otherwise>
                    </c:choose>
                    <td><input name="price_rub" type="text" size="3" value="${editMedicine.rub}"
                               required pattern="\d{0,3}" min="0" align="right" title="Стоимость в рублях от 0 до 999">
                        руб.
                        <input name="price_kop" type="text" size="2" value="${editMedicine.coin}"
                               required pattern="\d{0,2}" min="0" align="right" title="Стоимость в копейках от 0 до 99">
                        коп.
                    </td>
                    <td><textarea name="pharm_form" class="table_field_high" type="text" size="5"
                                  required>${editMedicine.pharm_form}</textarea></td>
                    <td><input type="submit" value="Set changes"></td>
                </tr>
                <input type="hidden" name="medicineCommand" value="setChanges"/>
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="medicine_id" value="${editMedicine.id}">
            </form>
        </c:if>

        <c:forEach var="medicine" items="${medicineList}">
            <tr>
                <td><c:out value="${medicine.name}"/></td>
                <td><c:out value="${medicine.indivisible_amount}"/></td>
                <td><c:out value="${medicine.amount}"/></td>
                <td><c:out value="${medicine.dosage}"/></td>
                <td><c:out value="${medicine.exp_date}"/></td>
                <td>
                    <c:if test="${medicine.recipe_required == true}"> Да </c:if>
                    <c:if test="${medicine.recipe_required == false}"> Нет </c:if>
                </td>
                <td><c:out value="${medicine.price/100}"/></td>
                <td><c:out value="${medicine.pharm_form}"/></td>
                <td>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/list"
                          method="post">
                        <input type="submit" value="Edit">
                        <input type="hidden" name="medicineCommand" value="medicineForEdit">
                        <input type="hidden" name="medicine_id" value="${medicine.id}">
                        <input type="hidden" name="accountLogin" value="${accountLogin}">
                    </form>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/list"
                          method="post">
                        <input type="submit" value="Delete">
                        <input type="hidden" name="medicineCommand" value="medicineForDelete">
                        <input type="hidden" name="medicine_id" value="${medicine.id}">
                        <input type="hidden" name="accountLogin" value="${accountLogin}">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
