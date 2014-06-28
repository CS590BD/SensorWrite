<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    	               "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    	<title>group.seven - rest services</title>
  </head>
  <body>
    <h1>CREATE</h1>
    <p>usage:
    <br>http://server:port/application/rest/hbase/create/tablename/column1:column2:column3:column4:column5</p>
    <p>example: create a table of cars with three columns: mazda, ford, honda
    <br>http://localhost:8080/group.seven/rest/hbase/create/cars/mazda:ford:honda</p>
  </body>
</html> 
