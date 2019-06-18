:: Gerrit Code Review - Standalone Daemon Installation
:: How to use:
:: put this batch file in the same folder as gerrit.war, and then click batch
:: pre-requsites: JRE must be installed first

:: The ¡°ssh-keygen¡± command must be available during the init phase to generate SSH host keys.
:: Please note that the path must not be double-quoted.
SET CURRENT=%~dp0
SET PATH=%PATH%;%CURRENT%bin

:: initilize the gerrit site: $site_path
java -jar gerrit.war init -d ./gerrit_review_site

:: start daemon automatically
::daemon_run.bat

PAUSE