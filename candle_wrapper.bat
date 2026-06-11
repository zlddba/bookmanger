@echo off
setlocal
echo %date% %time% CANDLE_WRAPPER called with: %* >> %TEMP%\candle_wrapper.log
copy "%TEMP%\candle_wrapper.log" "C:\Users\zlddb\AppData\Local\Temp\candle_debug\" 2>/dev/null
mkdir "%TEMP%\candle_debug" 2>/dev/null
rem Copy the WXS and config files
for %%a in (%*) do (
    echo %%a | find ".wxs" >/dev/null
    if not errorlevel 1 (
        copy "%%a" "%TEMP%\candle_debug\" 2>/dev/null
    )
)
rem Run actual candle
"E:\dazhuan\ruanjiancheshi\codes\bookmanger\build\wix311\candle.exe" %* >> %TEMP%\candle_wrapper_out.log 2>> %TEMP%\candle_wrapper_err.log
set EXIT_CODE=%ERRORLEVEL%
echo Exit code: %EXIT_CODE% >> %TEMP%\candle_wrapper.log
exit /b %EXIT_CODE%
