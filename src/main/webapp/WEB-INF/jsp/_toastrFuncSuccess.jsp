<script type="text/javascript">
    $(document).ready(function () {
        <%--toastr.success('${requestScope.get('msg')}');--%>
        toastr.success('${sessionScope.get('successMessage')}');
    });
</script>
