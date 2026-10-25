# Builds and launches all HealthSafe services, each in its own window.
# Run from the project root:  .\scripts\start-all.ps1
$services = 'ingestion-service', 'ward-service', 'alert-level-service', 'staffing-service', 'equipment-alert-service'

foreach ($s in $services) {
    Write-Host "Building $s ..." -ForegroundColor Cyan
    Push-Location $s
    mvn -q package
    Pop-Location
}

foreach ($s in $services) {
    Start-Process powershell -ArgumentList '-NoExit', '-Command', "cd '$PWD\$s'; java -jar target\$s.jar"
}
Write-Host "Started. Try: curl.exe -s http://localhost:7033/staffing/W-05" -ForegroundColor Green