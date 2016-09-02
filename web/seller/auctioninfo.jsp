<%@ page import="Javabeans.Auction" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Javabeans.Photo" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="/basics/maxcdn.jsp" />
    <jsp:include page="/basics/auct_inf.jsp" />
    <title>Auctions</title>
    <style>



        #clockdiv{
            font-family: sans-serif;
            color: #fff;
            display: inline-block;
            font-weight: 100;
            text-align: center;
            font-size: 16px;
        }

        #clockdiv > div{
            padding: 4px;
            border-radius: 3px;
            background: #17aaa6;
            display: inline-block;
        }

        #clockdiv div > span{
            padding: 10px;
            border-radius: 3px;
            background: rgba(57, 211, 207, 0.4);
            display: inline-block;
        }

        .smalltext{
            padding-top: 3px;
            font-size: 12px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row">
            <center>
            <h3>Auction Info</h3>
            <% ArrayList<Auction> aList = (ArrayList<Auction>) request.getSession().getAttribute("aList");
                ArrayList<Photo> pList = (ArrayList<Photo>) request.getSession().getAttribute("pList");%>

            <% String pointer = (String) request.getSession().getAttribute("pointer");
                int point = Integer.parseInt(pointer);%>

            <div class="table-responsive">
                <table  class="table table-striped table-bordered table-condensed table-hover"  cellpadding="2" border="2">
                <thead class="thead-default">
                    </tr>
                        <th>Owner Name:</th>
                        <th>Item name:</th>
                        <th>Country:</th>
                        <th>City:</th>
                        <th>Current bid:</th>
                        <th>Category:</th>
                        <th>End of auction : </th>
                        <th>Description: </th>
                     </tr>
                </thead>



                    <% Auction x = new Auction();
                        x = aList.get(point); %>
                    <tbody>
                    <tr>

                        <td><%=x.seller%></td>
                        <td><%=x.name%></td>
                        <td><%=x.country%></td>
                        <td><%=x.city%></td>
                        <td><%=x.curr%></td>
                        <td><%=x.cat%></td>
                        <td><%=x.end%></td>
                        <td><%=x.description%></td>

                    </tr>
                    </tbody>
                </table>
            </div>

            <script>

                var end = new Date("<%=x.end%>");

                var _second = 1000;
                var _minute = _second * 60;
                var _hour = _minute * 60;
                var _day = _hour * 24;
                var timer;

                function showRemaining() {
                    var now = new Date();
                    var distance = end - now;
                    if (distance < 0) {

                        clearInterval(timer);
                        document.getElementById('clockdiv').innerHTML = '<p style="color:red;">EXPIRED!</p>';

                        return;
                    }
                    var days = Math.floor(distance / _day);
                    var hours = Math.floor((distance % _day) / _hour);
                    var minutes = Math.floor((distance % _hour) / _minute);
                    var seconds = Math.floor((distance % _minute) / _second);

                    document.getElementById('clockdiv').innerHTML =  ' <div> <span id="days">' +days+ '</span> <div class="smalltext">Days</div> </div> ';
                    document.getElementById('clockdiv').innerHTML +=' <div> <span id="hours">' + hours + '</span> <div class="smalltext">hrs</div> </div> ';
                    document.getElementById('clockdiv').innerHTML +=  ' <div> <span id="minutes">'+ minutes +'</span> <div class="smalltext">mins</div> </div> ';
                    document.getElementById('clockdiv').innerHTML +=  ' <div> <span id="seconds">'+ seconds +'</span> <div class="smalltext">secs</div> </div> ';
                }

                timer = setInterval(showRemaining, 1000);
            </script>

            <div id="clockdiv">
            </div>

            <br>

            <h3><b><a href="./BBservlet?action=viewphoto&id=<%=x.id%>&seller=<%=x.seller%>"><span class="glyphicon glyphicon-arrow-right"></span> See photos for this item</a></b></h3>

            <div class="panel panel-warning">
                <div class="panel-heading"><h3>Upload a photo :</h3></div>
                <div class="panel-body">
                    <form method="post" action="./../BBservlet?action=upload&seller=<%=x.seller%>&id=<%=x.id%>" enctype="multipart/form-data">
                        <table border="0" class="forms">
                            <tr>

                                <td><input type="file" class="btn btn-default btn-file" name="photo_file" accept="image/gif, image/jpeg, image/png" size="50"/></td>

                            </tr>

                            <tr>
                                <td colspan="2">
                                    <center>
                                    <button type="submit" class="btn btn-info"  id="send2"> Upload </button></center>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
            </center>

    </div>
</div>
<jsp:include page="/basics/footer.jsp" />
</body>
</html>