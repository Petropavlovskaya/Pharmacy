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
        <%@include file="/css/style.css" %>
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
    <div>
        <c:if test="${not empty message}">
            <p class="p-red">${message}<br></p>
        </c:if>

        <h3><fmt:message key="label.account.balanceReplenishment"/>:</h3>
        <form action="${pageContext.request.contextPath}/customer/cabinet/profile" method="post">
            <input name="balance" type="text" size="6"
                   required pattern="\d{1,4}" min="1" title=<fmt:message key="label.account.balanceFieldTitle"/>>
            <input type="submit" value=<fmt:message key="label.account.buttonReplenish"/>>
            <input type="hidden" name="customerCommand" value="increaseBill">
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
    <div>
        <br>
        <script src="${pageContext.request.contextPath}/toastr/toastr.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/toastr/jquery-3.5.1.min.js" type="text/javascript"></script>
        <script type="text/javascript">$('#invokesToastMessage').click(function () {
            // toastr["success"]("Test", "A")

            toastr.options = {
                "closeButton": true,
                "debug": false,
                "newestOnTop": false,
                "progressBar": false,
                "positionClass": "toast-top-right",
                "preventDuplicates": false,
                "onclick": null,
                "showDuration": "300",
                "hideDuration": "1000",
                "timeOut": "5000",
                "extendedTimeOut": "1000",
                "showEasing": "swing",
                "hideEasing": "linear",
                "showMethod": "fadeIn",
                "hideMethod": "fadeOut"
            }
            toastr.success('Test', 'title');

        });</script>

        <button id="invokesToastMessage">Click Alert!</button>
    </div>
</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>