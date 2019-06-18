Package with scripts to configure gerrit on windows

HOW to run Gerrit:
1.Configure Gerrit if it's the first time to start Gerrit(only once): run "init.bat"
2.To run Gerrit, run "daemon_run.bat" 
3.to open gerrit web interface, goto ".\gerrit_review_site\etc\gerrit.config",
  copy the URL from "canonicalWebUrl" to web browser


NOTE:
a) Make sure git for windows is installed
a) Make sure to rename the .war package to "gerrit.war" before running the scripts if you upgraded gerrit.
b) current package is gerrit-3.0.0.war.
c) JRE 1.8 and above shall be ready.
d) "ssh-keygen.exe" commands in "bin" copied from "git for windows", as the initialization phase needs this command.
e) you can configure as below to avoid registering a third party accounts:
Gerrit\gerrit_review_site\etc\gerrit.config file:
[auth]
	type = development_become_any_account

TBD:   
To run gerrit under tomcat, refer to: https://blogs.sap.com/2013/06/03/install-gerrit-in-tomcat-under-windows/
