
echo please copy *.war to dir war 


set CURRENT_DIR=%~dp0


xcopy /Y %CURRENT_DIR%build\*.war %CURRENT_DIR%..\package\war