<%@page import="Javabeans.User"%>
<%@ page import="Javabeans.Message" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/basics/maxcdn.jsp" />
    <jsp:include page="/basics/nav.jsp" />
    <title>Messages</title>
</head>
<body>
    <% User user2 = (User) request.getSession().getAttribute("user");
    ArrayList<Message> mList = (ArrayList<Message>) request.getSession().getAttribute("mList"); %>

    <h3>Inbox</h3>
    <table class="forms" cellpadding="2" border="2">
        <tr class="def">
            <td>From</td>
            <td>To</td>
            <td>Message</td>
            <td>ItemID</td>
        </tr>
        <% for (int i=0; i<mList.size(); i++)  {
            Message m = new Message();
            m = mList.get(i);%>
        <tr>
            <td><%=m.from%></td>
            <td><%=m.to%></td>
            <td><%=m.message%></td>
            <td><%=m.itemID%></td>
            <td>
                <form method="post" action="./BBservlet?action=deletemsg&msgid=<%=m.msgID%>">
                    <input type="submit" value="delete message"/>
                </form>
            </td>
        </tr>
        <%}%>

    </table>
    <h3>Sent</h3>
    <h3>Send a new message</h3>

<jsp:include page="/basics/footer.jsp" />
</body>
</html>
