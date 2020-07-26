<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Profile</title>
    <style>
        <%@include file="/css/style.css" %>
        <%@include file="/toastr/toastr.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/toastr/toastr.min.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_customer_menu.jsp"/>
</div>
<div id="center_no_right">
    <p class="p-error">${requestScope.get('errorMessage')}</p>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.5.0/jquery.min.js"></script>
    <c:import url="../../_toastr.jsp"/>
    <c:if test="${not empty sessionScope.get('successMessage')}" >
        <c:import url="../../_toastrFuncSuccess.jsp"/>
    </c:if>
    <div>
        <h3><fmt:message key="label.account.balanceReplenishment"/>:</h3>
        <form action="${pageContext.request.contextPath}/customer/cabinet/profile" method="post">
            <input name="balance" type="text" size="6"
                   required pattern="\d{1,4}" min="1" title=<fmt:message key="label.account.balanceFieldTitle"/>>
            <input type="submit" value=<fmt:message key="label.account.buttonReplenish"/>>
            <input type="hidden" name="frontCommand" value="increaseBill">
        </form>
        <br>
        <%--    <h3>Текущий баланс:</h3>--%>
        <c:choose>
            <c:when test="${customer.balance >= 0}">
                <p2><fmt:message key="label.account.balanceMessage"/>: ${customer.balanceRub} <fmt:message
                        key="label.rub"/> ${customer.balanceCoin} <fmt:message key="label.kop"/></p2>
            </c:when>
            <c:otherwise>
                <p2><fmt:message key="label.account.balanceMessage"/>: - ${customer.balanceRub*(-1)} <fmt:message
                        key="label.rub"/>
                        ${customer.balanceCoin*(-1)} <fmt:message key="label.kop"/>
                </p2>
            </c:otherwise>
        </c:choose>
    </div>

    <div>
        <br>
        <br>
        <c:import url="../../_accountProfile.jsp"/>
    </div>

</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>