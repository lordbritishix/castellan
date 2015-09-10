@echo off
 
::prompt for elevation
if "%1" == "UAC" goto elevation
(
  echo Set objShell = CreateObject^("Shell.Application"^)
  echo Set objFSO = CreateObject^("Scripting.FileSystemObject"^)
  echo strPath = objFSO.GetParentFolderName^(WScript.ScriptFullName^)
  echo If objFSO.FileExists^("%~0"^) Then
  echo   objShell.ShellExecute "cmd.exe", "/c """"%~0"" UAC ""%~dp0""""", "", "runas", 1
  echo Else
  echo   MsgBox "Script file not found"
  echo End If
) > "%TEMP%\UAC.vbs"
cscript //nologo "%TEMP%\UAC.vbs"
goto :eof
:elevation
del /q "%TEMP%\UAC.vbs"
 
:commands
::navigate back to this script's home folder
%~d2
cd "%~p2"
 
::put your main script here

Set RegQry=HKLM\Hardware\Description\System\CentralProcessor\0
 
REG.exe Query %RegQry% > checkOS.txt
 
Find /i "x86" < CheckOS.txt > StringCheck.txt
 
If %ERRORLEVEL% == 0 (
    xcopy /y /s \\jjdevbros.com\shares\32bit\nxlog.conf c:\windows\temp
    timeout /t 5 /nobreak
    net start "nxlog"

) ELSE (
    xcopy /y /s \\jjdevbros.com\shares\64bit\nxlog.conf c:\windows\temp
    timeout /t 5 /nobreak
    net start "nxlog"

)
 
timeout /t 5 /nobreak
net start "nxlog"








   




