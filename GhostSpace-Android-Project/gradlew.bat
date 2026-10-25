@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Gradle start up script for Windows
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

java -Xmx2048m -Dfile.encoding=UTF-8 -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
