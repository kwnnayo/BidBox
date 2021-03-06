package Servlets;

import ItemRecommendation.Recommendation;
import JBCrypt.BCrypt;
import Javabeans.*;
import xmlClasses.xmlAuction;
import xmlClasses.xmlAuctions;
import xmlClasses.xmlFunctions;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/BBservlet?action=upload")
@MultipartConfig(maxFileSize = 16177215)

public class BBservlet extends HttpServlet {

    private DataBase db = new DataBase();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(BBservlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(BBservlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, JAXBException {

        HttpSession session = request.getSession(true);
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        String page = request.getParameter("page");
        PrintWriter out = response.getWriter();

        if (action == null || action.equals("")) {

            switch (page) {
                case "admin":
                    request.getRequestDispatcher("/admin/admin.jsp").include(request, response);
                    break;
                case "userlist":
                    request.getRequestDispatcher("/admin/user_list.jsp").include(request, response);
                    break;
                case "userinfo":
                    request.getRequestDispatcher("/admin/user_info.jsp").include(request, response);
                    break;
                case "verify":
                    request.getRequestDispatcher("/admin/user_list.jsp").include(request, response);
                    break;
                case "userlogin":
                    request.getRequestDispatcher("/welcome/login.jsp").include(request, response);
                    break;
                case "addauction":
                    request.getRequestDispatcher("/seller/add_auction.jsp").include(request, response);
                    break;
                case "auctionlist":
                    request.getRequestDispatcher("/seller/auctionlist.jsp").include(request, response);
                    break;
                case "auctioninfo":
                    request.getRequestDispatcher("/seller/auctioninfo.jsp").include(request, response);
                    break;
                case "viewphoto":
                    request.getRequestDispatcher("/seller/photos.jsp").include(request, response);
                    break;
                case "log_out":
                    request.getRequestDispatcher("index.jsp").include(request, response);
                    break;
                case "msglist":
                    request.getRequestDispatcher("/user/msg2.jsp").include(request, response);
                    break;
                case "searchres":
                    request.getRequestDispatcher("/welcome/search_res.jsp").include(request, response);
                    break;
                case "auction_search":
                    request.getRequestDispatcher("/welcome/search_info.jsp").include(request, response);
                    break;
                case "auctionsformarsh":
                    request.getRequestDispatcher("/admin/auctions_marshall.jsp").include(request, response);
                    break;
                case "purchasedlist":
                    request.getRequestDispatcher("/seller/purchased.jsp").include(request, response);
                    break;
                case "purchasedinfo":
                    request.getRequestDispatcher("/seller/purchasedinfo.jsp").include(request, response);
                    break;
                case "send_msg":
                    request.getRequestDispatcher("/user/send_msg.jsp").include(request, response);
                    break;
                case "myprofile":
                    request.getRequestDispatcher("/user/myprofile.jsp").include(request, response);
                    break;
            }

        } else if (action.equals("signup_page")) {
            request.getRequestDispatcher("/welcome/signup.jsp").include(request, response);
        } else if (action.equals("signup")) {


            String FirstName = request.getParameter("name");
            String LastName = request.getParameter("surname");
            String UserName = request.getParameter("username");
            String Password = request.getParameter("pass");
            Password = BCrypt.hashpw(Password, BCrypt.gensalt());
            String Email = request.getParameter("email");
            String Phone = request.getParameter("phone");
            String afm = request.getParameter("afm");
            String city = request.getParameter("city");
            String Country = request.getParameter("country");

            String query = null;
            db.openConn();

            String query3 = "SELECT COUNT(*) AS total FROM user where username='" + UserName + "'";
            ResultSet rs3 = db.executeQuery(query3);
            int exists = 0;
            int exists2 = 0;

            while (rs3.next()) {
                if (rs3.getInt("total") > 0) {
                    exists = 1;
                    out.println("<font color=red><b>" + UserName + "</b> is already in use</font>");
                    request.getRequestDispatcher("/welcome/signup.jsp").include(request, response);
                }
            }


            String query4 = "SELECT COUNT(*) AS total FROM user where email='" + Email + "'";
            ResultSet rs4 = db.executeQuery(query4);

            while (rs4.next()) {
                if (rs4.getInt("total") > 0) {
                    exists2 = 1;
                }
            }

            if (exists == 0 && exists2 == 0) {



               query = "INSERT INTO user VALUES(0, '" +UserName+ "',"
                        + "'" + Password + "',"
                        + "'" + FirstName + "',"
                        + "'" + LastName + "',"
                        + "'" + Email + "',"
                        + "'" + Phone + "',"
                        + "'" + Country + "',"
                        + "'" + city + "',"
                        + "'" + afm + "',"
                        + "'" + 0 + "',"
                        + 0 + ","
                        + "'" + 0 + "') ";

                db.executeUpdate(query);


                request.getRequestDispatcher("/welcome/success_signup.jsp").include(request, response);
            }

            db.closeConnection();

        } else if (action.equals("login")) {

            try {

                String username = request.getParameter("Username");


                String query = "select * from user where username='" + username + "' ";

                db.openConn();

                ResultSet rs = db.executeQuery(query);


                if (rs.next() ) {

                    String pass=rs.getString("pass");
                    if (!(BCrypt.checkpw(request.getParameter("Password"), pass))){
                        request.getRequestDispatcher("./welcome/login_fail.jsp").include(request, response);
                        return;
                    }

                    User user = User.getUser(username);
                    ArrayList<Category> cList = Category.get_all_cat();
                    session.setAttribute("cList",cList);
                    session.setAttribute("user", user);
                    session.setAttribute("guest",user.username);
                    session.setAttribute("username", user.username);

                    if (user.ver == 0) {
                        request.getRequestDispatcher("/welcome/unverified.jsp").include(request, response);
                    } else {
                        if (Objects.equals(user.username, "admin")) {
                            response.sendRedirect("/BBservlet?page=admin");
                        } else {
                            response.sendRedirect("/BBservlet?page=userlogin");
                        }


                    }

                } else {


                    request.getRequestDispatcher("./welcome/login_fail.jsp").include(request, response);

                }

                db.closeConnection();

            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }

        }else if(action.equals("home")){

            String username = request.getParameter("Username");
            User user = User.getUser(username);
            ArrayList<Category> cList = Category.get_all_cat();
            session.setAttribute("cList",cList);
            session.setAttribute("user", user);
            session.setAttribute("guest",user.username);
            session.setAttribute("Username", user.username);

            if (Objects.equals(user.username, "admin")) {
                response.sendRedirect("/BBservlet?page=admin");
            } else {
                response.sendRedirect("/BBservlet?page=userlogin");
            }
        }
        else if (action.equals("login_guest")) {    //LOGIN

            try {

                String username = request.getParameter("Username");
                String password = request.getParameter("Password");
                String query = "select * from user where username='" + username + "'";

                db.openConn();

                ResultSet rs = db.executeQuery(query);

                if (rs.next()) {
                    String pass=rs.getString("pass");
                    if (!(BCrypt.checkpw(request.getParameter("Password"), pass))){
                        request.getRequestDispatcher("./welcome/login_fail.jsp").include(request, response);
                        return;
                    }
                    User user = User.getUser(username);
                    session.setAttribute("user", user);

                    if (user.ver == 0) {
                        request.getRequestDispatcher("/welcome/unverified.jsp").include(request, response);
                    } else {

                        if (Objects.equals(user.username, "admin")) {
                            response.sendRedirect("/BBservlet?page=admin");
                        } else {
                            int id = Integer.parseInt(request.getParameter("auctionid"));


                            String seller = request.getParameter("seller");
                            session.setAttribute("seller", seller);
                            response.sendRedirect("/BBservlet?action=auction_search&auctionid=" + id + "&seller=" + seller + "");
                        }


                    }

                } else {

//
                    request.getRequestDispatcher("./welcome/login_fail.jsp").include(request, response);

                }

                db.closeConnection();

            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (action.equals("userlist")) {

            int page_num = Integer.parseInt(request.getParameter("page_num"));
            int total = User.usersCounter();
            ArrayList<User> uList = User.usersPerPage(page_num);
            session.setAttribute("uList", uList);

            request.setAttribute("page_num", page_num);
            request.setAttribute("total", total);
            request.getRequestDispatcher("/admin/user_list.jsp").include(request, response);
//
        } else if (action.equals("verify_pend")) {

            int page_num = Integer.parseInt(request.getParameter("page_num"));
            int total = User.unverUsersCounter();
            ArrayList<User> uList = User.unverUsersPerPage(page_num);
            session.setAttribute("uList", uList);

            request.setAttribute("page_num", page_num);
            request.setAttribute("total", total);
            request.getRequestDispatcher("/admin/unver_user_list.jsp").include(request, response);

        } else if (action.equals("userinfo")) {

            String pointer = request.getParameter("pointer");
            session.setAttribute("pointer", pointer);
            response.sendRedirect("/BBservlet?page=userinfo");

        } else if (action.equals("verify")) {
            String user_name = request.getParameter("user_name2");
            String query = "UPDATE user SET verified=1 WHERE username='" + user_name + "'";
            db.openConn();
            int rs = db.executeUpdate(query);
            db.closeConnection();
            request.setAttribute("user_name",user_name);
            request.getRequestDispatcher("/admin/success_verify.jsp").include(request, response);

        } else if (action.equals("addpage")) {
            ArrayList<Category> cList = Category.get_all_cat();
            session.setAttribute("cList", cList);
            response.sendRedirect("/BBservlet?page=addauction");

        } else if (action.equals("addauction")) {

            String seller = request.getParameter("seller");
            String name = request.getParameter("name");
            String cats[] = request.getParameterValues("category");

            float latitude = Float.parseFloat(request.getParameter("latitude"));
            float longtitude = Float.parseFloat(request.getParameter("longtitude"));
            String country = request.getParameter("country");
            String city = request.getParameter("city");
            float buy_pr = Float.parseFloat(request.getParameter("buy_price"));
            float first_bid = Float.parseFloat(request.getParameter("first_bid"));
            float curr;
            curr = first_bid;
            int num_bid;
            num_bid = 0;

            String st;
            String end = request.getParameter("end");
            end = (end.replace("T", " "));

            if( request.getParameter("start") == null ) {
                st = null;
            }
            else{
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                st = dateFormat.format(date);
                st = "'" + st + "'";
            }

            String description = request.getParameter("description");

            db.openConn();

            String query = "INSERT INTO auction VALUES(0, '" + latitude + "',"
                    + "'" + longtitude + "',"
                    + "'" + seller + "',"
                    + "'" + name + "',"
                    + "'" + country + "',"
                    + "'" + city + "',"
                    + "'" + buy_pr + "',"
                    + "'" + first_bid + "',"
                    + "'" + curr + "',"
                    + "'" + num_bid + "',"
                    + st + ","
                    + "'" + end + "',"
                    + "'" + description + "',"
                    + "'" + 0 + "',"
                    + "'" + 0 + "',"
                    + "'" + 0 + "') ";

            PreparedStatement prest;
            prest = db.getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prest.executeUpdate();
            ResultSet rs = prest.getGeneratedKeys();

            if (rs.next()) {
                int last_inserted_id = rs.getInt(1);
                for (int i = 0; i < cats.length; i++) {
                    query = "INSERT INTO auction_has_cat VALUES(0, '" + last_inserted_id + "',"
                            + "'" + Integer.parseInt(cats[i]) + "') ";
                    db.executeUpdate(query);
                }
                request.getRequestDispatcher("/seller/success_add.jsp").include(request, response);
            }

//

            db.closeConnection();

        } else if (action.equals("upload")) { //UPLOAD A PHOTO

            String owner = request.getParameter("seller");
            int id = Integer.parseInt(request.getParameter("id"));
            InputStream inputStream = null;


            Part filePart = request.getPart("photo_file");
            String header = filePart.getHeader("content-disposition");
            String filename = header.substring(header.indexOf("filename=\"")).split("\"")[1];

            if (filePart != null) {

                inputStream = filePart.getInputStream();

            }

            try {

                db.openConn();
                Connection conn = db.getConn();


                String sql = "INSERT INTO photo (owner,pic,pic_name,itemID) values(?,?,?,?)";

                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, owner);

                statement.setString(3, filename);
                statement.setInt(4, id);
                if (inputStream != null) {

                    statement.setBlob(2, inputStream);
                }


                int row = statement.executeUpdate();

                if (row > 0) {
                    request.getRequestDispatcher("/seller/success_photo.jsp").include(request, response);
                } else {
                    request.getRequestDispatcher("/seller/failure_photo.jsp").include(request, response);
                }

            } finally {
                db.closeConnection();
            }

        } else if (action.equals("auctionlist")) {

            String seller = request.getParameter("username");
            int page_num = Integer.parseInt(request.getParameter("page_num"));
            int total = Auction.getnum(seller);
            session.setAttribute("page_num", page_num);
            session.setAttribute("total", total);
            String query = "SELECT * FROM auction WHERE seller='" + seller + "' LIMIT " + (page_num - 1) * 10 + ", " + 10 + "";
            ArrayList<Auction> aList = Auction.search_auction(query);
            ArrayList<Photo> photos = Photo.PhotoPerItem(aList);
            session.setAttribute("aList", aList);
            session.setAttribute("photos", photos);

            response.sendRedirect("/BBservlet?page=auctionlist");
        } else if (action.equals("auctioninfo")) {

            String pointer = request.getParameter("pointer");
            session.setAttribute("pointer", pointer);

            int itemID = Integer.parseInt(request.getParameter("itemID"));
            ArrayList<Category> cList = Category.get_its_cat(itemID);

            session.setAttribute("cList", cList);

            ArrayList<Photo> pList = Photo.pdoSelectAll(itemID);
            session.setAttribute("pList", pList);



            response.sendRedirect("/BBservlet?page=auctioninfo");

        }  else if (action.equals("purchasedinfo")) {

            String pointer = request.getParameter("pointer");
            session.setAttribute("pointer", pointer);


            int itemID = Integer.parseInt(request.getParameter("itemID"));
            ArrayList<Category> cList = Category.get_its_cat(itemID);
            session.setAttribute("boughtcList", cList);

            ArrayList<Photo> pList = Photo.pdoSelectAll(itemID);
            session.setAttribute("boughtpList", pList);



            response.sendRedirect("/BBservlet?page=purchasedinfo");

        }else if (action.equals("bought_items")) {

            User user = (User) request.getSession().getAttribute("user");
            int page_num = Integer.parseInt(request.getParameter("page_num"));

            String countquery = "SELECT * FROM auction WHERE buyerID='" +user.userID+"' AND sold >=1";
            int total = Auction.resultCounter( countquery );

            session.setAttribute("page_num", page_num);
            String query="SELECT * FROM auction WHERE buyerID='" +user.userID+"' AND sold >=1 LIMIT " + (page_num - 1) * 10 + ", " + 10 + "";
            ArrayList<Auction> bList = Auction.search_auction(query);
            session.setAttribute("total", total);
            session.setAttribute("boughtList", bList);

            response.sendRedirect("/BBservlet?page=purchasedlist");

        }else if( action.equals("rate")){
            int pointer=Integer.parseInt(request.getParameter("pointer")) ;
            int sold=Integer.parseInt(request.getParameter("sold")) ;
            request.setAttribute("pointer", pointer);
            int buyerID = Integer.parseInt(request.getParameter("buyer_id")) ;
            int auctionID = Integer.parseInt(request.getParameter("auctionID")) ;
            String username =(String) request.getSession().getAttribute("username");
            db.openConn();
            String query;
            if (sold==1){
                query="UPDATE auction SET sold=2 WHERE itemID='"+ auctionID + "'";
            }else {
                query="UPDATE auction SET sold=4 WHERE itemID='"+ auctionID + "'";
            }


            db.executeUpdate(query);
            if(request.getAttribute("up")!=null && !request.getAttribute("up").equals("") ) {
                query = "UPDATE user SET rating_bidder=rating_bidder+1 WHERE userID='" +buyerID+"'";
            }else{
                query = "UPDATE user SET rating_bidder=rating_bidder-1 WHERE userID='" +buyerID+"'";
            }
            db.executeUpdate(query);
            db.closeConnection();
            response.sendRedirect("/BBservlet?action=auctionlist&username="+username+"&page_num=1");


        }else if( action.equals("rate2")){
            int pointer=Integer.parseInt(request.getParameter("pointer")) ;
            int sold=Integer.parseInt(request.getParameter("sold")) ;
            request.setAttribute("pointer", pointer);
            String seller_name = (request.getParameter("seller_name")) ;
            int auctionID = Integer.parseInt(request.getParameter("auctionID")) ;
            String username =(String) request.getSession().getAttribute("username");
            db.openConn();
            String query;
            if (sold==1){
                query="UPDATE auction SET sold=3 WHERE itemID='"+ auctionID + "'";
            }else {
                query="UPDATE auction SET sold=4 WHERE itemID='"+ auctionID + "'";
            }


            db.executeUpdate(query);
            if(request.getAttribute("up")!=null && !request.getAttribute("up").equals("") ) {
                query = "UPDATE user SET rating_bidder=rating_seller+1 WHERE username='" +seller_name+"'";
            }else{
                query = "UPDATE user SET rating_bidder=rating_seller-1 WHERE username='" +seller_name+"'";
            }
            db.executeUpdate(query);
            db.closeConnection();
            response.sendRedirect("/BBservlet?action=bought_items&username="+username+"&page_num=1");


        }
        else if (action.equals("viewphoto")) {
            String id = request.getParameter("id");

            int itemID=Integer.parseInt(id);
            session.setAttribute("id", id);
            String seller = request.getParameter("seller");
            ArrayList<Photo> pList = Photo.pdoSelectAll(itemID);
            session.setAttribute("pList", pList);


            response.sendRedirect("/BBservlet?page=viewphoto");

        }
        else if (action.equals("viewphoto2")) {

            String name = request.getParameter("pic_name");
            Connection con = null;
            Blob image = null;
            byte[ ] imgData = null ;
            Statement stmt = null;
            ResultSet rs = null;

            try {

                db.openConn();
                con = db.getConn();
                stmt = con.createStatement();

                rs = stmt.executeQuery("SELECT pic from photo where pic_name='"+name+"'");

                if (rs.next()) {

                    image = rs.getBlob(1);
                    imgData = image.getBytes(1,(int)image.length());

                }else {

                    System.out.println("Display Blob Example");
                    System.out.println("image not found for given id");

                }


                response.reset();
                response.setContentType("image/gpg");
                OutputStream o = response.getOutputStream();
                o.write(imgData);
                o.flush();
                o.close();

            }catch (Exception e) {
                System.out.println("Unable To Display image");
                System.out.println("Image Display Error=" + e.getMessage());

            } finally {
                try {
                    rs.close();
                    stmt.close();
                    con.close();
                    db.closeConnection();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (action.equals("logout")) {

            request.getSession().invalidate();
            response.sendRedirect("/BBservlet?page=log_out");

        }
        else if (action.equals("msglist")){
            db.openConn();
            String username = request.getParameter("username");
            session.setAttribute("username", username);

            ArrayList<Message> mList = Message.get_inbox(username);
            session.setAttribute("mList", mList);
            ArrayList<Message> mList2 = Message.get_sent(username);
            session.setAttribute("mList2", mList2);
            String query="UPDATE message SET seen=1 WHERE owner='"+ username +"' ";
            db.executeUpdate(query);
            db.closeConnection();

            response.sendRedirect("/BBservlet?page=msglist");
        }else if (action.equals("read")){
            int pointer = Integer.parseInt(request.getParameter("pointer"));
            int flag = Integer.parseInt(request.getParameter("flag"));

            session.setAttribute("point",pointer);
            session.setAttribute("flag",flag);
            request.getRequestDispatcher("/user/readmsg.jsp").include(request, response);

        }
        else if (action.equals("deletemsg")){
            String username = request.getParameter("username");
            int message_id = Integer.parseInt(request.getParameter("msgid"));
            String query = "DELETE FROM message WHERE msgID='"+message_id+"'";

            db.openConn();
            int rs3 = db.executeUpdate(query);
            db.closeConnection();

            response.sendRedirect("/BBservlet?action=msglist&username="+username+"");

        }else if (action.equals("recommendations")){
            int seller = Integer.parseInt(request.getParameter("seller"));
            session.setAttribute("guest","dummy");
            ArrayList<Integer> recommended_items = Recommendation.getfromDB(seller);
            if(recommended_items.size()==0){
                request.getRequestDispatcher("/user/check_later.jsp").include(request, response);
                return;
            }
            ArrayList<Auction> aList = Auction.recommended_auctions(recommended_items);
            session.setAttribute("search_list",aList);
            ArrayList<Photo> photos = Photo.PhotoPerItem(aList);
            session.setAttribute("photos", photos);
            request.setAttribute("page_num", 1);
            request.getRequestDispatcher("/welcome/search_res.jsp").include(request, response);

        }
        else if (action.equals("searchres")){



            String seller = request.getParameter("seller");
            session.setAttribute("guest",seller);
            String choice = request.getParameter("choice");
            String terms = request.getParameter("keywords");
            String location = request.getParameter("location");

            int from_pr;
            int to_pr;

            int inactive = 0;

            if(!"".equals(request.getParameter("from_pr")) && request.getParameter("from_pr")!=null) {
                from_pr = Integer.parseInt(request.getParameter("from_pr"));
            }
            if(!"".equals(request.getParameter("to_pr"))&& request.getParameter("to_pr")!=null) {
                to_pr = Integer.parseInt(request.getParameter("to_pr"));
            }

            String query = "SELECT * FROM ";

            String from = "auction WHERE ";
            String from_where_cat;
            String where = "seller !='"+ seller + "'";

            if( !choice.equals("any") ){
                from_where_cat =
                        "auction,auction_has_cat WHERE auction.itemID="
                        + "auction_has_cat.itemID AND auction_has_cat.catID = " + choice + " AND ";

                query = query + from_where_cat;
            }
            else{
                query = query + from;
            }

            if( request.getParameter("inactive") == null ) {
                where = where +" AND expired=0 AND sold=0 ";
            }
            else{
                inactive = Integer.parseInt(request.getParameter("inactive"));
            }

            if( location != null && !location.isEmpty()) {
                where = where + " AND ( (auction.city LIKE '%" + location +"%') OR (auction.country LIKE '%"
                + location + "%') ) ";
            }

            if((!"".equals(request.getParameter("from_pr"))&& request.getParameter("from_pr")!=null) && (!"".equals(request.getParameter("to_pr"))&& request.getParameter("to_pr")!=null)) {
                from_pr = Integer.parseInt(request.getParameter("from_pr"));
                to_pr = Integer.parseInt(request.getParameter("to_pr"));

                where = where +"AND curr BETWEEN " + from_pr + " AND " + to_pr + "";
            }else{
                if(!"".equals(request.getParameter("from_pr"))&& request.getParameter("from_pr")!=null){
                    from_pr = Integer.parseInt(request.getParameter("from_pr"));
                    where  = where + "AND curr >= " + from_pr + " ";
                }else if(!"".equals(request.getParameter("to_pr"))&& request.getParameter("to_pr")!=null){
                    to_pr = Integer.parseInt(request.getParameter("to_pr"));
                    where = where + "AND curr <= " + to_pr + " ";
                }
            }

            if( terms != null && !terms.isEmpty()) {
                where = where + " AND ( (name LIKE '%" + terms + "%') OR (description LIKE '%" + terms + "%') )";
            }

            where = where + " AND st IS NOT NULL";
            query = query + where;

            System.out.println(query);
            System.out.println(query);


            ArrayList<Auction> aList =Auction.search_auction(query);

            session.setAttribute("search_list", aList);
            ArrayList<Photo> photos = Photo.PhotoPerItem(aList);

            session.setAttribute("photos", photos);
            request.setAttribute("page_num", 1);


            request.getRequestDispatcher("/welcome/search_res.jsp").include(request, response);



        }else if (action.equals("search_paging")){

            int i=Integer.parseInt(request.getParameter("page_num")) ;
            request.setAttribute("page_num", i);


            request.getRequestDispatcher("/welcome/search_res.jsp").include(request, response);

        }
        else if (action.equals("auction_search")){


            int id = Integer.parseInt(request.getParameter("auctionid"));
            Auction auction = Auction.getAuctionbyid(id);
            request.setAttribute("auction", auction);

            String seller = request.getParameter("seller");
            session.setAttribute("seller", seller);

            ArrayList<Photo> pList = Photo.pdoSelectAll(id);
            ArrayList<Category> cList = Category.get_its_cat(id);
            session.setAttribute("cList", cList);
            session.setAttribute("pList", pList);

            request.getRequestDispatcher("/welcome/search_info.jsp").include(request, response);

        } else if (action.equals("auction_search_guest")){


            int id = Integer.parseInt(request.getParameter("auctionid"));
            Auction auction = Auction.getAuctionbyid(id);
            request.setAttribute("auction", auction);

            String seller = request.getParameter("seller");
            session.setAttribute("seller", seller);

            ArrayList<Photo> pList = Photo.pdoSelectAll(id);
            session.setAttribute("pList", pList);
            ArrayList<Category> cList = Category.get_its_cat(id);
            session.setAttribute("cList", cList);

            request.getRequestDispatcher("/welcome/search_info_guest.jsp").include(request, response);

        }
        else if (action.equals("place_bid")){

            String seller = request.getParameter("seller");
            session.setAttribute("seller", seller);


            int itemid = Integer.parseInt(request.getParameter("itemid"));
            session.setAttribute("itemid", itemid);

            int bidderid = Integer.parseInt(request.getParameter("bidderid"));
            float amount = Float.parseFloat(request.getParameter("amount"));



            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String curdate = dateFormat.format(date);

            String query = "INSERT INTO bid VALUES(0, '" + bidderid + "',"
                    + "'" + itemid + "',"
                    + "'" + amount + "',"
                    + "'" + curdate + "')";

            db.openConn();

            Integer i = db.executeUpdate(query);

            Auction auction = Auction.getAuctionbyid(itemid);

            if(auction.buy_pr==amount){
                query="UPDATE auction SET buyerID='"+bidderid+"', expired= 1 , curr='"+amount+"', sold=1 ,num_bid=num_bid+1 WHERE  itemID='"+itemid+"'";

            }else{
                query = "UPDATE auction SET buyerID='"+bidderid+"',curr='" + amount + "' ,num_bid=num_bid+1 WHERE itemID='"+itemid+"'";


            }
            i=db.executeUpdate(query);
            db.closeConnection();

            response.sendRedirect("/BBservlet?action=auction_search&seller="+seller+"&auctionid="+itemid+"");
        }
        else if (action.equals("check_username")) {


            db.openConn();
            String UserName = request.getParameter("username");
            String query3 = "SELECT COUNT(*) AS total FROM user where username='" + UserName + "'";
            ResultSet rs3 = db.executeQuery(query3);
            int exists = 0;

            response.setContentType("text/html;charset=UTF-8");


            while (rs3.next()) {
                if (rs3.getInt("total") > 0) {
                    exists = 1;

                    out.println("<font color=red><b>" + UserName + "</b> is already in use</font>");



                } else {

                    out.println("<font color=green><b>" + UserName + "</b> is available!</font>");

                }

            }
        }
        else if (action.equals("check_email")) {


            db.openConn();
            String email = request.getParameter("email");
            String query3 = "SELECT COUNT(*) AS total FROM user where email='" + email + "'";
            ResultSet rs3 = db.executeQuery(query3);
            response.setContentType("text/html;charset=UTF-8");

            while (rs3.next()) {

                if (rs3.getInt("total") > 0) {
                    out.println("<font color=red><b>" + email + "</b> is already in use</font>");
                } else {
                    out.println("<font color=green><b>" + email + "</b> is available!</font>");
                }
            }
        }
        else if (action.equals("send_msg")) {

            response.sendRedirect("/BBservlet?page=send_msg");

        }
        else if (action.equals("send_msgf")) {

            db.openConn();
            String user = request.getParameter("user");
            String receiver = request.getParameter("receiver");
            String message = request.getParameter("message");


            String itemID = request.getParameter("itemID");

            String query = "INSERT INTO message VALUES(0, '" + message + "',"
                    + "'" + 0 + "',"
                    + "'" + user + "',"
                    + "'" + receiver + "',"
                    + "'" + itemID + "',"
                    + "'" + user + "')";

            db.executeUpdate(query);
            query = "INSERT INTO message VALUES(0, '" + message + "',"
                    + "'" + 0 + "',"
                    + "'" + user + "',"
                    + "'" + receiver + "',"
                    + "'" + itemID + "',"
                    + "'" + receiver + "')";
            db.executeUpdate(query);

            db.closeConnection();

            response.sendRedirect("/BBservlet?action=msglist&username="+user+"");

        }
        else if (action.equals("delete_item")) {

            int itemid = Integer.parseInt(request.getParameter("id"));
            String uname = request.getParameter("username");

            //First we delete the item's photo due to foreign key
            String query = "DELETE FROM photo WHERE itemID='"+itemid+"'";

            db.openConn();
            int rs3 = db.executeUpdate(query);

            query = "DELETE FROM auction_has_cat WHERE itemID='"+itemid+"'";
            rs3 = db.executeUpdate(query);

            //After we have delete its photos, we delete the auction itself
            query = "DELETE FROM auction WHERE itemID='"+itemid+"'";
            rs3 = db.executeUpdate(query);

            db.closeConnection();

            response.sendRedirect("/BBservlet?action=auctionlist&username="+uname+"&page_num=1");

        }
        else if (action.equals("edit_item")) {

            int itemid = Integer.parseInt(request.getParameter("id"));
            String uname = request.getParameter("username");
            ArrayList<Category> cList = Category.get_all_cat();
            session.setAttribute("cList", cList);
            Auction auction = Auction.getAuctionbyid(itemid);



            request.setAttribute("auction",auction);

            request.getRequestDispatcher("/seller/edit_auction.jsp").include(request, response);
        }
        else if (action.equals("do_edit_auction")) {

            String seller = request.getParameter("seller");
            String name = request.getParameter("name");
            String cats[] = request.getParameterValues("category");
            float latitude = Float.parseFloat(request.getParameter("latitude"));
            float longtitude = Float.parseFloat(request.getParameter("longtitude"));
            String country = request.getParameter("country");
            String city = request.getParameter("city");
            float buy_pr = Float.parseFloat(request.getParameter("buy_price"));
            float first_bid = Float.parseFloat(request.getParameter("first_bid"));
            float curr;
            curr= first_bid;
            int itemid = Integer.parseInt(request.getParameter("itemid"));

            String end = request.getParameter("end");

            end = (end.replace("T"," "));
            String description = request.getParameter("description");

            db.openConn();

            String query = "UPDATE auction SET latitude='" + latitude + "',"
                    + "longtitude='" + longtitude + "',"
                    + "seller='" + seller + "',"
                    + "name='" + name + "',"
                    + "country='" + country + "',"
                    + "city='" + city + "',"
                    + "buy_pr='" + buy_pr + "',"
                    + "first_bid='" + first_bid + "',"
                    + "curr='" + curr + "',"
                    + "end='" + end + "',"
                    + "description='" + description + "' WHERE itemID='"+itemid+"'";


            Integer i = db.executeUpdate(query);
            query = "DELETE FROM auction_has_cat WHERE itemID='"+itemid+"'";
            i = db.executeUpdate(query);
            for(int k=0; k<cats.length; k++){
                query = "INSERT INTO auction_has_cat VALUES(0, '" + itemid + "',"
                        + "'" + Integer.parseInt(cats[k]) + "') ";
                i=db.executeUpdate(query);
            }

            db.closeConnection();


            response.sendRedirect("/BBservlet?action=auctionlist&page_num=1&username=" + seller);


        }
        else if (action.equals("myprofile")) {

            String uname = request.getParameter("username");
            User user = User.getUser(uname);
            session.setAttribute("user", user);

            response.sendRedirect("/BBservlet?page=myprofile");

        }
        else if(action.equals("searchpage")){
            ArrayList<Category> cList = Category.get_all_cat();
            request.setAttribute("cList", cList);


            request.getRequestDispatcher("/welcome/search.jsp").include(request, response);

        } else if(action.equals("guest_searchpage")){
            ArrayList<Category> cList = Category.get_all_cat();
            request.setAttribute("cList", cList);


            request.getRequestDispatcher("/welcome/search_guest.jsp").include(request, response);
        }else if(action.equals("auctions_unmarshall")){
            ServletContext context = request.getServletContext();
            String file = context.getRealPath("/XMLfiles");
            File f = new File(file);
            String [] filenames = f.list();
            File [] fileobjects = f.listFiles();
            for(int i=0;i<fileobjects.length;i++){

                System.out.println(filenames[i]);


            }
            session.setAttribute("filenames",filenames);


            request.getRequestDispatcher("/admin/auctions_unmarshall.jsp").include(request,response);

        }
        else if (action.equals("unmarshall")) {
            ServletContext context = request.getServletContext();
            String filename=request.getParameter("item_option");
            String file = context.getRealPath("/XMLfiles/"+filename);



            File f = new File(file);
            JAXBContext jaxbContext = JAXBContext.newInstance(xmlAuctions.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            xmlAuctions auctions = (xmlAuctions) jaxbUnmarshaller.unmarshal(f);

            auctions.sendauctions();

            request.getRequestDispatcher("/admin/auctions_unmarshall.jsp").include(request, response);

        }
        else if (action.equals("auctions_marsh")){

            int page_num = Integer.parseInt(request.getParameter("page_num"));
            int total = Auction.getnum("*");
            session.setAttribute("page_num", page_num);
            session.setAttribute("total", total);
            String query = "SELECT * FROM auction LIMIT " + (page_num - 1) * 10 + ", " + 10 + "";
            ArrayList<Auction> allauctions = Auction.search_auction(query);
            session.setAttribute("allauctions", allauctions);
            ArrayList<Photo> photos = Photo.PhotoPerItem(allauctions);
            session.setAttribute("photos", photos);
            response.sendRedirect("/BBservlet?page=auctionsformarsh");

        }
        else if (action.equals("marshall")) {
            String checkboxValues[] = request.getParameterValues("item_option");
            int page_num = Integer.parseInt(request.getParameter("page_num"));
            Integer[] itemIDs=new Integer[checkboxValues.length];
            for(int i=0;i<checkboxValues.length;i++){
                itemIDs[i]=Integer.parseInt(checkboxValues[i]);

            }
            File file = new File("C:\\Users\\kwnst\\Desktop\\custom3.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(xmlAuctions.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            xmlAuctions auctions = new xmlAuctions();


            auctions.getauctions( itemIDs );

            jaxbMarshaller.marshal(auctions, file);



            response.sendRedirect("/BBservlet?action=auctions_marsh&page_num="+page_num);

        }
        else if(action.equals("show_profile") ){
            String uname = request.getParameter("username");
            String sellerun = request.getParameter("sellerun");

            User seller = User.getUser(sellerun);

            request.setAttribute("seller",seller);
            request.getRequestDispatcher("/welcome/show_profile.jsp").include(request, response);
        }
        else if(action.equals("start_auction") ){

            String uname = request.getParameter("username");
            int itemid = Integer.parseInt(request.getParameter("id"));
            db.openConn();
            String start;

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            start = dateFormat.format(date);
            String query="UPDATE auction SET st='"+start+"' WHERE itemID='"+itemid+"'";
            int i=db.executeUpdate(query);

            db.closeConnection();

            response.sendRedirect("/BBservlet?action=auctionlist&page_num=1&username=" + uname);
        }
        else if(action.equals("pending_bids") ){

            String username =(String) request.getSession().getAttribute("username");
            int page_num = Integer.parseInt( request.getParameter("page_num") );

            ArrayList<Auction> pendlist = new ArrayList<Auction>();

            String query = "SELECT DISTINCT(auction.itemID) FROM user,bid,auction WHERE user.username='" + username + "'" +
                    "AND user.userID = bid.userID AND bid.itemID = auction.itemID "+
                    "AND auction.sold = 0 AND auction.expired = 0";

            int total = Auction.resultCounter( query );

            query = query + " LIMIT " + (page_num - 1) * 10 + ", " + 10 + "";
            db.openConn();
            System.out.println( query );
            ResultSet rs = db.executeQuery( query );

            while( rs.next() ){
                pendlist.add( Auction.getAuctionbyid( rs.getInt("itemID") ) );
            }

            db.closeConnection();


            ArrayList<Photo> photos = Photo.PhotoPerItem( pendlist );
            session.setAttribute("aList",pendlist);
            request.setAttribute("aList",pendlist);

            request.setAttribute("photos",photos);
            request.setAttribute("total",total);

            request.setAttribute("page_num",page_num);

            request.getRequestDispatcher("/user/pending_bids.jsp").include(request,response);
        }

    }
}