:: Gerrit Code Review - Standalone Daemon Installation
:: How to use:
:: put this batch file in the same folder as gerrit.war, and then click batch
:: pre-requsites: JRE must be installed first

:: To run the daemon after site initialization execute
:: To stop the daemon press Ctrl+C.
cd .\gerrit_review_site
if not exist bin\gerrit.war  copy ..\gerrit.war  bin\gerrit.war
java.exe -jar bin\gerrit.war daemon --console-log

PAUSE