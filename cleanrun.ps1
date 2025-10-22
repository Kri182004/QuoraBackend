# cleanrun.ps1
Write-Host "🧹 Stopping Gradle Daemons..."
./gradlew --stop

Write-Host "🗑️ Removing build directory..."
# Delete the build folder, ignore errors if it doesn't exist
Remove-Item -Recurse -Force .\build -ErrorAction SilentlyContinue

Write-Host "🚀 Running clean bootRun..."
./gradlew clean bootRun
