$ErrorActionPreference = 'Stop'

Set-Location "$PSScriptRoot\.."

New-Item -ItemType Directory -Force -Path ".\lib" | Out-Null

$flatlafJar = ".\lib\flatlaf-3.4.jar"
$flatlafExtrasJar = ".\lib\flatlaf-extras-3.4.jar"
$jsvgJar = ".\lib\jsvg-1.2.0.jar"

if (!(Test-Path $flatlafJar)) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar" -OutFile $flatlafJar
}

if (!(Test-Path $flatlafExtrasJar)) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/formdev/flatlaf-extras/3.4/flatlaf-extras-3.4.jar" -OutFile $flatlafExtrasJar
}

if (!(Test-Path $jsvgJar)) {
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/github/weisj/jsvg/1.2.0/jsvg-1.2.0.jar" -OutFile $jsvgJar
}

New-Item -ItemType Directory -Force -Path ".\out" | Out-Null
$javaFiles = Get-ChildItem -Path . -Filter "*.java" | ForEach-Object { $_.FullName }

$classpath = "$flatlafJar;$flatlafExtrasJar;$jsvgJar"

$localJavaHome = ".\java-1.8.0-openjdk-1.8.0.482.b08-1.win.jdk.x86_64"
$javacCmd = $null
$javawCmd = $null

if (Test-Path "$localJavaHome\bin\javac.exe") {
    $javacCmd = "$localJavaHome\bin\javac.exe"
    $javawCmd = "$localJavaHome\bin\javaw.exe"
} else {
    $javac = Get-Command javac -ErrorAction SilentlyContinue
    $javaw = Get-Command javaw -ErrorAction SilentlyContinue
    if ($javac -and $javaw) {
        $javacCmd = $javac.Source
        $javawCmd = $javaw.Source
    } else {
        throw "Java not found. Install JDK 8+ and ensure javac/javaw are on PATH."
    }
}

& $javacCmd -encoding UTF-8 -cp $classpath -d ".\out" $javaFiles
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed"
}

Start-Process -FilePath $javawCmd -ArgumentList "-cp", "out;$classpath", "Main" -WorkingDirectory (Get-Location)
Write-Output "Inventory app launched."
exit 0
