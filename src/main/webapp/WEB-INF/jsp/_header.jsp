<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>

<fmt:setLocale value="${sessionScope.get('lang')}"/>
<c:set value="${sessionScope.get('lang')}" var="locale"/>
<c:if test="${locale==null}">
    <c:set value="ru" var="locale"/>
</c:if>
<fmt:setBundle basename="messages"/>
<c:set value="ru" var="ru"/>
<c:set value="en" var="en"/>
<c:set value="pl" var="pl"/>

<div align="center">
    <%--    <c:out value="${locale}" ></c:out>--%>
    <img class="img-center" src="${pageContext.request.contextPath}/images/logo-z.gif" alt="On-line pharmacy" />
    <img class="img-left" src="${pageContext.request.contextPath}/images/doctor.gif" alt="Doctor"/>
</div>
<div align="right">
    <label class="language">
        <c:if test="${locale != ru}">
            <input class="ru" type="radio" name="locale"><a href="?locale=ru">
            <img src="${pageContext.request.contextPath}/images/RU-z.jpg" alt="RU"></a>
        </c:if>
        <c:if test="${locale == ru}">
            <input class="pl-ru" type="radio" name="locale"><a href="?locale=pl">
            <img src="${pageContext.request.contextPath}/images/PL-z.jpg" alt="PL"></a>
        </c:if>
        <c:if test="${locale != en}">
            <input class="en" type="radio" name="locale"><a href="?locale=en">
            <img src="${pageContext.request.contextPath}/images/GB-z.jpg" alt="EN"></a>
        </c:if>
        <c:if test="${locale == en}">
            <input class="pl-en" type="radio" name="locale"><a href="?locale=pl">
            <img src="${pageContext.request.contextPath}/images/PL-z.jpg" alt="PL"></a>
        </c:if>
    </label>
</div>

