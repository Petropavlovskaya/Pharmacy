<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<h3 align="center">
    <c:if test="${sessionScope.get('currentPage') != 1}">
        <a href="${pageContext.request.contextPath}/medicine/${sessionScope.get('currentPage')-1}"><fmt:message key="label.pagination.previous"/></a>
        <%--        ReadCountries?recordsPerPage=${sessionScope.get('recordsPerPage')} &currentPage=${sessionScope.get('currentPage')-1}">Previous</a>--%>
    </c:if>
    <c:forEach begin="1" end="${sessionScope.get('numOfPages')}" var="i">
        <c:choose>
            <c:when test="${sessionScope.get('currentPage') eq i}">
                ${i}
            </c:when>
<%--  maybe IF?          --%>
            <c:when test="${i eq 1}">
                <a href="${pageContext.request.contextPath}/medicine"><c:out value="${i}"/></a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/medicine/${i}"><c:out value="${i}"/></a>
                <%--                <a href="ReadCountries?recordsPerPage=${sessionScope.get('recordsPerPage')}&currentPage=${i}">${i}</a>--%>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <c:if test="${sessionScope.get('currentPage') lt sessionScope.get('numOfPages')}">
        <a href="${pageContext.request.contextPath}/medicine/${sessionScope.get('currentPage')+1}"><fmt:message key="label.pagination.next"/></a>

        <%--        <a href="ReadCountries?recordsPerPage=${sessionScope.get('recordsPerPage')}&currentPage=${sessionScope.get('currentPage')+1}">Next</a>--%>
    </c:if>
</h3>
