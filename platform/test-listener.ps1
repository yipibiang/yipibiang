$l = New-Object System.Net.HttpListener
$l.Prefixes.Add('http://localhost:50001/')
try {
    $l.Start()
    Write-Host 'HttpListener started successfully on port 50001'
    $l.Stop()
    Write-Host 'HttpListener stopped'
} catch {
    Write-Host "Failed: $($_.Exception.Message)"
}
Start-Sleep -Seconds 60
