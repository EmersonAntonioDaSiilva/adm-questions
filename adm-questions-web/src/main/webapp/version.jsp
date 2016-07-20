<pre>

VERSION: ${pom.version}
BUILD DATE: ${build.date}
<%= request.getLocalAddr().replaceAll("10.200.242.","").replaceAll("10.200.40.","").replaceAll("10.200.240.","") + "-" + System.getProperty("jboss.server.name") %> 

</pre>
