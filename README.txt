Description of the package:
1.apache-tomcat-9.0.20 - Tomcat. The servlet container for Jenkins and Gitblit, Gerrit is not working in this container(TBD)
2.ci - Jenkinsfile and wrapper scripts
3.Gerrit - Gerrit web application. 
4.README.md - this file

To run Tomcat:
1. enter into "apache-tomcat-9.0.20\bin\"
2. run pre-startup.bat
This batch is to set the enviornment variable JRE_HOME, if your JRE path is not the same, you need to modify it.

Tomcat is the prerequsites to run Jenkins and Gitblit in this package, as it's the container for other applications.

To configure Tomcat (only if you newly installed or upgraded Tomcat, otherwise you can reuse the configure):
1. configure Tomcat ports for different Protocols(eg: http): tomcat\conf\server.xml
2. configure Tomcat users (you must configure at least one user, by default, there is no user configured):
tomcat\conf\tomcat-users.xml, and Configure the "role" and "username".

To run any application managed by Tomcat:
1. open any web brower (eg: chrome, IE etc.)
2. enter into tomcat manager(you can start any program managed by tomcat): 
localhost:8080/manager  username: admin, password: password
enter into jenkins directly: localhost:8080/jenkins   create your first account when start.
enter into gitblit: localhost:8080/gitblit   create your first account when start.

NOTE:
TBD: Gerrit can't be run from Tomcat currently, you need to run Gerrit separately from "CI/Gerrit".
Read "README" in Gerrit for more information.

To modify the Gitblit data folder:
1.By default, gitblit store data in "Tomcat\webapps\gitblit\WEB-INF\data", this is not convenience to maintain.
eg. if you want upgrade the application.
2.Change the "data" folder to other places you want:
Open file: ".\CI\apahe-tomcat-9.0.20\webapps\gitblit\WEB-INF\web.xml"
change the data item "env-entry-value" to your desired location, eg: C:\Users\hcai2\
Note that in the package, it uses default "data" folder.

To use the "ci" package (which includes the jenkinsfile and the wrapper batch files):
1. put the "ci" folder into the root path of your repository
2. create new Pipeline projects in Jenkins, and configure the source script to "pipeline script from SCM",
change the "Script Path" to "./ci/jenkinsfile.groovy"