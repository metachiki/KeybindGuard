@echo off
setlocal
cd /d "%~dp0.."
if not exist "build\core-test-classes" mkdir "build\core-test-classes"
javac --release 21 -d "build\core-test-classes" "src\main\java\dev\keybindguard\client\KeybindFile.java" "src\main\java\dev\keybindguard\client\StartupKeybindProtection.java" "src\main\java\dev\keybindguard\client\StartupKeybindGuard.java" "src\test\java\dev\keybindguard\client\KeybindFileTest.java"
if errorlevel 1 exit /b 1
java -cp "build\core-test-classes" dev.keybindguard.client.KeybindFileTest
exit /b %errorlevel%
