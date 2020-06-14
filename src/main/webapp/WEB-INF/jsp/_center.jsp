<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div align="center">

   <c:choose>
        <c:when test="${empty accountLogin}">
            <h3>Для возможности осуществить заказ, пожалуйста
                <br>
                <a href="${pageContext.request.contextPath}/login">Войдите</a>
                или
                <a href="${pageContext.request.contextPath}/signup">Зарегистрируйтесь</a>
            </h3>
        </c:when>
    </c:choose>

    <br><br>
    <h2> Наша он-лайн аптека - это широкий выбор лекарственных средств,
        удобный способ оплаты и экономия Вашего времени!
    </h2>
</div>
