
set WORKSPACE=E:\ops-eclipse\workspacefb
set flashBuilderc="D:\Program Files\adobe\Adobe Flash Builder 4.6\FlashBuilderC.exe"
set TE_RELEASEDIR=E:/build-opsdev
set CURRENT_DIR=%~dp0

REM works with either FlashBuilderC.exe or eclipsec.exe 
cd /d %CURRENT_DIR%
%flashBuilderc% --launcher.suppressErrors -noSplash -application org.eclipse.ant.core.antRunner  -data "%WORKSPACE%"  -file "%CURRENT_DIR%\build.xml" antwar -DTeReleaseDir=%TE_RELEASEDIR%