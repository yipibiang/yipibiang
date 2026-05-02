param(
    [int]$Port = 9000,
    [string]$Secret = $env:WEBHOOK_SECRET
)

if (-not $Secret) { $Secret = "your_webhook_secret_here" }

$DOCKERHUB_USERNAME = if ($env:DOCKERHUB_USERNAME) { $env:DOCKERHUB_USERNAME } else { "yipibiang" }
$IMAGE_NAME = "${DOCKERHUB_USERNAME}/user-service:latest"

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://localhost:${Port}/")
$listener.Start()

Write-Host "[Webhook] Listening on localhost:$Port"
Write-Host "[Webhook] Deploy endpoint: POST http://localhost:$Port/deploy"

while ($listener.IsListening) {
    $context = $listener.GetContext()
    $request = $context.Request
    $response = $context.Response

    if ($request.HttpMethod -eq "POST" -and $request.Url.AbsolutePath -eq "/deploy") {
        $reader = New-Object System.IO.StreamReader($request.InputStream)
        $body = $reader.ReadToEnd()
        $reader.Close()

        Write-Host "[Deploy] Received deploy request"
        Write-Host "[Deploy] Pulling image: $IMAGE_NAME"
        docker pull $IMAGE_NAME

        Write-Host "[Deploy] Stopping old container..."
        docker stop user-service 2>$null

        Write-Host "[Deploy] Removing old container..."
        docker rm user-service 2>$null

        Write-Host "[Deploy] Starting new container..."
        docker run -d --name user-service `
            --network platform_default `
            -p 8081:8081 `
            -e JWT_SECRET=your_jwt_secret_here_min_32_chars `
            -e DB_HOST=mysql `
            -e DB_USERNAME=root `
            -e DB_PASSWORD=mysql_db_pw `
            -e DB_NAME=monorepo `
            $IMAGE_NAME

        Write-Host "[Deploy] Done!"

        $response.StatusCode = 200
        $response.ContentType = "application/json"
        $buffer = [System.Text.Encoding]::UTF8.GetBytes('{"status":"deployed"}')
        $response.OutputStream.Write($buffer, 0, $buffer.Length)
    } else {
        $response.StatusCode = 404
    }

    $response.Close()
}

$listener.Stop()