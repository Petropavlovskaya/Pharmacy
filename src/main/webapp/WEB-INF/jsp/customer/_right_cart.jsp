<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<%--<body>
--%>
<p <%--align="centr"--%>>КОРЗИНА</p>
<%--<table>
    <thead>
    <th>Наименование&lt;%&ndash;Medicine name&ndash;%&gt;</th>
    <th>Неделимое кол-во&lt;%&ndash;Indivisible amount&ndash;%&gt;</th>
    <th>В наличии&lt;%&ndash;Total amount&ndash;%&gt;</th>
    <th>Дозировка&lt;%&ndash;Dosage&ndash;%&gt;</th>
    <th>Годен до&lt;%&ndash;Expiration date&ndash;%&gt;</th>
    <th>Требуется рецепт&lt;%&ndash;Recipe required&ndash;%&gt;</th>
    <th>Цена&lt;%&ndash;Price&ndash;%&gt;</th>
    <th>Форма выпуска&lt;%&ndash;Pharmacy form&ndash;%&gt;</th>
    <th>Количество&lt;%&ndash;Amount for buy&ndash;%&gt;</th>
    <th>Действие&lt;%&ndash;Pharmacy form&ndash;%&gt;</th>
    </thead>


    <c:forEach var="medItem" items="${medicineCartList}">
        <tr>
                &lt;%&ndash;            <td><c:out value="${medItem.id}"/></td>&ndash;%&gt;
            <td><c:out value="${medItem.name}"/></td>
            <td><c:out value="${medItem.indivisible_amount}"/></td>
            <td><c:out value="${medItem.amount}"/></td>
            <td><c:out value="${medItem.dosage}"/></td>
            <td><c:out value="${medItem.exp_date}"/></td>
            <td>
                <c:if test="${medItem.recipe_required == true}"> Да </c:if>
                <c:if test="${medItem.recipe_required == false}"> Нет </c:if>
            </td>
            <td><c:out value="${medItem.price/100}"/></td>
            <td><c:out value="${medItem.pharm_form}"/></td>
            <td><input name="amountForBuy" class="table_field_high" type="text" form="addIntoCart" size="7"
                       pattern="\d{1,3}" min="1" max="100" title="Количество от 1 до 100"></td>
            <td>
                <form id="addIntoCart" class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/favourite"
                      method="post">
                    <input type="submit" value="To cart">
                    <input type="hidden" name="medicineCommand" value="medicineForCart">
                    <input type="hidden" name="medicine_id" value="${medItem.id}">
                </form>
                <br>
                <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/favourite"
                      method="post">
                    <input type="submit" value="Out of favorite">
                    <input type="hidden" name="medicineCommand" value="medicineInrFavorite">
                    <input type="hidden" name="medicine_id" value="${medItem.id}">
                </form>
            </td>
                &lt;%&ndash;</div>&ndash;%&gt;

        </tr>
    </c:forEach>
</table>--%>

<%--
</body>--%>
</html>

