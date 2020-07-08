<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>


<form id="getPage" method="post" action="${pageContext.request.contextPath}/${sessionScope.get("accountRole")}/medicine/list">
    <label for="records"><fmt:message key="label.pagination.perPageMessage"/>:</label>
    <select id="records" name="recordsPerPage" onchange="this.form.submit()">

        <c:if test="${sessionScope.get('recordsPerPage') == 5}">
            <option value="5" selected>5</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 5}">
            <option value="5">5</option>
        </c:if>

        <c:if test="${sessionScope.get('recordsPerPage') == 10}">
            <option value="10" selected>10</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 10}">
            <option value="10">10</option>
        </c:if>

        <c:if test="${sessionScope.get('recordsPerPage') == 15}">
            <option value="15" selected>15</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 15}">
            <option value="15">15</option>
        </c:if>

    </select>
</form>

