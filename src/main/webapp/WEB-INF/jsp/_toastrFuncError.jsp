<script type="text/javascript">
    $(document).ready(function () {
        <%--toastr.success('${requestScope.get('msg')}');--%>
        toastr.error('${requestScope.get('msgErrorText')}');
    });
</script>
