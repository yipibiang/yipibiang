param([int]$Port = 50002)
$l = New-Object System.Net.HttpListener
$l.Prefixes.Add("http://localhost:${Port}/")
try {
    $l.Start()
    Write-Host "Listening on $Port"
    Start-Sleep -Seconds 120
    $l.Stop()
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
